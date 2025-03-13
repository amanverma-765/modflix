package com.ark.cassini.model

import kotlinx.serialization.Serializable

@Serializable
data class Provider(
    val name: String,
    val value: String,
    val url: String
)