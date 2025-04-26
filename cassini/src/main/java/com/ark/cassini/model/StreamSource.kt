package com.ark.cassini.model

import com.ark.cassini.model.enums.MediaType

data class StreamSource(
    val type: MediaType,
    val episodes: List<Episode>?,
    val movieLinks: Map<Source, String>?
) {

    data class Episode(
        val title: String,
        val links: Map<Source, String>
    )

    enum class Source(val value: String) {
        VCLOUD("VegaCloud"),
        FILEPRESS("FilePress")
    }
}