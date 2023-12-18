package day13

import java.io.File
import kotlin.math.min

const val DAY = "13"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

private const val ROCK = '#'
private const val ASH = '.'

class Lava(private val rows: ArrayList<CharArray>) {
    private val transposed: ArrayList<CharArray> by lazy {
        (0 until rows.first().size).map { index ->
            rows.map { it[index] }.toCharArray()
        }.toCollection(ArrayList())
    }
    val horizontalMirrorIndex by lazy { getMirrorIndex(rows).firstOrNull() }
    val verticalMirrorIndex by lazy { getMirrorIndex(transposed).firstOrNull() }
    val horizontalSmudgeMirrorIndex by lazy { getSmudgeMirrorIndex(rows, horizontalMirrorIndex) }
    val verticalSmudgeMirrorIndex by lazy { getSmudgeMirrorIndex(transposed, verticalMirrorIndex) }

    private fun getMirrorIndex(lines: ArrayList<CharArray>) =
        (1..<lines.size).mapNotNull { index ->
            val bottom = lines.subList(index, min(2 * index, lines.size)).reversed()
            val top = lines.subList(index - bottom.size, index)
            if (top contentEquals bottom) index else null
        }

    private fun getSmudgeMirrorIndex(strings: ArrayList<CharArray>, originalIndex: Int?): Int? {
        val lines = strings.toCollection(ArrayList())
        lines.forEach { line ->
            line.forEachIndexed { charIndex, char ->
                line[charIndex] = if (char == ROCK) ASH else ROCK
                val mirrorIndex = getMirrorIndex(lines).firstOrNull { it != originalIndex }
                line[charIndex] = char
                if (mirrorIndex != null && mirrorIndex != originalIndex)
                    return mirrorIndex
            }
        }
        return null
    }
}

private infix fun List<CharArray>.contentEquals(bottom: List<CharArray>): Boolean {
    return if (size == bottom.size) {
        indices.all { this[it] contentEquals bottom[it] }
    } else {
        false
    }
}

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()

    val lavas = getLavas(lines)

    val task01 = lavas.sum(Lava::verticalMirrorIndex, Lava::horizontalMirrorIndex)
    println("Task01: $task01") // 35360

    val task02 = lavas.sum(Lava::verticalSmudgeMirrorIndex, Lava::horizontalSmudgeMirrorIndex)
    println("Task02: $task02")// 36755
}

private fun getLavas(lines: List<String>): MutableList<Lava> {
    val lavas = mutableListOf<Lava>()
    var rows = arrayListOf<CharArray>()
    lines.forEach { line ->
        if (line != "")
            rows.add(line.toCharArray())
        else {
            lavas.add(Lava(rows))
            rows = arrayListOf()
        }
    }
    if (rows.isNotEmpty())
        lavas.add(Lava(rows))
    return lavas
}

private fun MutableList<Lava>.sum(vertical: Lava.() -> Int?, horizontal: Lava.() -> Int?) =
    sumOf { it.vertical() ?: 0 } + sumOf { it.horizontal() ?: 0 } * 100