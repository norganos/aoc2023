package de.linkel.aoc.base

interface AdventDay<T> {
    val day: Int
    val parts: List<QuizPart>
    fun test(part: QuizPart, input: String): T
    fun solve(part: QuizPart, args: List<String>): T
    fun solve(part: QuizPart): T {
        return solve(part, emptyList())
    }
}
