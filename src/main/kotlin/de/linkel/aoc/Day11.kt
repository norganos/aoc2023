package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import jakarta.inject.Singleton

@Singleton
class Day11: AbstractLinesAdventDay<Long>() {
    override val day = 11

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        val galaxies =
            Grid.parse(lines) { _, char ->
                if (char == '#') true else null
            }
                .let { grid ->
                    // part 2 example uses factor 100, part 2 puzzle 1.000.000...
                    // -1 because we don't multiply the line itself, but add copies, so we have to subtract the original
                    val expansionFactor = (if (part == QuizPart.A) 2 else if (grid.width == 10) 100 else 1_000_000) - 1

                    val points = grid.getAllData()
                        .map { it.point }
                    val xx = (0 until grid.width).filter { x ->
                            points.none { it.x == x }
                        }
                        .sortedDescending()
                    val yy = (0 until grid.height).filter { y ->
                            points.none { it.y == y }
                        }
                        .sortedDescending()
                    points.map { p ->
                        Point(
                            p.x + xx.count { it < p.x } * expansionFactor,
                            p.y + yy.count { it < p.y } * expansionFactor
                        )
                    }
                }
                .sortedWith(compareBy({ it.y }, { it.x }))
                .toList()
        return galaxies
                .flatMapIndexed { idx, galaxy ->
                    galaxies
                        .drop(idx + 1)
                        .map {
                            (it - galaxy).manhattenDistance.toLong()
                        }
                }
                .sum()
    }
}
