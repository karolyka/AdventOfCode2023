package day01

import java.io.File

const val DAY = "01"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()

    val digitRegex = "\\d".toRegex()
    val sumOf1 = sumByNumbers(lines, digitRegex)
    println(sumOf1)

    val sumOf2 = sumByNumbers(
        lines,
        "\\d|one|two|three|four|five|six|seven|eight|nine".toRegex(),
        "enin|thgie|neves|xis|evif|ruof|eerht|owt|eno|\\d".toRegex()
    )
    println(sumOf2)
}

private fun sumByNumbers(lines: List<String>, normalRegex: Regex, reservedRegex: Regex = normalRegex) =
    lines.sumOf { line ->
        val first = normalRegex.find(line)!!.value
        val last = reservedRegex.find(line.reversed())!!.value.reversed()
        first.parseToInt() * 10 + last.parseToInt()
    }

private fun String.parseToInt() = when (this) {
    "zero" -> 0
    "one" -> 1
    "two" -> 2
    "three" -> 3
    "four" -> 4
    "five" -> 5
    "six" -> 6
    "seven" -> 7
    "eight" -> 8
    "nine" -> 9
    else -> toInt()
}