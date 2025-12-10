package day10

import java.io.File

fun main() {
    val lines = File("src/day10/input.txt").readLines()

    val requiredLights = Array(lines.size) { i -> lines[i].substring(1, lines[i].indexOf(']')) }

    val buttons = Array(lines.size) { i ->
        val items = lines[i].split(' ')
        items.take(items.size - 1).takeLast(items.size - 2).map {
            it.substring(1, it.length - 1).split(',').map { it2 -> it2.toInt() }.toSet()
        }.toTypedArray()
    }

    val joltages = Array(lines.size) { i ->
        lines[i].substring(lines[i].indexOf('{') + 1, lines[i].indexOf('}')).split(',').map { it.toInt() }.toTypedArray()
    }

    println(part1(requiredLights, buttons))
    println(part2(requiredLights, buttons, joltages))
}

fun part1(allRequiredLights: Array<String>, allButtons: Array<Array<Set<Int>>>): Int {
    var buttonPresses = 0

    for (i in allRequiredLights.indices) {
        val requiredLights = allRequiredLights[i]
        val buttons = allButtons[i]

        val requiredButton = mutableSetOf<Int>()
        for ((index, light) in requiredLights.withIndex())
            if (light == '#')
                requiredButton.add(index)

        if (buttons.contains(requiredButton)) {
            buttonPresses++
            continue
        }

        //Button to the indexes of the buttons that simulate pressing the button
        val availableButtons = mutableMapOf<Set<Int>, Set<Int>>()
        for ((index, button) in buttons.withIndex())
            availableButtons[button] = setOf(index)

        val newButtons = mutableMapOf<Set<Int>, Set<Int>>()
        var done = false

        while (true) {
            for (button1 in availableButtons) {
                for (button2 in availableButtons) {
                    if (button1.value.containsAny(button2.value)) {
                        continue
                    }
                    val newButton = mutableSetOf<Int>()
                    for (lightIndex in requiredLights.indices)
                        if (button1.key.count { it == lightIndex } + button2.key.count { it == lightIndex } == 1)
                            newButton.add(lightIndex)

                    val toGetToNewButton = mutableSetOf<Int>()
                    toGetToNewButton.addAll(button1.value)
                    toGetToNewButton.addAll(button2.value)

                    val prevToGetToNewButton = availableButtons[newButton]

                    if (prevToGetToNewButton == null)
                        newButtons[newButton] = toGetToNewButton
                    else
                        newButtons[newButton] = setOf(toGetToNewButton, prevToGetToNewButton).minBy { it.size }

                    if (newButton == requiredButton) {
                        buttonPresses += toGetToNewButton.size
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
        }
    }

    return buttonPresses
}

fun part2(requiredLights: Array<String>, buttons: Array<Array<Set<Int>>>, joltages: Array<Array<Int>>): Long {
    return 0
}

fun <T> Collection<T>.containsAny(values: Collection<T>): Boolean {
    for (val1 in this)
        for (val2 in values)
            if (val1 == val2)
                return true
    return false
}