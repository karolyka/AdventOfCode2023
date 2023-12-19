package day14

import java.io.File

const val DAY = "14"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

private const val ROUNDED = 'O'
private const val CUBE = '#'
private const val EMPTY = '.'

private const val CYCLES = 1_000_000_000

enum class Tilt(val transpose: Boolean) {
    NORTH(false),
    WEST(true),
    SOUTH(false),
    EAST(true),
}

class Platform(val data: Array<CharArray>) {
    val indices: IntRange
        get() = data.indices

    val size: Int
        get() = data.size

    operator fun get(rowIndex: Int) = data[rowIndex]

    inline fun <R> mapIndexed(transform: (index: Int, CharArray) -> R): List<R> {
        return data.mapIndexed(transform)
    }

    fun first() = data.first()

    fun copy() = Platform(data.map { it.copyOf() }.toTypedArray())

    override fun equals(other: Any?) =
        if (other is Platform) {
            var index = 0
            data.all { it.contentEquals(other.data[index++]) }
        } else false

    override fun hashCode(): Int {
        return data.contentDeepHashCode()
    }

    override fun toString(): String = data.joinToString("\n") { it.joinToString("") }
}

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines().map { it.toCharArray() }.toTypedArray()

    val transpose01 = Transpose(Tilt.NORTH, Platform(lines.copyOf())).also { it.tilt() }
    val task01 = calculateHeight(transpose01.platform)

    println("Task01: $task01") // 111339

    var task02: Int? = null
    val platforms = mutableSetOf<Platform>()
    val transpose02 = Transpose(Tilt.NORTH, Platform(lines.copyOf()))
    var index = -1
    while (++index < CYCLES) {
        Tilt.entries.forEach(transpose02::tilt)
        val loopStartIndex = platforms.indexOf(transpose02.platform)
        if (loopStartIndex > -1) {
            val loopEndIndex = loopStartIndex + ((CYCLES - index) % (index - loopStartIndex)) - 1
            task02 = platforms.mapIndexed { ndx, platform -> ndx to platform }
                .first { it.first == loopEndIndex }
                .let { calculateHeight(it.second) }
            break
        } else
            platforms.add(transpose02.platform.copy())
    }
    if (task02 == null)
        task02 = calculateHeight(transpose02.platform)

    println("Task02: $task02")// 93736
}

class Transpose(private var direction: Tilt, val platform: Platform) {
    private lateinit var rowRange: IntProgression
    private lateinit var columnRange: IntProgression
    private val emptyRows = mutableListOf<Int>()

    init {
        setRanges()
    }

    private fun setRanges() {
        when (direction) {
            Tilt.NORTH -> {
                columnRange = platform.first().indices
                rowRange = platform.indices
            }

            Tilt.SOUTH -> {
                columnRange = platform.first().indices.reversed()
                rowRange = platform.indices.reversed()
            }

            Tilt.EAST -> {
                columnRange = platform.indices.reversed()
                rowRange = platform.first().indices.reversed()
            }

            Tilt.WEST -> {
                columnRange = platform.indices
                rowRange = platform.first().indices
            }
        }
    }

    private fun get(row: Int, column: Int) = if (direction.transpose) platform[column][row] else platform[row][column]

    private fun set(row: Int, column: Int, char: Char) {
        if (direction.transpose)
            platform[column][row] = char
        else
            platform[row][column] = char
    }

    fun tilt(newDirection: Tilt = direction) {
        if (newDirection != direction) {
            direction = newDirection
            setRanges()
        }

        columnRange.forEach { columnIndex ->
            emptyRows.clear()
            rowRange.forEach { rowIndex ->
                when (get(rowIndex, columnIndex)) {
                    ROUNDED -> if (emptyRows.isNotEmpty()) {
                        val empty = emptyRows.removeFirst()
                        set(empty, columnIndex, ROUNDED)
                        set(rowIndex, columnIndex, EMPTY)
                        emptyRows.add(rowIndex)
                    }

                    CUBE -> emptyRows.clear()
                    EMPTY -> emptyRows.add(rowIndex)

                    else -> throw Exception("Unsupported element at $rowIndex:$columnIndex")
                }
            }
        }

    }
}

private fun calculateHeight(tilted: Platform): Int {
    return tilted.mapIndexed { index, line ->
        line.count { it == ROUNDED } * (tilted.size - index)
    }.sum()
}
