package de.linkel.aoc

class Day09Test: AbstractDayTest<Int>() {
    override val exampleA = """
0 3 6 9 12 15
1 3 6 10 15 21
10 13 16 21 30 45
        """.trimIndent()
    override val exampleSolutionA = 114
    override val solutionA = 2008960228

    override val exampleSolutionB = 2
    override val solutionB = 1097

    override val implementation = Day09()
}
