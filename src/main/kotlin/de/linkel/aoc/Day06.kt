package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

@Singleton
class Day06: AbstractLinesAdventDay<Long>() {
    override val day = 6

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        val spaces = Regex("\\s+")
        val (times, distances) = lines
            .map { line ->
                if (part == QuizPart.A) {
                    line.split(spaces)
                        .drop(1)
                        .map { it.toLong() }
                } else {
                    listOf(line.substring(11).replace(" ", "").toLong())
                }
            }
            .toList()
        val races = times.zip(distances).toList()
        return races.fold(1) { p, r -> p * getPossibleWins(r) }
    }

    private fun ceil2(input: Double): Long {
        return if (ceil(input) == input) ceil(input + 1).toLong() else ceil(input).toLong()
    }
    private fun floor2(input: Double): Long {
        return if (ceil(input) == input) floor(input - 1).toLong() else floor(input).toLong()
    }

    private fun getPossibleWins(race: Pair<Long,Long>): Long {
        val disc = sqrt((race.first * race.first - 4 * race.second).toDouble())
        return floor2((disc + race.first) / 2) - ceil2((- disc + race.first) / 2) + 1
    }
}
