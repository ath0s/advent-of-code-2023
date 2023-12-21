
import kotlin.io.path.readText


class Day21: Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val map = filename.asPath().readText().parseMatrix { it }
        val start = map.find { it == 'S' }!!
        var coordinates = setOf(start)
        repeat(64) {
            coordinates = coordinates
                .flatMap { listOf( it.up(), it.down(), it.left(), it.right() ) }
                .filterToSet { it in map && map[it] != '#'}
        }
        return coordinates.size
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val map = filename.asPath().readText().parseMatrix { it }
        val start = map.find { it == 'S' }!!

        val width = map.width

        val count = 26501365L
        val cycles = count / width
        val remainder = count % width

        var coordinates = setOf(start)
        val regression = mutableListOf<Coordinate>()
        var steps = 0
        repeat(3) { i ->
            while (steps < i * width + remainder) {
                coordinates = coordinates
                    .flatMap { listOf(it.up(), it.down(), it.left(), it.right()) }
                    .filterToSet { it inInfinite map }
                steps++
            }
            regression += Coordinate(i, coordinates.size)
        }

        val quadraticCurve = { x: Long ->
            val x1 = regression[0].x.toDouble()
            val y1 = regression[0].y.toDouble()
            val x2 = regression[1].x.toDouble()
            val y2 = regression[1].y.toDouble()
            val x3 = regression[2].x.toDouble()
            val y3 = regression[2].y.toDouble()
            (((x - x2) * (x - x3)) / ((x1 - x2) * (x1 - x3)) * y1 +
                    (((x - x1) * (x - x3)) / ((x2 - x1) * (x2 - x3)) * y2) +
                    (((x - x1) * (x - x2)) / ((x3 - x1) * (x3 - x2)) * y3)).toLong()
        }

        return quadraticCurve(cycles)
    }

    private infix fun Coordinate.inInfinite(map: Matrix<Char>): Boolean {
        val x = ((this.x % map.width) + map.width) % map.width
        val y = ((this.y % map.height) + map.height) % map.height
        return map[x][y] != '#'
    }

    companion object : Day.Main("Day21.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}