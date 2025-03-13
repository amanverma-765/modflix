package com.ark.cassini.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieInfo(
    val title: String,
    val image: String,
    val synopsis: String,
    val imdbId: String,
    val type: String,
    val tags: List<String>? = null,
    val cast: List<String>? = null,
    val rating: String? = null,
    val linkList: List<Link>
) {
    @Serializable
    data class Link(
        val title: String,
        val quality: String? = null,
        val episodesLink: String? = null,
        val directLinks: List<DirectLink>? = null
    ) {
        @Serializable
        data class DirectLink(
            val title: String,
            val link: String,
            val type: String? = null
        )
    }
}