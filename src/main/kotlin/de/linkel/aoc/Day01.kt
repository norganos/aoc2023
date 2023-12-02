package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import java.lang.Exception
import kotlin.math.min

@Singleton
class Day01: AbstractLinesAdventDay<Int>() {
    override val day = 1

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        return lines
            .map { line ->
                val digits = findFirstAndLast(part, line)
                digits.first.digitToInt() * 10 + digits.second.digitToInt()
            }
            .sum()
    }

    private val replacements = listOf(
        "nine" to '9',
        "eight" to '8',
        "seven" to '7',
        "six" to '6',
        "five" to '5',
        "four" to '4',
        "three" to '3',
        "two" to '2',
        "one" to '1'
    )

    private fun findFirstDigit(input: String, indices: IntProgression): Char {
        val chars = input.toCharArray()
        return indices.firstNotNullOfOrNull { idx ->
            if (chars[idx].isDigit()) chars[idx]
            else replacements.firstNotNullOfOrNull { r ->
                if (input.substring(idx, min(idx + r.first.length, input.length)) == r.first) r.second
                else null
            }
        } ?: throw Exception("no digit in $input")
    }

    private fun findFirstAndLast(part: QuizPart, line: String): Pair<Char, Char> {
        return if (part == QuizPart.A) {
            val digits = line.toCharArray().filter { it.isDigit() }
            Pair(digits.first(), digits.last())
        }
        else {
            Pair(findFirstDigit(line, line.indices), findFirstDigit(line, line.indices.reversed()))
        }
    }
}
