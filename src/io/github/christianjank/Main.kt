package io.github.christianjank

import io.github.christianjank.lesson01.createFace
import io.github.christianjank.lesson01.createLines
import io.github.christianjank.lesson2.createTriangles

const val imageViewer = "\"C:/Program Files/paint.net/PaintDotNet.exe\""

fun main() {
    imageViewer.runCommand(fileArg = createLines())
    imageViewer.runCommand(fileArg = createFace())
    imageViewer.runCommand(fileArg = createTriangles())
}