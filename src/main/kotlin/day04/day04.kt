package day04

import java.io.File
import kotlin.math.pow

const val DAY = "04"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${
    when (TEST) {
        1 -> "_test_1"
        2 -> "_test_2"
        else -> ""
    }
}.txt"

private val whiteSpace = "\\s+".toRegex()

data class Card(val id: Int, val winning: Set<Int>, val numbers: Set<Int>) {
    val winningSize by lazy { winning.intersect(numbers).size }
    val points by lazy { if (winningSize > 0) 2 pow (winningSize - 1) else 0 }
}

infix fun Int.pow(exponent: Int): Int = toDouble().pow(exponent).toInt()
fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()
    val cards = lines.map { it.toCard() }

    val task01 = cards.sumOf { it.points }
    println("Task01: $task01")

    val copies = IntArray(cards.size) { 1 }
    cards.forEachIndexed { index, card ->
        if (card.winningSize > 0) {
            val inc = copies[index]
            (index.plus(1)..index.plus(card.winningSize)).forEach { copies[it] += inc }
        }
    }

    val task02 = copies.sum()
    println("Task02: $task02")
}

private fun String.toCard(): Card {
    val idAndCards = split(':')
    val id = idAndCards[0].substring(5).trim().toInt()
    val cars = idAndCards[1].split('|')
    return Card(id, cars[0].toNumbers(), cars[1].toNumbers())
}

private fun String.toNumbers(): Set<Int> {
    return trim().split(whiteSpace).map { it.trim().toInt() }.toSet()
}