package com.ark.cassini.utils

import android.content.Context
import kotlinx.io.files.Path
import org.koin.core.context.GlobalContext

actual fun platformPath(): Path {
    val context = GlobalContext.get().get<Context>()
    val cacheDir = context.cacheDir
    return Path(cacheDir.absolutePath)
}