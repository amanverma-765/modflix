package com.ark.modflix.presentation.features.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ark.modflix.presentation.features.home.logic.HomeUiEvent
import com.ark.modflix.presentation.features.home.logic.HomeUiState
import com.ark.modflix.presentation.features.home.logic.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun RootHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        uiEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    uiEvent: (HomeUiEvent) -> Unit,
) {
    Scaffold(
        topBar = { Text(text = "ModFlix") },
        modifier = modifier
    ) { innerPadding ->

        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            item {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    uiState.trendingBanners.forEach { banner ->
                        Text(text = banner.title)
                    }
                }
            }
        }
    }
}