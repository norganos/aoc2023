package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton

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

        return LongRange(0, Long.MAX_VALUE)
            .first { loc ->
                val seed = seedToSoil.reverseLookup(
                    soilToFertilizer.reverseLookup(
                        fertilizerToWater.reverseLookup(
                            waterToLight.reverseLookup(
                                lightToTemperature.reverseLookup(
                                    temperatureToHumidity.reverseLookup(
                                        humidityToLocation.reverseLookup(loc)
                                    )
                                )
                            )
                        )
                    )
                )
                seeds.any { seed in it }
            }
//        return seeds.minOf { seedRange ->
//            seedRange.minOf { seed ->
//                humidityToLocation.lookup(
//                        temperatureToHumidity.lookup(
//                            lightToTemperature.lookup(
//                                waterToLight.lookup(
//                                    fertilizerToWater.lookup(
//                                        soilToFertilizer.lookup(
//                                            seedToSoil.lookup(seed)
//                                        )
//                                    )
//                                )
//                            )
//                        )
//                    )
//                }
//            }
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
        fun lookup(input: Long): Long {
            for (mapping in mappings) {
                if (input in mapping.sourceRange) {
                    return mapping.destRange.first + (input - mapping.sourceRange.first)
                }
            }
            return input
        }
        fun reverseLookup(input: Long): Long {
            for (mapping in mappings) {
                if (input in mapping.destRange) {
                    return mapping.sourceRange.first + (input - mapping.destRange.first)
                }
            }
            return input
        }
    }

    data class Mapping(
        val sourceRange: LongRange,
        val destRange: LongRange
    )
}
