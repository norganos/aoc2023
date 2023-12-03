package de.linkel.aoc

class Day03Test: AbstractDayTest<Int>() {
    override val exampleA = """
467..114..
...*......
..35..633.
......#...
617*......
.....+.58.
..592.....
......755.
...${'$'}.*....
.664.598..
        """.trimIndent()
    override val exampleSolutionA = 4361
    override val solutionA = 559667

    override val exampleSolutionB = 467835
    override val solutionB = 86841457

    override val implementation = Day03()
}
