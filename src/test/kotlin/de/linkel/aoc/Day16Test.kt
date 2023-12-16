package de.linkel.aoc

class Day16Test: AbstractDayTest<Int>() {
    override val exampleA = """
.|...\....
|.-.\.....
.....|-...
........|.
..........
.........\
..../.\\..
.-.-/..|..
.|....-|.\
..//.|....
        """.trimIndent()
    override val exampleSolutionA = 46
    override val solutionA = 7498

    override val exampleSolutionB = 51
    override val solutionB = 7846

    override val implementation = Day16()
}
