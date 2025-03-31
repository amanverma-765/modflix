package com.ark.modflix.presentation.features.listing.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ark.cassini.model.enums.VegaFilter
import com.ark.modflix.presentation.components.CatalogBanner
import com.ark.modflix.presentation.features.listing.logic.ListUiEvent
import com.ark.modflix.presentation.features.listing.logic.ListUiState
import com.ark.modflix.presentation.features.listing.logic.MediaListViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import org.koin.androidx.compose.koinViewModel


@Composable
fun RootMediaListScreen(
    modifier: Modifier = Modifier,
    category: VegaFilter,
    viewModel: MediaListViewModel = koinViewModel(),
    onCatalogBannerClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    MediaListScreen(
        uiState = uiState.value,
        uiEvent = viewModel::onEvent,
        category = category,
        onCatalogBannerClicked = onCatalogBannerClicked,
        modifier = modifier,
        onBackClicked = onBackClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun MediaListScreen(
    uiState: ListUiState,
    uiEvent: (ListUiEvent) -> Unit,
    category: VegaFilter,
    onCatalogBannerClicked: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hazeState = remember { HazeState() }
    val page = remember { mutableIntStateOf(1) }
    val gridState = rememberLazyGridState()

    // Detect when to load more data
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

            lastVisibleItemIndex > (totalItems - 5) && totalItems > 0 && !uiState.isLoading
        }
    }

    // Fetch new page when `page` changes
    LaunchedEffect(page.intValue) {
        uiEvent(ListUiEvent.FetchCatalog(category = category, page = page.intValue))
    }

    // Trigger next page load
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            page.intValue += 1
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = category.title) },
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(
                                imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                                contentDescription = "Navigate Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
                    modifier = Modifier.hazeEffect(
                        state = hazeState,
                        style = HazeMaterials.ultraThin()
                    )
                )
                if (uiState.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            state = gridState,
            contentPadding = innerPadding,
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .hazeSource(state = hazeState)
        ) {
            items(uiState.catalogs) { item ->
                CatalogBanner(
                    mediaCatalog = item,
                    onClick = onCatalogBannerClicked,
                )
            }
        }
    }
}