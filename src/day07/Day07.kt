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

            for (y in splitterCoord.second - 2 downTo 2 step 2) {
                if (grid[splitterCoord.first][y] == '^')
                    break

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
    val newWorldsForSplitter = mutableMapOf<Pair<Int, Int>, Long>()

    val maxY: Int = splitterCoords.last().second

    for (splitterCoord in splitterCoords.reversed()) {
        if (splitterCoord.second == maxY)
            newWorldsForSplitter[splitterCoord] = 1

        else {
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