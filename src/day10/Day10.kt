package day10

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
    val lines = File("src/day10/input.txt").readLines()

    val requiredLights = Array(lines.size) { i -> lines[i].substring(1, lines[i].indexOf(']')) }

    val buttons = Array(lines.size) { i ->
        val items = lines[i].split(' ')
        items.take(items.size - 1).takeLast(items.size - 2).map {
            it.substring(1, it.length - 1).split(',').map { it2 -> it2.toInt() }
        }.toTypedArray()
    }

    val joltages = Array(lines.size) { i ->
        lines[i].substring(lines[i].indexOf('{') + 1, lines[i].indexOf('}')).split(',').map { it.toInt() }.toIntArray()
    }

    println(part1(requiredLights, buttons))
    println(part2(buttons.map { it1 -> it1.map { it2 -> it2.toIntArray() }.toTypedArray() }.toTypedArray(), joltages))
}

fun part1(allRequiredLights: Array<String>, allButtons: Array<Array<List<Int>>>): Int {
    var buttonPresses = 0

    for (i in allRequiredLights.indices) {
        val requiredLights = allRequiredLights[i]
        val buttons = allButtons[i]

        val requiredButton = mutableListOf<Int>()
        for ((index, light) in requiredLights.withIndex())
            if (light == '#')
                requiredButton.add(index)

        if (buttons.contains(requiredButton)) {
            buttonPresses++
            continue
        }

        //Button to the indexes of the buttons that simulate pressing the button
        val availableButtons = mutableMapOf<List<Int>, Set<Int>>()
        for ((index, button) in buttons.withIndex())
            availableButtons[button] = setOf(index)

        val newButtons = mutableMapOf<List<Int>, Set<Int>>()
        var done = false

        while (true) {
            for (button1 in availableButtons) {
                for (button2 in availableButtons) {
                    if (containsAny(button1.value, button2.value)) {
                        continue
                    }
                    val newButton = mutableListOf<Int>()
                    for (lightIndex in requiredLights.indices)
                        if (button1.key.count { it == lightIndex } + button2.key.count { it == lightIndex } == 1)
                            newButton.add(lightIndex)

                    val prevToGetToButton = availableButtons[newButton]
                    if (prevToGetToButton != null)
                        newButtons[newButton] = prevToGetToButton
                    else {
                        val toGetToNewButton = mutableSetOf<Int>()
                        toGetToNewButton.addAll(button1.value)
                        toGetToNewButton.addAll(button2.value)
                        newButtons[newButton] = toGetToNewButton
                    }

                    if (newButton == requiredButton) {
                        buttonPresses += newButtons[newButton]!!.size
                        done = true
                        break
                    }
                }
                if (done)
                    break
            }
            if (done)
                break

            availableButtons.putAll(newButtons)
            newButtons.clear()
        }
    }

    return buttonPresses
}
/**Is any value in the first set also in the second set?*/
fun containsAny(vals1: Set<Int>, vals2: Set<Int>): Boolean {
    for (val1 in vals1)
        for (val2 in vals2)
            if (val1 == val2)
                return true
    return false
}

data object PInt {
    var v: Int = 0
}

fun part2(allButtons: Array<Array<IntArray>>, allJoltages: Array<IntArray>): Int {
    var buttonPresses = 0
    var amount = 0

    for (index in allJoltages.indices) {
        val buttons = allButtons[index]
        val joltages = allJoltages[index]

        val matrix = Array(joltages.size) { i ->
            IntArray(buttons.size + 1) { j ->
                if (j < buttons.size)
                    if (buttons[j].contains(i)) 1 else 0
                else joltages[i]
            }
        }
        reduce(matrix)

        val maxValues = IntArray(buttons.size) { i -> buttons[i].minOf { joltages[it] } }
        val minimumPresses = PInt
        minimumPresses.v = 1000000

        solveMatrix(matrix, buttons, joltages,
            min(matrix.lastIndex, matrix[0].lastIndex - 1), matrix[0].lastIndex - 1,
            maxValues, IntArray(buttons.size) { 1000000 }, minimumPresses)

        buttonPresses += minimumPresses.v
        amount++
    }

    println("Amount: $amount")

    return buttonPresses
}

fun println(matrix: Array<IntArray>) {
    for (y in 0 until matrix.size) {
        println(matrix[y].joinToString(" "))
    }

    println()
}

fun swap(matrix: Array<IntArray>, row1Index: Int, row2Index: Int) {
    if (row1Index == row2Index)
        return
    val temp = matrix[row1Index]
    matrix[row1Index] = matrix[row2Index]
    matrix[row2Index] = temp
}

fun scale(row: IntArray, s: Int) {
    for(i in 0..row.lastIndex)
        row[i] *= s
}

fun lcm(n1: Int, n2: Int): Int {
    assert(n1 > 0 && n2 > 0)
    var lcm = max(n1, n2)

    while (true) {
        if (lcm % n1 == 0 && lcm % n2 == 0)
            return lcm
        lcm++
    }
}

fun reduce(rowToReduce: IntArray, reducingRow: IntArray, reducingColumn: Int) {
    if (rowToReduce[reducingColumn] == 0)
        return
    assert(reducingRow[reducingColumn] > 0)
    if (rowToReduce[reducingColumn] < 0)
        scale(rowToReduce, -1)

    //Getting rowToReduce[reducingColumn] to equal reducingRow[reducingColumn] so you can
    //subtract the rows once by scaling the values in the rows at reducingColumn to their lowest common multiple
    val scaleTo = lcm(reducingRow[reducingColumn], rowToReduce[reducingColumn])
    scale(rowToReduce, scaleTo / rowToReduce[reducingColumn])
    scale(reducingRow, scaleTo / reducingRow[reducingColumn])
    assert(rowToReduce[reducingColumn] == reducingRow[reducingColumn])

    for (i in rowToReduce.indices)
        rowToReduce[i] -= reducingRow[i]

}

fun reduce(matrix: Array<IntArray>) {
    for (diagonal in 0..min(matrix.lastIndex, matrix[0].lastIndex - 1)) {

        for (reducingRow in diagonal..matrix.lastIndex) {
            if (matrix[reducingRow][diagonal] != 0) {
                swap(matrix, reducingRow, diagonal)
                break
            }
        }

        if (matrix[diagonal][diagonal] < 0)
            scale(matrix[diagonal], -1)

        for (rowToReduce in diagonal + 1..matrix.lastIndex) {
            if (matrix[rowToReduce][diagonal] != 0) {
                reduce(matrix[rowToReduce], matrix[diagonal], diagonal)
            }
        }
    }
}

fun solveMatrix(matrix: Array<IntArray>, buttons: Array<IntArray>, joltages: IntArray, rowToSolve: Int, nextUnknown: Int,
                maxValues: IntArray, alreadyAssigned: IntArray, minimumPresses: PInt) {

    if (rowToSolve == -1) {
        val accumulatedJolts = IntArray(joltages.size)
        for (buttonIndex in buttons.indices)
            for (counter in buttons[buttonIndex])
                accumulatedJolts[counter] += alreadyAssigned[buttonIndex]

        if (accumulatedJolts.contentEquals(joltages))
            minimumPresses.v = min(minimumPresses.v, alreadyAssigned.sum())
        return
    }

    if (nextUnknown > rowToSolve) {
        for (guess in 0..maxValues[nextUnknown]) {
            alreadyAssigned[nextUnknown] = guess
            solveMatrix(matrix, buttons, joltages, rowToSolve, nextUnknown - 1, maxValues, alreadyAssigned, minimumPresses)
        }
        return
    }

    if (matrix[rowToSolve][nextUnknown] == 0) {
        for (guess in 0..maxValues[nextUnknown]) {
            alreadyAssigned[nextUnknown] = guess
            solveMatrix(matrix, buttons, joltages, rowToSolve - 1, nextUnknown - 1, maxValues, alreadyAssigned, minimumPresses)
        }
        return
    }

    var rowTargetSum = matrix[rowToSolve].last()
    for (known in nextUnknown + 1 until alreadyAssigned.size) {
        rowTargetSum -= matrix[rowToSolve][known] * alreadyAssigned[known]
    }

    if (rowTargetSum % matrix[rowToSolve][nextUnknown] != 0)
        return

    val tentativeSolution = rowTargetSum / matrix[rowToSolve][nextUnknown]
    if (tentativeSolution < 0)
        return

    alreadyAssigned[nextUnknown] = tentativeSolution

    solveMatrix(matrix, buttons, joltages, rowToSolve - 1, nextUnknown - 1, maxValues, alreadyAssigned, minimumPresses)
    return
}

fun assert(assertion: Boolean) {
    assert(assertion, "")
}
fun assert(assertion: Boolean, message: String) {
    if (!assertion)
        throw AssertionError(message)
}