package io.github.christianjank.lesson01

import io.github.christianjank.renderer.Image
import io.github.christianjank.renderer.BGRAColor
import io.github.christianjank.renderer.BGRAColor.Companion.BLUE
import io.github.christianjank.renderer.BGRAColor.Companion.GREEN
import io.github.christianjank.renderer.BGRAColor.Companion.RED
import io.github.christianjank.renderer.Sketch
import kotlin.math.abs

@ExperimentalUnsignedTypes
class Bresenham : Sketch {
    override val name = "Lesson1_Bresenham"
    override val width = 300
    override val height = 300
    override val image = Image(width, height, BGRAColor.BytesPerPixel.RGBA)

    init {
        draw()
    }

    fun draw() {
        image.lineWithPrimitiveBresenham(30, 10, 270, 60, GREEN) // coarse steps means there are gaps
        image.lineWithBresenhamFillingX(30, 30, 270, 120, RED) // dynamic steps based on the x-length
        image.lineWithBresenhamFillingX(30, 30, 90, 270, RED) // steep line means there are gaps once again
        image.line(30.0, 60.0, 90.0, 270.0, BLUE)
        image.line(30.0, 60.0, 270.0, 150.0, BLUE)
        image.flipVertically() // so that the origin is at the bottom left corner of the image
    }
}


@ExperimentalUnsignedTypes
fun Image.lineWithPrimitiveBresenham(x0: Int, y0: Int, x1: Int, y1: Int, color: BGRAColor) {
    var t = 0.0
    val step = 0.05
    while (t <= 1) {
        val x = x0 + (x1 - x0) * t
        val y = y0 + (y1 - y0) * t
        this.setPixel(x.toInt(), y.toInt(), color)
        t += step
    }
}

@ExperimentalUnsignedTypes
fun Image.lineWithBresenhamFillingX(x0: Int, y0: Int, x1: Int, y1: Int, color: BGRAColor) {
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

