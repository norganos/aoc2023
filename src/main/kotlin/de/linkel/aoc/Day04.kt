package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton

@Singleton
class Day04: AbstractLinesAdventDay<Int>() {
    override val day = 4
    override val parts = QuizPart.BOTH

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        return 0
    }
}
