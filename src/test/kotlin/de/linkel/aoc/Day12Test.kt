package de.linkel.aoc

class Day12Test: AbstractDayTest<Long>() {
    override val exampleA = """
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
        """.trimIndent()
    override val exampleSolutionA = 21L
    override val solutionA = 7344L

    override val exampleSolutionB = 525152L
    override val solutionB = 1088006519007L

    override val implementation = Day12()
}
