package com.ark.cassini.model

import kotlinx.serialization.Serializable

@Serializable
data class MediaCatalog(
    val title: String,
    val link: String,
    val imgUrl: String,
    val date: String
)