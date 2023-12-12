package de.linkel.aoc

import java.math.BigInteger

class Day12Test: AbstractDayTest<BigInteger>() {
    override val exampleA = """
???.### 1,1,3
.??..??...?##. 1,1,3
?#?#?#?#?#?#?#? 1,3,1,6
????.#...#... 4,1,1
????.######..#####. 1,6,5
?###???????? 3,2,1
        """.trimIndent()
    override val exampleSolutionA = 21L.toBigInteger()
    override val solutionA = 7344L.toBigInteger()

    override val exampleSolutionB = 525152L.toBigInteger()
    override val solutionB = 1379793119L.toBigInteger()

    override val implementation = Day12()
}
/* wrong:
6193
7358
7344
22854629599L
22854629599L
22854629599
1379793119L
1088006519007
 */
