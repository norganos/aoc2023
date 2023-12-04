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
        val cards = lines
            .mapNotNull { pattern.matchEntire(it) }
            .map { match ->
                val winners = match.groupValues[2].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()
                val numbers = match.groupValues[3].split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet()

                ScoreCard(
                    number = match.groupValues[1].toInt(),
                    winningNumbers = winners.intersect(numbers)
                )
            }
        return if (part == QuizPart.A) cards.sumOf { it.points }
        else {
            val mapped = cards.associateBy { it.number }.toMutableMap()
            mapped.keys.sorted().toList()
                .forEach { nr ->
                    val card = mapped[nr]!!
                    (1..(card.winningNumbers.size))
                        .map { card.number + it }
                        .mapNotNull { mapped[it] }
                        .forEach {
                            mapped[it.number] = it.copy(count = it.count + card.count)
                        }
            }
            return mapped.values.sumOf { it.count }
        }
    }

    data class ScoreCard(
        val number: Int,
        val winningNumbers: Set<Int>,
        val count: Int = 1
    ) {
        val points = if (winningNumbers.isNotEmpty()) 2f.pow(winningNumbers.size - 1).toInt() else 0
    }
}
