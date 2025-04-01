package com.ark.modflix.presentation.features.detail.logic

sealed interface DetailUiEvent {
    data object ClearErrorMsg: DetailUiEvent
    data class AddToWatchList(val imdbId: Long) : DetailUiEvent
    data class RemoveFromWatchList(val imdbId: Long) : DetailUiEvent
    data class FetchMediaInfo(val url: String) : DetailUiEvent
}