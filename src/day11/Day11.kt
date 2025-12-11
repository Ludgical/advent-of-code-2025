package day11

import java.io.File
import kotlin.collections.set

fun main() {
    val lines = File("src/day11/input.txt").readLines()

    val connections = lines.associate { line ->
        line.take(3) to line.substring(5).split(" ").toSet()
    }
    println(connections)

    println(part1(connections))
    println(part2(connections))
}

fun part1(connections: Map<String, Set<String>>): Int {
    val pathAmountToOutMap = mutableMapOf<String, Int>()

    return getPathAmountToOut("you", connections, pathAmountToOutMap)
}

fun getPathAmountToOut(device: String, connections: Map<String, Set<String>>, pathAmountToOutMap: MutableMap<String, Int>): Int {
    if (device == "out")
        return 1
    var pathAmountToOut = pathAmountToOutMap[device]
    if (pathAmountToOut != null)
        return pathAmountToOut

    pathAmountToOut = 0
    for (output in connections[device]!!) {
        pathAmountToOut += getPathAmountToOut(output, connections, pathAmountToOutMap)
        pathAmountToOutMap[device] = pathAmountToOut
    }

    return pathAmountToOut
}

fun part2(connections: Map<String, Set<String>>): Long {
    val pathAmountToOutMap = mutableMapOf<Pair<String, Pair<Boolean, Boolean>>, Long>()

    return getPathAmountToOutFftAndDac("svr", connections, pathAmountToOutMap, passedThroughFft=false, passedThroughDac=false)
}

fun getPathAmountToOutFftAndDac(
    device: String, connections: Map<String, Set<String>>, pathAmountToOutFftAndDacMap: MutableMap<Pair<String, Pair<Boolean, Boolean>>, Long>,
    passedThroughFft: Boolean, passedThroughDac: Boolean): Long {

    if (device == "out")
        return if (passedThroughFft && passedThroughDac) 1 else 0

    var pathAmountToOut = pathAmountToOutFftAndDacMap[Pair(device, Pair(passedThroughFft, passedThroughDac))]
    if (pathAmountToOut != null)
        return pathAmountToOut

    pathAmountToOut = 0

    for (output in connections[device]!!) {
        pathAmountToOut += getPathAmountToOutFftAndDac(
            output, connections, pathAmountToOutFftAndDacMap,
            passedThroughFft || device == "fft",
            passedThroughDac || device == "dac",
        )
        pathAmountToOutFftAndDacMap[Pair(device, Pair(passedThroughFft, passedThroughDac))] = pathAmountToOut
    }

    return pathAmountToOut
}