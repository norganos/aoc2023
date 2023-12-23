package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Area
import de.linkel.aoc.utils.grid.Point
import jakarta.inject.Singleton
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.collections.filter
import kotlin.collections.first
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.last
import kotlin.collections.lastIndex
import kotlin.collections.listOf
import kotlin.collections.mutableSetOf
import kotlin.collections.plus
import kotlin.collections.setOf
import kotlin.collections.sumOf
import kotlin.math.max

@Singleton
class Day23: AbstractLinesAdventDay<Int>() {
    override val day = 23

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        val map = lines.toList()
        val start = map.first().indexOf('.') to 0
        val end = map.last().indexOf('.') to map.lastIndex

        return if (part == QuizPart.A) longestPath(map, start, end)
        else {
            val area = Area(
                x = 0,
                y = 0,
                width = map.first().length,
                height = map.size
            )

            val startPoint = Point(start.first, start.second)
            val endPoint = Point(end.first, end.second)
            val segments = extractSegments(area, map, startPoint, endPoint)
            longestPath(segments, startPoint to segments.first { it.start == startPoint }, segments.first { it.end == endPoint })
        }
    }

    fun longestPath(map: List<String>, start: Pair<Int, Int>, end: Pair<Int, Int>): Int {
        val xRange = 0 until map.first().length
        val yRange = 0 until map.size
        val queue = Stack<List<Pair<Int,Int>>>()
        queue.push(listOf(start))
        var max = 0
        while (queue.isNotEmpty()) {
            val path = queue.pop()
            val pos = path.last()
            if (pos == end) {
                max = max(max, path.size)
            } else when (map[pos.second][pos.first]) {
                    '>' -> listOf((pos.first + 1) to pos.second)
                    '<' -> listOf((pos.first - 1) to pos.second)
                    '^' -> listOf(pos.first to (pos.second - 1))
                    'v' -> listOf(pos.first to (pos.second + 1))
                    else -> listOf(
                        (pos.first + 1) to pos.second,
                        (pos.first - 1) to pos.second,
                        pos.first to (pos.second + 1),
                        pos.first to (pos.second - 1),
                    )
                }
                .filter { it.first in xRange && it.second in yRange }
                .filter { map[it.second][it.first] != '#' }
                .filter { it !in path }
                .forEach {
                    queue.push(path + it)
                }
        }
        return max - 1
    }

    data class Segment(
        val start: Point,
        val end: Point,
        val length: Int
    ) {
        fun other(point: Point) = if (point == start) end else start
    }

    fun extractSegments(area: Area, map: List<String>, initStart: Point, finalEnd: Point): Set<Segment> {
        val queue = ArrayDeque<Pair<Point, Point>>()
        queue.add(initStart to initStart)
        val visited = mutableSetOf(initStart)
        val result = mutableSetOf<Segment>()
        while (queue.isNotEmpty()) {
            var (start, pos) = queue.removeFirst()
            var steps = 0
            while (true) {
                visited.add(pos)
                if (pos != start) {
                    steps++
                }
                val rawNeighbors = listOf(
                        pos + NORTH,
                        pos + WEST,
                        pos + EAST,
                        pos + SOUTH
                    )
                    .filter { it in area }
                    .filter { map[it.y][it.x] != '#' }

                if (pos != initStart && rawNeighbors.size == 1) {
                    if (pos == finalEnd) {
                        result.add(Segment(start, pos, steps))
                    }
                    break
                }

                val neighbors = rawNeighbors
                    .filter { it !in visited }

                if (neighbors.size == 1) {
                    pos = neighbors.first()
                } else {
                    result.add(Segment(start, pos, steps))
                    neighbors
                        .forEach {
                            queue.addLast(pos to it)
                        }
                    break
                }
            }
        }
        return result
    }

    fun longestPath(graph: Set<Segment>, start: Pair<Point, Segment>, end: Segment): Int {
        data class State(val point: Point, val path: List<Segment>, val visited: Set<Point>)

        val queue = Stack<State>()
        queue.push(State(start.first, listOf(start.second), setOf(start.second.start, start.second.end)))
        var max = 0

        while (queue.isNotEmpty()) {
            val (pos, path, blacklist) = queue.pop()
            val segment = path.last()
            if (segment == end) {
                val len = path.sumOf { it.length }
                if (len > max) {
                    max = len
                    println("zwischenergebnis $max")
                }
            } else {
                val endPoint = segment.other(pos)
                graph
                    .filter { it.start == endPoint || it.end == endPoint }
                    .filter { it !in path }
                    .filter { it.other(endPoint) !in blacklist }
                    .forEach {
                        queue.push(State(endPoint,path + it, blacklist + it.other(endPoint)))
                    }
            }
        }
        return max
    }
}
