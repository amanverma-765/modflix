package com.ark.modflix.presentation.features.detail.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ark.cassini.model.MediaInfo
import com.ark.modflix.presentation.components.ErrorScreen
import com.ark.modflix.presentation.components.MediaBanner
import com.ark.modflix.presentation.features.detail.components.BasicInfoSection
import com.ark.modflix.presentation.features.detail.components.CastItem
import com.ark.modflix.presentation.features.detail.components.DetailSection
import com.ark.modflix.presentation.features.detail.components.SynopsisSection
import com.ark.modflix.presentation.features.detail.components.YoutubeBanner
import com.ark.modflix.presentation.features.detail.logic.DetailUiEvent
import com.ark.modflix.presentation.features.detail.logic.DetailUiState
import com.ark.modflix.presentation.features.detail.logic.DetailViewModel
import org.koin.androidx.compose.koinViewModel
import java.security.Key


@Composable
fun RootDetailScreen(
    modifier: Modifier = Modifier,
    pageUrl: String,
    posterUrl: String?,
    navigateBack: () -> Unit,
    viewModel: DetailViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DetailScreen(
        modifier = modifier,
        pageUrl = pageUrl,
        uiState = uiState,
        posterUrl = posterUrl,
        uiEvent = viewModel::onEvent,
        navigateBack = navigateBack
    )
}

@Composable
private fun DetailScreen(
    modifier: Modifier = Modifier,
    pageUrl: String,
    posterUrl: String?,
    navigateBack: () -> Unit,
    uiState: DetailUiState,
    uiEvent: (DetailUiEvent) -> Unit
) {
    LaunchedEffect(Unit) {
        uiEvent(DetailUiEvent.FetchMediaInfo(pageUrl))
    }

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

                uiState.errorMsg != null -> {
                    ErrorScreen(
                        errorMessage = uiState.errorMsg,
                        onRetry = { uiEvent(DetailUiEvent.FetchMediaInfo(pageUrl)) },
                        onDismiss = {
                            uiEvent(DetailUiEvent.ClearErrorMsg)
                            navigateBack()
                        }
                    )
                }

                uiState.mediaInfo != null -> {
                    MediaDetailContent(
                        mediaInfo = uiState.mediaInfo,
                        onAddToWatchlist = { },
                        onRemoveFromWatchlist = { },
                        posterUrl = posterUrl
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaDetailContent(
    mediaInfo: MediaInfo,
    posterUrl: String?,
    onAddToWatchlist: () -> Unit,
    onRemoveFromWatchlist: () -> Unit
) {

    val uriHandler = LocalUriHandler.current

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            MediaBanner(
                bannerInfo = mediaInfo,
                posterUrl = posterUrl,
                onWatchNowClicked = { /* TODO: Handle watch now click */ },
                onDownloadClicked = { /* TODO: Handle download click */ },
                isDetailsBanner = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            )
        }

        // Media info section (rating, runtime, genres, release info)
        item { BasicInfoSection(mediaInfo = mediaInfo) }

        // Cast section
        if (!mediaInfo.creditsCast.isNullOrEmpty()) {
            item {
                Column {
                    Text(
                        text = "Cast",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(mediaInfo.creditsCast!!, key = { it.id }) { cast ->
                            CastItem(cast = cast)
                        }
                    }
                }
            }
        }

        // Trailer section as shown in the image
        item {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Trailer",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp)
                )
                YoutubeBanner(
                    mediaInfo = mediaInfo,
                    posterUrl = posterUrl,
                    onClick = {
                        val ytBaseUrl = "https://www.youtube.com/results?search_query="
                        uriHandler.openUri(
                            uri = mediaInfo.trailers?.first()
                                ?: (ytBaseUrl + mediaInfo.title + " trailer")
                        )
                    },
                )
            }
        }


        // Synopsis section
        if (!mediaInfo.synopsis.isNullOrBlank()) {
            item {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Synopsis",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                    SynopsisSection(synopsis = mediaInfo.synopsis!!)
                }
            }
        }

        // Description section
        if (mediaInfo.details.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleLarge,
//                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    DetailSection(
                        details = mediaInfo.details,
                    )
                }
            }
        }
    }
}