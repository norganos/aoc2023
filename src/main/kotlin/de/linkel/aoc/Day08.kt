package de.linkel.aoc

import com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator
import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class Day08: AbstractLinesAdventDay<Long>() {
    override val day = 8

    data class NodeDesc(val left: String, val right: String)

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        val pattern = Regex("([A-Z0-9]+) = \\(([A-Z0-9]+), ([A-Z0-9]+)\\)")
        val iterator = lines.iterator()
        val instr = iterator.next()
        assert(iterator.next() == "")
        val nodes = iterator.asSequence()
            .mapNotNull { pattern.matchEntire(it) }
            .map { it.groupValues[1] to NodeDesc(it.groupValues[2], it.groupValues[3]) }
            .toMap()

        return if (part == QuizPart.A) {
            countSteps(nodes, "AAA", setOf("ZZZ"), instr)
        } else {
            val end = nodes.keys
                .filter { it.endsWith("Z") }
                .toSet()
            nodes.keys
                .filter { it.endsWith("A") }
                .also { println("start: ${it.joinToString(", ")}") }
                .map { countSteps(nodes, it, end, instr) }
                .also { println("steps: ${it.joinToString(", ")}") }
                .reduce(::lcm)
        }
    }

    private fun countSteps(nodes: Map<String, NodeDesc>, start: String, end: Set<String>, instr: String): Long {
        var steps = 0L
        var pos = start
        while (pos !in end) {
            val now = nodes[pos]!!
            pos = if (instr[(steps % instr.length).toInt()] == 'L') now.left else now.right
            steps++
        }
        return steps
    }

    private fun lcm(a: Long, b: Long): Long {
        var lcm = max(a, b)

        while (true) {
            if (lcm % a == 0L && lcm % b == 0L) {
                break
            }
            lcm += max(a, b)
        }
        return lcm
    }
}
