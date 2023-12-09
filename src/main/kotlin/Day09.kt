import kotlin.io.path.readLines

private val NUMBER_PATTERN = Regex("""-?\d+""")

class Day09: Day {
    override fun partOne(filename: String, verbose: Boolean): Long {
        val histories = parseHistories(filename)

        val resolved = histories.map {
            it.resolveNext().apply {
                if (verbose) {
                    println("${it.joinToString(", ")} -> $this")
                }
            }
        }

        return resolved.sum()
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val histories = parseHistories(filename)

        val resolved = histories.map {
            it.resolvePrevious().apply {
                if (verbose) {
                    println("${it.joinToString(", ")} -> $this")
                }
            }
        }

        return resolved.sum()
    }

    private fun parseHistories(filename: String) =
        filename.asPath().readLines()
            .map { line -> NUMBER_PATTERN.findAll(line).map { it.value.toLong() }.toList() }

    private fun List<Long>.resolveNext(): Long =
        if (all { it == 0L }) {
            0
        } else {
            last() + windowed(2) { (first, second) ->
                second - first
            }.resolveNext()
        }

    private fun List<Long>.resolvePrevious(): Long =
        if (all { it == 0L }) {
            0
        } else {
            first() - windowed(2) { (first, second) ->
                second - first
            }.resolvePrevious()
        }

    companion object : Day.Main("Day09.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main(true)
    }
}