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
            allowedRanges = translation.fillSpaces().filter(allowedRanges).destRanges,
            translation = Translation()
        )
    }

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        val iterator = lines.iterator()
        val seedsLineValues = iterator.next().substringAfter(":").split(spaces).filter { it.isNotEmpty() }.map { it.toLong() }
        val seeds = if (part == QuizPart.A) seedsLineValues.map { LongRange(it, it) }
                else seedsLineValues.windowed(2, step = 2).map { LongRange(it[0], it[0] + it[1] - 1) }

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

        fun fillSpaces(): Translation {
            var maxLast = -1L

            val newMappings = mappings.toMutableList()
            val blackMappings = mappings.flatMap { listOf(it.sourceRange, it.destRange) }
            while (true) {
                val minFirst = blackMappings
                    .filter { it.first > maxLast }
                    .minOfOrNull { it.first }
                if (minFirst == null) {
                    newMappings.add(Mapping(LongRange(maxLast + 1, Long.MAX_VALUE), LongRange(maxLast + 1, Long.MAX_VALUE)))
                    break
                }
                if (minFirst - 1 >= maxLast + 1) {
                    newMappings.add(
                        Mapping(
                            LongRange(maxLast + 1, minFirst - 1),
                            LongRange(maxLast + 1, minFirst - 1)
                        )
                    )
                }
                maxLast = blackMappings
                    .filter { it.last > minFirst && it.last > minFirst }
                    .minOfOrNull { it.last } ?: Long.MAX_VALUE
                if (maxLast == Long.MAX_VALUE) {
                    break
                }
            }
            return Translation(newMappings.toList())
        }

        fun filter(sourceRanges: Collection<LongRange>): Translation {
            return Translation(
                sourceRanges.flatMap { sourceRange ->
                    mappings
                        .filter { sourceRange.intersects(it.sourceRange) }
                        .map { mapping ->
                            val fromValue = max(sourceRange.first, mapping.sourceRange.first)
                            val toValue = min(sourceRange.last, mapping.sourceRange.last)
                            val offset = mapping.destRange.first - mapping.sourceRange.first
                            val newSource = LongRange(fromValue, toValue)
                            val newDest = LongRange(fromValue + offset, toValue + offset)
                            Mapping(
                                sourceRange = newSource,
                                destRange = newDest
                            )
                        }
                }
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
