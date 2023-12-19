package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import kotlin.math.abs

@Singleton
class Day18: AbstractLinesAdventDay<Long>() {
    override val day = 18

    data class Point(
        val x: Long,
        val y: Long
    ) {
        override fun toString(): String = "${x}x${y}"
    }
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
    operator fun Vector.plus(vector: Vector): Vector = GenericVector(this.deltaX + vector.deltaX, this.deltaY + vector.deltaY)

    override fun process(part: QuizPart, lines: Sequence<String>): Long {
        return lines.map { line ->
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
            dir * len
        }.let {
            solve(it.toList())
        }
    }

    private fun points(digPlan: List<Vector>): List<Point> =
        digPlan.runningFold(Point(0, 0)) { acc, shiftVector ->
            acc + shiftVector
        }

    private fun perimeterPoints(vectors: List<Vector>): Long =
        vectors.sumOf { abs(it.deltaX) + abs(it.deltaY) } // Manhattan Distance is ok here because we only have 90 degree turns

    private fun shoelaceFormula(corners: List<Point>): Long =
        corners.zipWithNext().sumOf { (a, b) -> a.x * b.y - a.y * b.x } / 2

    private fun pickTheorem(area: Long, perimeter: Long): Long =
        area + perimeter / 2L + 1L

    private fun solve(digPlan: List<Vector>): Long
            = pickTheorem(shoelaceFormula(points(digPlan)), perimeterPoints(digPlan))
}
