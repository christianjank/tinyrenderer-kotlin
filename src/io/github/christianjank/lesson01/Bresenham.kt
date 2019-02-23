package io.github.christianjank.lesson01

import io.github.christianjank.Canvas
import io.github.christianjank.Color
import io.github.christianjank.Color.Companion.BLUE
import io.github.christianjank.Color.Companion.GREEN
import io.github.christianjank.Color.Companion.RED
import io.github.christianjank.writeTGA
import java.io.File
import kotlin.math.abs

fun main() {
    createLines()
}

private const val outputFile = "Lesson1_Bresenham.tga"

fun createLines(): String {
    val image = Canvas(100, 100, Color.BytesPerPixel.RGB)
    image.lineWithPrimitiveBresenham(10, 0, 90, 20, GREEN) // coarse steps means there are gaps
    image.lineWithBresenhamFillingX(10, 10, 90, 40, RED) // dynamic steps based on the x-length
    image.lineWithBresenhamFillingX(10, 10, 30, 90, RED) // steep line means there are gaps once again
    image.line(10.0, 20.0, 30.0, 90.0, BLUE)
    image.line(10.0, 20.0, 90.0, 50.0, BLUE)
    image.flipVertically() // so that the origin is at the bottom left corner of the image
    writeTGA(image, File(outputFile))
    return outputFile
}

fun Canvas.lineWithPrimitiveBresenham(x0: Int, y0: Int, x1: Int, y1: Int, color: Color) {
    var t = 0.0
    val step = 0.05
    while (t <= 1) {
        val x = x0 + (x1 - x0) * t
        val y = y0 + (y1 - y0) * t
        this.setPixel(x.toInt(), y.toInt(), color)
        t += step
    }
}

fun Canvas.lineWithBresenhamFillingX(x0: Int, y0: Int, x1: Int, y1: Int, color: Color) {
    val stepDiv = x0 - x1
    val step: Double = if (stepDiv != 0) 1 / abs(stepDiv).toDouble() else 1.1

    var t = 0.0
    while (t <= 1) {
        val x = x0 + (x1 - x0) * t
        val y = y0 + (y1 - y0) * t
        this.setPixel(x.toInt(), y.toInt(), color)
        t += step
    }
}
