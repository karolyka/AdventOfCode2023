package day02

import java.io.File
import java.lang.Integer.max

const val DAY = "02"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${
    when (TEST) {
        1 -> "_test_1"
        2 -> "_test_2"
        else -> ""
    }
}.txt"

data class Cubes(val red: Int, val green: Int, val blue: Int) {
    operator fun compareTo(other: Cubes): Int {
        return max(max(red.compareTo(other.red), green.compareTo(other.green)), blue.compareTo(other.blue))
    }
}

data class Game(val id: Int, val sets: List<Cubes>) {
    operator fun compareTo(other: Cubes): Int {
        return sets.maxOfOrNull { it.compareTo(other) } ?: 1
    }
}

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()

    val games01 = lines.map { line ->
        val semicolonPosition = line.indexOf(':')
        val id = line.substring(5, semicolonPosition).toInt()
        val cubes = line.substring(semicolonPosition + 1)
            .split(';')
            .map { it.trim().split(',') }
            .map { it.toCubes() }
        Game(id, cubes)
    }.toList()

    val cubes01 = Cubes(12, 13, 14)
    val task01 = games01.filter { it <= cubes01 }.sumOf { it.id }
    println("Task01: $task01")

    val task02 = games01.sumOf { it.sets.powerOf() }
    println("Task02: $task02")
}

private fun Collection<Cubes>.powerOf(): Int {
    var red = 0
    var green = 0
    var blue = 0
    forEach {
        red = max(red, it.red)
        green = max(green, it.green)
        blue = max(blue, it.blue)
    }
    return red * green * blue
}

private fun Collection<String>.toCubes(): Cubes {
    var red = 0
    var green = 0
    var blue = 0
    map { it.trim().split(' ') }
        .map { it.component2() to it.first().toInt() }
        .forEach {
            when (it.first) {
                "red" -> red = it.second
                "green" -> green = it.second
                "blue" -> blue = it.second
                else -> throw Exception()
            }
        }
    return Cubes(red, green, blue)
}
