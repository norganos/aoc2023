package de.linkel.aoc

import de.linkel.aoc.base.AbstractLinesAdventDay
import de.linkel.aoc.base.QuizPart
import de.linkel.aoc.utils.grid.Grid
import de.linkel.aoc.utils.grid.Point
import de.linkel.aoc.utils.grid.Vector
import jakarta.inject.Singleton
import java.util.PriorityQueue

@Singleton
class Day17: AbstractLinesAdventDay<Int>() {
    override val day = 17

    override fun process(part: QuizPart, lines: Sequence<String>): Int {
        val grid = Grid.parse(lines) { pos, char ->
            char.digitToInt()
        }
        val dest = Point(grid.width - 1, grid.height - 1)
        val start = State(Point(0, 0), Direction.EAST, 0)
        return if (part == QuizPart.A) {
            dijkstra(
                start,
                { state -> state.pos == dest },
                { state -> state.possibleNextStates(1, 3).filter { it.pos in grid } },
                { _, to -> grid[to.pos]!! }
            )!!.first
        } else {
            dijkstra(
                start,
                { state -> state.pos == dest && state.straight >= 4 },
                { state -> state.possibleNextStates(4, 10).filter { it.pos in grid } },
                { _, to -> grid[to.pos]!! }
            )!!.first
        }
    }

    fun print(grid: Grid<Int>, path: Collection<Point>) {
        val points = path.toSet()
        println(
            buildString {
                (0 until grid.height).forEach { y ->
                    (0 until grid.width).forEach { x ->
                        val p = Point(x, y)
                        if (p in points) {
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

enum class Direction(
    val vector: Vector
) {
    NORTH(Vector(0, -1)) {
        override val left get() = WEST
        override val right get() = EAST
        override val opposite get() = SOUTH
    },
    EAST(Vector(1, 0)) {
        override val left get() = NORTH
        override val right get() = SOUTH
        override val opposite get() = WEST
    },
    SOUTH(Vector(0, 1)) {
        override val left get() = EAST
        override val right get() = WEST
        override val opposite get() = NORTH
    },
    WEST(Vector(-1, 0)) {
        override val left get() = SOUTH
        override val right get() = NORTH
        override val opposite get() = EAST
    };

    abstract val left: Direction
    abstract val right: Direction
    abstract val opposite: Direction
}

operator fun Point.plus(direction: Direction): Point = this + direction.vector

data class State(
    val pos: Point,
    val direction: Direction,
    val straight: Int
) {
    fun possibleNextStates(minStraight: Int = 1, maxStraight: Int = 100): List<State> {
        return buildList {
            if (straight < maxStraight) {
                add(State(pos + direction, direction, straight + 1))
            }
            if (straight >= minStraight) {
                add(State(pos + direction.right, direction.right, 1))
                add(State(pos + direction.left, direction.left, 1))
            }
        }
    }
}

fun <S> dijkstra(
    start: S,
    endLambda: (S) -> Boolean,
    nextLamba: (S) -> Iterable<S>,
    costLambda: (S, S) -> Int
): Pair<Int, List<S>>? {
    data class Evaluated(val distance: Int, val prev: S?)

    data class Planned(val state: S, val distance: Int) : Comparable<Planned> {
        override fun compareTo(other: Planned): Int = distance.compareTo(other.distance)
    }

    val queue = PriorityQueue(listOf(Planned(start, 0)))
    val evaluated = mutableMapOf(start to Evaluated(0, null))

    while (queue.isNotEmpty()) {
        val current = queue.remove()
        if (endLambda(current.state)) {
            val winningPath = mutableListOf<S>()
            var s: S? = current.state
            while (s != null) {
                winningPath.add(0, s)
                s = evaluated[s]!!.prev
            }
            return current.distance to winningPath
        }

        nextLamba(current.state)
            .filter { it !in evaluated }
            .forEach { state ->
                val distance = current.distance + costLambda(current.state, state)
                queue.add(Planned(state, distance))
                evaluated[state] = Evaluated(distance, current.state)
            }
    }
    return null
}
