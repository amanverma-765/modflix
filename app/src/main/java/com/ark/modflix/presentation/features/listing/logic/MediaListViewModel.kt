package com.ark.modflix.presentation.features.listing.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
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
        try {
            if (!uiState.value.loadedPages.contains(page)) {
                _uiState.update { it.copy(isLoading = true) }
                val newCatalogs = cassini.fetchVegaCatalog(filter = category, page = page)
                    ?: throw RuntimeException("Failed to fetch catalog data")
                _uiState.update { currentState ->
                    currentState.copy(
                        loadedPages = currentState.loadedPages.apply { add(page) },
                        isLoading = false,
                        catalogs = currentState.catalogs + newCatalogs,
                        page = page
                    )
                }
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