import Day.Main
import kotlin.io.path.readLines

class Day01 : Day {
    override fun partOne(filename: String, verbose: Boolean) =
        filename.asPath()
            .readLines()
            .sumOf { line ->
                val firstDigit = line.toCharArray().first { it.isDigit() }
                val lastDigit = line.toCharArray().last { it.isDigit() }
                "$firstDigit$lastDigit".toInt()
            }

    override fun partTwo(filename: String, verbose: Boolean): Int {

        return filename.asPath()
            .readLines()
            .sumOf { line ->
                val (firstValue, lastValue) = line.extractNumbers()
                val result = "${numbers[firstValue]}${numbers[lastValue]}"
                if (verbose) {
                    println("$line -> $firstValue $lastValue -> $result")
                }
                result.toInt()
            }
    }

    private val numbers = mapOf(
        "one" to 1,
        "1" to 1,
        "two" to 2,
        "2" to 2,
        "three" to 3,
        "3" to 3,
        "four" to 4,
        "4" to 4,
        "five" to 5,
        "5" to 5,
        "six" to 6,
        "6" to 6,
        "seven" to 7,
        "7" to 7,
        "eight" to 8,
        "8" to 8,
        "nine" to 9,
        "9" to 9,
    )
    private val numberRegex = numbers
        .keys.joinToString(prefix = "(", separator = "|", postfix = ")").toRegex()

    internal fun String.extractNumbers(): Pair<String, String> {
        val matchResults = numberRegex.findOverlapping(this)
        val firstResult = matchResults.first()
        val lastResult = matchResults.last()
        val firstValue = firstResult.value
        val lastValue = lastResult.value
        return firstValue to lastValue
    }

    companion object : Main("Day01.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main(true)
    }
}