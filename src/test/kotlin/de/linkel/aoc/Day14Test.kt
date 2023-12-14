package de.linkel.aoc

class Day14Test: AbstractDayTest<Int>() {
    override val exampleA = """
O....#....
O.OO#....#
.....##...
OO.#O....O
.O.....O#.
O.#..O.#.#
..O..#O..O
.......O..
#....###..
#OO..#....
        """.trimIndent()
    override val exampleSolutionA = 136
    override val solutionA = 108889

    override val exampleSolutionB = 64
    override val solutionB = 104671

    override val implementation = Day14()
}
