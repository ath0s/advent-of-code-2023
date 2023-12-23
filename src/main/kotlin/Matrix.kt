import AnsiColor.RESET
import AnsiColor.WHITE_BOLD_BRIGHT
import java.nio.file.Path
import kotlin.io.path.readLines

typealias Matrix<T> = Array<Array<T>>

inline fun <reified T> Matrix(xSize: Int, ySize: Int, init: (Coordinate) -> T): Matrix<T> =
    Array(ySize) { y -> Array(xSize) { x -> init(Coordinate(x, y)) } }

val <T> Matrix<T>.length get() =
    size * get(0).size

val <T> Matrix<T>.x get() =
    this[0].indices

val <T> Matrix<T>.y get() =
    indices

val <T> Matrix<T>.height
    get() = size

val <T> Matrix<T>.width
    get() = first().size
/**
 * Interpret [this] as a filename and parse its contents to a matrix
 */
internal inline fun <reified T> Path.parseMatrix(charTransformer: (Char) -> T): Matrix<T> =
        readLines()
        .map { it.map(charTransformer).toTypedArray() }
        .toTypedArray()

internal inline fun <reified T> String.parseMatrix(charTransformer: (Char) -> T): Matrix<T> =
    lines()
        .map { it.map(charTransformer).toTypedArray() }
        .toTypedArray()

fun String.parseMatrix() =
    parseMatrix { it }

fun <T> Matrix<T>.getOrthogonalNeighbors(coordinate: Coordinate) =
    listOf(
        Coordinate(coordinate.x, coordinate.y - 1),
        Coordinate(coordinate.x - 1, coordinate.y),
        Coordinate(coordinate.x + 1, coordinate.y),
        Coordinate(coordinate.x, coordinate.y + 1)
    ).filter { it in this }

fun <T> Matrix<T>.getAllNeighbors(coordinate: Coordinate) =
    (coordinate.y - 1..coordinate.y + 1).flatMap { y ->
        (coordinate.x - 1..coordinate.x + 1).map { x ->
            Coordinate(x, y)
        }
    }.filter { it != coordinate }.filter { it in this }

fun <T> Matrix<T>.getAllNeighbors(partialRow: PartialRow) =
    (partialRow.y - 1..partialRow.y + 1).flatMap { y ->
        (partialRow.xRange.first - 1..partialRow.xRange.last + 1).map { x ->
            Coordinate(x, y)
        }
    }.filter { it !in partialRow }.filter { it in this }

fun <T> Matrix<T>.getAllAbove(coordinate: Coordinate) =
    (this.y.first until coordinate.y).reversed().map { y -> Coordinate(coordinate.x, y) }

fun <T> Matrix<T>.getAllBelow(coordinate: Coordinate) =
    (coordinate.y + 1..this.y.last).map { y -> Coordinate(coordinate.x, y) }

fun <T> Matrix<T>.getAllLeft(coordinate: Coordinate) =
    (this.x.first until coordinate.x).reversed().map { x -> Coordinate(x, coordinate.y) }

fun <T> Matrix<T>.getAllRight(coordinate: Coordinate) =
    (coordinate.x + 1..this.x.last).map { x -> Coordinate(x, coordinate.y) }

fun <T> Matrix<T>.print(toChar: (T) -> Char, highlight: (Coordinate) -> Boolean) =
    forEachIndexed { y: Int, row: Array<T> ->
        row.forEachIndexed { x, value ->
            if (highlight(Coordinate(x, y))) {
                print("$WHITE_BOLD_BRIGHT${toChar(value)}$RESET")
            } else {
                print(toChar(value))
            }
        }
        println()
    }

fun <T> Matrix<T>.print(highlight: (Coordinate) -> Boolean) =
    forEachIndexed { y: Int, row: Array<T> ->
        row.forEachIndexed { x, value ->
            if (highlight(Coordinate(x, y))) {
                print("$WHITE_BOLD_BRIGHT$value$RESET")
            } else {
                print("$value")
            }
        }
        println()
    }

fun <T, R> Matrix<T>.mapIndexedNotNull(transform: (Coordinate, T) -> R?) =
    flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, value -> transform(Coordinate(x, y), value) }
    }


fun <T> Matrix<T>.updateEach(transform: (T) -> T) =
    forEachIndexed { coordinate, value ->
        set(coordinate, transform(value))
    }

fun <T> Matrix<T>.forEachIndexed(action: (Coordinate, T) -> Unit): Unit =
    forEachIndexed { y: Int, row: Array<T> ->
        row.forEachIndexed { x, value ->
            action(Coordinate(x, y), value)
        }
    }

fun <T> Matrix<T>.filterIndexed(predicate: (Coordinate, T) -> Boolean) =
    flatMapIndexed { y, row ->
        row.mapIndexedNotNull{ x, value ->
            val coordinate = Coordinate(x, y)
            if(predicate(coordinate, value)) {
                coordinate
            } else {
                null
            }
        }
    }

fun <T> Matrix<T>.first(predicate: (T) -> Boolean): Coordinate =
    mapIndexed { y, row ->
        val x = row.indexOfFirst(predicate)
        Coordinate(x, y)
    }.first()

fun <T> Matrix<T>.last(predicate: (T) -> Boolean): Coordinate =
    mapIndexed { y, row ->
        val x = row.indexOfLast(predicate)
        Coordinate(x, y)
    }.last()

fun <T> Matrix<T>.update(coordinate: Coordinate, transform: (T) -> T) {
    val previous = get(coordinate)
    set(coordinate, transform(previous))
}

fun <T> Matrix<T>.lastIndex() =
    Coordinate(x.last, y.last)

operator fun <T> Matrix<T>.contains(coordinate: Coordinate) =
    coordinate.y in (0..lastIndex) && coordinate.x in (0..get(coordinate.y).lastIndex)

operator fun <T> Matrix<T>.get(coordinate: Coordinate) =
    this[coordinate.y][coordinate.x]

operator fun <T> Matrix<T>.set(coordinate: Coordinate, value: T) {
    this[coordinate.y][coordinate.x] = value
}
fun <T> Matrix<T>.switch(from: Coordinate, to: Coordinate) {
    val old = this[to]
    this[to] = this[from]
    this[from] = old
}

fun <T> Matrix<T>.find(predicate: (T) -> Boolean): Coordinate? {
    for ((y, row) in withIndex()) {
        row.forEachIndexed { x, value ->
            if (predicate(value)) {
                return Coordinate(x, y)
            }
        }
    }
    return null
}

data class PartialRow(val y: Int, val xRange: IntRange) {
    operator fun contains(coordinate: Coordinate) =
        coordinate.y == y && coordinate.x in xRange
}

operator fun Matrix<Char>.get(partialRow: PartialRow) =
    get(partialRow.y).toCharArray().concatToString().substring(partialRow.xRange)

inline fun <reified T> Matrix<T>.rotate(): Matrix<T> =
    first().indices.reversed().map { x ->
        indices.map { y -> this[y][x] }.toTypedArray()
    }.toTypedArray()

fun <T> Matrix<T>.asString() =
    joinToString("\n") { row -> row.joinToString("") { it.toString() } }

inline fun <reified T> Matrix<T>.copy() =
    Array(size) {
        this[it].copyOf()
    }