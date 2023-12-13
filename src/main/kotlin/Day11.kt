import java.util.*

class Day11: Day {
    override fun partOne(filename: String, verbose: Boolean): Long =
        shortestPathBetweenGalaxies(filename, 1)

    override fun partTwo(filename: String, verbose: Boolean): Long =
        shortestPathBetweenGalaxies(filename, 999_999)

    private fun shortestPathBetweenGalaxies(filename: String, expansion: Long): Long {
        val matrix = filename.asPath().parseMatrix { it }

        val expandedRows = matrix.expandedRows()
        val expandedColumns = matrix.expandedColumns()

         fun Coordinate.adjust() =
             adjust(expandedRows, expandedColumns, expansion)

        val galaxies = matrix.filterIndexed { _, it ->
            it == '#'
        }.toSet()

        val pairs = galaxies.flatMapToSet { galaxy ->
            (galaxies - galaxy).map { setOf(galaxy, it) }
        }.map { it.first() to it.drop(1).first() }

        return pairs
            .map { (first, second) -> first.adjust() to second.adjust() }
            .sumOf { (first, second) -> manhattanDistance(first, second) }
    }

    private fun Matrix<Char>.expandedRows() : SortedSet<Int> =
        mapIndexedNotNullTo(sortedSetOf()) { index: Int, row: Array<Char> ->
            index.takeIf { row.all { it == '.' } }
        }

    private fun Matrix<Char>.expandedColumns() : SortedSet<Int> =
        x.mapNotNullTo(sortedSetOf()) { xIndex ->
            xIndex.takeIf { all { it[xIndex] == '.' } }
        }

    private fun Coordinate.adjust(expandedRows: SortedSet<Int>, expandedColumns: SortedSet<Int>, expansion: Long) =
        x + (expandedColumns.headSet(x).size * expansion) to y + (expandedRows.headSet(y).size * expansion)

    companion object : Day.Main("Day11.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}