package com.ark.modflix.presentation.features.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ark.cassini.model.MediaInfo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloaderSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    links: List<MediaInfo.DownloadLink>?,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        modifier = modifier.padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Column(Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            Text(
                text = "Download Options",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            if (links == null) {
                Text(
                    text = "No download links available",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                return@ModalBottomSheet
            } else {
                LazyColumn(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(links) { downloadLink ->
                        DownloadLinkCard(downloadLink = downloadLink)
                    }
                }
            }
        }

    }
}


@Composable
private fun DownloadLinkCard(downloadLink: MediaInfo.DownloadLink) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = downloadLink.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            downloadLink.quality?.let { quality ->
                Text(
                    text = "Quality: $quality",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            downloadLink.size?.let { size ->
                Text(
                    text = "Size: $size",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            downloadLink.directLinks?.let { directLinks ->
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))
                directLinks.forEach { directLink ->
                    androidx.compose.material3.OutlinedButton(
                        onClick = { /* Handle link click */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(text = "Download from ${directLink.source}")
                    }
                }
            } ?: Text(
                text = "No direct links available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}