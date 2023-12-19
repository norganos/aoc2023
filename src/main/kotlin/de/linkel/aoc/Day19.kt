package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.iterables.intersect
import jakarta.inject.Singleton

@Singleton
class Day19: AbstractLinesAdventDay<Long>() {
    override val day = 19

    enum class Op(val char: Char, val lambda: (value: Int, threshold: Int) -> Boolean) {
        GT('>', { v, t -> v > t }),
        LT('<', { v, t -> v < t })
    }

    data class Rule(
        val prop: String,
        val op: Op,
        val threshold: Int,
        val dest: String
    ) {
        fun matches(part: Map<String,Int>): Boolean = op.lambda(part[prop]!!, threshold)

        fun validRange(): IntRange {
            return when(op) {
                Op.GT -> (threshold + 1)..4000
                Op.LT -> 1..<threshold
            }
        }
        fun invalidRange(): IntRange {
            return when(op) {
                Op.GT -> 1..(threshold)
                Op.LT -> (threshold)..4000
            }
        }
    }

    data class Workflow(
        val rules: List<Rule>,
        val otherwise: String
    ) {
        fun process(part: Map<String,Int>): String {
            for (rule in rules) {
                if (rule.matches(part))
                    return rule.dest
            }
            return otherwise
        }
    }

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        val iterator = lines.iterator()
        val rulePattern = Regex("([xmas])([<>])([0-9]+):([a-zAR]+)")
        val workflows = iterator
            .asSequence()
            .takeWhile { it.isNotEmpty() }
            .associate { line ->
                val name = line.substringBefore("{")
                val ruleStrings = line.substringAfter("{").substringBefore("}").split(",")
                val rules = ruleStrings.dropLast(1)
                    .map { token ->
                        val match = rulePattern.matchEntire(token) ?: throw Exception("$token does not match rule regex")
                        Rule(match.groupValues[1], Op.entries.first { it.char == match.groupValues[2][0]}, match.groupValues[3].toInt(), match.groupValues[4])
                    }
                val otherwise = ruleStrings.last()
                name to Workflow(rules, otherwise)
            }
        return if (part == QuizPart.A) {
                iterator
                .asSequence()
                .map { line ->
                    line.substring(1, line.length - 1).split(",")
                        .associate { it.substringBefore("=") to it.substringAfter("=").toInt() }
                }
                .map { machinePart ->
                    var dest = "in"
                    while (dest != "A" && dest != "R") {
                        dest = workflows[dest]!!.process(machinePart)
    //                    println ("$machinePart to $dest")
                    }
                    machinePart to dest
                }
                .filter { (_, dest) -> dest == "A" }
                .sumOf { (machinePart, _) -> machinePart.values.sum() }
                .toLong()
        } else {
            searchPathes(workflows, listOf("in"), mapOf("x" to 1..4000, "m" to 1..4000, "a" to 1..4000, "s" to 1..4000))
                .sumOf {
                    vc -> vc.values.fold(1L) { p, r -> p * (r.last - r.first + 1).toLong() }
                }
        }
    }
    fun Map<String,IntRange>.constraintBy(rule: Rule): Map<String,IntRange> {
        val base = this
        return buildMap {
            putAll(base)
            if (base.containsKey(rule.prop)) {
                put(rule.prop, base[rule.prop]!!.intersect(rule.validRange()))
            } else {
                put(rule.prop, rule.validRange())
            }
        }
    }
    fun Map<String,IntRange>.constraintByNot(rule: Rule): Map<String,IntRange> {
        val base = this
        return buildMap {
            putAll(base)
            if (base.containsKey(rule.prop)) {
                put(rule.prop, base[rule.prop]!!.intersect(rule.invalidRange()))
            } else {
                put(rule.prop, rule.invalidRange())
            }
        }
    }

    fun searchPathes(workflows: Map<String, Workflow>, path: List<String>, constraints: Map<String,IntRange>): Set<Map<String, IntRange>> {
        return if (path.last() == "A") {
            setOf(constraints)
        } else if (constraints.any { it.value.isEmpty() }) {
            emptySet()
        } else if (path.last() == "R") {
            emptySet()
        } else {
            buildSet {
                val wf = workflows[path.last()] ?: throw Exception("workflow ${path.last()} not found")
                var otherwise = constraints
                for (r in wf.rules) {
                    addAll(searchPathes(workflows, path + r.dest, otherwise.constraintBy(r)))
                    otherwise = otherwise.constraintByNot(r)
                }
                addAll(searchPathes(workflows, path + wf.otherwise, otherwise))
            }
        }
    }
}
