package io.github.christianjank.lesson2


fun createMutableTriangle() {
    val triangleU = MutableTriangle.A(1.0, 2.0).B(3.0, 4.0).C(5.0, 6.0)

    val triangleV: MutableTriangle = MutableTriangle().apply {
        a = MutableVertex(1.0, 2.0)
        b = MutableVertex(3.0, 4.0)
        c = MutableVertex(5.0, 6.0)
    }
}

fun createTriangleViaBuilder() {
    val triangleU = triangle {
        a = vertex { x = 1.0; y = 2.0; z = 3.0 }
        b = vertex { x = 1.0; y = 2.0; z = 3.0 }
        c = vertex { x = 1.0; y = 2.0; z = 3.0 }
    }

    val triangleV = triangle {
        a = MutableVertex(x = 1.0, y = 2.0, z = 3.0)
        b = MutableVertex(1.0, 2.0, 3.0)
        c = vertex { x = 1.0; y = 2.0; z = 3.0 }
    }
}

fun createImmutableTriangle() {
    immutableTriangle {
        A { x = 1.0; y = 2.0; z = 3.0 }
        B { x = 1.0; y = 2.0; z = 3.0 }
        C { x = 1.0; y = 2.0; z = 3.0 }
    }

    immutableTriangle {
        vertexB
        B {
            this@immutableTriangle.vertexB
        }
    }
}

fun triangle(init: MutableTriangle.() -> Unit): MutableTriangle = MutableTriangle().apply(init)

fun vertex(init: MutableVertex.() -> Unit): MutableVertex = MutableVertex().also(init)

data class MutableVertex(
        var x: Double = 0.0,
        var y: Double = 0.0,
        var z: Double = 0.0
)

data class MutableTriangle(
        var a: MutableVertex = MutableVertex(),
        var b: MutableVertex = MutableVertex(),
        var c: MutableVertex = MutableVertex()
) {
    fun A(x: Double, y: Double, z: Double = 0.0) = apply { a = MutableVertex(x, y, z) }

    fun B(x: Double, y: Double, z: Double = 0.0) = apply { b = MutableVertex(x, y, z) }

    fun C(x: Double, y: Double, z: Double = 0.0) = apply { c = MutableVertex(x, y, z) }

    companion object {
        fun A(x: Double, y: Double, z: Double = 0.0) = MutableTriangle(a = MutableVertex(x, y, z))
        fun B(x: Double, y: Double, z: Double = 0.0) = MutableTriangle(b = MutableVertex(x, y, z))
        fun C(x: Double, y: Double, z: Double = 0.0) = MutableTriangle(c = MutableVertex(x, y, z))
    }
}

@DslMarker
annotation class TriangleDsl

data class ImmutableTriangle(
        val a: ImmutableVertex = ImmutableVertex(),
        val b: ImmutableVertex = ImmutableVertex(),
        val c: ImmutableVertex = ImmutableVertex()
)

data class ImmutableVertex(
        val x: Double = 0.0,
        val y: Double = 0.0,
        val z: Double = 0.0
)

@TriangleDsl
fun immutableTriangle(block: TriangleBuilder.() -> Unit): ImmutableTriangle {
    return TriangleBuilder().apply(block).build()
}

@TriangleDsl
class TriangleBuilder {
    var vertexA: ImmutableVertex = ImmutableVertex()
    var vertexB: ImmutableVertex = ImmutableVertex()
    var vertexC: ImmutableVertex = ImmutableVertex()

    fun A(block: VertexBuilder.() -> Unit) {
        vertexA = VertexBuilder().apply(block).build()
    }

    fun B(block: VertexBuilder.() -> Unit) {
        vertexB = VertexBuilder().apply(block).build()
    }

    fun C(block: VertexBuilder.() -> Unit) {
        vertexC = VertexBuilder().apply(block).build()
    }

    fun build(): ImmutableTriangle = ImmutableTriangle(vertexA, vertexB, vertexC)

}

@TriangleDsl
class VertexBuilder {
    var x: Double = 0.0
    var y: Double = 0.0
    var z: Double = 0.0

    fun build(): ImmutableVertex = ImmutableVertex(x, y, z)
}

