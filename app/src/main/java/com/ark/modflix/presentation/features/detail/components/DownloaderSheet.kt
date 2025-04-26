package com.ark.modflix.presentation.features.detail.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.ark.cassini.model.MediaInfo
import com.ark.cassini.model.StreamSource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloaderSheet(
    modifier: Modifier = Modifier,
    isSheetLoading: Boolean,
    streamSource: StreamSource?,
    downloadPageLinks: List<MediaInfo.DownloadLink>?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onQualityChange: (urls: List<String>) -> Unit
) {
    var selectedLinkIndex by remember { mutableIntStateOf(0) }

    // Initialize with first available link
    LaunchedEffect(downloadPageLinks) {
        if (!downloadPageLinks.isNullOrEmpty() && downloadPageLinks.size > selectedLinkIndex) {
            downloadPageLinks[selectedLinkIndex].directLinks?.let { directLinks ->
                onQualityChange(directLinks.map { it.link })
            }
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        modifier = modifier.padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            when {
                isSheetLoading -> LoadingIndicator()
                downloadPageLinks == null || streamSource == null -> NoLinksAvailable()
                else -> AvailableLinks(
                    downloadPageLinks = downloadPageLinks,
                    selectedLinkIndex = selectedLinkIndex,
                    streamSource = streamSource,
                            onLinkSelected = { index, links ->
                        selectedLinkIndex = index
                        onQualityChange(links)
                    }
                )
            }
        }
    }
}


@Composable
private fun AvailableLinks(
    downloadPageLinks: List<MediaInfo.DownloadLink>,
    selectedLinkIndex: Int,
    streamSource: StreamSource,
    onLinkSelected: (Int, List<String>) -> Unit
) {
    // Quality selector card
    if (downloadPageLinks.isNotEmpty()) {
        QualitySelector(
            downloadPageLinks = downloadPageLinks,
            selectedLinkIndex = selectedLinkIndex,
            onLinkSelected = onLinkSelected
        )
    }

    // Stream sources list
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(StreamSource.Source.entries.toTypedArray()) { source ->
            StreamSourceItem(sourceType = source) { sourceType ->
                // Handle stream source click
                Logger.e(streamSource.toString())
            }
        }
    }
}

@Composable
private fun StreamSourceItem(
    sourceType: StreamSource.Source,
    onClick: (sourceType: StreamSource.Source) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = sourceType.value,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.basicMarquee()
                    )
                }

                Text(
                    text = "Stream",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = { onClick(sourceType) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Play",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Watch Now",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun NoLinksAvailable() {
    Text(
        text = "No download links available",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}