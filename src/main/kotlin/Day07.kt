import Day07.HandType.*
import kotlin.io.path.readLines

private val cardOrder = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
private val cardOrderJokerWildcard = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')
private const val JOKER = 'J'

class Day07 : Day {
    override fun partOne(filename: String, verbose: Boolean): Any {
        val hands = filename.asPath().readLines()
            .map { line ->
                val (cards, bet) = line.split(' ')
                Hand(cards.toCharArray(), bet.toInt())
            }

        return hands
            .sortedDescending()
            .mapIndexed { index, hand ->
                hand.bet * (index + 1)
            }.sum()
    }

    override fun partTwo(filename: String, verbose: Boolean): Any {
        val hands = filename.asPath().readLines()
            .map { line ->
                val (cards, bet) = line.split(' ')
                Hand(cards.toCharArray(), bet.toInt(), true)
            }

        return hands
            .sortedDescending()
            .mapIndexed { index, hand ->
                (hand.bet * (index + 1)).also {
                    if (verbose) {
                        println("$hand ${hand.type} rank ${index + 1} -> $it")
                    }
                }
            }.sum()
    }

    private class Hand(
        val cards: CharArray,
        val bet: Int,
        private val jokerWildcard: Boolean = false
    ) : Comparable<Hand> {
        val type: HandType

        private val orderOfCards = cardOrderJokerWildcard.takeIf { jokerWildcard } ?: cardOrder

        init {
            val byColor = cards.groupBy { it }.let {
                if (jokerWildcard) {
                    it - JOKER
                } else {
                    it
                }
            }
            val jokers = cards.count { it == JOKER }
            val type = when {
                jokers == 5 -> FiveOfAKind
                byColor.values.first().count() == 5 -> FiveOfAKind
                byColor.values.any { it.count() == 4 } -> FourOfAKind
                byColor.values.any { it.count() == 3 } && byColor.values.any { it.count() == 2 } -> FullHouse
                byColor.values.any { it.count() == 3 } -> ThreeOfAKind
                byColor.values.count { it.count() == 2 } == 2 -> TwoPair
                byColor.values.count { it.count() == 2 } == 1 -> OnePair
                else -> HighCard
            }

            this.type = if (jokerWildcard) {
                type.withJokers(jokers)
            } else {
                type
            }
        }

        override fun compareTo(other: Hand): Int =
            compareBy<Hand> { it.type }
                .thenComparing { hand1, hand2 ->
                    hand1.cards.zip(hand2.cards)
                        .firstOrNull { (a, b) -> a != b }
                        ?.let { (a, b) -> orderOfCards.indexOf(a) - orderOfCards.indexOf(b) }
                        ?: 0
                }.compare(this, other)
    }

    private enum class HandType(val withJokers: (Int) -> HandType) {
        FiveOfAKind({
            when (it) {
                0, 5 -> FiveOfAKind
                else -> throw IllegalStateException("Cheater!")
            }
        }),
        FourOfAKind({
            when (it) {
                0 -> FourOfAKind
                1 -> FiveOfAKind
                else -> throw IllegalStateException("Cheater!")
            }
        }),
        FullHouse({
            when (it) {
                0 -> FullHouse
                1 -> FourOfAKind
                2 -> FiveOfAKind
                else -> throw IllegalStateException("Cheater!")
            }
        }),
        ThreeOfAKind({
            when (it) {
                0 -> ThreeOfAKind
                1 -> FourOfAKind
                2 -> FiveOfAKind
                else -> throw IllegalStateException("Cheater!")
            }
        }),
        TwoPair({
            when (it) {
                0 -> TwoPair
                1 -> FullHouse
                else -> throw IllegalStateException("Cheater!")
            }
        }),
        OnePair({
            when (it) {
                0 -> OnePair
                1 -> ThreeOfAKind
                2 -> FourOfAKind
                3 -> FiveOfAKind
                else -> throw IllegalStateException("Cheater!")
            }
        }),
        HighCard({
            when (it) {
                0 -> HighCard
                1 -> OnePair
                2 -> ThreeOfAKind
                3 -> FourOfAKind
                4 -> FiveOfAKind
                else -> throw IllegalStateException("Cheater!")
            }
        }),
    }

    companion object : Day.Main("Day07.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}