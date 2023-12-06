package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton

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

    private fun getPossibleWins(race: Pair<Long,Long>): Long {
        return (1L until race.first).fold(0L) { s, ms ->
            val speed = ms
            val remaining = race.first - ms
            val distance = speed * remaining
            if (distance > race.second) s + 1 else s
        }
    }
}
