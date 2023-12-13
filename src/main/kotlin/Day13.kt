import kotlin.io.path.readText
import kotlin.math.min

class Day13 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int =
        countReflections(filename, 0, verbose)

    override fun partTwo(filename: String, verbose: Boolean): Int =
        countReflections(filename, 1, verbose)

    private fun countReflections(filename: String, smudges: Int, verbose: Boolean): Int {
        val patterns = filename.asPath().readText().split("\n\n")

        var numberOfVertical = 0
        val verticalValue = patterns
            .mapNotNull { pattern ->
                pattern.findReflection(smudges)?.also {
                    if (verbose) {
                        for (i in pattern.lines().first().indices) {
                            print("${i + 1}")
                        }
                        println()
                        println(" ".repeat(it - 1) + "><")
                        println(pattern)
                        println()
                    }
                }
            }
            .onEach { numberOfVertical++ }
            .sum()

        var numberOfHorizontal = 0
        val horizontalValue = patterns
            .mapNotNull { pattern ->
                pattern.parseMatrix().rotate().asString().findReflection(smudges)?.also {
                    if (verbose) {
                        pattern.lines().forEachIndexed { y, line ->
                            val number = y + 1
                            print("$number".padStart(2, ' '))
                            when (number) {
                                it -> println("v$line")
                                it + 1 -> println("^$line")
                                else -> println(" $line")
                            }
                        }
                        println()
                    }
                }
            }
            .onEach { numberOfHorizontal++ }
            .sumOf { it * 100 }

        if (numberOfVertical + numberOfHorizontal != patterns.size) {
            throw IllegalStateException("number of vertical $numberOfVertical, number of horizontal $numberOfHorizontal, expected ${patterns.size}")
        }

        return verticalValue + horizontalValue
    }

    private fun String.findReflection(smudges: Int): Int? {
        val lines = lines()
        for (x in 1..lines.first().lastIndex) {
            if (lines.sumOf { line ->
                    val length = min(line.length - x, x)
                    val left = line.substring(x - length..<x)
                    val right = line.substring(x until x + length).reversed()
                    left levenshteinDistance right
                } == smudges) {
                return x
            }
        }
        return null
    }

    private infix fun String.levenshteinDistance(other: String) =
        zip(other) { a, b -> a != b }.count { it }

    companion object : Day.Main("Day13.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}