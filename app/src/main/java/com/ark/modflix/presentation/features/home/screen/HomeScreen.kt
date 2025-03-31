package com.ark.modflix.presentation.features.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ark.cassini.model.MediaCatalog
import com.ark.modflix.presentation.features.home.components.BannerCarousel
import com.ark.modflix.presentation.features.home.components.CatalogBanner
import com.ark.modflix.presentation.features.home.logic.HomeUiEvent
import com.ark.modflix.presentation.features.home.logic.HomeUiState
import com.ark.modflix.presentation.features.home.logic.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun RootHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onWatchNowClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        uiEvent = viewModel::onEvent,
        onWatchNowClicked = onWatchNowClicked,
        modifier = modifier
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    uiEvent: (HomeUiEvent) -> Unit,
    onWatchNowClicked: () -> Unit
) {
    Scaffold(modifier = modifier) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                bottom = 24.dp,
                top = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            // Loading indicator or Banner carousel
            item {
                Box(
                    modifier
                        .fillMaxWidth()
                        .height(480.dp)
                ) {
                    if (uiState.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    } else if (uiState.trendingBanners.isNotEmpty()) {
                        BannerCarousel(
                            banners = uiState.trendingBanners,
                            onWatchNowClicked = onWatchNowClicked,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Display catalog sections
            val catalogSections = listOf(
                "Trending Now" to uiState.homeCatalog?.trending?.take(8),
                "Latest Release" to uiState.homeCatalog?.latest?.take(8),
                "Netflix" to uiState.homeCatalog?.netflix?.take(8),
                "Amazon Prime" to uiState.homeCatalog?.amazonPrime?.take(8),
                "Disney+" to uiState.homeCatalog?.disneyPlus?.take(8),
                "K-Drama" to uiState.homeCatalog?.kDrama?.take(8),
                "Anime" to uiState.homeCatalog?.anime?.take(8)
            )

            catalogSections.forEach { (title, media) ->
                media?.takeIf { it.isNotEmpty() }?.let { movies ->
                    item {
                        CatalogSection(
                            title = title,
                            mediaItems = movies,
                            onMediaClicked = { /* Handle movie click */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogSection(
    modifier: Modifier = Modifier,
    title: String,
    mediaItems: List<MediaCatalog>,
    onMediaClicked: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "See All",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(mediaItems) { item ->
                CatalogBanner(
                    mediaCatalog = item,
                    onClick = onMediaClicked
                )
            }
        }
    }
}