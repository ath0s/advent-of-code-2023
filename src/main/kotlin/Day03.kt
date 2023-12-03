import kotlin.io.path.readLines

private val NUMBER_PATTERN = Regex("""\d+""")

class Day03 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val matrix = filename.parseMatrix { it }
        return filename.asPath().readLines()
            .flatMapIndexed { y, line ->
                NUMBER_PATTERN.findAll(line)
                    .map { PartialRow(y, it.range) }
                    .filter { partialRow ->
                        matrix.isAdjacentToSymbol(partialRow)
                    }
                    .map { matrix[it].toInt() }
            }
            .sum()
    }

    override fun partTwo(filename: String, verbose: Boolean): Any {
        val matrix = filename.parseMatrix { it }
        val numbers = filename.asPath().readLines()
            .flatMapIndexed { y, line ->
                NUMBER_PATTERN.findAll(line)
                    .map { PartialRow(y, it.range) }
            }
        val gearPartNumbers = numbers.mapNotNullToSet { currentNumber ->
            matrix.getAllNeighbors(currentNumber)
                .firstOrNull { matrix[it] == '*' }
                ?.let { gearCoordinate ->
                    val otherNumbers = numbers - currentNumber
                    val otherNumber = matrix.getAllNeighbors(gearCoordinate)
                        .firstNotNullOfOrNull { neighbor ->
                            otherNumbers.firstOrNull { neighbor in it }
                        }
                    otherNumber?.let { setOf(currentNumber, it) }
                }
        }

        return gearPartNumbers.sumOf { gearParts ->
            gearParts.map { matrix[it].toInt() }.reduce(Int::times)
        }
    }

    private fun Matrix<Char>.isAdjacentToSymbol(partialRow: PartialRow) =
        getAllNeighbors(partialRow).any { get(it) != '.' }

    companion object : Day.Main("Day03.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}