import org.intellij.lang.annotations.Language

class Day10 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val matrix = parseMaze(filename)
        val loop = matrix.findLoop()

        if (verbose) {
            matrix.print(Pipe::symbol) { it in loop }
        }

        return loop.size / 2
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val matrix = parseMaze(filename)
        val loop = matrix.findLoop()

        val inside = mutableSetOf<Coordinate>()
        var isInside = false
        matrix.forEachIndexed { coordinate: Coordinate, pipe: Pipe ->
            if (coordinate.x == 0) {
                isInside = false
            }
            val isPartOfLoop = coordinate in loop
            if (isPartOfLoop && pipe in verticalPipes) {
                isInside = !isInside
            }
            if (!isPartOfLoop && isInside) {
                inside += coordinate
            }
        }

        if (verbose) {
            matrix.print(Pipe::symbol) { it in inside }
        }

        return inside.size
    }

    private enum class Pipe(val symbol: Char, vararg val moves: (Coordinate) -> Coordinate) {
        NORTH_SOUTH('|', Coordinate::up, Coordinate::down),
        EAST_WEST('-', Coordinate::right, Coordinate::left),
        NORTH_EAST('L', Coordinate::up, Coordinate::right),
        NORTH_WEST('J', Coordinate::up, Coordinate::left),
        SOUTH_WEST('7', Coordinate::down, Coordinate::left),
        SOUTH_EAST('F', Coordinate::down, Coordinate::right),
        GROUND('.'),
        START('S', Coordinate::up, Coordinate::down, Coordinate::right, Coordinate::left)
    }

    private val verticalPipes = setOf(Pipe.NORTH_SOUTH, Pipe.NORTH_EAST, Pipe.NORTH_WEST)

    private fun parseMaze(@Language("file-reference") filename: String) =
        filename.asPath().parseMatrix { char ->
            enumValues<Pipe>().first { it.symbol == char }
        }

    private fun Matrix<Pipe>.findLoop(): Set<Coordinate> {
        val start = find { it == Pipe.START } ?: throw IllegalStateException("Start not found!")
        var last = start
        var current = Pipe.START.moves
            .map { it(start) }
            .filter { it in this }
            .first { coordinate -> this[coordinate].moves.any { it(coordinate) == start } }

        val pipes = mutableSetOf(start)
        while (current != start) {
            pipes += current
            val next = this[current].moves.map { it(current) }.first { it != last }
            last = current
            current = next
        }

        this[start] = (enumValues<Pipe>().toSet() - Pipe.START).first {
            it.moves.all { it(start) in pipes }
        }
        return pipes
    }

    companion object : Day.Main("Day10.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}