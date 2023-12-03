import kotlin.io.path.readLines

private val GAME_PATTERN = Regex("""Game (\d+): """)
private val COLOR_PATTERN = Regex("""(\d+) (red|green|blue)""")

class Day02 : Day {

    override fun partOne(filename: String, verbose: Boolean): Any {
        val games = filename.asPath().readLines()
            .map { line -> line.parseGame() }
            .filter { game ->
                game.grabs.none { grab ->
                    grab.red > 12 || grab.green > 13 || grab.blue > 14
                }
            }

        return games.sumOf { it.id }
    }

    override fun partTwo(filename: String, verbose: Boolean): Any {
        val games = filename.asPath().readLines()
            .map { line -> line.parseGame() }
        val powerOfMin = games.map { game ->
            val minRed = game.grabs.maxOf { it.red }
            val minGreen = game.grabs.maxOf { it.green }
            val minBlue = game.grabs.maxOf { it.blue }
            minRed * minGreen * minBlue
        }
        return powerOfMin.sum()
    }

    private fun String.parseGame() : Game  {
        val gamePart = GAME_PATTERN.find(this)!!
        val gameNumber = gamePart.groups[1]!!.value.toInt()
        val grabs = substring(gamePart.range.last + 1)
            .split("; ")
            .map { grab ->
                val colors = grab.split(", ").associate {
                    val (amount, color) = COLOR_PATTERN.find(it)!!.destructured
                    color to amount.toInt()
                }
                Grab(
                    colors["red"] ?: 0,
                    colors["green"] ?: 0,
                    colors["blue"] ?: 0
                )
            }
        return Game(gameNumber, grabs)
    }

    private data class Game(val id: Int, val grabs: List<Grab>)

    private data class Grab(
        val red: Int = 0,
        val green: Int = 0,
        val blue: Int = 0
    )

    companion object : Day.Main("Day02.txt") {
        @JvmStatic
        fun main(args: Array<String>) =
            main()
    }
}