import kotlin.io.path.readText

private val NUMBER_PATTERN = Regex("""\d+""")

class Day05 : Day {
    override fun partOne(filename: String, verbose: Boolean): Long {
        val (seeds, maps) = parse(filename)

        return seeds.minOf { seed -> maps.fold(seed) { acc, map -> map.next(acc) } }
    }

    override fun partTwo(filename: String, verbose: Boolean): Long {
        val (seedRanges, maps) = parse(filename)
        val seeds = seedRanges.chunked(2) { (from, length) ->
            from ..< from + length
        }

        return lowestLocation(maps) { location -> seeds.any { location in it } }
    }

    private fun parse(filename: String): Pair<List<Long>, List<FoodProductionMap>> {
        val parts = filename.asPath().readText().split("\n\n")
        val seeds = NUMBER_PATTERN.findAll(parts.first()).map { it.value.toLong() }.toList()
        val maps = parts.drop(1).map { part ->
            val lines = part.lines()
            val (from, _, to) = lines.first().substringBefore(" ").split('-')
            val ranges = lines.drop(1).map { line ->
                val (destinationRangeStart, sourceRangeStart, rangeLength) = line.split(' ')
                val destinationStart = destinationRangeStart.toLong()
                val sourceStart = sourceRangeStart.toLong()
                val length = rangeLength.toLong()
                MapRange(
                    destinationRange = destinationStart..<destinationStart + length,
                    sourceRange = sourceStart..<sourceStart + length
                )
            }
            FoodProductionMap(from, to, ranges)
        }
        return seeds to maps
    }

    private data class FoodProductionMap(
        val from: String,
        val to: String,
        val ranges: List<MapRange>
    ) {
        fun previous(to: Long) =
            ranges.firstOrNull { to in it.destinationRange }?.run {
                val distance = to - destinationRange.first
                sourceRange.first + distance
            } ?: to

        fun next(from: Long) =
            ranges.firstOrNull { from in it.sourceRange }?.run {
                val distance = from - sourceRange.first
                destinationRange.first + distance
            } ?: from
    }

    private data class MapRange(
        val destinationRange: LongRange,
        val sourceRange: LongRange
    )

    private fun lowestLocation(maps: List<FoodProductionMap>, predicate: (Long) -> Boolean): Long {
        val reversedMaps = maps.reversed()
        for (location in 1..Long.MAX_VALUE) {
            val seed = reversedMaps.fold(location) { acc, map -> map.previous(acc) }
            if (predicate(seed)) {
                return location
            }
        }
        throw IllegalStateException("Cannot find lowest location!")
    }

    companion object : Day.Main("Day05.txt") {
        @JvmStatic
        fun main(args: Array<String>) = main()
    }
}