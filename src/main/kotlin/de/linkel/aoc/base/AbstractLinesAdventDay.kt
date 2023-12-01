package de.linkel.aoc.base

import java.io.BufferedReader

abstract class AbstractLinesAdventDay<T>: AbstractFileAdventDay<T>() {
    override fun process(part: QuizPart, reader: BufferedReader): T {
        return reader.useLines { sequence ->
            process(part, sequence)
        }
    }

    protected abstract fun process(part: QuizPart, lines: Sequence<String>): T
}
