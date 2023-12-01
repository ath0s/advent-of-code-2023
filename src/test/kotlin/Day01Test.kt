import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class Day01Test : DayTest<Day01>("Day01_part1_test.txt", "Day01_part2_test.txt") {
    override val partOneExpected = 142
    override val partTwoExpected = 281

    @Test
    fun `Should resolve overlapping`() {
        val(first,last) = target.run {
            "636qbxtkzroneighttpv".extractNumbers()
        }
        assertEquals("6", first)
        assertEquals("eight", last)
    }
}