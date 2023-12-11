package de.linkel.aoc

class Day10Test: AbstractDayTest<Int>() {
    override val exampleA = """
..F7.
.FJ|.
SJ.L7
|F--J
LJ...
        """.trimIndent()
    override val exampleSolutionA = 8
    override val solutionA = 6828

    override val exampleB = """
FF7FSF7F7F7F7F7F---7
L|LJ||||||||||||F--J
FL-7LJLJ||||||LJL-77
F--JF--7||LJLJ7F7FJ-
L---JF-JLJ.||-FJLJJ7
|F|F-JF---7F7-L7L|7|
|FFJF7L7F-JF7|JL---7
7-L-JL7||F7|L7F-7F7|
L.L7LFJ|||||FJL7||LJ
L7JLJL-JLJLJL--JLJ.L
    """.trimIndent()
    override val exampleSolutionB = 10
    override val solutionB = 459

    override val implementation = Day10()
}
/*

    L
  S-->P     S
    R       |
           R|L
            v
            P
         L
       P<--S
         R
 */
