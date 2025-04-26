package com.ark.modflix.presentation.features.detail.logic

import com.ark.cassini.model.enums.MediaType

sealed interface DetailUiEvent {
    data object ClearErrorMsg : DetailUiEvent
    data class ToggleWatchList(val title: String) : DetailUiEvent
    data class FetchMediaInfo(val pageUrl: String) : DetailUiEvent
    data class FetchStreamSources(
        val downloadPageUrls: List<String>,
        val mediaType: MediaType
    ): DetailUiEvent
}