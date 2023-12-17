import Direction.RIGHT
import astar.findShortestPathByPredicate

class Day17: Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val matrix = filename.asPath().parseMatrix { it.digitToInt() }
        val start = Movement(Coordinate(0,0), RIGHT)
        val destination = matrix.lastIndex()
        val path = findShortestPathByPredicate(
            start,
            { (coordinate) -> coordinate == destination},
            {it.next(max = 3).filter {  (coordinate) -> coordinate in matrix }},
            { _, (coordinate) -> matrix[coordinate]}
        )
        return path.getScore()
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val matrix = filename.asPath().parseMatrix { it.digitToInt() }
        val start = Movement(Coordinate(0, 0), RIGHT)
        val destination = matrix.lastIndex()
        val path = findShortestPathByPredicate(
            start,
            { (coordinate, _, length) -> coordinate == destination && length >= 4 },
            { it.next(min = 4, max = 10).filter { (coordinate) -> coordinate in matrix } },
            { _, (coordinate) -> matrix[coordinate] }
        )
        return path.getScore()
    }

    private data class Movement(val coordinate: Coordinate, val direction: Direction, val length: Int = 0)

    private fun Movement.next(min: Int = 0, max: Int): List<Movement> =
        buildList(3) {
            if (length < max) {
                add(Movement(direction.move(coordinate), direction, length + 1))
            }

            if (length == 0 || length >= min) {
                add(Movement(direction.left().move(coordinate), direction.left(), 1))
                add(Movement(direction.right().move(coordinate), direction.right(), 1))
            }
        }

    companion object : Day.Main("Day17.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}