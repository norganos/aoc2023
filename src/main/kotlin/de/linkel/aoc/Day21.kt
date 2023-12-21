package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Area
import de.linkel.aoc.utils.grid.Point
import jakarta.inject.Singleton
import java.util.*

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
            dijkstra(area, rocks, start, if (width < 15) 6 else 64)
        else {
            assert(width == height)
            assert(width % 2 == 1)
            assert(height % 2 == 1)
            val max = if (width < 15) 5000 else 26501365
            val step = width * 2
            val rem = max % step

            val probe = 5
            (0..probe)
                .map { i -> dijkstra(area, rocks, start, step * i + rem) }
                .toSeq()
                .prepare()
                .toExtrapolation()
                .extrapolate((max - rem) / step - probe)
        }
    }

    fun dijkstra(area: Area, rocks: Set<Point>, start: Point, max: Int): Long {
        val queue = PriorityQueue<Pair<Point, Int>>(compareBy { it.second })
        queue.add(start to 0)
        val seen = mutableSetOf(start)
        var count = 0L
        while (queue.isNotEmpty()) {
            val (point, distance) = queue.remove()
            if (distance > max) {
                break
            }
            if (distance % 2 == max % 2) {
                count++
            }

            listOf(
                point + NORTH,
                point + WEST,
                point + SOUTH,
                point + EAST
            )
                .filter {
                    if (it in area)
                        it !in rocks
                    else {
                        val p = Point(it.x.mod(area.width), it.y.mod(area.height))
                        p in area && p !in rocks
                    }
                }
                .filter { it !in seen }
                .forEach {
                    queue.add(it to distance + 1)
                    seen.add(it)
                }
        }
        return count
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
