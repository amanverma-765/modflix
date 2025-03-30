package com.ark.cassini.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Provider(
    val name: String,
    val value: String,
    val url: String
)