package io.github.christianjank.lesson01

import io.github.christianjank.Canvas
import io.github.christianjank.Color
import io.github.christianjank.writeTGA
import java.io.File
import java.net.URL

fun main() {
    createFace()
}

private const val outputFile = "Lesson1_Face.tga"

fun createFace(): String {
    val readModel = readModel()

    val image = Canvas(1000, 1000, Color.BytesPerPixel.RGB)

    for (face in readModel.faces) {
        val vertexOne = readModel.vertices[face.one]
        val vertexTwo = readModel.vertices[face.two]
        val vertexThree = readModel.vertices[face.three]

        val width = image.width - 1
        val height = image.height - 1
        val x0 = (vertexOne.x + 1) * width * 0.5
        val x1 = (vertexTwo.x + 1) * width * 0.5
        val x2 = (vertexThree.x + 1) * width * 0.5
        val y0 = (vertexOne.y + 1) * height * 0.5
        val y1 = (vertexTwo.y + 1) * height * 0.5
        val y2 = (vertexThree.y + 1) * height * 0.5
        image.line(x0, y0, x1, y1, Color.GREEN)
        image.line(x1, y1, x2, y2, Color.GREEN)
        image.line(x2, y2, x0, y0, Color.GREEN)
    }
    image.flipVertically()
    writeTGA(image, File(outputFile))
    return outputFile
}

fun readModel(): Model {
    val resource: URL = Vertex::class.java.getResource("face.obj")
    val readText = resource.readText()

    val vertices = readText.lineSequence()
        .filter { s -> s.startsWith("v ") }
        .map { s ->
            val split = s.split(" ")
            Vertex(split[1].toDouble(), split[2].toDouble(), split[3].toDouble())
        }
        .toList()

    val faces = readText.lineSequence()
        .filter { s -> s.startsWith("f ") }
        .map { s ->
            val split = s.split(" ")
            Face(
                split[1].split("/")[0].toInt() - 1,
                split[2].split("/")[0].toInt() - 1,
                split[3].split("/")[0].toInt() - 1
            )
        }
        .toList()

    return Model(vertices, faces)
}

data class Model(val vertices: List<Vertex>, val faces: List<Face>)

data class Vertex(val x: Double, val y: Double, val z: Double)

data class Face(val one: Int, val two: Int, val three: Int)