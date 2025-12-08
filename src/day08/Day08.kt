package day08

import java.io.File
import kotlin.math.pow

fun main() {
    val lines = File("src/day08/input.txt").readLines()

    val junctions = Array(lines.size) { i ->
        val nums = lines[i].split(",").map { it.toInt() }
        Triple(nums[0], nums[1], nums[2])
    }

    println(part1(junctions))
    println(part2(junctions))
}

fun part1(junctions: Array<Triple<Int, Int, Int>>): Int {
    val circuits = mutableMapOf<MutableSet<Triple<Int, Int, Int>>, Int>()
    for (i in junctions.indices)
        circuits[mutableSetOf(junctions[i])] = 1

    val connectionAmount = 1000
    val closestJunctionPairs = mutableMapOf<Pair<Triple<Int, Int, Int>, Triple<Int, Int, Int>>, Int>()

    for (junc1 in junctions) {
        for (junc2 in junctions) {
            if (junc1 == junc2 || closestJunctionPairs.containsKey(Pair(junc2, junc1)))
                continue

            val dist = ((junc2.first - junc1.first).toDouble().pow(2) +
                       (junc2.second - junc1.second).toDouble().pow(2) +
                       (junc2.third - junc1.third).toDouble().pow(2)).toInt()

            if (closestJunctionPairs.size < connectionAmount) {
                closestJunctionPairs[Pair(junc1, junc2)] = dist
                continue
            }

            var highestDist = -1
            var pairWithHighestDist = Pair(Triple(-1, -1, -1), Triple(-1, -1, -1))
            for (junctionPair in closestJunctionPairs.keys) {
                if (closestJunctionPairs[junctionPair]!! > highestDist) {
                    highestDist = closestJunctionPairs[junctionPair]!!
                    pairWithHighestDist = junctionPair
                }
            }

            if (highestDist > dist) {
                closestJunctionPairs[Pair(junc1, junc2)] = dist
                closestJunctionPairs.remove(pairWithHighestDist)
            }
        }
    }

    for (junctionPair in closestJunctionPairs.keys.sortedBy { closestJunctionPairs[it] }) {
        //Find the circuit with the first junction
        var circuitWithFirstJunc = emptySet<Triple<Int, Int, Int>>()
        for (circuit in circuits.keys)
            if (circuit.contains(junctionPair.first)) {
                circuitWithFirstJunc = circuit
                break
            }
        //Find the circuit with the second junction
        var circuitWithSecondJunc = emptySet<Triple<Int, Int, Int>>()
        for (circuit in circuits.keys)
            if (circuit.contains(junctionPair.second)) {
                circuitWithSecondJunc = circuit
                break
            }

        //Ignore if the closest junction (junctionPair.second) is already in the circuit
        if (circuitWithFirstJunc.contains(junctionPair.second) || circuitWithSecondJunc.contains(junctionPair.first)) {
            continue
        }

        //Add the closest junction (junctionPair.second) to the circuit
        val newCircuit = circuitWithFirstJunc.toMutableSet()
        newCircuit.addAll(circuitWithSecondJunc)

        circuits[newCircuit] = newCircuit.size

        circuits.remove(circuitWithFirstJunc)
        circuits.remove(circuitWithSecondJunc)
    }

    val sortedCircuitSizes = circuits.values.sortedDescending()

    var result = 1

    for (size in sortedCircuitSizes.take(3))
        result *= size

    return result
}

fun part2(junctions: Array<Triple<Int, Int, Int>>): Long {
    val circuits = mutableSetOf<MutableSet<Triple<Int, Int, Int>>>()
    for (junction in junctions)
        circuits.add(mutableSetOf(junction))

    //Pair of junctions mapped to the distance between them
    val junctionPairs = mutableMapOf<Pair<Triple<Int, Int, Int>, Triple<Int, Int, Int>>, Int>()

    for (junc1 in junctions) {
        for (junc2 in junctions) {
            if (junc1 == junc2 || junctionPairs.containsKey(Pair(junc2, junc1)))
                continue

            val dist = ((junc2.first - junc1.first).toDouble().pow(2) +
                    (junc2.second - junc1.second).toDouble().pow(2) +
                    (junc2.third - junc1.third).toDouble().pow(2)).toInt()

            junctionPairs[Pair(junc1, junc2)] = dist
        }
    }

    for (junctionPair in junctionPairs.keys.sortedBy { junctionPairs[it] }) {
        //Find the circuit with the first junction
        var circuitWithFirstJunc = emptySet<Triple<Int, Int, Int>>()
        for (circuit in circuits)
            if (circuit.contains(junctionPair.first)) {
                circuitWithFirstJunc = circuit
                break
            }
        //Find the circuit with the second junction
        var circuitWithSecondJunc = emptySet<Triple<Int, Int, Int>>()
        for (circuit in circuits)
            if (circuit.contains(junctionPair.second)) {
                circuitWithSecondJunc = circuit
                break
            }

        //Ignore if a junction is already in the opposite circuit
        if (circuitWithFirstJunc.contains(junctionPair.second) || circuitWithSecondJunc.contains(junctionPair.first)) {
            continue
        }

        //Merge the circuits into one
        val newCircuit = circuitWithFirstJunc.toMutableSet()

        newCircuit.addAll(circuitWithSecondJunc)
        circuits.add(newCircuit)

        circuits.remove(circuitWithFirstJunc)
        circuits.remove(circuitWithSecondJunc)

        if (circuits.size == 1)
            return junctionPair.first.first.toLong() * junctionPair.second.first
    }
    return -1
}