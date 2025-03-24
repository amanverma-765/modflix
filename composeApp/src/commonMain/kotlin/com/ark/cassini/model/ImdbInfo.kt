package com.ark.cassini.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ImdbInfo(
    @SerialName("meta")
    val meta: Meta
) {
    @Serializable
    data class Meta(
        @SerialName("awards")
        val awards: String? = null,
        @SerialName("background")
        val background: String? = null,
        @SerialName("behaviorHints")
        val behaviorHints: BehaviorHints? = null,
        @SerialName("cast")
        val cast: List<String>? = null,
        @SerialName("country")
        val country: String? = null,
        @SerialName("credits_cast")
        val creditsCast: List<CreditsCast>? = null,
        @SerialName("credits_crew")
        val creditsCrew: List<CreditsCrew>? = null,
        @SerialName("description")
        val description: String? = null,
        @SerialName("director")
        val director: List<String>? = null,
        @SerialName("genres")
        val genres: List<String>? = null,
        @SerialName("id")
        val id: String,
        @SerialName("imdb_id")
        val imdbId: String,
        @SerialName("imdbRating")
        val imdbRating: String? = null,
        @SerialName("language")
        val language: String? = null,
        @SerialName("logo")
        val logo: String? = null,
        @SerialName("moviedb_id")
        val movieDbId: Int? = null,
        @SerialName("name")
        val name: String,
        @SerialName("poster")
        val poster: String? = null,
        @SerialName("releaseInfo")
        val releaseInfo: String? = null,
        @SerialName("runtime")
        val runtime: Int? = null,
        @SerialName("slug")
        val slug: String? = null,
        @SerialName("trailers")
        val trailers: List<Trailer>? = null,
        @SerialName("type")
        val type: String
    ) {
        @Serializable
        data class BehaviorHints(
            @SerialName("defaultVideoId")
            val defaultVideoId: String? = null,
            @SerialName("hasScheduledVideos")
            val hasScheduledVideos: Boolean? = null
        )

        @Serializable
        data class CreditsCast(
            @SerialName("character")
            val character: String? = null,
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String,
            @SerialName("profile_path")
            val profilePath: String? = null
        )

        @Serializable
        data class CreditsCrew(
            @SerialName("department")
            val department: String? = null,
            @SerialName("id")
            val id: Int,
            @SerialName("job")
            val job: String? = null,
            @SerialName("name")
            val name: String,
            @SerialName("profile_path")
            val profilePath: String? = null
        )

        @Serializable
        data class Trailer(
            @SerialName("source")
            val source: String,
            @SerialName("type")
            val type: String? = null
        )
    }
}