package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import jakarta.inject.Singleton
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

@Singleton
class Day24: AbstractLinesAdventDay<Int>() {
    override val day = 24

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        val hailStones = lines
            .map { line ->
                val (x, y, z) = line.substringBefore("@")
                    .split(",")
                    .map { it.trim().toLong() }
                val (dx, dy, dz) = line.substringAfter("@")
                    .split(",")
                    .map { it.trim().toLong() }
                HailStone(
                    position = Point3d(x, y, z),
                    velocity = Vector3d(dx, dy, dz)
                )
            }
        return if (part == QuizPart.A) {
            val hailStoneProjections = hailStones
                .map { it.toProjection() }
                .toList()
            val xyRange = if (hailStoneProjections.size < 10) (7L..27L) else (200000000000000L..400000000000000L)
            hailStoneProjections
                .mapIndexed{ idx,hailStone ->
                    hailStoneProjections
                        .drop(idx + 1)
                        .asSequence()
                        .map { hailStone.intersection(it) to it }
                        .filter { (p, _) -> p != null }
                        .map { (p, o) -> p!! to o }
                        .filter { (p, _) -> p.x >= xyRange.first && p.x <= xyRange.last && p.y >= xyRange.first && p.y <= xyRange.last }
                        .filter { (p, _) -> hailStone.velocity.sameDir(p - hailStone.position) }
                        .filter { (p, o) -> o.velocity.sameDir(p - o.position) }
                        .count()
                }
                .sum()
        } else 0
    }

    /*  # gesucht: p: Projectile(position: Point3d, velocity: Vector3d)
        # gegeben: n hailStones h[0] bis h[n-1]
        #          kollision mit h[x] in t[x]

        t[?] > 0
        p.position + p.velocity * t[0] = h[0].position + h[0].velocity * t[0]
        p.position + p.velocity * t[1] = h[1].position + h[1].velocity * t[1]
        ...

        # 7 unbekannte, 3 gleichungen
        p.pos.x + p.vel.dx * t[0] = h[0].pos.x + h[0].vel.dx * t[0]
        p.pos.y + p.vel.dy * t[0] = h[0].pos.y + h[0].vel.dy * t[0]
        p.pos.z + p.vel.dz * t[0] = h[0].pos.z + h[0].vel.dz * t[0]

        # 7 unbekannte, 3 gleichungen
        p.pos.x + p.vel.dx * t[1] = h[1].pos.x + h[1].vel.dx * t[1]
        p.pos.y + p.vel.dy * t[1] = h[1].pos.y + h[1].vel.dy * t[1]
        p.pos.z + p.vel.dz * t[1] = h[1].pos.z + h[1].vel.dz * t[1]

        # jeder hagelstein erhöht gleichungen um 3, erhöht unbekannte aber nur um 1
        # hagelstein | unbekannte | gleichungen
        # 1          | 7          | 3
        # 2          | 8          | 6
        # 3          | 9          | 9
        # => nach 3 hagelkörnern sollte das system lösbar sein


     */

    data class Point2d(val x: Double, val y: Double) {
        operator fun plus(vector: Vector2d) = copy(x + vector.deltaX, y + vector.deltaY)
        operator fun minus(other: Point2d) = Vector2d(x - other.x, y - other.y)
        override fun toString(): String = "$x, $y"
    }
    data class Vector2d(val deltaX: Double, val deltaY: Double) {
        val length = sqrt(deltaX.pow(2) + deltaY.pow(2))
        operator fun times(factor: Double) = copy(deltaX = deltaX * factor, deltaY = deltaY * factor)
        operator fun div(divisor: Double) = copy(deltaX = deltaX / divisor, deltaY = deltaY / divisor)
        fun normalized() = if (length == 1.0) this else this / length
        fun det(other: Vector2d): Double = deltaX * other.deltaY - deltaY * other.deltaX
        fun sameDir(other: Vector2d) = other.deltaX.sign == deltaX.sign && other.deltaY.sign == deltaY.sign
        override fun toString(): String = "$deltaX, $deltaY"
    }
    data class Point3d(val x: Long, val y: Long, val z: Long) {
        operator fun plus(vector: Vector3d) = copy(x + vector.deltaX, y + vector.deltaY, z + vector.deltaZ)
        operator fun minus(other: Point3d) = Vector3d(x - other.x, y - other.y, z - other.z)
        override fun toString(): String = "$x, $y, $z"
        fun to2d() = Point2d(x.toDouble(), y.toDouble())
    }
    data class Vector3d(val deltaX: Long, val deltaY: Long, val deltaZ: Long) {
        operator fun times(factor: Long) = copy(deltaX = deltaX * factor, deltaY = deltaY * factor, deltaZ = deltaZ * factor)
        operator fun times(factor: Int) = copy(deltaX = deltaX * factor, deltaY = deltaY * factor, deltaZ = deltaZ * factor)
        fun det(other: Vector3d): Double = deltaX.toDouble() * other.deltaY.toDouble() - deltaY.toDouble() * other.deltaX.toDouble()
        override fun toString(): String = "$deltaX, $deltaY, $deltaZ"
        fun to2d() = Vector2d(deltaX.toDouble(), deltaY.toDouble())
    }

    private operator fun LongRange.contains(point: Point3d): Boolean = point.x in this && point.y in this

    data class HailStone(
        val position: Point3d,
        val velocity: Vector3d
    ) {
        fun toProjection(): HailStoneProjection = HailStoneProjection(position = position.to2d(), velocity = velocity.to2d())
        override fun toString(): String = "$position @ $velocity"
    }
    data class HailStoneProjection(
        val position: Point2d,
        val velocity: Vector2d
    ) {
        val velocityNormalized = velocity.normalized()
        fun intersection(other: HailStoneProjection): Point2d? {
            val p = position
            val r = velocityNormalized
            val q = other.position
            val s = other.velocityNormalized
            val pqx = q.x - position.x
            val pqy = q.y - position.y
            val rx = r.deltaX
            val ry = r.deltaY
            val rxt = -ry
            val ryt = rx
            val qx = pqx * rx + pqy * ry
            val qy = pqx * rxt + pqy * ryt
            val sx = s.deltaX * rx + s.deltaY * ry
            val sy = s.deltaX * rxt + s.deltaY * ryt

            if (sy == 0.0) return null
            val a = qx - qy * sx / sy
            return Point2d(p.x + a * rx, p.y + a * ry)
        }
        override fun toString(): String = "$position @ $velocity"
    }
}
