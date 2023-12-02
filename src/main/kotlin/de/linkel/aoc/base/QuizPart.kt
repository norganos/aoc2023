package de.linkel.aoc.base

enum class QuizPart(
    val postfix: String
) {
    A("a"),
    B("b");

    companion object {
        val BOTH = listOf(A, B)
        val ONLY_A = listOf(A)
    }
}
