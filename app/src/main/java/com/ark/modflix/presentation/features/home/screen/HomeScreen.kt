package com.ark.modflix.presentation.features.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ark.cassini.model.MediaCatalog
import com.ark.cassini.model.enums.VegaFilter
import com.ark.modflix.presentation.components.ErrorScreen
import com.ark.modflix.presentation.features.home.components.BannerCarousel
import com.ark.modflix.presentation.features.home.components.CatalogSection
import com.ark.modflix.presentation.features.home.logic.HomeUiEvent
import com.ark.modflix.presentation.features.home.logic.HomeUiState
import com.ark.modflix.presentation.features.home.logic.HomeViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun RootHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onBannerClicked: (pageUrl: String, posterUrl: String?) -> Unit,
    onSeeAllClicked: (category: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState,
        uiEvent = viewModel::onEvent,
        modifier = modifier,
        onBannerClicked = onBannerClicked,
        onSeeAllClicked = onSeeAllClicked
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    uiEvent: (HomeUiEvent) -> Unit,
    onBannerClicked: (pageUrl: String, posterUrl: String?) -> Unit,
    onSeeAllClicked: (category: String) -> Unit
) {
    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 0.dp,
                    bottom = innerPadding.calculateBottomPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Rtl),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Rtl)
                )
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.homeCatalog != null -> {
                    HomeScreenContent(
                        onBannerClicked = onBannerClicked,
                        onSeeAllClicked = onSeeAllClicked,
                        uiState = uiState,
                        uiEvent = uiEvent
                    )
                }

                uiState.errorMsg != null -> {
                    ErrorScreen(
                        errorMessage = uiState.errorMsg,
                        onRetry = { uiEvent(HomeUiEvent.FetchHomeData) },
                        onDismiss = {
                            uiEvent(HomeUiEvent.ClearErrorMsg)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onBannerClicked: (pageUrl: String, posterUrl: String?) -> Unit,
    onSeeAllClicked: (category: String) -> Unit,
    uiState: HomeUiState,
    uiEvent: (HomeUiEvent) -> Unit
) {

    val lazyState = rememberLazyListState()

    LazyColumn(
        state = lazyState,
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Banner carousel
        item {
            Box(
                modifier
                    .fillMaxWidth()
                    .height(480.dp)
            ) {
                if (uiState.trendingBanners.isNotEmpty()) {
                    BannerCarousel(
                        banners = uiState.trendingBanners,
                        onWatchNowClicked = { pageUrl, posterUrl ->
                            onBannerClicked(pageUrl, posterUrl)
                        },
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
                }
            }
        }

        catalogSections.forEach { (filter, media) ->
            media?.takeIf { it.isNotEmpty() }?.let { movies ->
                item {
                    CatalogSection(
                        mediaItems = movies,
                        title = filter.title,
                        onBannerClicked = onBannerClicked,
                        onSeeAllClicked = { onSeeAllClicked(filter.name) }
                    )
                }
            }
        }
    }
}