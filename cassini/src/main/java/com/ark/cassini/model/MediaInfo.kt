package com.ark.cassini.model

import com.ark.cassini.model.enums.MediaType
import kotlinx.serialization.Serializable

@Serializable
data class MediaInfo(
    val title: String,
    val posterUrl: String?,
    val pageUrl: String,
    val synopsis: String?,
    val imdbId: String,
    val type: MediaType,
    val logoUrl: String?,
    val rating: Float?,
    val bgUrl: String?,
    val creditsCast: List<Cast>?,
    val genres: List<String>?,
    val runtime: String?,
    val releaseInfo: String?,
    val trailers: List<String>?,
    val details: Map<String, String>,
    val downloadLinks: List<DownloadLink>
) {

    @Serializable
    data class Cast(
        val character: String?,
        val id: Int,
        val name: String,
        val profileUrl: String?
    )

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
}