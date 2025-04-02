package com.ark.modflix.presentation.features.detail.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ark.cassini.Cassini
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(private val cassini: Cassini) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: DetailUiEvent) {
        when (event) {
            is DetailUiEvent.AddToWatchList -> {}
            DetailUiEvent.ClearErrorMsg -> _uiState.update { it.copy(errorMsg = null) }
            is DetailUiEvent.RemoveFromWatchList -> {}
            is DetailUiEvent.FetchMediaInfo -> fetchMediaInfo(event.url)
        }
    }

    private fun fetchMediaInfo(url: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val mediaInfo = cassini.fetchVegaInfo(url)
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
}