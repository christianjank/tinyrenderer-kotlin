package io.github.christianjank

import java.io.File
import java.io.IOException

fun String.runCommand(workingDir: File = File(System.getProperty("user.dir")), fileArg : String) {
    try {
        ProcessBuilder(this, System.getProperty("user.dir") + "/" + fileArg)
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}
