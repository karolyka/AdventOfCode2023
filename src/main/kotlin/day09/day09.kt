package day09

import java.io.File

const val DAY = "09"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()
    val sequences = lines.map { line -> line.split(' ').map { it.trim().toInt() } }

    val result = sequences.map { intList ->
        val firstAndLastElements = mutableListOf<Pair<Int, Int>>()
        var test = intList
        while (test.any { it != 0 }) {
            firstAndLastElements.add(test.first() to test.last())
            test = (0 until test.size - 1).map { test[it + 1] - test[it] }
        }

        val intProgression = (firstAndLastElements.size - 1) downTo 0
        val extrapolatedNext = intProgression.sumOf { firstAndLastElements[it].second }

        var extrapolatedPrior = 0
        intProgression.map { index ->
            extrapolatedPrior = firstAndLastElements[index].first - extrapolatedPrior
        }

        extrapolatedNext to extrapolatedPrior
    }

    val task01 = result.sumOf { it.first }
    println("Task01: $task01")

    val task02 = result.sumOf { it.second }
    println("Task02: $task02")
}
