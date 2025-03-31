package com.ark.modflix.presentation.features.detail.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun RootDetailScreen(
    modifier: Modifier = Modifier,
    pageUrl: String
) {
    DetailScreen(
        modifier = modifier,
        pageUrl = pageUrl
    )
}

@Composable
private fun DetailScreen(
    modifier: Modifier = Modifier,
    pageUrl: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(pageUrl)
    }
}