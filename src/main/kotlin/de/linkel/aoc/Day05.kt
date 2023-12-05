package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class Day05: AbstractLinesAdventDay<Long>() {
    override val day = 5
    private val spaces = Regex("\\s+")

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        val iterator = lines.iterator()
        val seedsLineValues = iterator.next().substringAfter(":").split(spaces).filter { it.isNotEmpty() }.map { it.toLong() }
        val seeds = if (part == QuizPart.A) seedsLineValues.map { LongRange(it, it) }
                else seedsLineValues.windowed(2, step = 2).map { LongRange(it[0], it[0] + it[1] - 1) }
        iterator.next()
        assert(iterator.next().trim() == "seed-to-soil map:")
        val seedToSoil = consumeUntilDelimiter(iterator, "soil-to-fertilizer map:")
        val soilToFertilizer = consumeUntilDelimiter(iterator, "fertilizer-to-water map:")
        val fertilizerToWater = consumeUntilDelimiter(iterator, "water-to-light map:")
        val waterToLight = consumeUntilDelimiter(iterator, "light-to-temperature map:")
        val lightToTemperature = consumeUntilDelimiter(iterator, "temperature-to-humidity map:")
        val temperatureToHumidity = consumeUntilDelimiter(iterator, "humidity-to-location map:")
        val humidityToLocation = consumeUntilDelimiter(iterator, "EOF")

        return seeds
            .filterTranslation(
                seedToSoil
                    .fillSpaces()
            )
            .destRanges
            .filterTranslation(
                soilToFertilizer
                    .fillSpaces()
            )
            .destRanges
            .filterTranslation(
                fertilizerToWater
                    .fillSpaces()
            )
            .destRanges
            .filterTranslation(
                waterToLight
                    .fillSpaces()
            )
            .destRanges
            .filterTranslation(
                lightToTemperature
                    .fillSpaces()
            )
            .destRanges
            .filterTranslation(
                temperatureToHumidity
                    .fillSpaces()
            )
            .destRanges
            .filterTranslation(
                humidityToLocation
                    .fillSpaces()
            )
            .destRanges
            .minOf { it.first }
    }

    
    private fun consumeUntilDelimiter(iterator: Iterator<String>, delimiter: String): Translation {
        val result = mutableListOf<Mapping>()
        while (iterator.hasNext()) {
            val line = iterator.next().trim()
            if (line.isEmpty()) continue
            if (line == delimiter) break
            val vals = line.split(spaces).map { it.trim().toLong() }
            result.add(
                Mapping(
                    destRange = LongRange(vals[0], vals[0] + vals[2] - 1),
                    sourceRange = LongRange(vals[1], vals[1] + vals[2] - 1)
                )
            )
        }
        return Translation(result)
    }

    data class Translation(
        val mappings: List<Mapping>
    ) {
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
    )

    private fun Collection<LongRange>.filterTranslation(translation: Translation): Translation {
        return translation.filter(this)
    }
}
fun LongRange.intersects(other: LongRange): Boolean {
    return (this.first <= other.first && this.last >= other.first) || (other.first <= this.first && other.last >= this.first)
}
