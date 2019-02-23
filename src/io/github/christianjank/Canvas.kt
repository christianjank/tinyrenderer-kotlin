package io.github.christianjank

import kotlin.math.abs

class Canvas constructor(
    val width: Int,
    val height: Int,
    val bytesPerPixel: Color.BytesPerPixel,
    val data: UByteArray = UByteArray(width * height * bytesPerPixel.value)
) {
    fun getPixel(x: Int, y: Int): Color {
        if (x < 0 || y < 0 || x >= width || y >= height) throw IndexOutOfBoundsException("Invalid values of x = $x or y = $y")

        val offset = coords(x, y)
        val rgbArr = UByteArray(4)
        for (i in 0 until bytesPerPixel.value) {
            rgbArr[i] = data[offset + i]
        }
        return Color(rgbArr, bytesPerPixel)
    }

    fun setPixel(x: Int, y: Int, c: Color) {
        if (x < 0 || y < 0 || x >= width || y >= height) throw IndexOutOfBoundsException("Invalid values of x = $x or y = $y")

        val offset = coords(x, y)
        val raw = c.rawColorArray
        for (i in 0 until bytesPerPixel.value) {
            data[offset + i] = raw[i]
        }
    }

    fun flipVertically() {
        val actualWidth = width * bytesPerPixel.value
        val tempArray = UByteArray(actualWidth)
        val mid: Int = (height / 2) - 1
        var upperY = height - 1
        for (y in 0..mid) {
            data.copyInto(tempArray, 0, coords(0, y), coords(width, y))
            data.copyInto(data, coords(0, y), coords(0, upperY), coords(width, upperY))
            tempArray.copyInto(data, coords(0, upperY), 0, tempArray.size)
            upperY -= 1
        }
    }

    fun flipHorizontally() {
        val upperY = height - 1
        val mid: Int = (width / 2) - 1
        for (y in 0..upperY) {
            for (x in 0..mid) {
                val rightX = width - 1 - x
                val leftPixel = getPixel(x, y)
                val rightPixel = getPixel(rightX, y)
                setPixel(x, y, rightPixel)
                setPixel(rightX, y, leftPixel)
            }
        }
    }

    fun line(x0: Double, y0: Double, x1: Double, y1: Double, color: Color) {
        val dx = x1 - x0
        val dy = y1 - y0
        val dxAbs = abs(dx)
        val dyAbs = abs(dy)
        val longSide = if (dxAbs > dyAbs) dxAbs else dyAbs
        val step: Double = if (longSide != 0.0) 1 / longSide else 1.1

        var t = 0.0
        while (t <= 1) {
            val x = x0 + dx * t
            val y = y0 + dy * t
            this.setPixel(x.toInt(), y.toInt(), color)
            t += step
        }
    }

    private fun coords(x: Int, y: Int) = ((y * width) + x) * bytesPerPixel.value

    //    TODO scale
    //    TODO clear
}