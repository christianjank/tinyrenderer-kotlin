package io.github.christianjank

import io.github.christianjank.lesson01.Bresenham
import io.github.christianjank.lesson01.FaceSketch
import io.github.christianjank.renderer.Sketch
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.image.PixelFormat
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import java.nio.ByteBuffer


@ExperimentalUnsignedTypes
class Renderer : Application() {

    override fun start(primaryStage: Stage) {
        val bresenham = Bresenham()
        val canvasPane = bresenham.createFxCanvas()

        val tabPane = TabPane()
        val bresenhamTab = Tab()
        bresenhamTab.text = "Bresenham"
        bresenhamTab.content = canvasPane
        tabPane.tabs.add(bresenhamTab)

        val faceSketch = FaceSketch()
        val faceSketchCanvas = faceSketch.createFxCanvas()
        val faceTab = Tab()
        faceTab.text = "Face"
        faceTab.content = faceSketchCanvas
        tabPane.tabs.add(faceTab)

        val root = BorderPane()
        root.center = tabPane
        primaryStage.scene = Scene(root)
        primaryStage.show()
    }
}

@ExperimentalUnsignedTypes
fun main(vararg args: String) {
    Application.launch(Renderer::class.java, *args)
}

@ExperimentalUnsignedTypes
fun Sketch.createFxCanvas(): StackPane {
    val canvas = javafx.scene.canvas.Canvas(
            this.width.toDouble(),
            this.height.toDouble()
    )
    val gc = canvas.graphicsContext2D
    val holder = StackPane()
    holder.children.add(canvas)
    holder.style = "-fx-background-color: black"

    gc.pixelWriter.setPixels(
            0,
            0,
            this.width,
            this.height,
            PixelFormat.getByteBgraInstance(),
            ByteBuffer.wrap(this.image.data.asByteArray()),
            this.width * 4)
    return holder
}