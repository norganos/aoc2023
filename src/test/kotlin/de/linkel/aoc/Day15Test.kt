package de.linkel.aoc

class Day15Test: AbstractDayTest<Int>() {
    override val exampleA = """
rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
        """.trimIndent()
    override val exampleSolutionA = 1320
    override val solutionA = 506869

    override val exampleSolutionB = 145
    override val solutionB = 271384

    override val implementation = Day15()
}
