import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph
import kotlin.io.path.readLines

class Day25: Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val components = filename.asPath().readLines()
            .map { line ->
                val (name, connections) = line.split(Regex("""\s*:\s*"""))
                Component(name, connections.split(' '))
            }

        val graph = SimpleWeightedGraph.createBuilder<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)
            .apply {
                components.forEach { component ->
                    component.connections.forEach { connection ->
                        addEdge(component.name, connection)
                    }
                }
            }.run {
                buildAsUnmodifiable()
            }

        val side = StoerWagnerMinimumCut(graph).minCut()

        return (graph.vertexSet().size - side.size) * side.size
    }

    private data class Component(val name: String, val connections: List<String>)

    override fun partTwo(filename: String, verbose: Boolean): Any {
        TODO("Not yet implemented")
    }

    companion object : Day.Main("Day25.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}