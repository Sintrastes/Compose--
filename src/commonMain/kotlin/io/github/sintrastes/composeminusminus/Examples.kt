package io.github.sintrastes.composeminusminus

import kotlinx.coroutines.GlobalScope

fun UI.Builder.CounterExample() = Column {
    val initialCount = 0

    val clicks = Button("+")

    val count = clicks.reduce(GlobalScope, initialCount) { _, count ->
        count + 1
    }

    val countDisplay = count
        .map { it.toString() }

    Text(countDisplay)
}

fun UI.Builder.AdderExample() = Column {
    val xValues = IntEntry(0)

    Text("+".const)

    val yValues = IntEntry(0)

    Text("=".const)

    val sum = combine(xValues, yValues) { x, y -> x + y }

    Text(sum.map { it.toString() })
}