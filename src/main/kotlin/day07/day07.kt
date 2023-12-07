package day07

import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

const val DAY = "07"
const val TEST = 0

@Suppress("KotlinConstantConditions")
val INPUT_FILE_NAME = "src/main/resources/day$DAY/input${
    when (TEST) {
        1 -> "_test_1"
        2 -> "_test_2"
        else -> ""
    }
}.txt"

const val CARDMAP = "0123456789ABC"
const val JOKER = 'J'

enum class Type {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE,
    FULL_HOUSE,
    FOUR,
    FIVE
}

abstract class AbstractHand(private val cards: String, val bid: Int) : Comparable<AbstractHand> {
    abstract val score: String

    private val type by lazy { toType(cards) }
    private val forScoring by lazy { cards.map { CARDMAP[score.indexOf(it)] }.toString() }
    abstract fun toType(string: String): Type

    override fun compareTo(other: AbstractHand): Int {
        val byType = type.ordinal.compareTo(other.type.ordinal)
        return if (byType == 0)
            forScoring.compareTo(other.forScoring)
        else byType
    }

    override fun toString(): String {
        return "AbstractHand(cards='$cards', bid=$bid, type=$type)"
    }
}

open class Hand(cards: String, bid: Int) : AbstractHand(cards, bid) {
    override val score = "23456789TJQKA"
    override fun toType(string: String): Type {
        val groups = string.groupBy { it }
        return when (groups.size) {
            1 -> Type.FIVE
            2 -> if (groups.any { it.value.size == 4 }) Type.FOUR else Type.FULL_HOUSE
            3 -> if (groups.any { it.value.size == 3 }) Type.THREE else Type.TWO_PAIR
            4 -> Type.ONE_PAIR
            else -> Type.HIGH_CARD
        }
    }
}

class HandJoker(cards: String, bid: Int) : Hand(cards, bid) {
    override val score = "J23456789TQKA"
    override fun toType(string: String): Type {
        val groups = string.filter { it != JOKER }.groupBy { it }
        val count = string.count { it == JOKER }
        return when (count) {
            0 -> super.toType(string)
            4, 5 -> Type.FIVE
            else -> { // 1, 2, 3
                when {
                    groups.any { it.value.size == 4 } -> Type.FIVE
                    groups.any { it.value.size == 3 } -> if (count == 1) Type.FOUR else Type.FIVE
                    groups.filter { it.value.size == 2 }.size == 2 -> Type.FULL_HOUSE
                    groups.any { it.value.size == 2 } -> when (count) {
                        1 -> Type.THREE
                        2 -> Type.FOUR
                        else -> Type.FIVE
                    }

                    else -> when (count) {
                        1 -> Type.ONE_PAIR
                        2 -> Type.THREE
                        else -> Type.FOUR
                    }
                }
            }
        }
    }
}

fun main() {
    val lines = File(INPUT_FILE_NAME).readLines()

    val task01 = lines.calculate(Hand::class)
    println("Task01: $task01")

    val task02 = lines.calculate(HandJoker::class)
    println("Task02: $task02")
}

fun List<String>.calculate(handKClass: KClass<out AbstractHand>) =
    map { handKClass.primaryConstructor!!.call(it.substring(0, 5), it.substring(6).trim().toInt()) }
        .sorted()
        .mapIndexed { index, hand -> index + 1 to hand }
        .sumOf { it.first * it.second.bid }