package day10

import java.io.File

const val DAY = "10"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

enum class CompassPoint {
    NORTH,
    SOUTH,
    EAST,
    WEST;

    fun opposite() = when (this) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        EAST -> WEST
        WEST -> EAST
    }
}

enum class Pipe(
    val code: Char,
    private val first: CompassPoint? = null,
    private val second: CompassPoint? = null
) {
    START('S'),
    EMPTY('.'),
    VERTICAL('|', CompassPoint.NORTH, CompassPoint.SOUTH),
    HORIZONTAL('-', CompassPoint.EAST, CompassPoint.WEST),
    L('L', CompassPoint.NORTH, CompassPoint.EAST),
    J('J', CompassPoint.NORTH, CompassPoint.WEST),
    E7('7', CompassPoint.SOUTH, CompassPoint.WEST),
    F('F', CompassPoint.SOUTH, CompassPoint.EAST);

    fun isConnectTo(to: CompassPoint) = first == to || second == to
    fun isConnectTo(a: CompassPoint, b: CompassPoint) = (a == first && b == second) || (b == first && a == second)
    fun toNorth() = isConnectTo(CompassPoint.NORTH)
}

enum class Element {
    NOTHING,
    EMPTY,
    PIPE,
    LONGEST,
    CONTINUING
}

data class Position(val row: Int, val column: Int) {
    fun to(compassPoint: CompassPoint) = when (compassPoint) {
        CompassPoint.NORTH -> Position(row - 1, column)
        CompassPoint.SOUTH -> Position(row + 1, column)
        CompassPoint.WEST -> Position(row, column - 1)
        CompassPoint.EAST -> Position(row, column + 1)
    }
}

data class PipeElement(
    var pipe: Pipe,
    val position: Position,
    var steps: Int = -1,
    var inLongest: Boolean? = null,
    var inside: Boolean? = null
)

class Field(lines: List<String>) {
    private val field = lines.mapIndexed { row, line ->
        line.mapIndexed { column, c -> PipeElement(c.toElement(), Position(row, column)) }.toTypedArray()
    }.toTypedArray()

    private val allField = field.flatten()
    private var startElement = allField.first { it.pipe == Pipe.START }.also { it.steps = 0 }
    private var steps = 0

    private val maxRow: Int = field.lastIndex
    private val maxColumn: Int = field.first().lastIndex

    fun dijkstra() {
        @Suppress("ControlFlowWithEmptyBody")
        while (doOneStep()) {
        }
    }

    fun markLongestRoute() {
        val maxSteps = getLongestPathLength()

        allField
            .filter { it.steps == maxSteps || it.pipe == Pipe.START }
            .forEach { it.inLongest = true }

        allField
            .filter { it.steps < 0 }
            .forEach {
                it.inLongest = false
                it.inside = false
            }

        ((maxSteps - 1) downTo 1).forEach { steps ->
            allField
                .filter { element -> element.steps == steps && element.inLongest == null }
                .forEach { element -> element.inLongest = hasBiggerNeighbour(element) }
        }
    }

    fun markInside() {
        changeStartElementToPipe()

        field.forEach(::markOneLine)

        startElement.pipe = Pipe.START
    }

    fun getLongestPathLength() = allField.maxOf { it.steps }

    fun getInsideCount() = allField.count { it.inside == true }

    private fun doOneStep(): Boolean {
        val result = allField
            .filter { element -> element.steps == steps }
            .map { element -> CompassPoint.entries.map { isAvailableStep(element, it) }.any { it } }
            .any { it }

        if (result)
            steps++
        return result
    }

    private fun changeStartElementToPipe() {
        val compasses = CompassPoint.entries
            .map { it to startElement.position.to(it) }
            .filter { isValid(it.second) && getElement(it.second).steps == 1 }

        validateCompasses(compasses)

        startElement.pipe = Pipe.entries.first { it.isConnectTo(compasses[0].first, compasses[1].first) }
    }

    private fun markOneLine(line: Array<PipeElement>) {
        var inside = false
        var type = Element.NOTHING
        var lastCornerPipe = Pipe.EMPTY
        line.forEach { element ->
            val newType = getNewType(element)
            type = when (newType) {
                Element.EMPTY,
                Element.PIPE -> {
                    element.inside = inside
                    newType
                }

                Element.LONGEST -> if (type == Element.CONTINUING)
                    type
                else {
                    checkCornerElements(element, lastCornerPipe, inside).run {
                        lastCornerPipe = first
                        inside = second
                    }
                    newType
                }

                Element.NOTHING,
                Element.CONTINUING -> throw Exception("Bad type")
            }
        }
    }

    private fun validateCompasses(compasses: List<Pair<CompassPoint, Position>>) {
        if (compasses.size != 2)
            throw Exception("Cannot determine pipe type at start position")
    }

    private fun checkCornerElements(element: PipeElement, lastCornerPipe: Pipe, inside: Boolean): Pair<Pipe, Boolean> {
        var resultCornerPipe = lastCornerPipe
        var resultInside = inside
        when (element.pipe) {
            Pipe.F,
            Pipe.J,
            Pipe.E7,
            Pipe.L -> checkCornerElement(element, lastCornerPipe, inside).run {
                resultCornerPipe = first
                resultInside = second
            }

            Pipe.VERTICAL -> resultInside = !inside
            Pipe.HORIZONTAL -> {}

            else -> throw Exception("Bad type")
        }
        return resultCornerPipe to resultInside
    }

    private fun checkCornerElement(element: PipeElement, lastCornerPipe: Pipe, inside: Boolean): Pair<Pipe, Boolean> {
        var resultInside = inside
        return if (lastCornerPipe == Pipe.EMPTY)
            element.pipe
        else {
            if (lastCornerPipe.toNorth() != element.pipe.toNorth())
                resultInside = !inside
            Pipe.EMPTY
        } to resultInside
    }

    private fun getNewType(element: PipeElement) = when {
        element.inLongest == true -> Element.LONGEST
        element.pipe == Pipe.EMPTY -> Element.EMPTY
        else -> Element.PIPE
    }

    private fun hasBiggerNeighbour(element: PipeElement) =
        CompassPoint.entries.any { element.pipe.isConnectTo(it) && isBiggerNeighbour(element, it) }

    private fun isBiggerNeighbour(element: PipeElement, compassPoint: CompassPoint) =
        element.position.to(compassPoint)
            .let { position ->
                isValid(position) && getElement(position).let {
                    it.inLongest == true && it.steps == element.steps + 1 && it.pipe.isConnectTo(compassPoint.opposite())
                }
            }

    private fun isAvailableStep(element: PipeElement, compassPoint: CompassPoint) =
        element.position.to(compassPoint).let {
            if (isValid(it, compassPoint.opposite())) {
                getElement(it).steps = element.steps + 1
                true
            } else {
                false
            }
        }

    private fun Char.toElement() = Pipe.entries.first { it.code == this }

    private fun isNotProcessed(position: Position) = getElement(position).steps == -1

    private fun isValid(position: Position) =
        position.row >= 0 && position.column >= 0 && position.row <= maxRow && position.column <= maxColumn

    private fun isValid(position: Position, compassPoint: CompassPoint) =
        isValid(position) &&
                isNotProcessed(position) && getElement(position).pipe.isConnectTo(compassPoint)

    private fun getElement(position: Position) = field[position.row][position.column]
}

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()

    val field = Field(lines)

    field.dijkstra()
    val task01 = field.getLongestPathLength() // 6690
    println("Task01: $task01")

    field.markLongestRoute()
    field.markInside()
    val task02 = field.getInsideCount() // 525
    println("Task02: $task02")
}
