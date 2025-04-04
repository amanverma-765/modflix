package com.ark.modflix.presentation.features.detail.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(
    modifier: Modifier = Modifier,
    pageUrl: String,
    posterUrl: String?,
    navigateBack: () -> Unit,
    uiState: DetailUiState,
    uiEvent: (DetailUiEvent) -> Unit
) {

//    val scrollBehavior =
//        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        uiEvent(DetailUiEvent.FetchMediaInfo(pageUrl))
    }

    Scaffold(
        modifier = modifier,
//        topBar = {
//            WatchListTopBar(
//                isInWatchList = uiState.isInWatchList,
//                onBackClick = navigateBack,
//                scrollBehaviour = scrollBehavior,
//                onWatchListClick = {
//                    uiEvent(DetailUiEvent.ToggleWatchList(uiState.mediaInfo?.title!!))
//                }
//            )
//        }
    ) { innerPadding ->
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
                        posterUrl = posterUrl,
//                        scrollBehavior = scrollBehavior
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaDetailContent(
    mediaInfo: MediaInfo,
    posterUrl: String?,
//    scrollBehavior: TopAppBarScrollBehavior
) {

    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
//            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
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
                        style = MaterialTheme.typography.titleLarge
                    )
                    DetailSection(
                        details = mediaInfo.details,
                    )
                }
            }
        }
    }
}