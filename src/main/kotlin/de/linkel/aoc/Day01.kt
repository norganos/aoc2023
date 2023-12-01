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

    private val replacements = mapOf(
        "nine" to "9",
        "eight" to "8",
        "seven" to "7",
        "six" to "6",
        "five" to "5",
        "four" to "4",
        "three" to "3",
        "two" to "2",
        "one" to "1"
    )
    private val digitNounsPattern = Regex(replacements.entries.joinToString("|") { it.key })
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
        return replacements[match] ?: throw Exception("???")
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
