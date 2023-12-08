package day03

import java.io.File
import kotlin.math.max
import kotlin.math.min

const val DAY = "03"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"


data class Position(val line: Int, val char: Int)

data class IntWithPosition(val lineIndex: Int, val range: IntRange, val value: Int) {
    fun isAdjacentToSymbol(lines: List<String>): Boolean {
        val line = lines[lineIndex]
        return (range.first > 0 && line[range.first - 1].isSymbol()) ||
                (range.last < line.length - 1 && line[range.last + 1].isSymbol()) ||
                (lineIndex > 0 && lines[lineIndex - 1].wideSubstring(range).containSymbol()) ||
                (lineIndex < lines.size - 1 && lines[lineIndex + 1].wideSubstring(range).containSymbol())
    }

    fun isAdjacentToGearSymbol(lines: List<String>): List<Position> {
        val line = lines[lineIndex]
        val positions = mutableListOf<Position>()
        if (range.first > 0 && line[range.first - 1] == '*')
            positions.add(Position(lineIndex, range.first - 1))
        if (range.last < line.length - 1 && line[range.last + 1] == '*')
            positions.add(Position(lineIndex, range.last + 1))
        if (lineIndex > 0) {
            val prevIndex = lineIndex - 1
            val prevLine = lines[prevIndex]
            val extendRange = range.extend(prevLine)
            prevLine.substring(extendRange)
                .allIndexOf('*')
                .forEach { positions.add(Position(prevIndex, extendRange.first + it)) }
        }
        if (lineIndex < lines.size - 1) {
            val nextIndex = lineIndex + 1
            val nextLine = lines[nextIndex]
            val extendRange = range.extend(nextLine)
            nextLine.substring(extendRange)
                .allIndexOf('*')
                .forEach { positions.add(Position(nextIndex, extendRange.first + it)) }
        }
        return positions
    }

    private fun IntRange.extend(string: String) =
        IntRange(max(0, first - 1), min(last + 1, string.length - 1))

    private fun String.wideSubstring(intRange: IntRange) = substring(intRange.extend(this))

    private fun String.containSymbol() = any { it.isSymbol() }

    private fun Char.isSymbol() = listOf('.', ' ').contains(this).not() && ('0'..'9').contains(this).not()

    private fun String.allIndexOf(char: Char) =
        mapIndexed { index, c -> if (c == char) index else null }.filterNotNull()
}

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()

    val numberRegex = "\\d+".toRegex()
    val allNumber = lines.mapIndexed { index, line ->
        numberRegex.findAll(line).map { IntWithPosition(index, it.range, it.value.toInt()) }.toList()
    }.flatten()

    val task01 = allNumber
        .filter { it.isAdjacentToSymbol(lines) }
        .sumOf { it.value }
    println("Task01: $task01")

    val gearNumbers = allNumber
        .map { it to it.isAdjacentToGearSymbol(lines) }
        .filter { it.second.isNotEmpty() }

    validateTooManyAdjacentSymbols(gearNumbers)

    val joined = getJoined(gearNumbers)

    validateTooManyJoinedNumbers(joined)

    val task02 = joined.sumOf { it.first.value * it.second.first().first().first.value }

    println("Task02: $task02")
}

private fun validateTooManyJoinedNumbers(joined: List<Pair<IntWithPosition, List<List<Pair<IntWithPosition, List<Position>>>>>>) {
    if (joined.any { it.second.size > 1 }) {
        throw Exception("Too many joins")
    }
}

private fun getJoined(gearNumbers: List<Pair<IntWithPosition, List<Position>>>) =
    gearNumbers.map { number ->
        number.first to number.second.map { position ->
            gearNumbers.filter { isLaterPosition(it, number) && it.second.contains(position) }
        }
    }.filter { it.second.first().isNotEmpty() }

private fun isLaterPosition(
    it: Pair<IntWithPosition, List<Position>>,
    number: Pair<IntWithPosition, List<Position>>
) = (it.first.lineIndex > number.first.lineIndex
        || (it.first.lineIndex == number.first.lineIndex && it.first.range.first > number.first.range.first))

private fun validateTooManyAdjacentSymbols(gearNumbers: List<Pair<IntWithPosition, List<Position>>>) {
    if (gearNumbers.any { it.second.size > 1 }) {
        throw Exception("Too many * signs")
    }
}
