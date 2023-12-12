package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import java.math.BigInteger
import kotlin.math.max
import kotlin.math.min

@Singleton
class Day12: AbstractLinesAdventDay<BigInteger>() {
    override val day = 12

    override fun process(part: QuizPart, lines: Sequence<String>): BigInteger {
        return lines
//            .take(20)
            .map { it.substringBefore(' ') to it.substringAfter(' ').split(",").map(String::toInt).toList() }
            .map { (mask, groups) ->
                if (part == QuizPart.A) mask to groups
                else List(5) { mask }.joinToString("?") to List(5) { groups }.flatten()
            }
            .sumOf { (mask, groups) ->
                val totalHashes = groups.sum()
                val filter = mask.replace(".", "\\.").replace("?", ".").toRegex()
                val r = countPossibilities2(mask,  groups).toBigInteger()
//                val r = countPossibilities(mask, 0, groups, totalHashes, filter).toLong()
//                val b = bruteForce(mask, groups, filter)
//                if (r != b) {
//                    println("-------------------------------")
//                    println("$mask ${groups.joinToString(",")}")
//                    println("algo   => $r")
//                    println("force  => $b")
//                }
                r
            }
    }

    private val cache = mutableMapOf<Pair<String,String>,Int>()
    private val cache2 = mutableMapOf<String,Long>()
    private val groupChars = "?#".toCharArray()

//    private fun bruteForce(input: String, groups: List<Int>, filter: Regex): Int {
//        return if (!input.contains('?')) {
//            if (filter.matchEntire(input) != null && input.split(".").filter { it.isNotEmpty() }.map { it.length }.toList() == groups) 1
//            else 0
//        } else {
//            bruteForce(input.replaceFirst('?', '.'), groups, filter) + bruteForce(input.replaceFirst('?', '#'), groups, filter)
//        }
//    }

    private fun countPossibilities2(mask: String, groups: List<Int>): Long {
        return if (mask.isEmpty())
            if (groups.isEmpty()) 1L else 0L
        else {
            val cacheKey = mask + groups.joinToString(",")
            cache2[cacheKey] ?: when(mask[0]) {
                '.' -> countPossibilities2(mask.substring(1), groups)
                '#' -> consumeGroup(mask, groups)
                '?' -> consumeGroup(mask, groups) + countPossibilities2(mask.substring(1), groups)
                else -> throw Exception("unexpected char...")
            }.also {
                cache2[cacheKey] = it
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
                countPossibilities2(mask.substring(group+1), groups.drop(1))
            else 0L
        }
    }

    private fun countPossibilities(mask: String, start: Int, groups: List<Int>, totalHash: Int, filter: Regex): Int {
        val groupStart = mask.indexOfAny(groupChars, start)
        if (groupStart == -1) {
            if (groups.isEmpty()) {
//                println("$mask ${groups.joinToString(",")}  @$start -> 1 (no group found and nothing to place left)")
                return 1
            } else {
//                println("$mask ${groups.joinToString(",")}  @$start -> 0 (no group found but still need to place something)")
                return 0
            }
        }
        val groupEnd = mask.indexOf('.', groupStart).let { if (it == -1) mask.length else it }
        if (groups.isEmpty()) {
            if (mask.substring(groupStart, groupEnd).contains("#")) {
//                println("$mask ${groups.joinToString(",")}  @$start -> 0 (nothing to place left but group contains #)")
                return 0
            } else {
//                println("$mask ${groups.joinToString(",")}  @$start -> 1 (nothing to place left and group can be filled with dots)")
                return 1
            }
//        val groupStart = mask.lastIndexOfAny(groupDelimiters, firstQ)
        }
        val maxGroupLength = groupEnd - groupStart
        val dotsToPlace = mask.length - totalHash - mask.count { it == '.' }
        val group = groups.first()
        return (
                if (maxGroupLength <= dotsToPlace) {
                    val replacement = ".".repeat(maxGroupLength)
                    val tailMask = "${mask.substring(0, groupStart)}${replacement}${mask.substring(groupStart+replacement.length)}"

                    if (filter.matchEntire(tailMask) == null || tailMask.count { it == '#' || it == '?' } < totalHash) {
//                        println("$tailMask ${groups.joinToString(",")}  @${groupStart + replacement.length} -> 0 (does not match pattern / maximum reachable # count too low)")
                        0
                    } else {
                        val cacheKey = tailMask.substring(groupStart + replacement.length) to groups.joinToString(",")
                        cache[cacheKey] ?: countPossibilities(tailMask, groupStart + replacement.length, groups, totalHash, filter).also { cache[cacheKey] = it }
                    }
                } else 0
            ) + if (group <= maxGroupLength) {
            val potentialDotsInFront = max(min(dotsToPlace, maxGroupLength), 0)
                .let {
                    if (mask[groupStart] == '#') 0 else it // if we have a hash in front of the first question mark, we can't place a dot there
                }
            (0 until potentialDotsInFront + 1)
                .sumOf { dots ->
                    val replacement = "${".".repeat(dots)}${"#".repeat(group)}".let { if (it.length < maxGroupLength) "$it." else it }
                    val tailMask = "${mask.substring(0, groupStart)}${replacement}${mask.substring(groupStart+replacement.length)}"
                    val tailGroup = groups.drop(1)

                    if (filter.matchEntire(tailMask) == null || tailMask.count { it == '#' || it == '?' } < totalHash || tailMask.count { it == '#' } > totalHash) {
//                        println("$tailMask ${tailGroup.joinToString(",")}  @${groupStart + replacement.length} -> 0 (does not match pattern / maximum reachable # count too low)")
                        0
                    } else {
                        val cacheKey = tailMask.substring(groupStart + replacement.length) to tailGroup.joinToString(",")
                        cache[cacheKey] ?: countPossibilities(tailMask, groupStart + replacement.length, tailGroup, totalHash, filter).also { cache[cacheKey] = it }
                    }
                }
        } else 0
//        return groups.toSet().sumOf { group ->
//            if (group <= maxGroupLength) {
//                val potentialDotsInFront = max(min(dotsToPlace, maxGroupLength - group), 0)
//                    .let {
//                        if (firstQ != groupStart) 0 else it // if we have a hash in front of the first question mark, we can't place a dot there
//                    }
//                (0 until potentialDotsInFront + 1)
//                    .sumOf { dots ->
//                        val replacement = "${".".repeat(dots)}${"#".repeat(group)}".let { if (it.length < maxGroupLength) "$it." else it }
//                        val tailMask = "${mask.substring(0, groupStart)}${replacement}${mask.substring(groupStart+replacement.length)}"
//                        val groupIndex = groups.indexOf(group)
//                        val tailGroup =  groups.filterIndexed { i, _ ->  i != groupIndex}
//                        countPossibilities(tailMask, tailGroup, totalHash, filter)
//                    }
//            } else 0
//        }
    }
}

fun <T> allPermutations(set: List<T>): Set<List<T>> {
    if (set.isEmpty()) return emptySet()

    fun <T> _allPermutations(list: List<T>): Set<List<T>> {
        if (list.isEmpty()) return setOf(emptyList())

        val result: MutableSet<List<T>> = mutableSetOf()
        for (i in list.indices) {
            _allPermutations(list - list[i]).forEach {
                    item -> result.add(item + list[i])
            }
        }
        return result
    }

    return _allPermutations(set)
}
