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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.ark.modflix.presentation.features.detail.components.DownloaderSheet
import com.ark.modflix.presentation.features.detail.components.SynopsisSection
import com.ark.modflix.presentation.features.detail.components.YoutubeBanner
import com.ark.modflix.presentation.features.detail.logic.DetailUiEvent
import com.ark.modflix.presentation.features.detail.logic.DetailUiState
import com.ark.modflix.presentation.features.detail.logic.DetailViewModel
import kotlinx.coroutines.launch
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
    var isBottomSheetVisible by remember { mutableStateOf(false) }
 val sheetState = rememberModalBottomSheetState(
     skipPartiallyExpanded = true
 )
    val coroutineScope = rememberCoroutineScope()

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
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                )
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
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
                        uiState = uiState,
                        onWatchNowClicked = {
                            isBottomSheetVisible = true
                        },
                        onDownloadClicked = {
                            isBottomSheetVisible = true
                        }
                    )
                }
            }
        }
        if (isBottomSheetVisible) {
            DownloaderSheet(
                sheetState = sheetState,
                links = uiState.mediaInfo?.downloadLinks,
                onDismiss = {
                    coroutineScope.launch {
                        isBottomSheetVisible = false
                    }
                }
            )
        }
    }
}


@Composable
private fun MediaDetailContent(
    mediaInfo: MediaInfo,
    posterUrl: String?,
    uiState: DetailUiState,
    onWatchNowClicked: () -> Unit,
    onDownloadClicked: () -> Unit
) {

    val uriHandler = LocalUriHandler.current

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            MediaBanner(
                bannerInfo = mediaInfo,
                posterUrl = posterUrl,
                onWatchNowClicked = onWatchNowClicked,
                onDownloadClicked = onDownloadClicked,
                isDetailsBanner = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
            )
        }

        // Media info section (rating, runtime, genres, release info)
        if (mediaInfo.rating != null || mediaInfo.runtime != null || mediaInfo.releaseInfo != null) {
            item { BasicInfoSection(mediaInfo = mediaInfo, isInWatchList = uiState.isInWatchList) }
        }

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
            Column(Modifier.padding(12.dp)) {
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