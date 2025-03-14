package com.ark.cassini.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieInfo(
    val title: String,
    val imgUrl: String,
    val synopsis: String,
    val imdbId: String,
    val type: Type,
    val rating: Float?,
    val details: HashMap<String, String>,
    val downloadLinks: List<DownloadLink>
) {
    @Serializable
    data class DownloadLink(
        val name: String,
        val quality: String? = null,
        val directLinks: List<DirectLink>? = null
    ) {
        @Serializable
        data class DirectLink(
            val source: String,
            val link: String
        )
    }

    enum class Type {
        MOVIE, SERIES
    }
}