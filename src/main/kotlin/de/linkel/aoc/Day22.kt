package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.space.discrete.Point3d
import jakarta.inject.Singleton

@Singleton
class Day22: AbstractLinesAdventDay<Int>() {
    override val day = 22

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        val regex = Regex("(\\d+),(\\d+),(\\d+)~(\\d+),(\\d+),(\\d+)")
        val (bricks, _) = settle(
            lines
                .mapIndexed { idx, line ->
                    val matched = regex.matchEntire(line)!!.groupValues.subList(1, 7)
                        .map { it.toInt() }
                    val (x1, y1, z1) = matched.take(3)
                    val (x2, y2, z2) = matched.drop(3).take(3)
                    Brick(
                        id = idx.toString(),
                        supportedBy = emptySet(),
                        points = (x1..x2).flatMap { x ->
                                (y1..y2).flatMap { y ->
                                    (z1..z2).map { z ->
                                        Point3d(x, y, z - 1) // we want our cubes to land on 0
                                    }
                                }
                            }
                            .toSet()
                    )
                }
                .toList()
        )

        return if (part == QuizPart.A) {
            bricks.count { brick ->
                bricks
                    .filter { it.id != brick.id }
                    .none { it.supportedBy.size == 1 && brick.id in it.supportedBy }
            }
        } else {
            bricks.sumOf {  remove ->
                settle(bricks.filter { it.id != remove.id }).second
            }
//            val graph = bricks
//                .associate { it.id to it.supportedBy }
//            bricks
//                .sumOf { remove ->
//                    val r = chainReaction(
//                        graph
//                            .filter { it.key != remove.id }
//                        ,
//                        setOf(remove.id)
//                    ).size
////                    println("${it.id} -> $r")
//                    r
//                }
        }
    }

//    private fun chainReaction(bricks: Map<String, Set<String>>, remove: Set<String>): Set<String> {
//        val afterRemoval = bricks
//            .mapValues { it.value - remove }
//        val falling = afterRemoval
//            .entries
//            .filter { it.value.isEmpty() }
//            .map { it.key }
//            .toSet()
//        val withoutFalling = afterRemoval.filter { it.value.isNotEmpty() }
//        return falling + if (falling.isNotEmpty()) chainReaction(withoutFalling, falling) else emptySet()
//    }

    data class Brick(
        val id: String,
        val supportedBy: Set<String>,
        val points: Set<Point3d>
    )
    data class Layer(
        val z: Int,
        val points: Map<Point, String>
    ) {
        fun intersects(other: Layer): Set<String> {
            return this.points.entries.filter { it.key in other.points }.map { it.value }.toSet()
        }
    }

    private fun settle(bricks: Iterable<Brick>): Pair<List<Brick>, Int> {
        val settled = mutableListOf<Brick>()
        val layers = mutableMapOf<Int,Layer>()
        var falling = 0
        bricks
            .sortedBy { brick -> brick.points.maxOf { it.z } }
            .forEach { brick ->
                val brickLayers = toLayers(brick)
                val bottomLayer = brickLayers.first() // bricks are always simple lines, se no check in z necessary
                val elevation = brickLayers.minOf { it.z }
                var descendance = 0
                var supporters = setOf("floor")
                while (elevation - descendance > 0) {
                    val layer = layers[elevation - descendance - 1]
                    supporters = layer?.intersects(bottomLayer) ?: emptySet()
                    if (supporters.isNotEmpty()) {
                        break
                    }
                    descendance++
                }
                if (descendance > 0) {
                    falling++
                }
                brickLayers
                    .map { it.copy(z = it.z - descendance) }
                    .forEach { l ->
                        layers[l.z] = layers[l.z]?.let{ it.copy(points = it.points + l.points) } ?: l
                    }
                settled += Brick(
                    id = brick.id,
                    supportedBy = supporters,
                    points = brick.points.map { it.copy(z = it.z - descendance) }.toSet()
                )
            }
        return settled to falling
    }

    private fun toLayers(brick: Brick): List<Layer> {
        return brick.points
            .groupBy { it.z }
            .entries
            .sortedBy { it.key }
            .map { e ->
                Layer(
                    z = e.key,
                    points = e.value.associate { Point(it.x, it.y) to brick.id }
                )
            }
    }
}
