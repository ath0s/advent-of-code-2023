import Direction.DOWN
import Direction.LEFT
import Direction.RIGHT
import Direction.UP
import kotlin.math.abs

data class LongCoordinate(
    val x: Long,
    val y: Long
) {
    override fun toString() = "$x,$y"

    infix fun manhattanDistance(other: LongCoordinate): Long =
        abs(x - other.x) + abs(y - other.y)

    fun move(direction: Direction, distance: Long = 1L) =
        when (direction) {
            UP -> LongCoordinate(x, y - distance)
            DOWN -> LongCoordinate(x, y + distance)
            LEFT -> LongCoordinate(x - distance, y)
            RIGHT -> LongCoordinate(x + distance, y)
        }
}

