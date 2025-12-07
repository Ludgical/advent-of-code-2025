package day07

import java.io.File

fun main() {
    val lines = File("src/day07/input.txt").readLines()

    val invertedGrid = Array(lines.size) { i -> lines[i].toList().toTypedArray() }
    val grid = Array(invertedGrid[0].size) { i -> Array(invertedGrid.size) { j -> invertedGrid[j][i] } }

    var startCoord = Pair(-1, -1)
    val splitterCoords = mutableListOf<Pair<Int, Int>>()
    for (y in grid[0].indices)
        for (x in grid.indices) {
            if (grid[x][y] == 'S')
                startCoord = Pair(x, y)
            else if (grid[x][y] == '^')
                splitterCoords.add(Pair(x, y))
        }

    println(part1(grid, startCoord, splitterCoords))
    println(part2(grid, splitterCoords))
}

fun part1(grid: Array<Array<Char>>, startCoord: Pair<Int, Int>, splitterCoords: MutableList<Pair<Int, Int>>): Int {
    val reachedSplitterCoords = mutableListOf(Pair(startCoord.first, 2))

    for (splitterCoord in splitterCoords) {
        if (splitterCoord.second != 2) {
            //Every splitter except for the one under the start

            for (y in splitterCoord.second - 2 downTo 2 step 2) {
                // If another splitter is directly over the current splitter,
                // the current splitter can't be reached
                if (grid[splitterCoord.first][y] == '^')
                    break

                // If another splitter is above the current splitter to the right or left,
                // and that splitter has been reached, the current splitter can be reached
                if (grid[splitterCoord.first + 1][y] == '^') {
                    if (reachedSplitterCoords.contains(Pair(splitterCoord.first + 1, y))) {
                        reachedSplitterCoords.add(splitterCoord)
                        break
                    }
                    continue
                }
                if (grid[splitterCoord.first - 1][y] == '^') {
                    if (reachedSplitterCoords.contains(Pair(splitterCoord.first - 1, y))) {
                        reachedSplitterCoords.add(splitterCoord)
                        break
                    }
                    continue
                }
            }
        }
    }

    return reachedSplitterCoords.size
}

fun part2(grid: Array<Array<Char>>, splitterCoords: MutableList<Pair<Int, Int>>): Long {
    // Map every splitter to the amount of new worlds that get created after a beam gets split on it

    val newWorldsForSplitter = mutableMapOf<Pair<Int, Int>, Long>()

    // The y coordinate of the lowest splitter
    val maxY: Int = splitterCoords.last().second

    // Start at the bottom
    for (splitterCoord in splitterCoords.reversed()) {
        // If the splitter is at the bottom, set the new world amount of the splitter to 1
        if (splitterCoord.second == maxY)
            newWorldsForSplitter[splitterCoord] = 1

        else {
            // If the splitter is not at the bottom, set the new world amount of the splitter to
            // the sum of the new world amounts of the splitters that are down to the right and
            // down to the left of the splitter
            var newWorlds = 1L
            for (x in setOf(splitterCoord.first - 1, splitterCoord.first + 1))
                for (y in splitterCoord.second + 2..maxY step 2)
                    if (grid[x][y] == '^') {
                        newWorlds += newWorldsForSplitter[Pair(x, y)]!!
                        break
                    }

            newWorldsForSplitter[splitterCoord] = newWorlds
        }
    }

    return newWorldsForSplitter[splitterCoords[0]]!! + 1
}