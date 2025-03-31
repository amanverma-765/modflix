package com.ark.modflix.presentation.features.listing.logic

import com.ark.cassini.model.enums.VegaFilter

sealed interface ListUiEvent {
    data object ClearErrorMsg: ListUiEvent
    data class FetchCatalog(val page: Int, val category: VegaFilter): ListUiEvent
}