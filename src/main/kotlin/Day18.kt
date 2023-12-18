import shoelace.shoelaceArea
import kotlin.io.path.readLines

private val DIG_PLAN_PATTERN = Regex("""(\w) (\d+) \((#\w+)\)""")

class Day18: Day {
    override fun partOne(filename: String, verbose: Boolean): Long {
        val digInstructions = filename.asPath().readLines()
            .map { it.toDigInstruction() }

        val corners = digInstructions.digTrench()

        if (verbose) {
            val minY = corners.minOf { it.y }
            val maxY = corners.maxOf { it.y }
            val minX = corners.minOf { it.x }
            val maxX = corners.maxOf { it.x }
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    val coordinate = LongCoordinate(x, y)
                    if (coordinate in corners) {
                        print("#")
                    } else {
                        print(".")
                    }
                }
                println()
            }
        }

        return corners.shoelaceArea()
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val digInstructions = filename.asPath().readLines()
            .map { it.toDigInstructionHex() }

        val corners = digInstructions.digTrench()

        return corners.shoelaceArea()
    }

    private fun String.toDigInstruction() =
        DIG_PLAN_PATTERN.matchEntire(this)!!.destructured.let { (direction, meters, _) ->
            DigInstruction(direction[0].toDirection(), meters.toLong())
        }

    private fun String.toDigInstructionHex() =
        DIG_PLAN_PATTERN.matchEntire(this)!!.destructured.let { (_, _, hex) ->
            val distance = hex.drop(1).take(5).toLong(16)
            val direction = when(val it = hex.last()) {
                '0' -> Direction.RIGHT
                '1' -> Direction.DOWN
                '2' -> Direction.LEFT
                '3' -> Direction.UP
                else -> throw IllegalArgumentException("Unknown direction $it")
            }
            DigInstruction(direction, distance,)
        }

    private fun Char.toDirection() =
        when(this) {
            'U' -> Direction.UP
            'D' ->Direction.DOWN
            'L' ->Direction.LEFT
            'R' ->Direction.RIGHT
            else -> throw IllegalArgumentException("Unknown direction $this")
        }

    private fun List<DigInstruction>.digTrench(): List<LongCoordinate> {
        var current = LongCoordinate(0,0)
        return map {instruction ->
            current = current.move(instruction.direction, instruction.meters)
            current
        }
    }

    private data class DigInstruction(val direction: Direction, val meters: Long) {
        override fun toString() = "${direction.name[0]} $meters"
    }

    companion object : Day.Main("Day18.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}