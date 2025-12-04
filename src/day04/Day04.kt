package day04

import java.io.File

fun main() {
    val input = input("04", "input")
    println(part1(input))
    println(part2(input))
}

fun input(day: String, type: String): Array<Array<Char>> {
    val rows = File("src/day$day/$type.txt").readLines()
    val grid = Array(rows.size) { i -> rows[i].toList().toTypedArray() }
    return grid
}

fun part1(grid: Array<Array<Char>>): Int {
    var accessible = 0

    //Look at every square
    for (y in 0 until grid.size) {
        for (x in 0 until grid[0].size) {
            if (canRemove(x, y, grid))
                accessible++
        }
    }

    return accessible
}

fun canRemove(x: Int, y: Int, grid: Array<Array<Char>>): Boolean {
    var fullAdjacentAmount = 0

    if (grid[x][y] == '.')
        return false

    //Look at every adjacent square
    for (dx in -1..1) {
        for (dy in -1..1) {
            if (dx == 0 && dy == 0)
                continue

            try {
                if (grid[x + dx][y + dy] == '@')
                    fullAdjacentAmount++
            }
            catch (_: IndexOutOfBoundsException) {
                //If it is out of bounds, it is empty and does not get incremented
            }
        }
    }

    return fullAdjacentAmount < 4
}

fun part2(valGrid: Array<Array<Char>>): Int {
    val grid = Array(valGrid.size) { i -> valGrid[i].copyOf() }

    var accessible = 0

    do {
        var removed = 0

        for (y in 0 until grid.size) {
            for (x in 0 until grid[0].size) {
                if (canRemove(x, y, grid)) {
                    removed++
                    grid[x][y] = '.'
                }
            }
        }

        accessible += removed
    } while (removed > 0)

    return accessible
}