package com.ark.modflix

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.ark.modflix.koin.platformModule
import com.ark.modflix.koin.sharedModule
import org.koin.core.context.startKoin

fun main() = application {
    startKoin { modules(sharedModule, platformModule) }
    Window(
        onCloseRequest = ::exitApplication,
        title = "ModFlix",
        state = rememberWindowState()
    ) {
        App()
    }
}