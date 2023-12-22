package de.linkel.aoc

class Day22Test: AbstractDayTest<Int>() {
    override val exampleA = """
1,0,1~1,2,1
0,0,2~2,0,2
0,2,3~2,2,3
0,0,4~0,2,4
2,0,5~2,2,5
0,1,6~2,1,6
1,1,8~1,1,9
        """.trimIndent()
    override val exampleSolutionA = 5
    override val solutionA = 468

    override val exampleSolutionB = 7
    override val solutionB = 75358

    override val implementation = Day22()
}
