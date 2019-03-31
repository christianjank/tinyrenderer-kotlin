package io.github.christianjank.renderer.fileformat

import io.github.christianjank.renderer.BGRAColor
import io.github.christianjank.renderer.BGRAColor.Companion.GREEN
import io.github.christianjank.renderer.BGRAColor.Companion.RED
import io.github.christianjank.renderer.Image
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import kotlin.experimental.and

fun main() {
    writeTest()
    readTest()
}

class TGAHeader {
    var idLength: Byte = 0
    var colormapType: Byte = 0
    var datatypeCode: Byte = 0
    var colormapOrigin: Short = 0
    var colormapLength: Short = 0
    var colormapDepth: Byte = 0
    var xOrigin: Short = 0
    var yOrigin: Short = 0
    var width: Short = 0
    var height: Short = 0
    var bitsPerPixel: Byte = 0
    var imageDescriptor: Byte = 0

    fun toByteArray(): ByteArray {
        val byteArray = ByteArray(TGA_HEADER_SIZE)
        val buffer = ByteBuffer.allocate(TGA_HEADER_SIZE)
        buffer
            .order(ByteOrder.LITTLE_ENDIAN)
            .put(idLength)
            .put(colormapType)
            .put(datatypeCode)
            .putShort(colormapOrigin)
            .putShort(colormapLength)
            .put(colormapDepth)
            .putShort(xOrigin)
            .putShort(yOrigin)
            .putShort(width)
            .putShort(height)
            .put(bitsPerPixel)
            .put(imageDescriptor)
            .flip()
            .get(byteArray)
        return byteArray
    }

    companion object {
        const val TGA_HEADER_SIZE = 18
        /**
         *  Assumes buffer position is at the start of the header bytes and will consume bytes, advancing the position
         *  past the header.
         *  @param buffer must be little-endian
         */
        fun readHeaderBytes(buffer: ByteBuffer): TGAHeader {
            val header = TGAHeader()
            header.idLength = buffer.get()
            header.colormapType = buffer.get()
            header.datatypeCode = buffer.get()
            header.colormapOrigin = buffer.short
            header.colormapLength = buffer.short
            header.colormapDepth = buffer.get()
            header.xOrigin = buffer.short
            header.yOrigin = buffer.short
            header.width = buffer.short
            header.height = buffer.short
            header.bitsPerPixel = buffer.get()
            header.imageDescriptor = buffer.get()
            return header
        }
    }
}

fun readTGA(file: File): Image {
    if (!file.exists()) throw IllegalArgumentException("File doesn't exist")

    val fileBytes = file.readBytes()
    val byteBuffer = ByteBuffer.wrap(fileBytes).order(ByteOrder.LITTLE_ENDIAN)
    val header = TGAHeader.readHeaderBytes(byteBuffer)
    val width = header.width
    val height = header.height
    val bytesPerPixel: BGRAColor.BytesPerPixel =
        BGRAColor.BytesPerPixel.fromInt(header.bitsPerPixel.toInt() ushr 3)

    println("Width: $width, height: $height, bytes-per-pixel: $bytesPerPixel")
    if (width <= 0 || height <= 0) {
        throw IllegalArgumentException("Invalid input values")
    }

    val byteArrayLength: Int = bytesPerPixel.value * width * height

    val imageBytes: UByteArray
    if (header.datatypeCode.toInt() == 3 || header.datatypeCode.toInt() == 2) {
        imageBytes = UByteArray(byteArrayLength)
        byteBuffer.get(imageBytes.asByteArray())
    } else if (header.datatypeCode.toInt() == 10 || header.datatypeCode.toInt() == 11) {
        imageBytes = loadRleData(byteBuffer, width.toInt(), height.toInt(), bytesPerPixel.value)
    } else {
        println("datatype code ${header.datatypeCode}")
        throw IllegalArgumentException("Invalid file format")
    }
    val image = Image(width.toInt(), height.toInt(), bytesPerPixel, imageBytes)
    if ((header.imageDescriptor and 0x20) == 0.toByte()) {
        image.flipVertically()
    }
    if ((header.imageDescriptor and 0x10) != 0.toByte()) {
        image.flipHorizontally()
    }
    return image
}

fun writeTGA(image: Image, file: File, rle: Boolean = false) {
    file.delete()
    file.createNewFile()
    if (!file.canWrite()) {
        throw IllegalArgumentException("Cannot write to file")
    }
    val developerAreaRef = byteArrayOf(0, 0, 0, 0)
    val extensionAreaRef = byteArrayOf(0, 0, 0, 0)
    val footer = "TRUEVISION-XFILE."
    val footerBytes = footer.toByteArray(StandardCharsets.US_ASCII)

    val header = TGAHeader()
    val bytesPerPixel = image.bytesPerPixel
    header.bitsPerPixel = (bytesPerPixel.value shl 3).toByte()
    header.width = image.width.toShort()
    header.height = image.height.toShort()
    header.datatypeCode = if (bytesPerPixel == BGRAColor.BytesPerPixel.GRAYSCALE) {
        if (rle) {
            11
        } else {
            3
        }
    } else {
        if (rle) {
            10
        } else {
            2
        }
    }
    header.imageDescriptor = 0x20 // top-left origin

    BufferedOutputStream(FileOutputStream(file))
        .use { buffer ->
            buffer.write(header.toByteArray())

            if (rle) {
                unloadRleData(buffer, image)
            } else {
                buffer.write(image.data.asByteArray())
            }

            buffer.write(developerAreaRef)
            buffer.write(extensionAreaRef)
            buffer.write(footerBytes)
            buffer.write(byteArrayOf(0x00))
        }
}

private fun loadRleData(fileBytes: ByteBuffer, width: Int, height: Int, bytesPerPixel: Int): UByteArray {
    val pixelCount: Int = width * height
    val targetByteBuffer = ByteBuffer.allocate(pixelCount * bytesPerPixel)
    var currentPixel = 0
    do {
        var chunkHeader = fileBytes.get().toUByte()
        if (chunkHeader < 128u) {
            chunkHeader++
            for (i in 0 until chunkHeader.toInt()) {
                targetByteBuffer.put(fileBytes.array(), fileBytes.position(), bytesPerPixel)
                fileBytes.position(fileBytes.position() + bytesPerPixel)
                currentPixel++
                if (currentPixel > pixelCount) {
                    throw IllegalStateException("Too many pixels read")
                }
            }
        } else {
            chunkHeader = (chunkHeader - 127u).toUByte()
            val compressedPixelData = ByteArray(bytesPerPixel)
            fileBytes.get(compressedPixelData)

            for (i in 0 until chunkHeader.toInt()) {
                targetByteBuffer.put(compressedPixelData)
                currentPixel++
                if (currentPixel > pixelCount) {
                    throw IllegalStateException("Too many pixels read")
                }
            }
        }
    } while (currentPixel < pixelCount)
    return targetByteBuffer.array().toUByteArray()
}

private fun unloadRleData(buffer: BufferedOutputStream, image: Image) {
    val maxChunkLength = 128
    val singleByteArray = ByteArray(1)
    val pixelCount = image.width * image.height
    val bytesPerPixel = image.bytesPerPixel
    var currentPixel = 0
    while (currentPixel < pixelCount) {
        val chunkStart = currentPixel * bytesPerPixel.value
        var currentByte = currentPixel * bytesPerPixel.value
        var runLength = 1
        var raw = true
        while (currentPixel + runLength < pixelCount && runLength < maxChunkLength) {
            var nextPixelEquals = true
            for (t in 0 until bytesPerPixel.value) {
                if (!nextPixelEquals) break

                nextPixelEquals = (image.data[currentByte + t] == image.data[currentByte + t + bytesPerPixel.value])
            }
            currentByte += bytesPerPixel.value
            if (runLength == 1) {
                raw = !nextPixelEquals
            }
            if (raw && nextPixelEquals) {
                runLength -= 1
                break
            }
            if (!raw && !nextPixelEquals) {
                break
            }
            runLength += 1
        }
        currentPixel += runLength
        val runLengthByte: UByte =
            if (raw) (runLength - 1).toUByte()
            else (runLength + 127).toUByte()

        val length = if (raw) runLength * bytesPerPixel.value else bytesPerPixel.value

        singleByteArray[0] = runLengthByte.toByte()
        buffer.write(singleByteArray)
        buffer.write(image.data.toByteArray(), chunkStart, length)
    }
}

private val testOutputFile = File("test-output.tga")
private val copiedFile = File("copied-$testOutputFile")

private fun readTest() {
    val readTGA = readTGA(testOutputFile)
    readTGA.flipHorizontally()
    for (x in 0..100) {
        for (y in 0..100) {
            readTGA.setPixel(x, y, GREEN)
        }
    }
    writeTGA(readTGA, copiedFile, false)
}

private fun writeTest() {
    val image = Image(1000, 1000, BGRAColor.BytesPerPixel.RGB)
    for (x in 0..100) {
        for (y in 0..100) {
            image.setPixel(x, y, RED)
        }
    }
    image.flipVertically()
    writeTGA(image, testOutputFile, false)
}
