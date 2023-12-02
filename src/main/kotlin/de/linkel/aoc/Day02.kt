package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import java.lang.Exception

@Singleton
class Day02: AbstractLinesAdventDay<Int>() {
    override val day = 2

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        val linePattern = Regex("Game ([0-9]+): (.+)")
        val pickPattern = Regex("\\s*([0-9]+) (blue|red|green)\\s*")
        val games = lines
            .map { line ->
                val lm = linePattern.matchEntire(line) ?: throw Exception("can't parse line $line")
                val gameNr = lm.groupValues[1].toInt()
                val picks = lm.groupValues[2]
                    .split(";")
                    .map { pick ->
                        pick.split(",")
                            .map { pickPattern.matchEntire(it) ?: throw Exception("can't parse picked cubes $it") }
                            .associate { it.groupValues[2] to it.groupValues[1].toInt() }
                    }
                Game(
                    nr = gameNr,
                    picks = picks
                )
            }

        val constraints = mapOf(
            "red" to 12,
            "green" to 13,
            "blue" to 14
        )
        return if (part == QuizPart.A) games
            .filter { game ->
                game.picks.all { pick ->
                    pick.entries.all { cube ->
                        cube.value <= constraints[cube.key]!!
                    }
                }
            }
            .sumOf { it.nr }
        else games
            .map { game ->
                constraints.keys
                    .map { color ->
                        game.picks.maxOf { it[color] ?: 0 }
                    }
                    .fold(1) { p, c -> p * c }
            }
            .sum()
    }

    data class Game(
        val nr: Int,
        val picks: List<Map<String,Int>>
    )
}
