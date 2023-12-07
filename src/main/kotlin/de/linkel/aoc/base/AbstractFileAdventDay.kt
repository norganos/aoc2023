package de.linkel.aoc.base

import java.io.BufferedReader
import java.io.File
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTimedValue

abstract class AbstractFileAdventDay<T>: AdventDay<T> {
    private val msFormat = DecimalFormat("#,##0.0")

    private fun getInput(part: QuizPart, args: List<String>): BufferedReader {
        return if (args.isNotEmpty())
            File(args.first()).bufferedReader()
        else {
            val resource = AbstractFileAdventDay::class.java.getResourceAsStream(String.format("/input%02d%s.txt", day, part.postfix)) ?: AbstractFileAdventDay::class.java.getResourceAsStream(String.format("/input%02d.txt", day))
            resource?.bufferedReader() ?: throw InputDataNotFoundException(day, part)
        }
    }

    override fun solve(part: QuizPart, args: List<String>): T {
        return getInput(part, args).use { reader ->
            callProcess(part, reader)
        }
    }

    override fun test(part: QuizPart, input: String): T {
        return input.reader().buffered(min(max(1, input.length), 1024)).use { reader ->
            callProcess(part, reader)
        }
    }

    private fun callProcess(part: QuizPart, reader: BufferedReader): T {
        println("solving AoC 2023 Day $day $part")
        val result = measureTimedValue {
            process(part, reader)
        }
        println("Solution is ${result.value}")
        println ("calculation took ${msFormat.format(result.duration.inWholeMicroseconds / 1000F)}ms")
        return result.value
    }

    protected abstract fun process(part: QuizPart, reader: BufferedReader): T

}
