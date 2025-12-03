package day03

import java.io.File

fun main() {
    val input = input("03", "input")
    println(part1(input))
    println(part2(input))
}

fun input(day: String, type: String): List<String> {
    val input = File("src/day$day/$type.txt").readLines()
    return input
}

fun indexOfHighest(bank: String, start: Int, charsLeftInNumber: Int): Int {
    var highest = 0
    var indexOfHighest = -1
    for (index in start..bank.lastIndex - charsLeftInNumber) {
        val num = bank[index].digitToInt()
        if (num > highest) {
            highest = num
            indexOfHighest = index
        }
    }
    return indexOfHighest
}

fun part1(banks: List<String>): Int {
    var sum = 0
    for (bank in banks) {
        val highestIndex = indexOfHighest(bank, 0, 1)
        val secondHighestIndex = indexOfHighest(bank, highestIndex + 1, 0)
        val highest = bank[highestIndex]
        val secondHighest = bank[secondHighestIndex]
        sum += (highest.toString() + secondHighest.toString()).toInt()
    }
    return sum
}

fun part2(banks: List<String>): Long {
    var sum = 0L
    for (bank in banks) {
        val highestIndices = Array(12) { 0 }
        for (i in highestIndices.indices)
            highestIndices[i] = indexOfHighest(bank, start = if (i == 0) 0 else highestIndices[i - 1] + 1, charsLeftInNumber = 11 - i)
        val num = String(CharArray(12) { i -> bank[highestIndices[i]] }).toLong()
        sum += num
    }
    return sum
}