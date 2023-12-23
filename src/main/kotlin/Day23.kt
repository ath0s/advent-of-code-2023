import kotlin.io.path.readText

class Day23: Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val map = filename.asPath().readText().parseMatrix { it }
        val start = map.first { it == '.' }
        val end = map.last { it == '.' }
        return map.depthFirstSearch(start, end)
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val map = filename.asPath().readText().parseMatrix { it }
        val start = map.first { it == '.' }
        val end = map.last { it == '.' }
        val adjacencyMap = map.toAdjacencyMap()
        return adjacencyMap.depthFirstSearch(start, end)
    }

    private fun Matrix<Char>.depthFirstSearch(current: Coordinate, end: Coordinate, steps: Int = 0, visited: Set<Coordinate> = setOf()): Int {
        if (current == end) {
            return steps
        }
        return getNextPoints(current)
            .filter { it !in visited }
            .takeIf { it.isNotEmpty() }
            ?.maxOf {
                depthFirstSearch(it, end, steps + 1, visited + current)
            } ?: 0
    }

    private fun Map<Coordinate, Map<Coordinate, Int>>.depthFirstSearch(
        current: Coordinate,
        end: Coordinate,
        seen: Map<Coordinate, Int> = emptyMap()
    ): Int =
        if (current == end) {
            seen.values.sum()
        } else (this[current] ?: emptyMap()).entries
            .filter { (neighbor) -> neighbor !in seen }
            .takeIf { it.isNotEmpty() }
            ?.maxOf { (neighbor, steps) ->
                depthFirstSearch(neighbor, end, seen + (neighbor to steps))
            } ?: 0

    private fun Matrix<Char>.getNextPoints(point: Coordinate): List<Coordinate> {
        val current = this[point]
        return Direction.entries.mapNotNull { dir ->
            dir.move(point).takeIf { next ->
                next in this && this[next] != '#' && (current == '.' || current.toDirection() == dir)
            }
        }
    }

    private fun Char.toDirection() =
        when(this) {
            '^' -> Direction.UP
            'v' -> Direction.DOWN
            '<' -> Direction.LEFT
            '>' -> Direction.RIGHT
            else -> throw IllegalArgumentException("Unknown direction $this")
        }

    private fun Matrix<Char>.toAdjacencyMap(): Map<Coordinate, Map<Coordinate, Int>> {
        val adjacencyMap = mapIndexedNotNull { coordinate: Coordinate, c: Char ->
            if (c != '#') {
                coordinate to Direction.entries.mapNotNull { direction ->
                    val next = direction.move(coordinate)
                    if (next in this && this[next] != '#') next to 1 else null
                }.toMutableMap()
            } else {
                null
            }
        }.toMutableMap()

        val iterator = adjacencyMap.iterator()
        while (iterator.hasNext()) {
            val (key, neighbors) = iterator.next()
            if (neighbors.size == 2) {
                val left = neighbors.keys.first()
                val right = neighbors.keys.last()
                val totalSteps = neighbors[left]!! + neighbors[right]!!
                adjacencyMap.getOrPut(left) { mutableMapOf() }.merge(right, totalSteps, ::maxOf)
                adjacencyMap.getOrPut(right) { mutableMapOf() }.merge(left, totalSteps, ::maxOf)
                listOf(left, right).forEach { adjacencyMap[it]?.remove(key) }
                iterator.remove()
            }
        }

        return adjacencyMap
    }

    companion object : Day.Main("Day23.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}