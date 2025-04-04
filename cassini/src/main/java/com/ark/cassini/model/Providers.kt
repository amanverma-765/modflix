package com.ark.cassini.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Provider(
    @SerialName("key")
    val key: String,
    @SerialName("name")
    val name: String,
    @SerialName("url")
    val url: String
)