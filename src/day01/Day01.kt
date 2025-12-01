package day01

import java.io.File

fun main() {
    val input = input("01", "input")
    println(part1(input))
    println(part2(input))
}

fun input(day: String, type: String): List<String> {
    val input = File("src/day$day/$type.txt").readLines()
    return input
}

fun part1(input: List<String>): Int {
    var dial = 50
    var zeros = 0
    for (rotation in input) {
        val direction = rotation[0]
        val amount = rotation.substring(1).toInt()

        if (direction == 'R')
            dial += amount
        else
            dial -= amount

        dial %= 100
        if (dial < 0)
            dial += 100

        if (dial == 0)
            zeros++
    }
    return zeros
}

fun part2(input: List<String>): Int {
    var dial = 50
    var zeros = 0
    for (rotation in input) {
        val direction = rotation[0]
        val amount = rotation.substring(1).toInt()

        println("\n$direction $amount")
        println("dial = $dial")

        val fullRotations = amount / 100
        zeros += fullRotations

        println("fullRotations: $fullRotations, zeros: $zeros")

        if (direction == 'R') {
            val remaining = amount % 100
            dial += remaining
            println("remaining = $remaining")

            println("dial = $dial")
            if (dial == 0) {
                zeros++
            }
            else if(dial > 99) {
                dial -= 100
                println("inc")
                zeros++
            }
        }
        else {
            val remaining = amount % 100
            dial -= remaining
            println("remaining = $remaining")

            println("dial = $dial")
            if(dial == 0) {
                zeros++
            }
            else if(dial < 0) {
                dial += 100
                if(remaining + (dial - 100) != 0) {
                    println("inc")
                    zeros++
                }
            }
        }
    }
    return zeros
}