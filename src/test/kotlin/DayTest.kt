import org.intellij.lang.annotations.Language
import java.lang.reflect.ParameterizedType
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class DayTest<D : Day>(
    @Language("file-reference") protected val filenamePartOne: String? = null,
    @Language("file-reference") protected val filenamePartTwo: String? = null,
) {
    constructor(filename: String):this(filename, filename)

    open val partOneExpected: Any? = null
    open val partTwoExpected: Any? = null

    protected val target: D = ((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>).kotlin.newInstance()

    @Test
    open fun `Part One`() {
        assumeNotNull(filenamePartOne)
        assumeNotNull(partOneExpected)

        val result = target.partOne(filenamePartOne, true)

        assertEquals(partOneExpected, result)
    }

    @Test
    open fun `Part Two`() {
        assumeNotNull(filenamePartTwo)
        assumeNotNull(partTwoExpected)

        val result = target.partTwo(filenamePartTwo, true)

        assertEquals(partTwoExpected, result)
    }
}
