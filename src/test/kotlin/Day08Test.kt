import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day08Test : DayTest<Day08>("Day08_part1_first_example.txt", "Day08_part2_test.txt") {
    override val partOneExpected = 2
    override val partTwoExpected = 6L

    @Test
    fun `Part One - second example`() {
        val result = target.partOne("Day08_part1_second_example.txt", true)

        assertEquals(6, result)
    }
}