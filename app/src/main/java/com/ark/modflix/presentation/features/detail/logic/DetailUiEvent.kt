package com.ark.modflix.presentation.features.detail.logic

sealed interface DetailUiEvent {
    data object ClearErrorMsg: DetailUiEvent
    data class ToggleWatchList(val title: String) : DetailUiEvent
    data class FetchMediaInfo(val url: String) : DetailUiEvent
}