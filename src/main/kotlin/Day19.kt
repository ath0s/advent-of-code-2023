import Day19.Comparator.Companion.toComparator
import kotlin.io.path.readText

private val WORKFLOW_PATTERN = Regex("""(?<name>\w+)\{(?<rules>.*),(?<default>\w+)}""")
private val RULE_PATTERN = Regex("""(?<attribute>[xmas])(?<comparator>[<>])(?<value>\d+):(?<outcome>\w+)""")
private val PART_PATTERN = Regex("""\{x=(?<x>\d+),m=(?<m>\d+),a=(?<a>\d+),s=(?<s>\d+)}""")

class Day19 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val (workflows, parts) = filename.asPath().readText().split("\n\n")
            .let { (workflows, parts) ->
                workflows.lines().map { it.toWorkflow() }.associateBy { it.name } to parts.lines().map { it.toPart() }
            }

        return parts.filter {
            workflows.resolve(it) == Accept
        }.sumOf { it.x + it.m + it.a + it.s }
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val workflows = filename.asPath().readText().substringBefore("\n\n")
            .lines()
            .map { it.toWorkflow() }
            .associateBy { it.name }

        val ranges = workflows.ranges()
        return ranges.sumOf {
            sequenceOf(it.x, it.m, it.a, it.s).fold(1L) { acc, range ->
                acc * (range.last - range.first + 1)
            }
        }
    }

    private data class Rule(
        val attribute: Attribute,
        val comparator: Comparator,
        val value: Int,
        val outcome: Outcome
    ) {
        operator fun invoke(part: Part): Outcome? =
            outcome.takeIf {
                comparator(part[attribute], value)
            }

        fun split(partRange: PartRange): Pair<PartRange, PartRange> =
            when (attribute) {
                Attribute.x -> {
                    val (match, noMatch) = comparator.split(partRange.x, value)
                    partRange.copy(x = match) to partRange.copy(x = noMatch)
                }

                Attribute.m -> {
                    val (match, noMatch) = comparator.split(partRange.m, value)
                    partRange.copy(m = match) to partRange.copy(m = noMatch)
                }

                Attribute.a -> {
                    val (match, noMatch) = comparator.split(partRange.a, value)
                    partRange.copy(a = match) to partRange.copy(a = noMatch)
                }

                Attribute.s -> {
                    val (match, noMatch) = comparator.split(partRange.s, value)
                    partRange.copy(s = match) to partRange.copy(s = noMatch)
                }
            }
    }

    private fun String.toRule(): Rule {
        val groups = RULE_PATTERN.matchEntire(this)!!.groups
        val attribute = enumValueOf<Attribute>(groups["attribute"]!!.value)
        val comparator = groups["comparator"]!!.value.toComparator()
        val value = groups["value"]!!.value.toInt()
        val outcome = groups["outcome"]!!.value.toOutcome()
        return Rule(attribute, comparator, value, outcome)
    }

    private data class Workflow(
        val name: String,
        val rules: List<Rule>,
        val default: Outcome
    ) {
        fun resolve(part: Part): Outcome =
            rules.firstNotNullOfOrNull { it(part) } ?: default

        fun ranges(partRange: PartRange): List<Pair<PartRange, Outcome>> = buildList {
            var currentRange = partRange
            for (rule in rules) {
                val (match, noMatch) = rule.split(currentRange)
                add(match to rule.outcome)
                currentRange = noMatch
            }
            add(currentRange to default)
        }
    }

    private fun Map<String, Workflow>.resolve(part: Part): Outcome {
        var workflow = get("in")!!
        do {
            when (val outcome = workflow.resolve(part)) {
                is Reference -> workflow = get(outcome.reference)!!
                else -> return outcome
            }
        } while (true)
    }

    private fun Map<String, Workflow>.ranges(): List<PartRange> =
        ranges(PartRange(), get("in")!!)

    private fun Map<String, Workflow>.ranges(partRange: PartRange, workflow: Workflow): List<PartRange> = buildList {
        workflow.ranges(partRange).forEach { (range, outcome) ->
            if (outcome is Accept) {
                add(range)
            } else if (outcome is Reference) {
                addAll(ranges(range, get(outcome.reference)!!))
            }
        }
    }

    private fun String.toWorkflow(): Workflow {
        val (name, rules, default) = WORKFLOW_PATTERN.matchEntire(this)!!.destructured
        return Workflow(name, rules.split(',').map { it.toRule() }, default.toOutcome())
    }

    private sealed interface Outcome

    private data object Accept : Outcome {
        override fun toString() = "A"
    }

    private data object Reject : Outcome {
        override fun toString() = "R"
    }

    @JvmInline
    private value class Reference(val reference: String) : Outcome {
        override fun toString() = reference
    }

    private fun String.toOutcome() =
        when (this) {
            "A" -> Accept
            "R" -> Reject
            else -> Reference(this)
        }

    private data class Part(
        val x: Int,
        val m: Int,
        val a: Int,
        val s: Int
    ) {
        operator fun get(attribute: Attribute): Int =
            when (attribute) {
                Attribute.x -> x
                Attribute.m -> m
                Attribute.a -> a
                Attribute.s -> s
            }
    }

    private data class PartRange(
        val x: IntRange = 1..4_000,
        val m: IntRange = 1..4_000,
        val a: IntRange = 1..4_000,
        val s: IntRange = 1..4_000,
    ) {
        operator fun get(attribute: Attribute) =
            when (attribute) {
                Attribute.x -> PartRange::x
                Attribute.m -> PartRange::m
                Attribute.a -> PartRange::a
                Attribute.s -> PartRange::s
            }
    }

    private fun String.toPart() =
        PART_PATTERN.matchEntire(this)!!.destructured.let { (x, m, a, s) ->
            Part(x.toInt(), m.toInt(), a.toInt(), s.toInt())
        }

    @Suppress("EnumEntryName")
    private enum class Attribute {
        x, m, a, s;
    }

    private enum class Comparator(private val char: Char) {
        LT('<'), GT('>');

        operator fun invoke(first: Int, second: Int) =
            when (this) {
                LT -> first < second
                GT -> first > second
            }

        fun split(range: IntRange, value: Int) =
            when (this) {
                LT -> (range.first until value) to (value..range.last)
                GT -> (value + 1..range.last) to (range.first..value)
            }

        override fun toString() =
            "$char"

        companion object {
            fun String.toComparator() =
                entries.first { it.char == single() }

        }
    }

    companion object : Day.Main("Day19.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}