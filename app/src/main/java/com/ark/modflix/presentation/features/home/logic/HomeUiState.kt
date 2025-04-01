package com.ark.modflix.presentation.features.home.logic

import com.ark.cassini.model.MediaInfo
import com.ark.modflix.model.HomeCatalog

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val trendingBanners: List<MediaInfo> = emptyList(),
    val homeCatalog: HomeCatalog? = null
)