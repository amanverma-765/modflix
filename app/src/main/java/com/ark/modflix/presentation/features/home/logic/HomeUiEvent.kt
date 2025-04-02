package com.ark.modflix.presentation.features.home.logic

sealed interface HomeUiEvent {
    data object ClearErrorMsg : HomeUiEvent
    data object FetchHomeData : HomeUiEvent
}