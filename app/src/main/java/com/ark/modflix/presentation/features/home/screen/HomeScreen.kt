package com.ark.modflix.presentation.features.home.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ark.cassini.model.MediaCatalog
import com.ark.cassini.model.enums.VegaFilter
import com.ark.modflix.presentation.features.home.components.BannerCarousel
import com.ark.modflix.presentation.components.CatalogBanner
import com.ark.modflix.presentation.features.home.logic.HomeUiEvent
import com.ark.modflix.presentation.features.home.logic.HomeUiState
import com.ark.modflix.presentation.features.home.logic.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun RootHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onWatchNowClicked: () -> Unit,
    onCatalogBannerClicked: () -> Unit,
    onSeeAllClicked: (category: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        uiEvent = viewModel::onEvent,
        onWatchNowClicked = onWatchNowClicked,
        modifier = modifier,
        onCatalogBannerClicked = onCatalogBannerClicked,
        onSeeAllClicked = onSeeAllClicked
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    uiEvent: (HomeUiEvent) -> Unit,
    onWatchNowClicked: () -> Unit,
    onCatalogBannerClicked: () -> Unit,
    onSeeAllClicked: (category: String) -> Unit
) {

    val lazyState = rememberLazyListState()

    Scaffold(modifier = modifier) { innerPadding ->
        LazyColumn(
            state = lazyState,
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            // Banner carousel
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
            val catalogSections = mutableListOf<Pair<VegaFilter, List<MediaCatalog>?>>().apply {
                VegaFilter.entries.forEach { filter ->
                    when (filter) {
                        VegaFilter.LATEST -> add(
                            Pair(
                                filter,
                                uiState.homeCatalog?.latest?.take(8)
                            )
                        )

                        VegaFilter.TRENDING -> add(
                            Pair(
                                filter,
                                uiState.homeCatalog?.trending?.take(8)
                            )
                        )

                        VegaFilter.NETFLIX -> add(
                            Pair(
                                filter,
                                uiState.homeCatalog?.netflix?.take(8)
                            )
                        )

                        VegaFilter.PRIME -> add(
                            Pair(
                                filter,
                                uiState.homeCatalog?.amazonPrime?.take(8)
                            )
                        )

                        VegaFilter.DISNEY_PLUS -> add(
                            Pair(
                                filter,
                                uiState.homeCatalog?.disneyPlus?.take(8)
                            )
                        )

                        VegaFilter.ANIME -> add(
                            Pair(
                                filter,
                                uiState.homeCatalog?.anime?.take(8)
                            )
                        )

                        VegaFilter.K_DRAMA -> add(
                            Pair(
                                filter,
                                uiState.homeCatalog?.kDrama?.take(8)
                            )
                        )

                        VegaFilter.MINI_TV -> add(
                            Pair(
                                filter,
                                uiState.homeCatalog?.miniTv?.take(8)
                            )
                        )
                    }
                }
            }

            catalogSections.forEach { (filter, media) ->
                media?.takeIf { it.isNotEmpty() }?.let { movies ->
                    item {
                        CatalogSection(
                            mediaItems = movies,
                            title = filter.title,
                            onCatalogBannerClicked = onCatalogBannerClicked,
                            onSeeAllClicked = { onSeeAllClicked(filter.name) }
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
    onCatalogBannerClicked: () -> Unit,
    onSeeAllClicked: () -> Unit
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
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onSeeAllClicked() })
                    }
            )
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(mediaItems) { item ->
                CatalogBanner(
                    mediaCatalog = item,
                    onClick = onCatalogBannerClicked
                )
            }
        }
    }
}