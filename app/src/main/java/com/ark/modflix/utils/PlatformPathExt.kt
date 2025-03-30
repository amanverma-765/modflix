package com.ark.modflix.utils

import android.content.Context
import kotlinx.io.files.Path
import org.koin.core.context.GlobalContext

fun getPlatformPath(): Path {
    val context = GlobalContext.get().get<Context>()
    val cacheDir = context.cacheDir
    return Path(cacheDir.absolutePath)
}