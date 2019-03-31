# Adaptation of Tiny Renderer in Kotlin
This is based on the [tinyrenderer course](https://github.com/ssloy/tinyrenderer/wiki).

Using it as a chance to also try out Kotlin.
Project is a simple IntelliJ module for now. 
 
 <!-- 
 Other stuff I might touch upon down the line
 - raymarching http://www.michaelwalczyk.com/blog/2017/5/25/ray-marching
 - https://thebookofshaders.com/ 
 -->
 
     if (segmentOnRight) {
         val dxRightUpper = leftVertex.x - rightVertex.x
         for (i in 0..triangleHeight) {
             val rightX: Double = if (i + minY >= segmentHeight) {
                 rightVertex.x + dxRightUpper * ((i + minY - segmentHeight) / aboveMidPointSteps)
             } else {
                 lowestVertex.x + dxRightLower * (i / midPointSteps)
             }
             val leftX: Double = lowestVertex.x + dxLeftLower * (i / triangleHeight.toDouble())
             drawLineBetween(leftX.toInt(), rightX.toInt(), image, i + minY, color)
         }
     } else {
         val dxLeftUpper = rightVertex.x - leftVertex.x
         for (i in 0..triangleHeight) {
             val leftX: Double = if (i + minY >= segmentHeight) {
                 leftVertex.x + dxLeftUpper * ((i + minY - segmentHeight) / aboveMidPointSteps)
             } else {
                 lowestVertex.x + dxLeftLower * (i / midPointSteps)
             }
 
             val rightX: Double = lowestVertex.x + dxRightLower * (i / triangleHeight.toDouble())
             drawLineBetween(leftX.toInt(), rightX.toInt(), image, i + minY, color)
         }
     }