package com.ark.modflix.presentation.features.detail.logic

import com.ark.cassini.model.MediaInfo
import com.ark.cassini.model.StreamSource

data class DetailUiState(
    val isLoading: Boolean = false,
    val isSheetLoading: Boolean = false,
    val errorMsg: String? = null,
    val isInWatchList: Boolean = false,
    val isInDownloadList: Boolean = false,
    val continueWatching: Long = 0L,
    val mediaInfo: MediaInfo? = null,
    val streamSource: StreamSource? = null
)