# Advent of Code 2023

This repo contains solutions for [Advent of code](https://adventofcode.com) puzzles in Kotlin.

It is based on micronaut for dependency injection.

As it depends on [aoc-utils](https://github.com/norganos/aoc-utils) authentication is necessary to access GitHub's
Maven Package Repository: as of now, the gradle config in use assumes that you provide a PAT either via Env-Variables
(`GITHUB_ACTOR` and `GITHUB_TOKEN`) or in gradle.properties (under `github.registry.user` and `github.registry.key`).

input files `src/main/resources/input*.txt` are ignored in `.gitignore`.
