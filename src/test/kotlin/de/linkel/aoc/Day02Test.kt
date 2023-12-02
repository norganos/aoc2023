package de.linkel.aoc

import de.linkel.aoc.base.QuizPart
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class Day02Test {
    val example = """
Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
        """.trimIndent()

    @Test
    fun `example part 1`() {
        Assertions.assertThat(Day02().test(QuizPart.A, example)).isEqualTo(8)
    }

    @Test
    fun `example part 2`() {
        Assertions.assertThat(Day02().test(QuizPart.B, example)).isEqualTo(2286)
    }

    @Test
    fun `solution part 1`() {
        Assertions.assertThat(Day02().solve(QuizPart.A)).isEqualTo(2283)
    }

    @Test
    fun `solution part 2`() {
        Assertions.assertThat(Day02().solve(QuizPart.B)).isEqualTo(78669)
    }
}
