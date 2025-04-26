package com.ark.modflix.presentation.features.detail.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ark.cassini.model.MediaInfo


@Composable
fun QualitySelector(
    downloadPageLinks: List<MediaInfo.DownloadLink>,
    selectedLinkIndex: Int,
    onLinkSelected: (Int, List<String>) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        var expanded by remember { mutableStateOf(false) }
        val currentSelection = downloadPageLinks[selectedLinkIndex]

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    ),
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = currentSelection.name,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QualityInfoColumn(label = "Quality", value = currentSelection.quality ?: "NA")
                    QualityInfoColumn(label = "Size", value = currentSelection.size ?: "Unknown")
                }

                OutlinedButton(
                    onClick = { expanded = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Change Quality",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select quality"
                    )
                }
            }
        }

        QualityDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            downloadPageLinks = downloadPageLinks,
            onLinkSelected = { index, links ->
                onLinkSelected(index, links)
                expanded = false
            }
        )
    }
}


@Composable
private fun QualityInfoColumn(label: String, value: String) {
    Column(modifier = Modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun QualityDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    downloadPageLinks: List<MediaInfo.DownloadLink>,
    onLinkSelected: (Int, List<String>) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        downloadPageLinks.forEachIndexed { index, link ->
            DropdownMenuItem(
                text = {
                    Column {
                        Text(
                            text = link.name,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "Quality: ${link.quality ?: "Unknown"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Size: ${link.size ?: "Unknown"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                onClick = {
                    link.directLinks?.let { directLinks ->
                        onLinkSelected(index, directLinks.map { it.link })
                    }
                },
                modifier = Modifier.padding(vertical = 4.dp)
            )

            if (index < downloadPageLinks.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
        }
    }
}