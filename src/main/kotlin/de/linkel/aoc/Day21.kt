package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Area
import de.linkel.aoc.utils.grid.Point
import jakarta.inject.Singleton
import java.util.*
import kotlin.time.measureTimedValue

@Singleton
class Day21: AbstractLinesAdventDay<Long>() {
    override val day = 21

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        var width = 0
        var height = 0
        var start = Point(0,0)
        val rocks = lines
            .flatMapIndexed { y, line ->
                if (width == 0) width = line.length
                if (height <= y) height = y + 1
                line
                    .mapIndexed { x, c ->
                        if (c == 'S')
                            start = Point(x, y)
                        if (c == '#')
                            Point(x, y)
                        else null
                    }
                    .filterNotNull()
            }
            .toSet()

        val area = Area(0, 0, width, height)
        return if (part == QuizPart.A)
            dijkstra(area, rocks, start, listOf(if (width < 15) 6 else 64)).first()
        else {
            assert(width == height)
            assert(width % 2 == 1)
            assert(height % 2 == 1)
            val max = if (width < 15) 5000 else 26501365
            val step = width * 2
            val rem = max % step

            val probe = 5
            val probed = measureTimedValue {
                dijkstra(area, rocks, start, (1..probe).map { i -> step * i + rem })
            }.let {
                println("finding took ${it.duration}")
                it.value
            }

            measureTimedValue {
                probed
                    .toSeq()
                    .prepare()
                    .toExtrapolation()
                    .extrapolate((max - rem) / step - probe)
            }.let {
                println("extrapolating took ${it.duration}")
                it.value
            }
        }
    }

    fun dijkstra(area: Area, rocks: Set<Point>, start: Point, max: List<Int>): List<Long> {
        val queue = PriorityQueue<Pair<Point, Int>>(compareBy { it.second })
        val result = mutableMapOf<Int,Long>()
        val saveSteps = max.map { it + 1 }.toMutableSet()
        queue.add(start to 0)
        val seen = mutableSetOf(start)
        var odd = 0L
        var even = 0L
        val maxMax = max.max()
        val dirs = listOf(
                NORTH,
                WEST,
                SOUTH,
                EAST
            ).asSequence()
        while (queue.isNotEmpty()) {
            val (point, distance) = queue.remove()
            if (distance in saveSteps) {
                result[distance-1] = if ((distance - 1) %2 == 0) even else odd
                saveSteps.remove(distance)
            }
            if (distance > maxMax) {
                break
            }
            if (distance % 2 == 0) even++ else odd++

            dirs
                .map { point + it }
                .filter {
                    Point(it.x.mod(area.width), it.y.mod(area.height)) !in rocks && it !in seen
                }
                .forEach {
                    queue.add(it to distance + 1)
                    seen.add(it)
                }
        }
        return max.map { result[it]!! }
    }

    private fun List<Long>.toSeq(): Seq = Seq(this)

    class Seq(
        input: List<Long>,
        val parent: Seq? = null
    ) {
        var values = input.toMutableList()
            private set

        fun prepare(): Seq {
            return if (values.last() == 0L) this
            else differentiate().prepare()
        }

        fun differentiate(): Seq {
            val diffs = values
                .windowed(2)
                .map { (a, b) -> b - a }
                .toList()
            if (diffs.isEmpty())
                throw Exception("not enough input iterations")
            return Seq(
                input = diffs,
                parent = this
            )
        }

        private fun lasts(): List<Long>
            = listOf(values.last()) + (parent?.lasts() ?: emptyList())

        fun toExtrapolation(): Extrapolation {
            return Extrapolation(lasts())
        }
    }

    class Extrapolation(
        val inputs: List<Long>
    ) {
        init {
            assert(inputs.first() == 0L)
        }

        fun extrapolate(steps: Int): Long {
            val vals = inputs.toMutableList()
            repeat(steps) {
                vals.indices.forEach { i ->
                    if (i < vals.lastIndex) {
                        vals[i+1] += vals[i]
                    }
                }
            }
            return vals.last()
        }
    }
}
