package com.ark.modflix

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ark.cassini.Cassini
import com.ark.modflix.presentation.theme.AppTheme
import kotlinx.coroutines.runBlocking

@Composable
fun App() = AppTheme {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        val cassini = Cassini()
        runBlocking {
            cassini.fetchVegaCatalog()
        }
    }
}