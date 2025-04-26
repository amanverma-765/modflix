package com.ark.modflix.presentation.features.detail.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ark.cassini.Cassini
import com.ark.cassini.model.enums.MediaType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(private val cassini: Cassini) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: DetailUiEvent) {
        when (event) {
            is DetailUiEvent.ToggleWatchList -> {}
            DetailUiEvent.ClearErrorMsg -> _uiState.update { it.copy(errorMsg = null) }
            is DetailUiEvent.FetchMediaInfo -> fetchMediaInfo(event.pageUrl)
            is DetailUiEvent.FetchStreamSources -> fetchStreamSources(
                event.downloadPageUrls,
                event.mediaType
            )
        }
    }

    private fun fetchMediaInfo(pageUrl: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val mediaInfo = cassini.fetchVegaInfo(pageUrl)
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    mediaInfo = mediaInfo,
                    errorMsg = if (mediaInfo == null) "Failed to fetch media info" else null,
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMsg = "Failed to fetch media info"
                )
            }
        }
    }

    private fun fetchStreamSources(
        downloadPageUrls: List<String>,
        mediaType: MediaType
    ) = viewModelScope.launch {
        _uiState.update { it.copy(isSheetLoading = true) }
        try {
            val streamSource = cassini.fetchAllStreamSources(downloadPageUrls, mediaType)
            _uiState.update { currentState ->
                currentState.copy(
                    isSheetLoading = false,
                    streamSource = streamSource,
                    errorMsg = if (streamSource == null) "Failed to fetch stream sources" else null,
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isSheetLoading = false,
                    errorMsg = "Failed to fetch stream source"
                )
            }
        }
    }
}