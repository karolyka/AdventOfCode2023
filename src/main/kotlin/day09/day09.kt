package day09

import java.io.File

const val DAY = "09"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()

//    println("Task01: $task01")

//    println("Task02: $task02")
}
