package com.ark.modflix.presentation.features.home.logic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.ark.cassini.Cassini
import com.ark.cassini.model.enums.VegaFilter
import com.ark.modflix.model.HomeCatalog
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val cassini: Cassini) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.ClearErrorMsg -> _uiState.update { it.copy(errorMessage = null) }
        }
    }

    init {
        fetchTrendingBanners()
        fetchHomeCatalog()
    }

    private fun fetchTrendingBanners() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val catalog = cassini.fetchVegaCatalog(
                filter = VegaFilter.TRENDING,
                page = 1
            ).shuffled().take(5)
            val info = catalog.map { media ->
                async {
                    val url = media.link
                    cassini.fetchVegaInfo(url)
                }
            }.awaitAll().filterNotNull()
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    trendingBanners = info
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Failed to fetch trending banners"
                )
            }
        }
    }

    private fun fetchHomeCatalog() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val results = listOf(
                async { cassini.fetchVegaCatalog() },
                async { cassini.fetchVegaCatalog(filter = VegaFilter.TRENDING) },
                async { cassini.fetchVegaCatalog(filter = VegaFilter.NETFLIX) },
                async { cassini.fetchVegaCatalog(filter = VegaFilter.PRIME) },
                async { cassini.fetchVegaCatalog(filter = VegaFilter.DISNEY_PLUS) },
                async { cassini.fetchVegaCatalog(filter = VegaFilter.K_DRAMA) },
                async { cassini.fetchVegaCatalog(filter = VegaFilter.ANIME) },
                async { cassini.fetchVegaCatalog(filter = VegaFilter.MINI_TV) }
            ).awaitAll()

            _uiState.update { currentState ->
                currentState.copy(
                    homeCatalog = HomeCatalog(
                        latest = results[0],
                        trending = results[1],
                        netflix = results[2],
                        amazonPrime = results[3],
                        disneyPlus = results[4],
                        kDrama = results[5],
                        anime = results[6],
                        miniTv = results[7]
                    ),
                    isLoading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "Failed to fetch catalog data"
                )
            }
        }
    }

}