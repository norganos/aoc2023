package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import kotlin.math.pow

@Singleton
class Day04: AbstractLinesAdventDay<Int>() {
    override val day = 4

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        val pattern = Regex("Card +([0-9]+): ([0-9 ]+) \\| ([0-9 ]+)")
        val winningNumbers = lines
            .mapNotNull { pattern.matchEntire(it) }
            .map { match ->
                val winners = match.groupValues[2].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
                val numbers = match.groupValues[3].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
                winners.intersect(numbers)
            }
        return if (part == QuizPart.A)
            winningNumbers
                .filter { it.isNotEmpty() }
                .sumOf { 2f.pow(it.size - 1).toInt() }
        else {
            winningNumbers
                .foldIndexed(mutableMapOf<Int, Int>()) { idx, map, card ->
                    val count = map[idx] ?: 1
                    if (!map.containsKey(idx)) {
                        map[idx] = 1
                    }
                    if (card.isNotEmpty()) {
                        (1..(card.size)).forEach {
                            map[idx + it] = (map[idx + it] ?: 1) + count
                        }
                    }
                    map
                }
                .values
                .sumOf { it }
        }
    }
}
