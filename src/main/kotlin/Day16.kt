import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP

class Day16 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val matrix = filename.asPath().parseMatrix { it }
        return matrix.countEnergized(Coordinate(0, 0), RIGHT, verbose)
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val matrix = filename.asPath().parseMatrix { it }
        val fromLeft = matrix.y.maxOf { y ->
            matrix.countEnergized(Coordinate(0, y), RIGHT, verbose)
        }
        val fromTop = matrix.x.maxOf { x ->
            matrix.countEnergized(Coordinate(x, 0), DOWN, verbose)
        }
        val fromRight = matrix.y.maxOf { y ->
            matrix.countEnergized(Coordinate(matrix.x.last, y), LEFT, verbose)
        }
        val fromBottom = matrix.x.maxOf { x ->
            matrix.countEnergized(Coordinate(x, matrix.y.last), UP, verbose)
        }
        return maxOf(fromLeft, fromTop, fromRight, fromBottom)
    }

    private val tiles = mapOf<Char, (Direction) -> List<Direction>>(
        '.' to { listOf(it) },
        '/' to {
            when (it) {
                UP -> listOf(RIGHT)
                DOWN -> listOf(LEFT)
                LEFT -> listOf(DOWN)
                RIGHT -> listOf(UP)
            }
        },
        '\\' to {
            when (it) {
                UP -> listOf(LEFT)
                DOWN -> listOf(RIGHT)
                LEFT -> listOf(UP)
                RIGHT -> listOf(DOWN)
            }
        },
        '|' to {
            when (it) {
                LEFT, RIGHT -> listOf(UP, DOWN)
                else -> listOf(it)
            }
        },
        '-' to {
            when (it) {
                UP, DOWN -> listOf(LEFT, RIGHT)
                else -> listOf(it)
            }
        }
    )

    private fun Matrix<Char>.countEnergized(start: Coordinate, direction: Direction, verbose: Boolean): Int {
        val visited = mutableSetOf<Pair<Coordinate, Direction>>()
        move(start, direction, visited, verbose)
        return visited.mapToSet { (coordinate) -> coordinate }.count()
    }

    private fun Matrix<Char>.move(
        coordinate: Coordinate,
        direction: Direction,
        visited: MutableSet<Pair<Coordinate, Direction>>,
        verbose: Boolean
    ) {

        if (coordinate !in this) {
            return
        }

        if (!visited.add(coordinate to direction)) {
            return
        }

        if (verbose) {
            print {
                it in visited.mapToSet { (coordinate) -> coordinate }
            }
            println()
        }

        tiles[this[coordinate]]!!(direction).forEach {
            move(it.move(coordinate), it, visited, verbose)
        }
    }


    companion object : Day.Main("Day16.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}