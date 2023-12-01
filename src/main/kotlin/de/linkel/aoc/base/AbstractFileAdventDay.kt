package de.linkel.aoc.base

import java.io.BufferedReader
import java.io.File
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.math.min

abstract class AbstractFileAdventDay<T>: AdventDay<T> {
    private val msFormat = DecimalFormat("#,##0")

    companion object {
        fun from(args: List<String>, name1: String, name2: String): BufferedReader {
            return if (args.isNotEmpty())
                File(args.first()).bufferedReader()
            else {
                val resource = AbstractFileAdventDay::class.java.getResourceAsStream("/$name1") ?: AbstractFileAdventDay::class.java.getResourceAsStream("/$name2")
                resource?.bufferedReader() ?: throw Exception("could not find resource $name1 or $name2")
            }
        }
    }

    override fun solve(part: QuizPart, args: List<String>): T {
        return from(args, String.format("input%02d%s.txt", day, part.prefix), String.format("input%02d.txt", day)).use { reader ->
            callProcess(part, reader)
        }
    }

    fun test(part: QuizPart, input: String): T {
        return input.reader().buffered(min(input.length, 1024)).use { reader ->
            callProcess(part, reader)
        }
    }

    private fun callProcess(part: QuizPart, reader: BufferedReader): T {
        println("solving AoC 2023 Day $day $part")
        val start = System.currentTimeMillis()
        val solution = process(part, reader)
        val duration = System.currentTimeMillis() - start
        println("Solution is $solution")
        println ("calculation took ${msFormat.format(duration)}ms")
        return solution
    }

    protected abstract fun process(part: QuizPart, reader: BufferedReader): T
}
