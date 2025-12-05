package day05

import java.io.File

fun main() {


    val input = File("src/day05/input.txt").readText()
    val parts = input.split("\n\n")

    val rangesStr = parts[0].split("\n")
    val ranges = Array(rangesStr.size) { i ->
        val values = rangesStr[i].split("-")
        LongRange(values[0].toLong(), values[1].toLong())
    }

    val idsStr = parts[1].split("\n")
    val ids = LongArray(idsStr.size) { i -> idsStr[i].toLong() }

    println(part1(ranges, ids))
    println(part2(ranges))
}

fun part1(ranges: Array<LongRange>, ids: LongArray): Int {
    println("ranges: $ranges, ids: $ids")
    var sum = 0
    for (id in ids) {
        for (range in ranges) {
            if (id in range) {
                sum++
                break
            }
        }
    }
    return sum
}

fun part2(rangesNoNulls: Array<LongRange>): Long {
    //Check every pair of ranges to see if they are overlapping
    //If they are, remove the overlapping part from the range at range1Index
    //Make the range at range1Index empty quicker to iterate fewer times

    //Make the array be able to contain nulls
    val ranges: Array<LongRange?> = Array(rangesNoNulls.size) { i -> rangesNoNulls[i] }

    for (range1Index in 0..ranges.lastIndex) {
        if (ranges[range1Index] == null)
            continue

        for (range2Index in range1Index + 1..ranges.lastIndex) {
            if (ranges[range2Index] == null)
                continue

            val range1 = ranges[range1Index]!!
            val range2 = ranges[range2Index]!!

            //Check if range2 contains entire range1
            if (range1.first >= range2.first && range1.last <= range2.last) {
                //Remove range1
                ranges[range1Index] = null
                break
            }
            //Check if range1 contains entire range2
            if (range2.first >= range1.first && range2.last <= range1.last) {
                //Remove range2
                ranges[range2Index] = null
                continue
            }

            //Check if the ranges are overlapping and range1 is lower (range1.first < range2.first)
            if (range1.first < range2.first && range1.last in range2) {
                ranges[range1Index] = null
                ranges[range2Index] = range1.first..range2.last
                break
            }
            //Check if the ranges are overlapping and range1 is lower (range1.first < range2.first)
            if (range1.last > range2.last && range1.first in range2) {
                ranges[range1Index] = null
                ranges[range2Index] = range2.first..range1.last
                break
            }
        }
    }

    //Count the amount of fresh ingredient IDs now that no ranges are overlapping

    var amount = 0L

    for (range in ranges) {
        if (range == null)
            continue
        amount += range.last - range.first + 1
    }

    return amount
}