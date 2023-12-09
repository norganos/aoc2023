package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton

@Singleton
class Day09: AbstractLinesAdventDay<Int>() {
    override val day = 9

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        return lines.map { line ->
            build(
                Seq(
                    line.split(" ")
                        .map { it.toInt() }
                        .toList()
                    )
                )
                .let {
                    if (part == QuizPart.A) it.append(0)
                        .root.values.last()
                    else it.prepend(0)
                        .root.values.first()
                }
        }
            .sum()
    }

    fun build(seq: Seq): Seq {
        return if (seq.values.all { it == 0 }) seq
        else build(seq.differentiate())
    }

    data class Seq(
        val values: List<Int>,
        val parent: Seq? = null
    ) {
        val root: Seq get() = parent?.root ?: this
        fun append(num: Int): Seq {
            return copy(
                values = values + listOf(num),
                parent = parent?.append(parent.values.last() + num)
            )
        }
        fun prepend(num: Int): Seq {
            return copy(
                values = listOf(num) + values,
                parent = parent?.prepend(parent.values.first() - num)
            )
        }
        fun differentiate(): Seq {
            return Seq(
                values = values
                    .windowed(2)
                    .map { (a, b) -> b - a }
                    .toList(),
                parent = this
            )
        }
    }
}
