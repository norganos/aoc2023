package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import jakarta.inject.Singleton

@Singleton
class Day16: AbstractLinesAdventDay<Int>() {
    override val day = 16

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        val grid = Grid.parse(lines) { pos, char ->
            when(char) {
                '/' -> SlashMirror()
                '\\' -> BackslashMirror()
                '-' -> DashSplitter()
                '|' -> PipeSplitter()
                '.' -> null
                else -> throw Exception("unknown char $char")
            }
        }
        val startBeams = if (part == QuizPart.A)
            listOf(
                Beam(pos = Point(-1,0), direction = EAST)
            )
        else {
            (0 until grid.width).flatMap { x ->
                listOf(
                    Beam(pos = Point(x, -1), direction = SOUTH),
                    Beam(pos = Point(x, grid.height), direction = NORTH)
                )
            } + (0 until grid.height).flatMap { y ->
                listOf(
                    Beam(pos = Point(-1, y), direction = EAST),
                    Beam(pos = Point(grid.width, y), direction = WEST)
                )
            }
        }
        return startBeams
            .maxOf { run(grid, it) }
    }

    fun run(grid: Grid<Thing>, startBeam: Beam): Int {
        return generateSequence(
            setOf(startBeam) to emptySet<Beam>()
        ) { (activeBeams, oldBeams) ->
            Pair(
                activeBeams
                    .asSequence()
                    .map { it.step() }
                    .filter { it.pos in grid }
                    .map {
                        it to grid[it.pos]
                    }
                    .flatMap { (beam, thing) ->
                        thing?.modify(beam) ?: listOf(beam)
                    }
                    .filter { it !in oldBeams }
                    .toSet(),
                oldBeams + activeBeams
                    .filter { it.pos in grid }
            )
        }
            .first { it.first.isEmpty() }
            .second
            .map{ it.pos }
            .toSet()
            .count()
    }

    interface Thing {
        val char: Char
        fun modify(beam: Beam): Set<Beam>
    }
    class SlashMirror: Thing {
        override val char = '/'
        override fun modify(beam: Beam): Set<Beam> {
            return setOf(
                when (beam.direction) {
                    NORTH -> beam.copy(
                        direction = EAST
                    )
                    EAST -> beam.copy(
                        direction = NORTH
                    )
                    SOUTH -> beam.copy(
                        direction = WEST
                    )
                    WEST -> beam.copy(
                        direction = SOUTH
                    )
                    else -> throw Exception("invalid direction ${beam.direction}}")
                }
            )
        }
    }
    class BackslashMirror: Thing {
        override val char = '\\'
        override fun modify(beam: Beam): Set<Beam> {
            return setOf(
               when (beam.direction) {
                    NORTH -> beam.copy(
                        direction = WEST
                    )
                    EAST -> beam.copy(
                        direction = SOUTH
                    )
                    SOUTH -> beam.copy(
                        direction = EAST
                    )
                    WEST -> beam.copy(
                        direction = NORTH
                    )
                    else -> throw Exception("invalid direction ${beam.direction}}")
                }
            )
        }
    }
    class DashSplitter: Thing {
        override val char = '-'
        override fun modify(beam: Beam): Set<Beam> {
            return if (beam.direction == EAST || beam.direction == WEST)
                setOf(beam)
            else setOf(
                beam.copy(
                    direction = EAST
                ),
                beam.copy(
                    direction = WEST
                ),
            )
        }
    }
    class PipeSplitter: Thing {
        override val char = '|'
        override fun modify(beam: Beam): Set<Beam> {
            return if (beam.direction == NORTH || beam.direction == SOUTH)
                setOf(beam)
            else setOf(
                beam.copy(
                    direction = NORTH
                ),
                beam.copy(
                    direction = SOUTH
                ),
            )
        }
    }

    data class Beam(
        val pos: Point,
        val direction: Vector
    ) {
        fun step(): Beam = copy(
                pos = pos + direction
            )
    }


    fun print(grid: Grid<Thing>, points: Set<Beam>) {
        val energized = points.map { it.pos }.toSet()
        println(
            buildString {
                (0 until grid.height).forEach { y ->
                    (0 until grid.width).forEach { x ->
                        val p = Point(x, y)
                        if (p in energized) {
                            append('#')
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

val NORTH = Vector(0, -1)
val WEST = Vector(-1, 0)
val SOUTH = Vector(0, 1)
val EAST = Vector(1, 0)
