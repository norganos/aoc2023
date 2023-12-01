package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import java.lang.Exception

@Singleton
class Day01: AbstractLinesAdventDay<Int>() {
    override val day = 1
    override val parts = QuizPart.BOTH

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        return lines
            .map { line ->
                val digits = fixLine(part, line).toCharArray().filter { it.isDigit() }
                (digits.first().toString() + digits.last().toString()).toInt()
            }
            .sum()
    }

    private val digitNounsPattern = Regex(
        "one|two|three|four|five|six|seven|eight|nine"
    )
    private fun getFirstMatch(input: String, maxStart: Int): MatchResult? {
        val match = digitNounsPattern.find(input)
        return if (match != null && match.range.first < maxStart)
            match
        else null
    }
    private fun getLastMatch(input: String, minStart: Int): MatchResult? {
        return (input.length - 3).downTo(minStart).firstNotNullOfOrNull {
            digitNounsPattern.find(input, it)
        }
    }
    private fun getReplacement(match: String): String {
        return when(match) {
            "nine" -> "9"
            "eight" -> "8"
            "seven" -> "7"
            "six" -> "6"
            "five" -> "5"
            "four" -> "4"
            "three" -> "3"
            "two" -> "2"
            "one" -> "1"
            else -> throw Exception("???")
        }
    }
    private fun replace(input: String, match: MatchResult?): String {
        return if (match != null)
            input.substring(0, match.range.first) + getReplacement(match.groupValues.first()) + input.substring(match.range.last + 1)
        else
            input
    }
    private fun fixLine(part: QuizPart, line: String): String {
        return if (part == QuizPart.A) line
        else line
            .let { s ->
                replace(s, getFirstMatch(s, s.toCharArray().indexOfFirst { it.isDigit() }))
            }
            .let { s ->
                replace(s, getLastMatch(s, s.toCharArray().indexOfLast { it.isDigit() }))
            }
    }
}
