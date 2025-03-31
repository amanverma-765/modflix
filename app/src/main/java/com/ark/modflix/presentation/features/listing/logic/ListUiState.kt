package com.ark.modflix.presentation.features.listing.logic

import com.ark.cassini.model.MediaCatalog

data class ListUiState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val catalogs: List<MediaCatalog> = emptyList(),
    val page: Int = 0
)