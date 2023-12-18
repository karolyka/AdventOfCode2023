package day05

import java.io.File
import kotlin.math.max
import kotlin.math.min

const val DAY = "05"
const val TEST = 0

private val whiteSpace = "\\s+".toRegex()

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

enum class Category {
    NOTHING, SEED, SOIL, FERTILIZER, WATER, LIGHT, TEMPERATURE, HUMIDITY, LOCATION
}

data class Range(val destinationStart: Long, val sourceStart: Long, val length: Long) {
    val sourceRange = LongRange(sourceStart, sourceStart + length)
    val delta = destinationStart - sourceStart
}

data class CategoryMap(val source: Category, val destination: Category)

infix fun Category.to(other: Category): CategoryMap = CategoryMap(this, other)

private val CMD_NO = Category.NOTHING to Category.NOTHING
private val CMD_SEED_TO_SOIL = Category.SEED to Category.SOIL
private val CMD_SOIL_TO_FERTILIZER = Category.SOIL to Category.FERTILIZER
private val CMD_FERTILIZER_TO_WATER = Category.FERTILIZER to Category.WATER
private val CMD_WATER_TO_LIGHT = Category.WATER to Category.LIGHT
private val CMD_LIGHT_TO_TEMPERATURE = Category.LIGHT to Category.TEMPERATURE
private val CMD_TEMPERATURE_TO_HUMIDITY = Category.TEMPERATURE to Category.HUMIDITY
private val CMD_HUMIDITY_TO_LOCATION = Category.HUMIDITY to Category.LOCATION
fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()

    val mapping = mutableMapOf<CategoryMap, MutableList<Range>>()
    var seeds = listOf<Long>()
    var command = CMD_NO
    lines.filter { it.isNotEmpty() }
        .forEach {
            when (it.substringBefore(':')) {
                "seeds" -> seeds = it.substringAfter(':').trim().toLongList()
                "seed-to-soil map" -> command = CMD_SEED_TO_SOIL
                "soil-to-fertilizer map" -> command = CMD_SOIL_TO_FERTILIZER
                "fertilizer-to-water map" -> command = CMD_FERTILIZER_TO_WATER
                "water-to-light map" -> command = CMD_WATER_TO_LIGHT
                "light-to-temperature map" -> command = CMD_LIGHT_TO_TEMPERATURE
                "temperature-to-humidity map" -> command = CMD_TEMPERATURE_TO_HUMIDITY
                "humidity-to-location map" -> command = CMD_HUMIDITY_TO_LOCATION
                else -> {
                    mapping[command] = mapping.getOrDefault(command, mutableListOf()).apply { add(it.toRange()) }
                }
            }
        }

    val task01 = seeds.minOf { seed ->
        getLocation(seed, mapping)
    }

    println("Task01: $task01")

    var seedRanges = seeds.toRangeList()
    mapping.values.forEach { rangeList ->
        val newSeedRanges = mutableListOf<LongRange>()
        rangeList.forEach { range ->
            val additionalRangeList = mutableListOf<LongRange>()
            seedRanges.forEach { seedRange ->
                if (range.isDisjoint(seedRange))
                    additionalRangeList.add(seedRange)
                else {
                    if (seedRange.first < range.sourceRange.first)
                        additionalRangeList.add(LongRange(seedRange.first, range.sourceRange.first))

                    if (seedRange.last > range.sourceRange.last)
                        additionalRangeList.add(LongRange(range.sourceRange.last, seedRange.last))

                    newSeedRanges.add(combinateRanges(seedRange, range))
                }
            }
            seedRanges = additionalRangeList.toMutableList()
        }
        seedRanges.addAll(newSeedRanges)
    }

    val task02 = seedRanges.minOf { it.first }

    println("Task02: $task02")
}

private fun List<Long>.toRangeList() =
    (chunked(2).map { LongRange(it[0], it[0] + it[1]) }).toMutableList()

private fun combinateRanges(seedRange: LongRange, range: Range): LongRange {
    val first = range.delta + max(seedRange.first, range.sourceRange.first)
    val last = range.delta + min(seedRange.last, range.sourceRange.last)
    return LongRange(first, last)
}

private fun Range.isDisjoint(seedRange: LongRange) =
    seedRange.last < sourceRange.first || seedRange.first > sourceRange.last

private fun getLocation(
    seed: Long,
    mapping: MutableMap<CategoryMap, MutableList<Range>>
): Long {
    var result = seed
    var category = Category.SEED
    while (category != Category.LOCATION) {
        val (categoryMap, ranges) = mapping.entries.first { it.key.source == category }
        ranges.firstOrNull { result in it.sourceRange }?.let { result += it.delta }
        category = categoryMap.destination
    }
    return result
}

private fun String.toLongList() = split(whiteSpace).map { it.trim().toLong() }.toList()

private fun String.toRange() = toLongList().let { Range(it[0], it[1], it[2]) }
