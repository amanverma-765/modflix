package com.ark.modflix.presentation.features.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ark.cassini.model.MediaInfo
import com.ark.cassini.model.enums.MediaType


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BasicInfoSection(
    modifier: Modifier = Modifier,
    mediaInfo: MediaInfo
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Rating and runtime row
        if (mediaInfo.rating != null || mediaInfo.runtime != null || mediaInfo.releaseInfo != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                // Rating
                mediaInfo.rating?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = it.toString(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Runtime
                mediaInfo.runtime?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = "Runtime",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (mediaInfo.type == MediaType.MOVIE) {
                                it
                            } else {
                                "$it/Ep"
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Release info
                mediaInfo.releaseInfo?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Release date",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // Genres
        mediaInfo.genres?.let { genres ->
            if (genres.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    genres.forEach { genre ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(genre) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        )
                    }
                }
            }
        }
    }
}