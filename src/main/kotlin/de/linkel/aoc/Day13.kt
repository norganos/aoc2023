package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import kotlin.math.min

@Singleton
class Day13: AbstractLinesAdventDay<Int>() {
    override val day = 13

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        return lines.split { it.isEmpty() }
            .sumOf { rows ->
                val cols = (0 until rows.first().length)
                    .map { c ->
                        rows.map { it[c] }.joinToString("")
                    }
                val mirroredRows = findReflection(rows)
                val mirroredCols = findReflection(cols)

                val aResult = mirroredCols + mirroredRows * 100
                if (part == QuizPart.A) {
                    aResult
                } else {
                    val mrows = rows.toMutableList()
                    val mcols = cols.toMutableList()
                    coordinates(rows.size, cols.size)
                        .firstNotNullOf { pos ->
                            val backup = mrows[pos.first][pos.second]
                            val char = if (backup == '#') '.' else '#'
                            mrows[pos.first] = mrows[pos.first].replaceIndex(pos.second, char)
                            mcols[pos.second] = mcols[pos.second].replaceIndex(pos.first, char)

                            val mirroredRows2 = findReflection(mrows, mirroredRows)
                            val mirroredCols2 = findReflection(mcols, mirroredCols)
                            val result = mirroredCols2 + mirroredRows2 * 100

                            mrows[pos.first] = mrows[pos.first].replaceIndex(pos.second, backup)
                            mcols[pos.second] = mcols[pos.second].replaceIndex(pos.first, backup)

                            result.takeIf { it != 0 }
                        }
                }
            }
    }

    private fun findReflection(input: List<String>, except: Int = -1): Int {
        return (1 until input.size)
            .filter { it != except }
            .firstNotNullOfOrNull { divider ->
                val size = min(divider, input.size - divider)
                val top = input.subList(divider - size, divider)
                val bottom = input.subList(divider, divider + size).reversed()
                if (top == bottom)
                    divider
                else null
            } ?: 0
    }
}

fun String.replaceIndex(idx: Int, char: Char): String {
    return "${this.substring(0, idx)}$char${this.substring(idx+1)}"
}

fun coordinates(rows: Int, cols: Int): Sequence<Pair<Int, Int>> {
    return sequence {
        (0 until rows)
            .forEach { r ->
                (0 until cols)
                    .forEach { c ->
                            yield(r to c)
                    }
            }
    }
}

fun <T> Sequence<T>.split(predicate: (T) -> Boolean): Sequence<List<T>> {
    val input = this
    return sequence {
        val buffer = mutableListOf<T>()
        input.forEach { element ->
            if (predicate(element)) {
                yield(buffer.toList())
                buffer.clear()
            } else {
                buffer.add(element)
            }
        }
        if (buffer.isNotEmpty()) {
            yield(buffer.toList())
        }
    }
}
