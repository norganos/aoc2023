package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton

@Singleton
class Day07: AbstractLinesAdventDay<Long>() {
    override val day = 7

    enum class Type {
        FIVE,
        FOUR,
        FULLHOUSE,
        THREE,
        TWOPAIR,
        PAIR,
        HIGH;

        operator fun plus(jokers: Int): Type = when (jokers) {
            0 -> this
            1 -> when(this) {
                FOUR -> FIVE
                THREE -> FOUR
                TWOPAIR -> FULLHOUSE
                PAIR -> THREE
                HIGH -> PAIR
                else -> throw Exception("combination of $this + $jokers joker should not happen")
            }
            2 -> when(this) {
                FULLHOUSE -> FIVE
                TWOPAIR -> FOUR
                PAIR -> THREE
                else -> throw Exception("combination of $this + $jokers jokers should not happen")
            }
            3 -> when(this) {
                FULLHOUSE -> FIVE
                THREE -> FOUR
                else -> throw Exception("combination of $this + $jokers jokers should not happen")
            }
            4 -> when(this) {
                FOUR -> FIVE
                else -> throw Exception("combination of $this + $jokers jokers should not happen")
            }
            5 -> when(this) {
                FIVE -> FIVE
                else -> throw Exception("combination of $this + $jokers jokers should not happen")
            }
            else -> throw Exception("combination of $this + $jokers jokers should not happen")
        }
    }

    data class Hand(
        val type: Type,
        val hand: String,
        val values: List<Int>,
        val bid: Long
    )
    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        return lines
            .map { line ->
                val (hand, bid) = line.split(" ")
                val amounts = hand.toCharArray()
                    .groupBy { it }
                    .entries
                    .map { Pair(it.key, it.value.size) }
                    .groupBy { it.second }
                val jokers = if (part == QuizPart.A) 0 else hand.count { it == 'J' }
                val type = (
                        if (amounts.containsKey(5)) Type.FIVE
                        else if (amounts.containsKey(4)) Type.FOUR
                        else if (amounts.containsKey(3) && amounts.containsKey(2)) Type.FULLHOUSE
                        else if (amounts.containsKey(3)) Type.THREE
                        else if (amounts.containsKey(2) && amounts[2]!!.size > 1) Type.TWOPAIR
                        else if (amounts.containsKey(2)) Type.PAIR
                        else Type.HIGH
                    ) + jokers

                val values = hand.toCharArray()
                    .map { c ->
                        if (c.isDigit()) c.digitToInt()
                        else when (c) {
                            'T' -> 10
                            'J' -> if (part == QuizPart.A) 11 else 1
                            'Q' -> 12
                            'K' -> 13
                            'A' -> 14
                            else -> throw Exception("invalid card $c")
                        }
                    }
                    Hand(type, hand, values, bid.toLong())
            }
            .sortedWith { a, b ->
                b.type.compareTo(a.type)
                    .takeIf { it != 0 }
                    ?: a.values.zip(b.values).firstNotNullOfOrNull { (ca, cb) ->
                        ca.compareTo(cb)
                            .takeIf { it != 0 }
                    } ?: 0
            }
            .mapIndexed { idx, hand ->  hand.bid * (idx + 1) }
            .sum()
    }
}

/*
 // variant: transform input hand (5 chars 0-9,T,J,Q,K,A) into a 6-char string with 1 character 1-7 for type, and 5 chars a-o (mapped from 0-9,T,J,Q,K,A)
 // so that usual string comparison sorts the hands correctly
            ...
                val _card = hand.toCharArray()
                    .map { c ->
                        if (c.isDigit()) c + 49 // 0-9 => a-j
                        else when (c) {
                            'T' -> 'k'
                            'J' -> if (part == QuizPart.A) 'l' else 'a'
                            'Q' -> 'm'
                            'K' -> 'n'
                            'A' -> 'o'
                            else -> throw Exception("invalid card $c")
                        }
                    }
                Pair("${Type.entries.size - type.ordinal}${_card}", bid.toLong())
            }
            .sortedBy { it.first }
            .mapIndexed { idx, (_, bid) ->  bid * (idx + 1) }
            .sum()
 */
