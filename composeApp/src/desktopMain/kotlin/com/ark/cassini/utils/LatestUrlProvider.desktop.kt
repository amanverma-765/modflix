package com.ark.cassini.utils

import kotlinx.io.files.Path
import java.io.File

actual fun platformPath(): Path {
    val userHome = System.getProperty("user.home")

    val cacheDir = File(userHome, ".cache/")
    if (!cacheDir.exists()) {
        cacheDir.mkdirs()
    }

    return Path(cacheDir.absolutePath)
}