import Day12.SpringCondition.*
import org.intellij.lang.annotations.Language
import kotlin.io.path.readLines

class Day12 : Day {
    override fun partOne(filename: String, verbose: Boolean): Long =
        parseConditionRecords(filename)
            .sumOf {
                with(mutableMapOf<Pair<ConditionRecord, Int>, Long>()) {
                    countArrangements(it + OPERATIONAL, 0)
                }
            }

    override fun partTwo(filename: String, verbose: Boolean): Long =
        parseConditionRecords(filename)
            .map {
                ConditionRecord(
                    it.springs.unfold(5),
                    it.damagedGroups.unfold(5)
                )
            }
            .sumOf {
                with(mutableMapOf<Pair<ConditionRecord, Int>, Long>()) {
                    countArrangements(it + OPERATIONAL, 0)
                }
            }

    private fun MutableMap<Pair<ConditionRecord, Int>, Long>.countArrangements(
        conditionRecord: ConditionRecord, groupLength: Int
    ): Long =
        getOrPut(conditionRecord to groupLength) {
            if (conditionRecord.springs.isEmpty()) {
                return if (conditionRecord.damagedGroups.isEmpty() && groupLength == 0) 1 else 0
            }

            var sum = 0L

            val spring = conditionRecord.springs.first()
            if (spring in setOf(OPERATIONAL, UNKNOWN)) {
                if (conditionRecord.damagedGroups.isNotEmpty() && groupLength == conditionRecord.damagedGroups.first()) {
                    sum += countArrangements(
                        conditionRecord.copy(
                            springs = conditionRecord.springs.drop(1),
                            damagedGroups = conditionRecord.damagedGroups.drop(1)
                        ),
                        0
                    )
                }
                if (groupLength == 0) {
                    sum += countArrangements(
                        conditionRecord.copy(springs = conditionRecord.springs.drop(1)), groupLength
                    )
                }
            }

            if (spring in setOf(DAMAGED, UNKNOWN)) {
                sum += countArrangements(
                    conditionRecord.copy(springs = conditionRecord.springs.drop(1)),
                    groupLength + 1
                )
            }

            sum
        }

    private fun parseConditionRecords(@Language("file-reference") filename: String) =
        filename.asPath().readLines()
            .map { line ->
                val (springs, damagedGroups) = line.split(" ")
                ConditionRecord(
                    springs.map { it.toSpringCondition() },
                    damagedGroups.split(",").map { it.toInt() }
                )
            }

    private enum class SpringCondition(val symbol: Char) {
        OPERATIONAL('.'),
        DAMAGED('#'),
        UNKNOWN('?');

        override fun toString() =
            "$symbol"
    }

    private fun Char.toSpringCondition() =
        enumValues<SpringCondition>().first { it.symbol == this }

    private data class ConditionRecord(val springs: List<SpringCondition>, val damagedGroups: List<Int>)

    @JvmName("unfoldSpringConditions")
    private fun List<SpringCondition>.unfold(copies: Int) =
        List(copies) { this }.reduce { acc, repetition -> acc + UNKNOWN + repetition }

    @JvmName("unfoldDamagedGroups")
    private fun List<Int>.unfold(copies: Int) =
        List(copies) { this }.flatten()

    private operator fun ConditionRecord.plus(spring: SpringCondition) =
        copy(springs = springs + spring)

    companion object : Day.Main("Day12.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}
