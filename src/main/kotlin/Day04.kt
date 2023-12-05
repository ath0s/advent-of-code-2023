import kotlin.io.path.readLines
import kotlin.math.pow

private val CARD_NUMBER_PATTERN = Regex("""Card\s+(\d+):""")
private val NUMBER_PATTERN = Regex("""\d+""")

class Day04: Day {
    override fun partOne(filename: String, verbose: Boolean): Int =
        parse(filename)
            .sumOf { (_, numberOfMatches) ->
                2.0.pow(numberOfMatches - 1).toInt()
            }

    override fun partTwo(filename: String, verbose: Boolean): Any {
        val cards = parse(filename)
        cards.forEachIndexed { index, card ->
            (1 .. minOf(card.numberOfMatches, cards.size -2 )).forEach {
                cards[index + it].copies += card.copies
            }
        }
        return cards.sumOf { it.copies }
    }

    private fun parse(filename: String) =
        filename.asPath()
            .readLines()
            .map { line ->
                val cardNumberMatch = CARD_NUMBER_PATTERN.find(line)!!
                val (numbersPart, winningNumbersPart) = line.substring(cardNumberMatch.range.last + 1).split("|")
                val numbers = NUMBER_PATTERN.findAll(numbersPart).map { it.value.toInt() }.toList()
                val winningNumbers =
                    NUMBER_PATTERN.findAll(winningNumbersPart).map { it.value.toInt() }.toSet()
                val numberOfMatches = numbers.count { it in winningNumbers }
                Card(cardNumberMatch.groups[1]!!.value.toInt(), numberOfMatches)
            }
    private data class Card(
        val cardNumber: Int,
        val numberOfMatches: Int,
        var copies: Int = 1
    )

    companion object : Day.Main("Day04.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}