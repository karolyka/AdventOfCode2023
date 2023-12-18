package day08

import java.io.File
import kotlin.math.max
import kotlin.math.min

const val DAY = "08"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${if (TEST == 0) "" else "_test_$TEST"}.txt"

data class Node(val key: String, val left: String, val right: String)

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()
    val instructions = lines.first()
    val nodes = lines.subList(2, lines.size)
        .map { Node(it.substring(0, 3), it.substring(7, 10), it.substring(12, 15)) }
        .associateBy { it.key }

    if (TEST < 3) {
        val task01 = task01(nodes, instructions)
        println("Task01: $task01")
    }

    val task02 = task02(nodes, instructions)
    println("Task02: $task02")
}

private fun task01(nodeMap: Map<String, Node>, instructions: String): Int {
    var node = nodeMap["AAA"]!!
    var steps = 0
    val maxInstructions = instructions.length
    while (node.key != "ZZZ") {
//        println("$steps -> ${steps % maxInstructions}")
        val direction = instructions[(steps % maxInstructions)]

        val nodeKey = if (direction == 'L') node.left else node.right
        node = nodeMap[nodeKey]!!
        steps++
    }
    return steps
}

private fun task02(nodeMap: Map<String, Node>, instructions: String): Long {
    var result = 1L
    val nodes = nodeMap.values.filter { it.key.endsWith('A') }.toSet()
    val maxInstructions = instructions.length
    nodes.forEach {
        var node = it
        var steps = 0
        while (!node.key.endsWith('Z')) {
//            println("$steps -> ${steps % maxInstructions}")
            val direction = instructions[(steps % maxInstructions)]

            val nodeKey = if (direction == 'L') node.left else node.right
            node = nodeMap[nodeKey]!!
            steps++
        }
        result = getLeastCommonMultiple(steps.toLong(), result)
    }

    return result
}

private fun getLeastCommonMultiple(int1: Long, int2: Long): Long {
    val lower = min(int1, int2)
    val higher = max(int1, int2)
    var lcm = higher
    while (lcm % lower != 0L)
        lcm += higher
    return lcm
}