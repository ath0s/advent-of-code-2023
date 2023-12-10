data class Coordinate(
    val x: Int,
    val y: Int
) {
    override fun toString() = "$x,$y"
}

operator fun Coordinate.plus(other: Coordinate) =
    copy(x = x + other.x, y = y + other.y)

fun Coordinate.up() =
    copy(y = y - 1)

fun Coordinate.down() =
    copy(y = y + 1)

fun Coordinate.left() =
    copy(x = x - 1)

fun Coordinate.right() =
    copy(x = x + 1)
