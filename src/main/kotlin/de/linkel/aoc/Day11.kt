package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Point
import jakarta.inject.Singleton

@Singleton
class Day11: AbstractLinesAdventDay<Long>() {
    override val day = 11

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        return lines
            .flatMapIndexed { y, line ->
                line.toCharArray()
                    .mapIndexed { x, c ->
                        if (c == '#') Point(x, y) else null
                    }
                    .filterNotNull()
            }
            .toList()
                .let { points ->
                    val width = points.maxOf { it.x } + 1
                    val height = points.maxOf { it.x } + 1
                    // part 2 example uses factor 100, part 2 puzzle 1.000.000...
                    // -1 because we don't multiply the line itself, but add copies, so we have to subtract the original
                    val expansionFactor = (if (part == QuizPart.A) 2 else if (width == 10) 100 else 1_000_000) - 1

                    val xx = (0 until width).filter { x ->
                            points.none { it.x == x }
                        }
                        .toList()
                    val yy = (0 until height).filter { y ->
                            points.none { it.y == y }
                        }
                        .toList()
                    points.map { p ->
                        Point(
                            p.x + xx.count { it < p.x } * expansionFactor,
                            p.y + yy.count { it < p.y } * expansionFactor
                        )
                    }
                }
                .toList()
                .combinationPairs(
                    withSelf = false,
                    withMirrors = false
                )
                .sumOf { (it.first - it.second).manhattenDistance.toLong() }
    }
}
fun <T> List<T>.combinationPairs(
    withSelf: Boolean = false,
    withMirrors: Boolean = false
): Sequence<Pair<T,T>> {
    val size = this.size
    val list = this
    return sequence {
        (0 until size)
            .forEach { i ->
                ((if (withMirrors) 0 else i) until size)
                    .forEach { j ->
                        if (withSelf || i != j) {
                            yield(Pair(list[i], list[j]))
                        }
                    }
            }
    }
}
