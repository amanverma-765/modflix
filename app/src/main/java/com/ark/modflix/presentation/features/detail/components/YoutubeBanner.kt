package com.ark.modflix.presentation.features.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import com.ark.cassini.model.MediaInfo

@Composable
fun YoutubeBanner(
    modifier: Modifier = Modifier,
    mediaInfo: MediaInfo,
    onClick: () -> Unit
) {

    val context = LocalContext.current

    Card(onClick = onClick) {
        ListItem(
            tonalElevation = 4.dp,
            modifier = modifier,
            headlineContent = {
                Text(
                    text = mediaInfo.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
            },
            supportingContent = {
                Text(
                    text = if (mediaInfo.synopsis == null) mediaInfo.releaseInfo.toString()
                    else (mediaInfo.synopsis?.take(60) + "..."),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(16f/9f)
                        .clip(MaterialTheme.shapes.medium)
                        .fillMaxHeight()
                ) {
                    AsyncImage(
                        imageLoader = ImageLoader(context),
                        model = mediaInfo.bgUrl,
                        contentDescription = "Trailer thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Play icon overlay
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play trailer",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        )
    }
}