package day12

import java.io.File

fun main() {
    val lines = File("src/day12/input.txt").readLines()

    val shapes = mutableListOf<MutableList<Array<BooleanArray>>>()
    val regions = mutableListOf<Pair<Int, Int>>()
    val quantities = mutableListOf<IntArray>()

    var index = 0
    while (index < lines.size) {
        if (lines[index].isEmpty()) {
            index++
            continue
        }
        else if (lines[index][1] == ':') {
            shapes.add(mutableListOf())
            val shapeIndex = lines[index][0].digitToInt()
            var shape = Array(3) { i -> lines[index + i + 1].toCharArray() }.map { arr -> arr.map { it == '#' }.toBooleanArray() }.toTypedArray()
            repeat (4) {
                val rotated = rotate(shape)
                shapes[shapeIndex].add(rotated)
                shapes[shapeIndex].add(flip(rotated))
                shape = Array(3) { i -> rotated[i].copyOf() }
            }

            index += 3
        }
        else if (lines[index].contains('x')) {
            val (region, quantity) = lines[index].split(':').map { it.trim() }

            val (width, height) = region.split('x').map { it.toInt() }
            regions.add(Pair(width, height))

            quantities.add(quantity.split(' ').map { it.toInt() }.toIntArray())
        }
        index++
    }

    shapes.sortByDescending {
        var filled = 0
        for (i in it[0].indices)
            for (j in it[0][i].indices)
                if (it[0][i][j])
                    filled++
        filled
    }

    for (group in shapes) {
        val toRemove = mutableListOf<Array<BooleanArray>>()
        for (i in group.indices) {
            for (j in 1 + i..group.lastIndex)
                if (deepEquals(group[i], group[j]))
                    toRemove.add(group[i])
        }
        group.removeAll(toRemove)
    }

    for (group in shapes) {
        for (shape in group) {
            for (bools in shape) {
                for (bool in bools)
                    print(if (bool) '#' else '.')
                println()
            }
            println()
        }
        println()
    }
    println(regions.toTypedArray().contentDeepToString())
    println(quantities.toTypedArray().contentDeepToString())

    val arrShapes = shapes.map { a -> a.map { b -> b }.toTypedArray() }.toTypedArray()

    println(part1(arrShapes, regions, quantities))
}

fun deepEquals(first: Array<BooleanArray>, other: Array<BooleanArray>): Boolean {
    for (i in 0..2)
        for (j in 0..2)
            if (first[i][j] != other[i][j])
                return false
    return true
}
fun deepCopy(arr: Array<BooleanArray>): Array<BooleanArray> {
    val width = arr.size
    val height = arr[0].size
    val new = Array(width) { BooleanArray(height) }
    for (i in 0 until width)
        for (j in 0 until height)
            new[i][j] = arr[i][j]
    return new
}

fun rotate(shape: Array<BooleanArray>): Array<BooleanArray> {
    val newShape = Array(3) { i -> shape[i].copyOf() }
    newShape[0][0] = shape[0][2]
    newShape[1][0] = shape[0][1]
    newShape[2][0] = shape[0][0]
    newShape[2][1] = shape[1][0]
    newShape[2][2] = shape[2][0]
    newShape[1][2] = shape[2][1]
    newShape[0][2] = shape[2][2]
    newShape[0][1] = shape[1][2]
    return newShape
}
fun flip(shape: Array<BooleanArray>): Array<BooleanArray> {
    val newShape = Array(3) { i -> shape[i].copyOf() }
    newShape[0][0] = shape[2][0]
    newShape[0][1] = shape[2][1]
    newShape[0][2] = shape[2][2]
    newShape[2][0] = shape[0][0]
    newShape[2][1] = shape[0][1]
    newShape[2][2] = shape[0][2]
    return newShape
}

fun part1(shapes: Array<Array<Array<BooleanArray>>>, regions: MutableList<Pair<Int, Int>>, quantities: List<IntArray>): Int {
    var amount = 0

    val toIgnore = mutableListOf<Int>()

    for (i in regions.indices) {
        val area = (regions[i].first) * (regions[i].second)
        var filled = 0
        for (quantity in quantities[i])
            filled += 9 * quantity

        if (area < filled)
            toIgnore.add(i)
    }

    println(regions.size)
    println(regions.size - toIgnore.size)

    for (i in regions.indices) {
        if (toIgnore.contains(i))
            continue

        val region = Array(regions[i].first) { BooleanArray(regions[i].second) { false } }

        if (allShapesFit(shapes, region, quantities[i])) {
            println("$i works")
            amount++
        }
    }

    return amount
}

fun allShapesFit(shapes: Array<Array<Array<BooleanArray>>>, region: Array<BooleanArray>, quantities: IntArray): Boolean {
    var allZeros = true
    for (v in quantities)
        if (v != 0) {
            allZeros = false
            break
        }
    if (allZeros)
        return true

    val width = region.size
    val height = region[0].size

    for (y in 0..region[0].lastIndex) {
        for (x in 0..region.lastIndex) {

            if (!region[x][y]) {
                for (quantityIndex in 0..quantities.lastIndex) {
                    val quantity = quantities[quantityIndex]
                    if (quantity == 0)
                        continue

                    for (shape in shapes[quantityIndex]) {
                        var canPlace = true
                        for (i in 0..2) {
                            for (j in 0..2) {
                                val newX = x + i
                                val newY = y + j
                                if (newX >= width || newX < 0 || newY >= height || newY < 0) {
                                    canPlace = false
                                    break
                                }
                                if (shape[i][j] && region[newX][newY]) {
                                    canPlace = false
                                    break
                                }
                            }
                            if (!canPlace)
                                break
                        }
                        if (!canPlace)
                            continue

                        val newRegion = deepCopy(region)
                        val newQuantities = quantities.copyOf()
                        newQuantities[quantityIndex]--
                        for (i in 0..2)
                            for (j in 0..2) {
                                val newX = x + i
                                val newY = y + j
                                newRegion[newX][newY] = region[newX][newY] || shape[i][j]
                            }

                        if (allShapesFit(shapes, newRegion, newQuantities))
                            return true
                    }
                }
            }
        }
    }
    return false
}