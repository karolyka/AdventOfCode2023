package day12

import java.io.File
import kotlin.math.min

const val DAY = "12"
const val TEST = 0

private const val OPERATIONAL = '.'
private const val DAMAGED = '#'
private const val UNKNOWN = '?'

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

class Springs private constructor(val conditions: String, val counts: List<Int>) {
    companion object {
        private val springCache = mutableMapOf<String, MutableMap<List<Int>, Springs>>()

        fun getOrCreate(codes: String, counts: List<Int>): Springs =
            springCache[codes]?.let { springsMutableMap ->
                springsMutableMap[counts] ?: Springs(codes, counts).also { springsMutableMap[counts] = it }
            } ?: Springs(codes, counts).also {
                springCache[codes] = mutableMapOf(counts to it)
            }
    }

    private val children: MutableList<Springs> by lazy {
        if (counts.isNotEmpty()) {
            val groupLen = counts.first()
            val lenForLefts = if (counts.size == 1)
                0
            else
                counts.subList(1, counts.size).sum() + counts.size - 1
            val len = (conditions.length - lenForLefts).let {
                if (it < conditions.length && conditions[it] == DAMAGED)
                    it - 1
                else
                    it
            }
            if (len > 0) {
                findPositions(conditions, groupLen, len).map {
                    val length = it + groupLen + 1
                    if (length < conditions.length)
                        getOrCreate(conditions.substring(it + groupLen + 1), counts.subList(1, counts.size))
                    else
                        EndSpring
                }.toMutableList()
            } else {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
    }
    private val isEmpty by lazy { this == EndSpring || (counts.isEmpty() && conditions.all { it != DAMAGED }) }
    private val isEnd by lazy { (children.size < 2 && (children.firstOrNull() ?: this).isEmpty) }

    private val isValid: Boolean by lazy {
        if (isEnd)
            true
        else {
            children.filterNot { it.isValid }.forEach { children.remove(it) }
            children.any { it.isValid }
        }
    }

    val multi: Long by lazy { if (isEnd) 1 else children.sumOf { it.multi } }

    private fun findPositions(code: String, len: Int, limit: Int): MutableList<Int> {
        val result = mutableListOf<Int>()
        val maxIndex = code.indexOf(DAMAGED).let { if (it == -1) limit - len else min(it, limit - len) }
        (0..maxIndex).forEach { index ->
            if (code[index] != OPERATIONAL) {
                val prior = if (index == 0) true else code[index - 1].let { it == OPERATIONAL || it == UNKNOWN }
                val next =
                    if (index == code.length - len) true else code[index + len].let { it == OPERATIONAL || it == UNKNOWN }
                if (prior && next && code.substring(index, index + len).isValid() && code.isNotDamaged(index + len))
                    result.add(index)
            }
        }
        return result
    }

    private fun String.isValid() = all { it == DAMAGED || it == UNKNOWN }
    private fun String.isNotDamaged(index: Int) = (index >= length) || (this[index] != DAMAGED)
}

private val EndSpring = Springs.getOrCreate("", emptyList())

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()
    val springs = lines.map { line ->
        line.split(' ').let { strings -> Springs.getOrCreate(strings[0], strings[1].split(',').map { it.toInt() }) }
    }

    val task01 = springs.sumOf { it.multi } // 8270
    println("Task01: $task01")

    val ss = springs.map { spring ->
        Springs.getOrCreate(
            (0 until 5).joinToString("?") { spring.conditions },
            (0 until 5).map { spring.counts }.flatten()
        )
    }

    val task02 = ss.sumOf { it.multi } // 204640299929836
    println("Task02: $task02")
}
