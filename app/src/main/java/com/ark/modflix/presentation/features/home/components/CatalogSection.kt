package com.ark.modflix.presentation.features.home.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ark.cassini.model.MediaCatalog
import com.ark.modflix.presentation.components.CatalogBanner


@Composable
fun CatalogSection(
    modifier: Modifier = Modifier,
    title: String,
    mediaItems: List<MediaCatalog>,
    onBannerClicked: (pageUrl: String, posterUrl: String?) -> Unit,
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
                    onClick = { onBannerClicked(item.link, item.imgUrl) }
                )
            }
        }
    }
}