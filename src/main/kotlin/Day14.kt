import Day14.TiltDirection.NORTH

class Day14 : Day {

    override fun partOne(filename: String, verbose: Boolean): Int {
        var matrix = filename.asPath().parseMatrix { it }

        matrix = matrix.tilt(NORTH)

        if (verbose) {
            matrix.printWithWeight()
        }
        return matrix.calculateLoad()
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        var matrix = filename.asPath().parseMatrix { it }

        val visitedStates = mutableSetOf<String>()
        var countUntilRepeat = 0
        while (matrix.asString() !in visitedStates) {
            countUntilRepeat++
            visitedStates += matrix.asString()
            matrix = matrix.cycle()
        }

        var countInCycle = 0
        val cycleStates = mutableSetOf<String>()
        while (matrix.asString() !in cycleStates) {
            countInCycle++
            cycleStates += matrix.asString()
            matrix = matrix.cycle()
        }

        val remainingCycles = (1_000_000_000 - countUntilRepeat) % countInCycle

        repeat(remainingCycles) {
            matrix = matrix.cycle()
        }

        if (verbose) {
            matrix.printWithWeight()
        }

        return matrix.calculateLoad()
    }

    private fun Matrix<Char>.tilt(direction: TiltDirection): Matrix<Char> {
        val newMatrix = this.copy()
        for (y in newMatrix.y.let(direction.yIndices)) {
            for (x in newMatrix[y].indices.let(direction.xIndices)) {
                var coordinate = Coordinate(x, y)
                val rock = newMatrix[coordinate]
                if (rock == 'O') {
                    var newCoordinate = coordinate.let(direction.move)
                    while (newCoordinate in newMatrix && newMatrix[newCoordinate] == '.') {
                        newMatrix.switch(coordinate, newCoordinate)
                        coordinate = newCoordinate
                        newCoordinate = newCoordinate.let(direction.move)
                    }
                }
            }
        }
        return newMatrix
    }

    private fun Matrix<Char>.cycle() =
        enumValues<TiltDirection>().fold(this) { previous, dir -> previous.tilt(dir) }

    private fun Matrix<Char>.printWithWeight() =
        forEachIndexed { y: Int, row ->
            print(row.joinToString(""))
            println(" ${size - y}")
        }

    private fun Matrix<Char>.calculateLoad() = mapIndexedNotNull { coordinate, rock ->
        rock.takeIf { it == 'O' }?.let { size - coordinate.y }
    }.sum()

    private enum class TiltDirection(
        val yIndices: (IntRange) -> IntProgression = { it },
        val xIndices: (IntRange) -> IntProgression = { it },
        val move: (Coordinate) -> Coordinate
    ) {
        NORTH(move = Coordinate::up),
        WEST(move = Coordinate::left),
        SOUTH(yIndices = IntRange::reversed, move = Coordinate::down),
        EAST(xIndices = IntRange::reversed, move = Coordinate::right)
    }

    companion object : Day.Main("Day14.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}