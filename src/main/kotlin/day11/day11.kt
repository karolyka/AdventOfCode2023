package day11

import java.io.File
import kotlin.math.abs

const val DAY = "11"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

data class Galaxy(var row: Long, var column: Long, val nr: Int) {
    constructor(row: Int, column: Int, nr: Int) : this(row.toLong(), column.toLong(), nr)
}

private const val GALAXY = '#'
private const val SPACE = '.'

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()
    var counter = 0
    val galaxies = lines.mapIndexed { row, line ->
        line.mapIndexed { column, c -> if (c == GALAXY) Galaxy(row, column, ++counter) else null }.filterNotNull()
    }.flatten()

    val universe01 = expandUniverse(lines, galaxies, 1)
    val task01 = getDistances(universe01).sum() // 9918828
    println("Task01: $task01")

    val universe02 = expandUniverse(lines, galaxies, 1_000_000 - 1)
    val task02 = getDistances(universe02).sum() // 692506533832
    println("Task02: $task02")
}

private fun getDistances(universe01: List<Galaxy>) = (0..<universe01.lastIndex).map { a ->
    val galaxyA = universe01[a]
    ((a + 1)..<universe01.size).map { b ->
        val galaxyB = universe01[b]
        abs(galaxyA.row - galaxyB.row) + abs(galaxyA.column - galaxyB.column)
    }
}.flatten()

private fun expandUniverse(lines: List<String>, galaxies: List<Galaxy>, expandBy: Int): List<Galaxy> {
    val newGalaxies = galaxies.map { it.copy() }

    ((lines.first().length - 1) downTo 1).forEach { column ->
        if (lines.all { it[column] == SPACE })
            newGalaxies.filter { it.column >= column }.forEach { it.column += expandBy }
    }

    ((lines.lastIndex) downTo 1).forEach { row ->
        if (lines[row].all { it == SPACE })
            newGalaxies.filter { it.row >= row }.forEach { it.row += expandBy }
    }
    return newGalaxies
}
