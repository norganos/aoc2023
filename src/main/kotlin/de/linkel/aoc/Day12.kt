package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton

@Singleton
class Day12: AbstractLinesAdventDay<Long>() {
    override val day = 12

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        return lines
            .map { it.substringBefore(' ') to it.substringAfter(' ').split(",").map(String::toInt).toList() }
            .map { (mask, groups) ->
                if (part == QuizPart.A) mask to groups
                else List(5) { mask }.joinToString("?") to List(5) { groups }.flatten()
            }
            .sumOf { (mask, groups) ->
                countPossibilities(mask,  groups)
            }
    }

    private val cache = mutableMapOf<String,Long>()

    private fun countPossibilities(mask: String, groups: List<Int>): Long {
        return if (mask.isEmpty())
            if (groups.isEmpty()) 1L else 0L
        else {
            val cacheKey = mask + groups.joinToString(",")
            cache[cacheKey] ?: when(mask[0]) {
                '.' -> countPossibilities(mask.substring(1), groups)
                '#' -> consumeGroup(mask, groups)
                '?' -> consumeGroup(mask, groups) + countPossibilities(mask.substring(1), groups)
                else -> throw Exception("unexpected char...")
            }.also {
                cache[cacheKey] = it
            }
        }
    }

    private fun consumeGroup(mask: String, groups: List<Int>): Long {
        return if (groups.isEmpty()) 0L
        else {
            val group = groups.first()
            if (mask.length < group) 0L
            else if (mask.substring(0, group).contains('.')) 0L
            else if (mask.length == group) if (groups.size == 1) 1L else 0L
            else if (mask[group] != '#')
                countPossibilities(mask.substring(group+1), groups.drop(1))
            else 0L
        }
    }
}
