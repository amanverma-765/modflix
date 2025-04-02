package com.ark.modflix.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface RootDestinations {
    @Serializable
    data object Home : RootDestinations

    @Serializable
    data object Search : RootDestinations

    @Serializable
    data class Detail(val pageUrl: String, val posterUrl: String?) : RootDestinations

    @Serializable
    data object Player : RootDestinations

    @Serializable
    data object WatchList : RootDestinations

    @Serializable
    data object Setting : RootDestinations

    @Serializable
    data object Downloader : RootDestinations

    @Serializable
    data class MediaList(val category: String) : RootDestinations
}