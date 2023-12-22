import kotlin.io.path.readLines
import kotlin.math.max
import kotlin.math.min

class Day22: Day {
    override fun partOne(filename: String, verbose: Boolean): Int {
        val bricks = filename.asPath().readLines()
            .mapIndexed { index, line ->
                line.toBrick(index)
            }
        bricks.dropBricks()
        val removableBricks = bricks.getRemovableBricks()
        return bricks.size - removableBricks.size
    }

    override fun partTwo(filename: String, verbose: Boolean): Int {
        val bricks = filename.asPath().readLines()
            .mapIndexed { index, line ->
                line.toBrick(index)
            }
        bricks.dropBricks()
        val removableBricks = bricks.getRemovableBricks()
        return removableBricks.sumOf { brickToRemove: Brick ->
            val bricksWithoutBrickToRemove = bricks.map { it.clone() }
                .filter { brick -> brick.id != brickToRemove.id }
            bricksWithoutBrickToRemove.dropBricks()
        }
    }

    private fun List<Brick>.dropBricks(): Int {
        val xSize = maxOf { it.maxX } + 1
        val ySize = maxOf { it.maxY } + 1
        val heightsMap = Matrix(xSize, ySize) { 0 }
        val topBricks = Matrix<Brick?>(xSize, ySize) { null }

        var bricksMoved = 0

        sortedBy { it.minHeight }
            .forEach { brick ->
                val footprintCoordinates = brick.footprint
                val restingZ = footprintCoordinates.maxOf { heightsMap[it] } + 1
                if (brick.minHeight > restingZ) {
                    bricksMoved++
                    brick dropTo restingZ
                }

                footprintCoordinates.mapNotNull { topBricks[it] }
                    .filterToSet { it.maxHeight == restingZ - 1 }
                    .forEach {
                        it.supporting += brick
                        brick.supportedBy += it
                    }

                footprintCoordinates.forEach {
                    heightsMap[it] = brick.maxHeight
                    topBricks[it] = brick
                }
            }
        return bricksMoved
    }

    private fun List<Brick>.getRemovableBricks() =
        flatMapToSet { brick -> brick.supportedBy.takeIf { it.size == 1 } ?: emptySet() }

    private fun String.toBrick(index: Int) : Brick {
        val (start, end) = split('~')
        return Brick(index, start.toCoordinate3d(), end.toCoordinate3d())
    }

    private fun String.toCoordinate3d() =
        split(',').let { (x, y, z) ->
            Coordinate3d(x.toInt(), y.toInt(), z.toInt())
        }

    data class Brick(
        val id: Int,
        val cubes: List<Coordinate3d>
    ) {
        constructor(id: Int, start: Coordinate3d, end: Coordinate3d) : this(id, getCubes(start, end))

        val supporting = mutableSetOf<Brick>()
        val supportedBy = mutableSetOf<Brick>()

        val maxX
            get() = cubes.maxOf { it.x }

        val maxY
            get() = cubes.maxOf { it.x }

        val minHeight
            get() = cubes.minOf { it.z }

        val maxHeight
            get() = cubes.maxOf { it.z }

        val footprint: List<Coordinate>
            get() = cubes.map { cube -> Coordinate(cube.x, cube.y) }

        infix fun dropTo(newZ: Int) {
            val dropDistance = minHeight - newZ
            cubes.forEach { cube ->
                cube.z -= dropDistance
            }
        }

        fun clone(): Brick =
            copy(cubes = cubes.map { it.copy() })
    }
    companion object : Day.Main("Day22.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}

private fun getCubes(start: Coordinate3d, end: Coordinate3d): List<Coordinate3d> =
    when {
        start.x != end.x -> getRange(start.x, end.x).map { x -> Coordinate3d(x, start.y, start.z) }
        start.y != end.y -> getRange(start.y, end.y).map { y -> Coordinate3d(start.x, y, start.z) }
        else -> getRange(start.z, end.z).map { z -> Coordinate3d(start.x, start.y, z) }
    }

private fun getRange(a: Int, b: Int): IntRange {
    return min(a, b)..max(a, b)
}
