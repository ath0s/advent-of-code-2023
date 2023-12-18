package shoelace

import LongCoordinate
import kotlin.math.absoluteValue

fun Iterable<LongCoordinate>.shoelaceArea(includingPerimeter: Boolean = true): Long {
    val iter = iterator()
    if(!iter.hasNext()) return 0
    val start = iter.next()
    var perimeter = 0L
    var points = 1L
    var sum = 0L
    var last = start

    fun continueLace(from: LongCoordinate, until: LongCoordinate) {
        check(from.x == until.x || from.y == until.y) { "Polygon corners are not 90 degrees!" }
        perimeter += from manhattanDistance until
        sum += from.x * until.y
        sum -= from.y * until.x
    }

    while(iter.hasNext()) {
        val next = iter.next()
        points += 1
        continueLace(last, next)
        last = next
    }

    continueLace(last, start)

    val insideArea = sum.absoluteValue / 2

    return when {
        points < 3 -> 0
        includingPerimeter -> insideArea - perimeter / 2 + 1 + perimeter
        else -> insideArea
    }
}