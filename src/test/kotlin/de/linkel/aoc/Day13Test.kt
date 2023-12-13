package de.linkel.aoc

class Day13Test: AbstractDayTest<Int>() {
    override val exampleA = """
#.##..##.
..#.##.#.
##......#
##......#
..#.##.#.
..##..##.
#.#.##.#.

#...##..#
#....#..#
..##..###
#####.##.
#####.##.
..##..###
#....#..#
        """.trimIndent()
    override val exampleSolutionA = 405
    override val solutionA = 29213

    override val exampleSolutionB = 400
    override val solutionB = 37453

    override val implementation = Day13()
}

/*
         9
0 2 4 6 8
 1 3 5 7

#...##..#   0
#....#..#   1
..##..###   2
#####.##.   3
#####.##.   4
..##..###   5
#....#..#   6
             7
 */
