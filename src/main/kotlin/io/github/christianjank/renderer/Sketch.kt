package io.github.christianjank.renderer

@ExperimentalUnsignedTypes
interface Sketch {
    val width: Int
    val height: Int
    val name: String
    val image: Image
}