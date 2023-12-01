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

    private val replacements = listOf(
        DigitWord("one", "1"),
        DigitWord("two", "2"),
        DigitWord("three", "3"),
        DigitWord("four", "4"),
        DigitWord("five", "5"),
        DigitWord("six", "6"),
        DigitWord("seven", "7"),
        DigitWord("eight", "8"),
        DigitWord("nine", "9")
    )

    private fun replaceFirst(input: String): String {
        val firstDigit = input.toCharArray().indexOfFirst { it.isDigit() }.let { if (it == -1) input.length else it }
        return if (firstDigit == 0) input
        else replacements
            .mapNotNull {
                val idx = input.indexOf(it.word)
                if (idx != -1 && idx < firstDigit) Match(idx, it)
                else null
            }
            .minByOrNull { it.offset }
            ?.let { resolve(input, it) }
            ?: input
    }
    private fun replaceLast(input: String): String {
        val lastDigit = input.toCharArray().indexOfLast { it.isDigit() }.let { if (it == -1) 0 else it }
        return if (lastDigit == input.length - 1) input
        else (input.length - 3).downTo(lastDigit)
            .firstNotNullOfOrNull { idx ->
                replacements
                    .firstOrNull { input.indexOf(it.word, idx) != -1 }
                    ?.let { resolve(input, Match(idx, it)) }
            }
            ?: input
    }
    private fun resolve(input: String, match: Match): String {
        return input.replaceRange(match.offset..<match.offset + match.digitWord.word.length, match.digitWord.digit)
    }
    private fun fixLine(part: QuizPart, line: String): String {
        return if (part == QuizPart.A) line
        else replaceLast(replaceFirst(line))
    }

    data class DigitWord(
        val word: String,
        val digit: String
    )
    data class Match(
        val offset: Int,
        val digitWord: DigitWord
    )
}
