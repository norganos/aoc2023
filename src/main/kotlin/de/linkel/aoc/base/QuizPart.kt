package de.linkel.aoc.base

enum class QuizPart(
    val prefix: String
) {
    A("a"),
    B("b");

    companion object {
        val BOTH = listOf(A, B)
        val ONLY_A = listOf(A)
    }
}
