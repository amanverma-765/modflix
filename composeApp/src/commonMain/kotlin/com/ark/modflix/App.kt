package com.ark.modflix

import androidx.compose.runtime.Composable
import com.ark.cassini.Cassini
import com.ark.modflix.presentation.features.home.screen.HomeScreenRoot
import com.ark.modflix.presentation.theme.AppTheme
import kotlinx.coroutines.runBlocking

@Composable
fun App() = AppTheme {
    val cassini = Cassini()
    runBlocking {
        cassini.fetchVegaCatalog()
    }
    HomeScreenRoot()
}