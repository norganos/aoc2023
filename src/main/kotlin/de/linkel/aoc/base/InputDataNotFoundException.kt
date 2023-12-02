package de.linkel.aoc.base

class InputDataNotFoundException(val day: Int, val part: QuizPart, message: String = "did not find input data for day $day part $part"): Exception(message) {}
