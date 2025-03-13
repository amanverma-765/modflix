package com.ark.cassini.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieCatalog(
    val title: String,
    val link: String,
    val image: String
)