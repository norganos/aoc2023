package de.linkel.aoc

import de.linkel.aoc.base.AbstractFileAdventDay
import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import java.io.BufferedReader

@Singleton
class Day15: AbstractLinesAdventDay<Int>() {
    override val day = 15

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        return if (part == QuizPart.A)
            lines.sumOf { line ->
                line.split(",").sumOf(::hash)
            }
        else {
            val boxes = List(256) { mutableListOf<Pair<String,Int>>() }
            lines.forEach { line ->
                line.split(",").forEach { token ->
                    if (token.endsWith('-')) {
                        val label = token.substringBefore('-')
                        boxes[hash(label)].removeFirst { it.first == label }
                    } else if (token.contains('=')) {
                        val label = token.substringBefore('=')
                        val focalLength = token.substringAfter('=').toInt()
                        val idx = boxes[hash(label)].indexOfFirst { it.first == label }
                        if (idx == -1) {
                            boxes[hash(label)].add(label to focalLength)
                        } else {
                            boxes[hash(label)][idx] = label to focalLength
                        }
                    }
                }
            }
            boxes.mapIndexed { boxIndex, box ->
                box.mapIndexed { lensIndex, (_, focalLength) ->
                    (boxIndex + 1) * (lensIndex + 1) * focalLength
                }
                    .sum()
            }
                .sum()
        }
    }

    private fun <T> MutableList<T>.removeFirst(predicate: (item: T) -> Boolean): T? {
        val idx = indexOfFirst(predicate)
        return if (idx == -1) null
        else  removeAt(idx)
    }

    private fun hash(token: String): Int = token.fold(0) { hash, char ->
        (17 * (hash + char.code)) % 256
    }.toInt()
}
