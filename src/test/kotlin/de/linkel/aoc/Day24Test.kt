package de.linkel.aoc

class Day24Test: AbstractDayTest<Int>() {
    override val exampleA = """
19, 13, 30 @ -2,  1, -2
18, 19, 22 @ -1, -1, -2
20, 25, 34 @ -2, -2, -4
12, 31, 28 @ -1, -2, -1
20, 19, 15 @  1, -5, -3
        """.trimIndent()
    override val exampleSolutionA = 2
    override val solutionA = 16050

    override val exampleSolutionB = 0
    override val solutionB = 0

    override val implementation = Day24()
}
