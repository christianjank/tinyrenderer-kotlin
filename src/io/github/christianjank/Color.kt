package io.github.christianjank

data class Color(val rawColorArray: UByteArray = UByteArray(size = 4), val bytespp: BytesPerPixel) {
    constructor(
        blue: UByte = 0u,
        green: UByte = 0u,
        red: UByte = 0u,
        alpha: UByte = 0u,
        bytespp: BytesPerPixel = BytesPerPixel.RGBA
    ) : this(ubyteArrayOf(blue, green, red, alpha), bytespp)

    val blue get() = rawColorArray[0]
    val green get() = rawColorArray[1]
    val red get() = rawColorArray[2]
    val alpha get() = rawColorArray[3]

    enum class BytesPerPixel(val value: Int) {
        GRAYSCALE(1),
        RGB(3),
        RGBA(4);

        companion object {
            fun fromInt(value: Int) = when (value) {
                1 -> GRAYSCALE
                3 -> RGB
                4 -> RGBA
                else -> throw IllegalArgumentException("Unknown bytesPerPixel: $value")
            }
        }
    }

    companion object {
        val WHITE: Color = Color(255u, 255u, 255u, 255u)
        val BLACK: Color = Color(0u, 0u, 0u, 255u)
        val RED: Color = Color(0u, 0u, 255u, 255u)
        val GREEN: Color = Color(0u, 255u, 0u, 255u)
        val BLUE: Color = Color(255u, 0u, 0u, 255u)
    }
}
