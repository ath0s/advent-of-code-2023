import Day20.Pulse.HIGH
import Day20.Pulse.LOW
import kotlin.io.path.readLines

class Day20 : Day {
    override fun partOne(filename: String, verbose: Boolean): Long {
        val modules = filename.asPath().readLines().map { it.toModule() }.associateBy { it.name }
        modules.values
            .filterIsInstance<Conjunction>()
            .forEach { module ->
                module.memory.putAll(
                    modules.values
                        .filter { module.name in it.destinations }
                        .associate { it.name to LOW })
            }
        var lowPulses = 0L
        var highPulses = 0L
        val queue = mutableListOf<Pair<String, Module>>()
        val broadcaster = modules.values.filterIsInstance<Broadcaster>().single()
        repeat(1_000) {
            lowPulses++
            queue += broadcaster.destinations.map { it to broadcaster }
            while (queue.isNotEmpty()) {
                val (name, origin) = queue.removeFirst()
                when (origin.pulse) {
                    HIGH -> highPulses++
                    LOW -> lowPulses++
                }
                if (name !in modules.keys) continue
                val module = modules[name]!!

                if (module.receive(origin)) {
                    queue += module.destinations.map { it to module }
                }
            }
        }
        return lowPulses * highPulses
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val modules = filename.asPath().readLines().map { it.toModule() }.associateBy { it.name }
        modules.values
            .filterIsInstance<Conjunction>()
            .forEach { module ->
                module.memory.putAll(
                    modules.values
                        .filter { module.name in it.destinations }
                        .associate { it.name to LOW })
            }
        var lowPulses = 0L
        val queue = mutableListOf<Pair<String, Module>>()
        val broadcaster = modules.values.filterIsInstance<Broadcaster>().single()
        val lastConjunction = modules.values.first { "rx" in it.destinations }.name
        val connectors = modules.values.filter { lastConjunction in it.destinations }.map { it.name }
        val cycles = connectors.associateWithTo(mutableMapOf()) { 0L }
        while (cycles.any { it.value == 0L }) {
            lowPulses++
            queue.addAll(broadcaster.destinations.map { it to broadcaster })
            while (queue.isNotEmpty()) {
                val (name, origin) = queue.removeFirst()
                if (name == lastConjunction && origin.pulse == HIGH && origin.name in connectors) {
                    cycles[origin.name] = lowPulses
                }
                if (name !in modules.keys) {
                    continue
                }
                val module = modules[name]!!
                if (module.receive(origin)) {
                    queue += module.destinations.map { it to module }
                }
            }
        }
        return cycles.values.lcm()
    }

    private sealed interface Module {
        val name: String
        val destinations: List<String>
        val pulse: Pulse

        fun receive(origin: Module): Boolean
    }

    private data class FlipFlop(
        override val name: String,
        override val destinations: List<String>,
    ) : Module {
        override var pulse: Pulse = LOW

        override fun receive(origin: Module) =
            if (origin.pulse == LOW) {
                pulse = !pulse
                true
            } else {
                false
            }
    }

    private data class Conjunction(
        override val name: String,
        override val destinations: List<String>,
    ) : Module {

        val memory: MutableMap<String, Pulse> = mutableMapOf()

        override val pulse
            get() = if (memory.values.all { it == HIGH }) {
                LOW
            } else {
                HIGH
            }

        override fun receive(origin: Module): Boolean {
            memory[origin.name] = origin.pulse
            return true
        }
    }

    private data class Broadcaster(
        override val name: String,
        override val destinations: List<String>,
    ) : Module {
        override var pulse = LOW
        override fun receive(origin: Module): Boolean {
            pulse = origin.pulse
            return true
        }
    }

    private fun String.toModule(): Module {
        val (name, destinationsPart) = split(" -> ")
        val destinations = destinationsPart.split(", ")
        return when {
            name == "broadcaster" -> Broadcaster(name, destinations)
            name.startsWith('%') -> FlipFlop(name.drop(1), destinations)
            name.startsWith('&') -> Conjunction(name.drop(1), destinations)
            else -> throw IllegalArgumentException("Unknown module type $name")
        }
    }

    private enum class Pulse {
        HIGH, LOW;

        operator fun not() =
            when (this) {
                HIGH -> LOW
                LOW -> HIGH
            }
    }

    companion object : Day.Main("Day20.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}