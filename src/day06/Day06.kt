package day06

import java.io.File

fun main() {
    val lines = File("src/day06/input.txt").readLines()
    val numbersStrings = lines.take(lines.size - 1).toTypedArray()

    val operatorsStr = lines[lines.lastIndex].split(" ").toMutableList()
    operatorsStr.removeIf { it.isEmpty() }
    val operators = Array(operatorsStr.size) { i -> operatorsStr[i][0] }

    println(part1(numbersStrings, operators))
    println(part2(numbersStrings, operators))
}

fun part1(numbersStrings: Array<String>, operators: Array<Char>): Long {
    val numbersOnLine = Array(numbersStrings.size) { i ->
        val numbersStr = numbersStrings[i].split(" ").toMutableList()
        numbersStr.removeIf { it.isEmpty() }
        Array(numbersStr.size) { i -> numbersStr[i].toInt() }
    }
    val numbers = Array(numbersOnLine[0].size) { i ->
        Array(numbersOnLine.size) { j -> numbersOnLine[j][i] }
    }

    return calc(numbers, operators)
}

fun part2(numbersStrings: Array<String>, operators: Array<Char>): Long {
    val numbers = mutableListOf(mutableListOf<Int>())

    for (str in numbersStrings)
        println(str)

    var index = 0
    for (i in numbersStrings[0].indices) {
        var number = ""
        for (j in numbersStrings.indices) {
            number += numbersStrings[j][i]
        }
        println(number)
        if (number.isBlank()) {
            numbers.add(mutableListOf())
            index++
        }
        else {
            numbers[index].add(number.trim().toInt())
        }
    }

    val arrNumbers = Array(numbers.size) { i -> Array(numbers[i].size) { j -> numbers[i][j] } }

    return calc(arrNumbers, operators)
}

fun calc(numbers: Array<Array<Int>>, operators: Array<Char>): Long {
    if (numbers.size != operators.size) {
        println("numbers: ${numbers[0].size}, operators: ${operators.size}")
        throw IllegalArgumentException("Size of numbers and operators do not match")
    }

    var sum: Long = 0

    for (i in numbers.indices) {
        var result: Long
        if (operators[i] == '+') {
            result = 0
            for (num in numbers[i])
                result += num
        }
        else {
            result = 1
            for (num in numbers[i])
                result *= num
        }
        sum += result
    }
    return sum
}