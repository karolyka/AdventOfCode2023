package day08

import java.io.File

const val DAY = "08"
const val TEST = 1

private val whiteSpace = "\\s+".toRegex()

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${
    when (TEST) {
        1 -> "_test_1"
        2 -> "_test_2"
        else -> ""
    }
}.txt"


fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()

//    println("Task01: $task01")

//    println("Task02: $task02")
}
