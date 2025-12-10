package day09

import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {
    val lines = File("src/day09/input.txt").readLines()

    val redTiles = Array(lines.size) { i ->
        val nums = lines[i].split(",").map { it.toInt() }
        Pair(nums[0], nums[1])
    }

    val redTilePairs = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
    for (redTile1 in redTiles) {
        for (redTile2 in redTiles) {
            redTilePairs.add(Pair(redTile1, redTile2))
        }
    }

    println(part1(redTilePairs))
    println(part2(redTiles, redTilePairs))
}

fun part1(redTilePairs: MutableList<Pair<Pair<Int, Int>, Pair<Int, Int>>>): Long {
    //Get the pair of red tiles with the biggest area between them
    val maxPair = redTilePairs.maxBy { abs((it.first.first - it.second.first + 1L) * (it.first.second - it.second.second + 1)) }

    return abs((maxPair.first.first - maxPair.second.first + 1L) * (maxPair.first.second - maxPair.second.second + 1))
}

fun part2(redTiles: Array<Pair<Int, Int>>, redTilePairs: MutableList<Pair<Pair<Int, Int>, Pair<Int, Int>>>): Long {
    redTilePairs.sortByDescending { abs((it.first.first - it.second.first + 1L) * (it.first.second - it.second.second + 1)) }

    val checkedPairs = mutableSetOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()

    for (rectCorners in redTilePairs) {
        if (rectCorners.first == rectCorners.second)
            continue
        if (checkedPairs.contains(Pair(rectCorners.second, rectCorners.first)))
            continue
        checkedPairs.add(rectCorners)

        val maxX = max(rectCorners.first.first, rectCorners.second.first)
        val minX = min(rectCorners.first.first, rectCorners.second.first)
        val maxY = max(rectCorners.first.second, rectCorners.second.second)
        val minY = min(rectCorners.first.second, rectCorners.second.second)


        var passedThroughGreen = false

        val tileIndex = redTiles.indexOf(rectCorners.first) + 1 % redTiles.size
        var prevTile = redTiles[if (tileIndex != 0) tileIndex - 1 else redTiles.lastIndex]

        for (redTileIndex in tileIndex..tileIndex + redTiles.size) {
            val redTile = redTiles[redTileIndex % redTiles.size]
            val xDiff = redTile.first - prevTile.first
            val yDiff = redTile.second - prevTile.second

            if (xDiff > 0 && redTile.first > minX) {
                if (redTile.second in minY + 1..maxY - 1) {
                    passedThroughGreen = true
                    break
                }
                else
                    if (redTile.first >= maxX)
                        break
            }
            else if (xDiff < 0 && redTile.first < maxX) {
                if (redTile.second in minY + 1..maxY - 1) {
                    passedThroughGreen = true
                    break
                }
                else
                    if (redTile.first <= minX)
                        break
            }

            else if (yDiff > 0 && redTile.second > minY) {
                if (redTile.first in minX + 1..maxX - 1) {
                    passedThroughGreen = true
                    break
                }
                else
                    if (redTile.second >= maxY)
                        break
            }
            else if (yDiff < 0 && redTile.second < maxY) {
                if (redTile.first in minX + 1..maxX - 1) {
                    passedThroughGreen = true
                    break
                }
                else
                    if (redTile.second <= minY)
                        break
            }

            prevTile = redTile
        }

        println(rectCorners)

        if (!passedThroughGreen)
            return abs((rectCorners.first.first - rectCorners.second.first + 1L) * (rectCorners.first.second - rectCorners.second.second + 1))
    }

    return -1
}