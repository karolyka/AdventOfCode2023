package day06

import java.io.File

const val DAY = "06"
const val TEST = 0

private val whiteSpace = "\\s+".toRegex()

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

data class Race(val time: Long, val distance: Long)

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()
    val times = lines[0].parseLongs()
    val distances = lines[1].parseLongs()
    val races = times.mapIndexed { index, time -> Race(time, distances[index]) }

    val task01 = races.map(::getBeats).reduce { acc, i -> acc * i }
    println("Task01: $task01")

    val raceTime = lines[0].filterThanParseLong()
    val raceDistance = lines[1].filterThanParseLong()
    val task02 = getBeats(Race(raceTime, raceDistance))
    println("Task02: $task02")
}

private fun getBeats(race: Race) =
    (0..race.time).map { (race.time - it) * it }.count { it > race.distance }

private fun String.parseLongs() = substringAfter(':').trim().split(whiteSpace).map { it.toLong() }
private fun String.filterThanParseLong() = filter { it in '0'..'9' }.toLong()
