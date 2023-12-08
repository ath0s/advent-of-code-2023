import Day08.Direction.LEFT
import Day08.Direction.RIGHT
import kotlin.io.path.readLines

private val NODE_PATTERN = Regex("""(\w+)\s+=\s+\((\w+),\s+(\w+)\)""")

class Day08 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val game = parse(filename)
        val startNode = game.nodes["AAA"]!!
        return game.countStepsToEnd(startNode, verbose) { it.name == "ZZZ" }
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val game = parse(filename)
        val startNodes = game.nodes.values.filter { it.name.last() == 'A' }.toTypedArray()
        return startNodes
            .map { startNode ->
                game.countStepsToEnd(startNode, verbose) {
                    it.name.last() == 'Z'
                }.toLong()
            }
            .reduce(::lcm)
    }

    private fun parse(filename: String): Game {
        val lines = filename.asPath().readLines()
        val instructions = lines.first().map {
            when (it) {
                'L' -> LEFT
                'R' -> RIGHT
                else -> throw IllegalArgumentException("Unknown direction $it")
            }
        }

        val nodes = lines.drop(2)
            .associate {
                val (node, left, right) = NODE_PATTERN.find(it)!!.destructured
                node to Node(node, left, right)
            }
        return Game(instructions, nodes)
    }

    private enum class Direction {
        LEFT, RIGHT
    }

    private data class Node(
        val name: String,
        val left: String,
        val right: String
    )

    private data class Game(
        val instructions: List<Direction>,
        val nodes: Map<String, Node>
    )

    private fun Game.countStepsToEnd(startNode: Node, verbose: Boolean = false, end: (Node) -> Boolean): Int {
        var currentNode = startNode
        return generateSequence(0) { index -> (index + 1).takeIf { it < instructions.size } ?: 0 }
            .onEach { index ->
                if (verbose) {
                    print("Moving from ${currentNode.name} to ")
                }
                currentNode = when (instructions[index]) {
                    LEFT -> nodes[currentNode.left]!!
                    RIGHT -> nodes[currentNode.right]!!
                }
                if (verbose) {
                    println(currentNode.name)
                }
            }
            .takeWhile { !end(currentNode) }
            .count() + 1
    }

    companion object : Day.Main("Day08.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}