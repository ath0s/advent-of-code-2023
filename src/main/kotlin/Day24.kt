import kotlin.io.path.readLines

class Day24: Day {
    override fun partOne(filename: String, verbose: Boolean): Long {
        var answer = 0L
        val hailstones = filename.asPath().readLines()
            .map { it.toHailstone() }
        val min = 200000000000000.0
        val max = 400000000000000.0
        hailstones.forEachIndexed { i, first ->
            hailstones.drop(i + 1).forEach { second ->
                val denom = ((first.xvel * second.yvel) - (first.yvel * second.xvel)).toDouble()
                if (denom == 0.0) {
                    if (first.x == second.x && first.y == second.y) {
                        if (first.x >= min && first.x <= max && first.y >= min && first.y <= max) {
                            answer++
                        }
                    }
                }
                val number1 =
                    ((second.x - first.x) * second.yvel.toDouble()) - ((second.y - first.y) * second.xvel.toDouble())
                val number2 =
                    ((first.x - second.x) * first.yvel.toDouble()) - ((first.y - second.y) * first.xvel.toDouble())
                val intersectionX: Double = (number1 / denom) * first.xvel + first.x
                val intersectionY: Double = (number1 / denom) * first.yvel + first.y
                if (intersectionX in min..max && intersectionY >= min && intersectionY <= max) {
                    if ((number1 / denom) > 0 && (number2 / denom) < 0) {
                        answer++
                    }
                }
            }
        }
        return answer
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val hailstones = filename.asPath().readLines()
            .map { it.toHailstone() }

        val h1 = hailstones[0]
        val h2 = hailstones[1]

        val range = 500
        for (vx in -range..range) {
            for (vy in -range..range) {
                for (vz in -range..range) {
                    if (vx == 0 || vy == 0 || vz == 0) {
                        continue
                    }

                    val A = h1.x
                    val a = h1.xvel - vx
                    val B = h1.y
                    val b = h1.yvel - vy
                    val C = h2.x
                    val c = h2.xvel - vx
                    val D = h2.y
                    val d = h2.yvel - vy

                    if (c == 0 || (a * d) - (b * c) == 0) {
                        continue
                    }

                    val t = (d * (C - A) - c * (D - B)) / ((a * d) - (b * c))

                    val x = h1.x + h1.xvel * t - vx * t
                    val y = h1.y + h1.yvel * t - vy * t
                    val z = h1.z + h1.zvel * t - vz * t

                    var hitall = true
                    for (i in hailstones.indices) {
                        val h = hailstones[i]
                        val u = when {
                            h.xvel != vx -> (x - h.x) / (h.xvel - vx)
                            h.yvel != vy -> (y - h.y) / (h.yvel - vy)
                            h.zvel != vz -> (z - h.z) / (h.zvel - vz)
                            else -> throw IllegalStateException()
                        }

                        if ((x + u * vx != h.x + u * h.xvel) || (y + u * vy != h.y + u * h.yvel) || (z + u * vz != h.z + u * h.zvel)) {
                            hitall = false
                            break
                        }
                    }

                    if (hitall) {
                        return x + y + z
                    }
                }
            }
        }
        return 0
    }

    private data class Hailstone(
        val x: Long,
        val y: Long,
        val z: Long,
        val xvel: Int,
        val yvel: Int,
        val zvel: Int
    )

    private fun String.toHailstone(): Hailstone {
        val (position, velocity) = split(Regex("""\s*@\s*"""))
        val (x, y, z) = position.split(Regex("""\s*,\s*""")).map { it.toLong() }
        val (xvel, yvel, zvel) = velocity.split(Regex("""\s*,\s*""")).map { it.toInt() }
        return Hailstone(x, y, z, xvel, yvel, zvel)
    }

    companion object : Day.Main("Day24.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}