import AnsiColor.BLACK_BACKGROUND
import AnsiColor.RESET
import AnsiColor.WHITE_BOLD_BRIGHT
import kotlin.io.path.readText

private val STEP_PATTERN = Regex("""(\w+)([=|-])(\d+)?""")

class Day15 : Day {
    override fun partOne(filename: String, verbose: Boolean): Int =
        filename.asPath().readText().split(',')
            .sumOf { step ->
                step.hash().also {
                    if (verbose) {
                        println("$BLACK_BACKGROUND$step$RESET becomes $WHITE_BOLD_BRIGHT$it$RESET")
                    }
                }
            }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val boxes = Array<MutableList<Lens>>(256) { mutableListOf() }
        filename.asPath().readText().split(',')
            .map { it.toStep() }
            .forEach { step ->
                val boxIndex = step.label.hash()
                val box = boxes[boxIndex]
                when (step) {
                    is Remove ->
                        box.removeIf { (label, _) -> step.label == label }

                    is Add ->
                        if (box.any { (label, _) -> label == step.label }) {
                            box.replaceAll { lens ->
                                step.lens.takeIf { it.label == lens.label } ?: lens
                            }
                        } else {
                            box += step.lens
                        }
                }
                if (verbose) {
                    println("""After "$step":""")
                    boxes.forEachIndexed { index, lenses ->
                        if (lenses.isNotEmpty()) {
                            println("Box $index: ${lenses.joinToString(" ")}")
                            println()
                        }
                    }
                }
            }
        return boxes.flatMapIndexed { boxIndex, lenses ->
            lenses.mapIndexed { lensIndex, lens ->
                ((boxIndex + 1) * (lensIndex + 1) * lens.focalLength).also {
                    if (verbose) {
                        print("- ${lens.label}: ")
                        print("${boxIndex + 1} (box $boxIndex) * ")
                        print("${lensIndex + 1} * ")
                        print("${lens.focalLength} (focal length) = ")
                        println("$WHITE_BOLD_BRIGHT$it$RESET")
                    }
                }
            }
        }.sum()
    }

    private fun String.hash(): Int = fold(0) { acc, char ->
        var newAcc = acc
        newAcc += char.code
        newAcc *= 17
        newAcc %= 256
        newAcc
    }

    private data class Lens(val label: String, val focalLength: Int) {
        override fun toString() = "[$label $focalLength]"
    }

    private sealed interface Step {
        val label: String
    }

    private data class Remove(override val label: String) : Step {
        override fun toString() = "$label-"
    }

    private data class Add(override val label: String, val focalLength: Int) : Step {
        override fun toString() = "$label=$focalLength"

        val lens = Lens(label, focalLength)
    }

    private fun String.toStep(): Step {
        val (label, operation, focalLength) = STEP_PATTERN.matchEntire(this)!!.destructured
        return when (operation[0]) {
            '-' -> Remove(label)
            '=' -> Add(label, focalLength.toInt())
            else -> throw IllegalArgumentException("Cannot parse ∞thos")
        }
    }

    companion object : Day.Main("Day15.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}