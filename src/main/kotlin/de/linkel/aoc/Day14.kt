package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Area
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import jakarta.inject.Singleton
import kotlin.math.abs

@Singleton
class Day14: AbstractLinesAdventDay<Int>() {
    override val day = 14

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        val grid = Grid.parse(lines) { pos, char ->
            char.takeIf { it != '.' }
        }
        var platform = Platform(
            width = grid.width,
            height = grid.height,
            rollers = grid.getAllData()
                .filter { it.data == 'O' }
                .map { it.point }
                .toSet(),
            blockers = grid.getAllData()
                .filter { it.data == '#' }
                .map { it.point }
                .toSet()
        )
        if (part == QuizPart.A) {
            platform = platform.tilt(Platform.NORTH)
        } else {
            val lastIterations = mutableListOf<Int>()
            var i = 0
            while (i < 1_000_000_000) {
                platform = platform
                    .tilt(Platform.NORTH)
                    .tilt(Platform.WEST)
                    .tilt(Platform.SOUTH)
                    .tilt(Platform.EAST)
                if (platform.rollers.hashCode() in lastIterations) {
                    val cycleLength = lastIterations.indexOf(platform.rollers.hashCode()) + 1
                    println("in lastIterations at pos $cycleLength")
                    while (i + cycleLength < 1_000_000_000) {
                        i += cycleLength
                    }
                    println(" > fast-forward to iteration $i")

                    lastIterations.clear()
                }
                lastIterations.add(0, platform.rollers.hashCode())
                if (lastIterations.size > 500) {
                    lastIterations.removeAt(500)
                }
                i++
            }
        }
        return platform.rollers
            .sumOf { platform.height - it.y }
    }

    data class Platform(
        val width: Int,
        val height: Int,
        val rollers: Set<Point>,
        val blockers: Set<Point>
    ) {
        companion object {
            val NORTH = Vector(0, -1)
            val WEST = Vector(-1, 0)
            val SOUTH = Vector(0, 1)
            val EAST = Vector(1, 0)
        }

        val area = Area(0, 0, width, height)

        fun tilt(direction: Vector): Platform {
            val points = rollers
                .toMutableSet()
            val stoppedRollers = mutableSetOf<Point>()

            val (edge, selector) = when (direction) {
                NORTH -> 0 to { it: Point -> it.y }
                SOUTH -> height to { it: Point -> it.y }
                WEST -> 0 to { it: Point -> it.x }
                EAST -> width to { it: Point -> it.x }
                else -> throw Exception("invalid direction")
            }
            while (points.isNotEmpty()) {
                points.toList()
                    .sortedBy { abs(edge - selector(it)) }
                    .forEach { pos ->
                        val dest = pos + direction
                        points.remove(pos)
                        if (dest in area && dest !in blockers && dest !in stoppedRollers && dest !in points) {
                            points.add(dest)
                        } else {
                            stoppedRollers.add(pos)
                        }
                    }
            }
            return copy(
                rollers = stoppedRollers
            )
        }

        override fun toString(): String {
            return buildString {
                (0 until height).forEach { y ->
                    (0 until width).forEach { x ->
                        val p = Point(x, y)
                        if (p in blockers) {
                            append('#')
                        } else if (p in rollers) {
                            append('O')
                        } else {
                            append('.')
                        }
                    }
                    append('\n')
                }
            }
        }
    }

}
