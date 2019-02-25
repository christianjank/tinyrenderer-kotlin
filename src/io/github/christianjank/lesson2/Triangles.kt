package io.github.christianjank.lesson2

import io.github.christianjank.Canvas
import io.github.christianjank.Color
import io.github.christianjank.Color.Companion.GREEN
import io.github.christianjank.Color.Companion.RED
import io.github.christianjank.Color.Companion.WHITE
import io.github.christianjank.lesson01.Vertex
import io.github.christianjank.writeTGA
import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
    createTriangles()
}

private const val outputFile = "triangles.tga"

fun createTriangles(): String {
    val image = Canvas(1000, 1000, Color.BytesPerPixel.RGB)
    val t0 = listOf(Vertex(10.0, 140.0), Vertex(50.0, 320.0), Vertex(70.0, 160.0))
    val t1 = listOf(Vertex(320.0, 100.0), Vertex(450.0, 1.0), Vertex(600.0, 180.0))
    val t2 = listOf(Vertex(180.0, 150.0), Vertex(120.0, 160.0), Vertex(130.0, 180.0))

    val t3 = listOf(Vertex(950.0, 950.0), Vertex(700.0, 950.0), Vertex(950.0, 750.0))
    val t4 = listOf(Vertex(700.0, 950.0), Vertex(700.0, 750.0), Vertex(950.0, 750.0))

    val t32 = listOf(Vertex(650.0, 650.0), Vertex(400.0, 650.0), Vertex(650.0, 450.0))
    val t42 = listOf(Vertex(400.0, 650.0), Vertex(400.0, 450.0), Vertex(650.0, 450.0))

    fillTriangle(t0, image, RED)
    fillTriangle(t1, image, WHITE)
    fillTriangle(t2, image, GREEN)

    fillTriangle(t3, image, GREEN)
    fillTriangle(t4, image, RED)
    fillTriangle(t32, image, WHITE)
    fillTriangle(t42, image, WHITE)
    outlineTriangle(t32, image, RED)
    outlineTriangle(t42, image, GREEN)

    image.flipVertically()
    writeTGA(image, File(outputFile))

    return outputFile
}

fun outlineTriangle(vertices: List<Vertex>, image: Canvas, color: Color) {
    image.line(vertices[0], vertices[1], color)
    image.line(vertices[1], vertices[2], color)
    image.line(vertices[2], vertices[0], color)
}


fun fillTriangle(vertices: List<Vertex>, image: Canvas, color: Color) {
    val sorted = vertices.sortedBy(Vertex::y)
    val lowestVertex = sorted[0]
    val middleVertex = sorted[1]
    val highestVertex = sorted[2]

    val dxUpper = highestVertex.x - middleVertex.x
    val dxLower = middleVertex.x - lowestVertex.x
    val dxLong = highestVertex.x - lowestVertex.x

    val minY = lowestVertex.y.toInt()
    val maxY = highestVertex.y.toInt()
    val triangleHeight = maxY - minY
    val segmentHeight = middleVertex.y
    val aboveMidPointSteps = maxY - segmentHeight
    val midPointSteps = segmentHeight - minY
    for (i in 0..triangleHeight) {
        val longSideEdge = lowestVertex.x + dxLong * (i / triangleHeight.toDouble())
        val segmentedSideEdge =
            if (i + minY >= segmentHeight) when (aboveMidPointSteps) {
                0.0 -> middleVertex.x
                else -> middleVertex.x + dxUpper * ((i + minY - segmentHeight) / aboveMidPointSteps)
            }
            else lowestVertex.x + dxLower * (i / midPointSteps)
        drawLineBetween(segmentedSideEdge.toInt(), longSideEdge.toInt(), image, i + minY, color)
    }
}

private fun drawLineBetween(edge1: Int, edge2: Int, image: Canvas, lineY: Int, color: Color) {
    val start = min(edge1, edge2)
    val end = max(edge1, edge2)
    for (lineX in start..end) {
        image.setPixel(lineX, lineY, color)
    }
}