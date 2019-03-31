package io.github.christianjank.renderer

@Suppress("unused")
@ExperimentalUnsignedTypes
data class BGRAColor(val rawColorArray: UByteArray = UByteArray(size = 4), val bytespp: BytesPerPixel) {
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
        val WHITE: BGRAColor = BGRAColor(255u, 255u, 255u, 255u)
        val BLACK: BGRAColor = BGRAColor(0u, 0u, 0u, 255u)
        val RED: BGRAColor = BGRAColor(0u, 0u, 255u, 255u)
        val GREEN: BGRAColor = BGRAColor(0u, 255u, 0u, 255u)
        val BLUE: BGRAColor = BGRAColor(255u, 0u, 0u, 255u)
    }
}
