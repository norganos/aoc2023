package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import jakarta.inject.Singleton

@Singleton
class Day10: AbstractLinesAdventDay<Int>() {
    override val day = 10

    data class Info(
        val connections: String,
        val start: Boolean = false,
        val distance: Int = -1,
        val connected: Boolean = false
    )

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        var start: Point? = null
        val grid = Grid.parse(lines) { pos, char ->
            when (char) {
                '.' -> null
                '|' -> Info("NS")
                '-' -> Info("EW")
                'L' -> Info("NE")
                'J' -> Info("NW")
                '7' -> Info("SW")
                'F' -> Info("SE")
                'S' -> {
                    start = pos
                    Info("NEWS", true, 0)
                }
                else -> throw Exception("unknown char $char")
            }
        }
        dijkstra(grid, start!!)
        return if (part == QuizPart.A) {
            grid.getAllData().maxOf { it.data.distance }
        } else {
            countIslands(grid, start!!)
        }
    }

    private val traversals = listOf(
        Traversal(Vector(1, 0), 'E', 'W', Vector(0, 1), Vector(0, -1)),
        Traversal(Vector(0, 1), 'S', 'N', Vector(-1, 0), Vector(1, 0)),
        Traversal(Vector(-1, 0), 'W', 'E', Vector(0, -1), Vector(0, 1)),
        Traversal(Vector(0, -1), 'N', 'S', Vector(1, 0), Vector(-1, 0))
    )

    private fun getNext(grid: Grid<Info>, pos: Point): List<Point> {
        return traversals
            .asSequence()
            .filter { t -> t.ownConnection in (grid[pos]?.connections ?: "") }
            .map { it to pos + it.vector }
            .filter { (_, p) -> p in grid }
            .filter { (t, p) -> t.otherConnection in (grid[p]?.connections ?: "") }
            .map { (_, p) -> p }
            .toList()
    }

    data class Traversal(
        val vector: Vector,
        val ownConnection: Char,
        val otherConnection: Char,
        val righthand: Vector,
        val lefthand: Vector
    )

    fun dijkstra(grid: Grid<Info>, start: Point) {
        val queue = mutableListOf(start)
        while (queue.isNotEmpty()) {
            val point = queue.removeFirst()
            val info = grid[point]!!
            getNext(grid, point)
                .filter { grid[it]?.connected == false }
                .forEach {
                    grid[it] = grid[it]!!.copy(connected = true, distance = info.distance + 1)
                    queue.add(it)
                }
        }
    }

    fun countIslands(grid: Grid<Info>, start: Point): Int {
        grid.filterData { pos, data -> !data.connected }
            .forEach { grid[it.point] = null }
        val sides = mutableMapOf(
            Side.L to SideArea(),
            Side.R to SideArea()
        )
        val end = getNext(grid, start).last()
        var pos = start
        var prev = end
        while (pos != end) {
            val info = grid[pos]!!
            val (traversal, next) = traversals
                .filter { t -> t.ownConnection in info.connections }
                .asSequence()
                .map { it to pos + it.vector }
                .filter { (_, p) -> p in grid }
                .filter { (t, p) -> t.otherConnection in (grid[p]?.connections ?: "") }
                .first { (_, p) -> p != prev }
            listOf(
                Pair(Side.L, pos + traversal.lefthand),
                Pair(Side.L, next + traversal.lefthand),
                Pair(Side.R, pos + traversal.righthand),
                Pair(Side.R, next + traversal.righthand)
            )
                .filter { (_, p) -> p in grid }
                .filter { (_, p) -> grid[p] == null }
                .forEach { (side, p) ->
                    val area = sides[side]!!
                    if (p !in area.points) {
                        sides[side] = area.copy(
                            points = area.points + mapOf(p to 0)
                        )
                    }
                }
            prev = pos
            pos = next
        }
        return sides
            .mapValues { expandArea(grid, it.value) }
            .also { m ->
                m.entries.forEach { e ->
                    println("${e.key} has ${e.value.points.size} points with ${e.value.points.values.max()} max distance and is ${if (e.value.outside) "outside" else "inside"}")
                }
            }
            .values
            .filter { !it.outside }
            .sumOf { it.points.size }
    }

    fun expandArea(grid: Grid<Info>, area: SideArea): SideArea {
        var outside = area.outside
        val points = area.points.toMutableMap()
        val queue = area.points.entries.sortedBy { it.value }.map { it.key }.toMutableList()
        println("area started with ${points}")
        while (queue.isNotEmpty()) {
            val point = queue.removeFirst()
            val distance = points[point]!!
            traversals
                .asSequence()
                .map { point + it.vector }
                .forEach { p ->
                    if (p !in grid) {
                        outside = true
                    } else if (grid[p] == null && p !in points) {
                        points[p] = distance + 1
                        queue.add(p)
                    }
                }
        }
        println("area expanded to ${outside} with ${points}")
        return SideArea(
            outside = outside,
            points = points
        )
    }

    enum class Side { L, R }
    data class SideArea(
        val outside: Boolean = false,
        val points: Map<Point, Int> = emptyMap()
    )

}
