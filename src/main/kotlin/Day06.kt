import kotlin.io.path.readLines

private val NUMBER_PATTERN = Regex("""\d+""")

class Day06: Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val lines = filename.asPath().readLines()
        val times = lines.first().extractNumbers()
        val distances = lines.drop(1).first().extractNumbers()
        val races = times.zip(distances) { time, distance ->
            Race(time, distance)
        }
        val possibleTimes = races.map { it.determinePossibleTimes() }

        return possibleTimes.map { it.count() }.reduce(Int::times)
    }

    override fun partTwo(filename: String, verbose: Boolean): Any {
        val lines = filename.asPath().readLines()
        val time = lines.first().extractNumbers().joinToString("").toLong()
        val distance = lines.drop(1).first().extractNumbers().joinToString("").toLong()
        val race = Race(time, distance)
        val possibleTimes = race.determinePossibleTimes()

        return possibleTimes.count()
    }

    private data class Race(
        val time: Long,
        val distance: Long
    )

    private fun Race.determinePossibleTimes(): List<Long> =
        (time - 1 downTo 1)
            .asSequence()
            .map { holdTime ->
                val raceTime = time - holdTime
                raceTime * holdTime
            }
            .filter { it > distance }
            .toList()

    private fun String.extractNumbers() =
        NUMBER_PATTERN.findAll(this).map { it.value.toLong() }.toList()

    companion object : Day.Main("Day06.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}