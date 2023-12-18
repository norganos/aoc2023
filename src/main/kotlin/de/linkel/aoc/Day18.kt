package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import jakarta.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class Day18: AbstractLinesAdventDay<Long>() {
    override val day = 18

    data class Cube(
        val dirIn: Char,
        val dirOut: Char
    )
    data class Point(
        val x: Long,
        val y: Long
    )
    interface Vector {
        val deltaX: Long
        val deltaY: Long
    }
    enum class Direction(
        override val deltaX: Long,
        override val deltaY: Long
    ): Vector {
        R(1, 0),
        D(0, 1),
        L(-1, 0),
        U(0, -1)
    }
    data class GenericVector(
        override val deltaX: Long,
        override val deltaY: Long
    ): Vector
    operator fun Point.plus(vector: Vector): Point = Point(this.x + vector.deltaX, this.y + vector.deltaY)
    operator fun Vector.times(factor: Long): Vector = GenericVector(this.deltaX * factor, this.deltaY * factor)

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        val origin = Point(0L,0L)
        val map = mutableMapOf<Point, Cube>()
        var minX = 0L
        var minY = 0L
        var maxX = 0L
        var maxY = 0L
//        val map = Grid<Cube>(origin, Dimension(1,1))
        map[origin] = Cube('0','?')
        lines.fold(origin) { pos, line ->
            val (dir, len) = if (part == QuizPart.A) {
                val token = line.split(' ')
                val dir = Direction.valueOf(token[0])
                val len = token[1].toLong()
                dir to len
            } else {
                val hex = line.substringAfter('#').substringBefore(')')
                val len = hex.substring(0, 5).toLong(16)
                val dir = Direction.entries[hex[5].digitToInt()]
                dir to len
            }
            val dest = pos + dir * len
//            map.stretchTo(dest)
            (1L..len).forEach {
                map[pos + dir * it] = Cube(line[0], line[0])
            }
            minX = min(minX, dest.x)
            minY = min(minY, dest.y)
            maxX = max(maxX, dest.x)
            maxY = max(maxY, dest.y)
            map[pos] = map[pos]!!.copy(dirOut = line[0])
            dest
        }

        var insides = 0L
        (minY..maxY)
            .forEach { y ->
//                var upIn = false
//                var downOut = false
                var inside = false
                (minX..maxX)
                    .forEach { x ->
                        val p = Point(x, y)
                        if (map[p] != null) {
//                            if (map[p]!!.dirIn == 'D' || map[p]!!.dirOut == 'U')
                            if (map[p]!!.dirIn == 'U' || map[p]!!.dirOut == 'D')
                                inside = !inside
//                            if (map[p]!!.dirIn == 'U')
//                                upIn = !upIn
//                            if (map[p]!!.dirOut == 'D')
//                                downOut = !downOut
                        } else if (inside) {
//                        } else if (upIn != downOut) {
                            insides++
//                            map[p] = Cube('#', '#')
                        }
                    }
            }

        return insides + map.size
    }
    fun printMap(grid: Grid<Cube>) {
        println(
            buildString {
                (grid.area.y until (grid.area.y + grid.area.height))
                    .forEach { y ->
                        (grid.area.x until (grid.area.x + grid.area.width))
                            .forEach { x ->
                                val p = Point(x, y)
                                if (grid[p] != null) {
                                    append(grid[p]!!.dirIn)
                                } else {
                                    append('.')
                                }
                            }
                        append('\n')
                    }
            }
        )
    }
}
