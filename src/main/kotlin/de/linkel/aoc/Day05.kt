package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import java.lang.Exception
import kotlin.math.max
import kotlin.math.min

@Singleton
class Day05: AbstractLinesAdventDay<Long>() {
    override val day = 5
    private val spaces = Regex("\\s+")

    data class State(
        val allowedRanges: Collection<LongRange>,
        val translation: Translation = Translation()
    ) {
        operator fun plus(line: String): State = copy(
            translation = translation + Mapping.of(line)
        )
        fun translate(): State = copy(
                allowedRanges = translation.filter(allowedRanges).destRanges,
                translation = Translation()
            )
    }

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        val iterator = lines.iterator()
        val seedsLineValues = iterator.next().substringAfter(":").split(spaces).filter { it.isNotEmpty() }.map { it.toLong() }
        val seeds = if (part == QuizPart.A) seedsLineValues.map { LongRange(it, it) }
                else seedsLineValues.chunked(2).map { (from, count) -> LongRange(from, from + count - 1) }

        return iterator.asSequence()
            .fold(State(seeds)) { state, line ->
                if (line.isEmpty()) state
                else if (line.endsWith("map:")) state.translate()
                else state + line
            }
            .translate()
            .allowedRanges
            .minOf { it.first }
    }

    data class Translation(
        val mappings: List<Mapping> = emptyList()
    ) {
        operator fun plus(mapping: Mapping) = copy(
            mappings = mappings + listOf(mapping)
        )

        fun filter(sourceRanges: Collection<LongRange>): Translation {
            return Translation(
                sourceRanges.flatMap { sourceRange ->
                    mappings
                        .filter { sourceRange.intersects(it.sourceRange) }
                        .map { mapping ->
                            val newSource = sourceRange.intersect(mapping.sourceRange)
                            val newDest = newSource.move(mapping.destRange.first - mapping.sourceRange.first)
                            Mapping(
                                sourceRange = newSource,
                                destRange = newDest
                            )
                        }
                } + mappings
                    .flatMap { listOf(it.sourceRange, it.destRange) }
                    .fold(sourceRanges) { remainders, range ->
                        remainders
                            .flatMap { it - range }
                    }
                    .map { Mapping(it, it) }
            )
        }

        val destRanges: Collection<LongRange> = mappings.map { it.destRange }
    }

    data class Mapping(
        val sourceRange: LongRange,
        val destRange: LongRange
    ) {
        companion object {
            private val pattern = Regex("(\\d+)\\s+(\\d+)\\s+(\\d+)")
            fun of(line: String): Mapping {
                val match = pattern.matchEntire(line) ?: throw Exception("invalid line format")
                return Mapping(
                    destRange = LongRange(match.groupValues[1].toLong(), match.groupValues[1].toLong() + match.groupValues[3].toLong() - 1),
                    sourceRange = LongRange(match.groupValues[2].toLong(), match.groupValues[2].toLong() + match.groupValues[3].toLong() - 1)
                )
            }
        }
    }
}

fun LongRange.intersects(other: LongRange): Boolean {
    return (this.first <= other.first && this.last >= other.first) || (other.first <= this.first && other.last >= this.first)
}
fun LongRange.intersect(other: LongRange): LongRange {
    return LongRange(max(this.first, other.first), min(this.last, other.last))
}
fun LongRange.move(offset: Long): LongRange {
    return LongRange(this.first + offset, this.last + offset)
}

operator fun LongRange.minus(other: LongRange): List<LongRange> {
    return if (this.first >= other.first && this.last <= other.last) emptyList() // we are completely inside the other -> nothing left
    else if (this.first < other.first && this.last > other.last) listOf(LongRange(this.first, other.first - 1), LongRange(other.last + 1, this.last)) // other is inside of us -> other cuts us in 2 parts
    else if (this.first < other.first && this.last > other.first) listOf(LongRange(this.first, other.first - 1)) // other cuts our upper end
    else if (this.last > other.last && this.first < other.last) listOf(LongRange(other.last + 1, this.last)) // other cuts our lower end
    else listOf(this) // no intersection -> we stay the same
}
