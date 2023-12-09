package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.mixins.append
import de.linkel.aoc.utils.mixins.extend
import de.linkel.aoc.utils.mixins.intersects
import de.linkel.aoc.utils.mixins.prepend
import jakarta.inject.Singleton

@Singleton
class Day03: AbstractLinesAdventDay<Int>() {
    override val day = 3

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        val digitPattern = Regex("[0-9]+")
        val symbolPattern = Regex("[^0-9.]")
        val asteriskPattern = Regex("\\*")
        val result = lines
            .prepend("")
            .append("")
            .windowed(3)
            .map { win ->
                val allNumbersPerLine = win.map { digitPattern.findAll(it) }.map { it.toList() }
                val allSymbols = win.flatMap { symbolPattern.findAll(it) }.toList()
                val partNumbers = allNumbersPerLine[1]
                    .filter { match ->
                        val checkrange = match.range.extend(front = 1, back = 1)
                        allSymbols
                            .any { it.range.intersects(checkrange) }
                    }
                    .sumOf { it.value.toInt() }
                val allNumbers = allNumbersPerLine.flatten()
                val gears = asteriskPattern.findAll(win[1])
                    .map { match ->
                        val checkrange = match.range.extend(front = 1, back = 1)
                        allNumbers
                            .filter { it.range.intersects(checkrange) }
                            .map { it.value.toInt() }
                    }
                    .filter { it.size == 2 }
                    .map { it[0] * it[1] }
                    .sum()
                Pair(partNumbers, gears)
            }
            .fold(Pair(0,0)) {
                sum, line ->
                Pair(sum.first + line.first, sum.second + line.second)
            }
        return if (part == QuizPart.A) result.first
            else result.second
    }
}
