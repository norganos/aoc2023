package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Area
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import de.linkel.aoc.utils.iterables.combineWith
import jakarta.inject.Singleton
import kotlin.math.abs

@Singleton
class Day14: AbstractLinesAdventDay<Int>() {
    override val day = 14

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        val grid = Grid.parse(lines) { pos, char ->
            char.takeIf { it != '.' }
        }
        var platformState = PlatformState(
            platform = Platform(
                width = grid.width,
                height = grid.height,
                blockers = grid.getAllData()
                    .filter { it.data == '#' }
                    .map { it.point }
                    .toSet()
            ),
            rollers = grid.getAllData()
                .filter { it.data == 'O' }
                .map { it.point }
                .toSet(),
        )

        if (part == QuizPart.A) {
            platformState = platformState.tilt(Platform.NORTH)
        } else {

            val lastIterations = mutableListOf<Set<Point>>()
            val lastIterations2 = mutableListOf<Int>()
            var i = 0
            while (i < 1_000_000_000) {
                platformState = platformState
                    .tilt(Platform.NORTH)
                    .tilt(Platform.WEST)
                    .tilt(Platform.SOUTH)
                    .tilt(Platform.EAST)
                if (platformState.rollers.hashCode() in lastIterations2) {
                    val idx = lastIterations2.indexOf(platformState.rollers.hashCode())
                    val other = lastIterations[idx]
                    println("rollers after iteration $i and the one $idx iterations ago have hashCode ${platformState.rollers.hashCode()}")
                    println("   are equal: ${platformState.rollers == other}")
                    if (platformState.rollers != other) {
                        println("    + ${platformState.rollers.filter { it !in other }.sortedWith(compareBy({ it.y }, {it.x})) }}")
                        platformState.rollers.filter { it !in other }.sortedWith(compareBy({ it.y }, {it.x})).forEach {
                            println("        $it ${it.hashCode()}")
                        }
                        println("    - ${other.filter { it !in platformState.rollers }.sortedWith(compareBy({ it.y }, {it.x})) }}")
                        other.filter { it !in platformState.rollers }.sortedWith(compareBy({ it.y }, {it.x})).forEach {
                            println("        $it ${it.hashCode()}")
                        }
                    }
                }
                if (platformState.rollers in lastIterations) {
                    val cycleLength = lastIterations.indexOf(platformState.rollers) + 1
                    println("result from $i found in lastIterations at pos $cycleLength")
                    while (i + cycleLength < 1_000_000_000) {
                        i += cycleLength
                    }
                    println(" > fast-forward to iteration $i")

                    lastIterations.clear()
                    lastIterations2.clear()
                }
                lastIterations.add(0, platformState.rollers)
                if (lastIterations.size > 500) {
                    lastIterations.removeAt(500)
                }
                lastIterations2.add(0, platformState.rollers.hashCode())
                if (lastIterations2.size > 500) {
                    lastIterations2.removeAt(500)
                }
                i++
            }
        }
        return platformState.rollers
            .sumOf { platformState.platform.height - it.y }
    }

    data class Platform(
        val width: Int,
        val height: Int,
        val blockers: Set<Point>
    ) {
        companion object {
            val NORTH = Vector(0, -1)
            val WEST = Vector(-1, 0)
            val SOUTH = Vector(0, 1)
            val EAST = Vector(1, 0)
        }

        val area = Area(0, 0, width, height)

//        val horizontalRollZones = (0 until height)
//            .flatMap { y ->
//                (blockers.filter { it.y == y } + setOf(Point(width, y)))
//                    .map { it.x }
//                    .fold(-1 to emptyList<IntRange>()) { (lastX, list), x ->
//                        x to (list + listOf((lastX+1 ) until x))
//                    }
//                    .second
//                    .filter { !it.isEmpty() }
//                    .map {
//                        RollZone(it, (y..y))
//                    }
//            }
//        val verticalRollZones = (0 until width)
//            .flatMap { x ->
//                (blockers.filter { it.x == x } + setOf(Point(x, height)))
//                    .map { it.y }
//                    .fold(-1 to emptyList<IntRange>()) { (lastY, list), y ->
//                        y to (list + listOf((lastY+1 ) until y))
//                    }
//                    .second
//                    .filter { !it.isEmpty() }
//                    .map {
//                        RollZone((x..x), it)
//                    }
//            }
    }

//    data class RollZone(
//        val xx: IntRange,
//        val yy: IntRange
//    ) {
//        val points = yy.toList().combineWith(xx.toList())
//            .map { (y, x) -> Point(x, y) }.toList()
//        fun take(n: Int, reversed: Boolean): List<Point> {
//            return if (reversed) points.takeLast(n)
//                else points.take(n)
//        }
//
//        operator fun contains(point: Point): Boolean = point.x in xx && point.y in yy
//    }

    data class PlatformState(
        val platform: Platform,
        val rollers: Set<Point>
    ) {
//        fun tilt(direction: Vector): PlatformState {
//            val (zones, reversed) = when (direction) {
//                Platform.NORTH -> platform.verticalRollZones to false
//                Platform.SOUTH -> platform.verticalRollZones to true
//                Platform.WEST -> platform.horizontalRollZones to false
//                Platform.EAST -> platform.horizontalRollZones to true
//                else -> throw Exception("invalid direction")
//            }
//            return copy(
//                rollers = zones.flatMap { zone ->
//                    zone.take(
//                        rollers.count { it in zone },
//                        reversed)
//                }
//                    .toSet()
//            )
//        }
        fun tilt(direction: Vector): PlatformState {
            val points = rollers
                .toMutableSet()
            val stoppedRollers = mutableSetOf<Point>()

            val (edge, selector) = when (direction) {
                Platform.NORTH -> 0 to { it: Point -> it.y }
                Platform.SOUTH -> platform.height to { it: Point -> it.y }
                Platform.WEST -> 0 to { it: Point -> it.x }
                Platform.EAST -> platform.width to { it: Point -> it.x }
                else -> throw Exception("invalid direction")
            }
            while (points.isNotEmpty()) {
                points.toList()
                    .sortedBy { abs(edge - selector(it)) }
                    .forEach { pos ->
                        val dest = pos + direction
                        points.remove(pos)
                        if (dest in platform.area && dest !in platform.blockers && dest !in stoppedRollers && dest !in points) {
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
                (0 until platform.height).forEach { y ->
                    (0 until platform.width).forEach { x ->
                        val p = Point(x, y)
                        if (p in platform.blockers) {
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
