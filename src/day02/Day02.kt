package day02

import java.io.File

fun main() {
    val input = input("02", "input")
    println(part1(input))
    println(part2(input))
}

fun input(day: String, type: String): Array<LongRange> {
    val input = File("src/day$day/$type.txt").readText()
    val strRanges = input.trim().split(",")
    return Array(strRanges.size) { i ->
        val values = strRanges[i].split("-")
        LongRange(values[0].toLong(), values[1].toLong())
    }
}

fun part1(input: Array<LongRange>): Long {
    var sumOfInvalid = 0L

    for (range in input) {
        if (range.first.toString().length % 2 == 1 && range.last.toString().length % 2 == 1)
            continue
        for (num in range) {
            val strNum = num.toString()
            val length = strNum.length
            if (length % 2 == 1)
                continue

            val halfLength = length / 2
            if (strNum.take(halfLength) == strNum.takeLast(halfLength))
                sumOfInvalid += num
        }
    }

    return sumOfInvalid
}

fun part2(input: Array<LongRange>): Long {
    var sumOfInvalid = 0L

    for (range in input) {
        for (num in range) {
            val strNum = num.toString()
            val length = strNum.length

            var isInvalid = false

            for (subStrLength in 1..length / 2) {
                if (length % subStrLength != 0)
                    continue

                var isRepeating = true

                for (startIndex in 0 until length step subStrLength) {
                    try {
                        val firstSubStr = strNum.substring(startIndex, startIndex + subStrLength)
                        val secondSubStr = strNum.substring(startIndex + subStrLength, startIndex + subStrLength * 2)
                        if (firstSubStr != secondSubStr) {
                            isRepeating = false
                            break
                        }
                    } catch (_: StringIndexOutOfBoundsException) { }
                }

                if (isRepeating) {
                    isInvalid = true
                    break
                }
            }

            if (isInvalid)
                sumOfInvalid += num
        }
    }

    return sumOfInvalid
}