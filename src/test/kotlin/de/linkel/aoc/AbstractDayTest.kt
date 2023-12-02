package de.linkel.aoc

import de.linkel.aoc.base.AdventDay
import de.linkel.aoc.base.InputDataNotFoundException
import de.linkel.aoc.base.QuizPart
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

abstract class AbstractDayTest<T> {
    protected abstract val exampleA: String
    protected abstract val exampleSolutionA: T
    protected open val exampleB: String get() = exampleA
    protected abstract val exampleSolutionB: T
    protected abstract val solutionA: T
    protected abstract val solutionB: T

    protected abstract val implementation: AdventDay<T>

    @Test
    fun `example part 1`() {
        Assertions.assertThat(implementation.test(QuizPart.A, exampleA)).isEqualTo(exampleSolutionA)
    }

    @Test
    fun `example part 2`() {
        Assertions.assertThat(implementation.test(QuizPart.B, exampleB)).isEqualTo(exampleSolutionB)
    }

    @Test
    fun `solution part 1`() {
        try {
            Assertions.assertThat(implementation.solve(QuizPart.A)).isEqualTo(solutionA)
        } catch(ignore: InputDataNotFoundException) {
        }
    }

    @Test
    fun `solution part 2`() {
        try {
            Assertions.assertThat(implementation.solve(QuizPart.B)).isEqualTo(solutionB)
        } catch(ignore: InputDataNotFoundException) {
        }
    }
}
