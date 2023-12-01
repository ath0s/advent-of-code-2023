import org.intellij.lang.annotations.Language
import kotlin.time.measureTimedValue

interface Day {

    fun partOne(@Language("file-reference") filename: String, verbose: Boolean = false): Any

    fun partTwo(@Language("file-reference") filename: String, verbose: Boolean = false): Any

    abstract class Main(@Language("file-reference") private val filename: String) {

        fun main(verbose: Boolean = false) {
            val day = Day()

            println(day::class.simpleName)

            measureTimedValue { day.partOne(filename, verbose) }.run {
                println("Part One: $value\t($duration)")
            }

            measureTimedValue { day.partTwo(filename, verbose) }.run {
                println("Part Two: $value\t($duration)")
            }

        }

        private fun Day() =
            javaClass.declaringClass.kotlin.newInstance<Day>()
    }
}

