package com.ark.modflix.presentation.features.listing.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ark.cassini.Cassini
import com.ark.cassini.model.enums.VegaFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MediaListViewModel(private val cassini: Cassini) : ViewModel() {

    private val _uiState = MutableStateFlow(ListUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: ListUiEvent) {
        when (event) {
            ListUiEvent.ClearErrorMsg -> _uiState.update { it.copy(errorMsg = null) }
            is ListUiEvent.FetchCatalog -> fetchCategoryCatalog(event.category, event.page)
        }
    }

    private fun fetchCategoryCatalog(category: VegaFilter, page: Int) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val newCatalogs = cassini.fetchVegaCatalog(filter = category, page = page)

            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    catalogs = currentState.catalogs + newCatalogs,
                    page = page
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMsg = "Failed to fetch $category catalog"
                )
            }
        }
    }

}