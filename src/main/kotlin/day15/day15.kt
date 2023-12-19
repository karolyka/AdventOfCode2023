package day15

import java.io.File

const val DAY = "15"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

private const val ADD = '='
private const val REMOVE = '-'

data class Holiday(val value: String) {
    val code: String
    val operation: Char
    val focal: Int?

    init {
        val index = value.indexOf(ADD).let { if (it > -1) it else value.indexOf(REMOVE) }
        code = value.substring(0, index)
        operation = value[index]
        focal = value.substring(index + 1).toIntOrNull()
    }

    private val hash by lazy { value.getHash() }
    val codeHash by lazy { code.getHash() }

    private fun String.getHash() = map { it.code }.fold(0) { acc, i -> (acc + i) * 17 % 256 }

    override fun hashCode() = hash

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Holiday

        return value == other.value
    }
}

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines().first().split(',').map { Holiday(it) }

    val task01 = lines.sumOf { it.hashCode() }
    println("Task01: $task01") // 519603

    val boxes = mutableMapOf<Int, MutableMap<String, Int>>()
    lines.forEach { label ->
        if (label.operation == ADD) {
            (boxes[label.codeHash] ?: mutableMapOf<String, Int>().also { boxes[label.codeHash] = it })[label.code] =
                label.focal!!
        } else {
            boxes[label.codeHash]?.remove(label.code)
        }
    }

    val task02 = boxes.entries.sumOf {
        it.value.entries.mapIndexed { index, mutableEntry -> (it.key + 1) * (index + 1) * mutableEntry.value }.sum()
    }
    println("Task02: $task02") // 244342
}
