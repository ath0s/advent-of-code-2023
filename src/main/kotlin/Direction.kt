enum class Direction(val move: (Coordinate) -> Coordinate) {
    UP(Coordinate::up),
    DOWN(Coordinate::down),
    LEFT(Coordinate::left),
    RIGHT(Coordinate::right)
}

fun Direction.left() =
    when (this) {
        Direction.UP -> Direction.LEFT
        Direction.DOWN -> Direction.RIGHT
        Direction.LEFT -> Direction.DOWN
        Direction.RIGHT -> Direction.UP
    }

fun Direction.right() =
    when (this) {
        Direction.UP -> Direction.RIGHT
        Direction.DOWN -> Direction.LEFT
        Direction.LEFT -> Direction.UP
        Direction.RIGHT -> Direction.DOWN
    }