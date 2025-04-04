package com.ark.cassini.model.mapper

import com.ark.cassini.model.ImdbInfo
import com.ark.cassini.model.MediaInfo
import com.ark.cassini.model.enums.MediaType
import com.ark.cassini.utils.AppConstants

internal object MediaInfoMapper {
    fun ImdbInfo.toMediaInfo(
        pageUrl: String,
        type: MediaType,
        details: Map<String, String>,
        postDownloadLinks: List<MediaInfo.DownloadLink>
    ) = MediaInfo(
        title = this.meta.name,
        synopsis = this.meta.description,
        imdbId = this.meta.imdbId,
        rating = this.meta.imdbRating?.toFloatOrNull(),
        type = type,
        downloadLinks = postDownloadLinks,
        posterUrl = this.meta.poster,
        bgUrl = this.meta.background,
        creditsCast = this.meta.creditsCast?.map { it.toCast() },
        genres = this.meta.genres,
        runtime = this.meta.runtime,
        releaseInfo = this.meta.releaseInfo,
        logoUrl = this.meta.logo,
        pageUrl = pageUrl,
        details = details,
        trailers = this.meta.trailers?.map { "https://www.youtube.com/watch?v=" + it.source }
    )

    private fun ImdbInfo.Meta.CreditsCast.toCast() = MediaInfo.Cast(
        character = this.character,
        id = this.id,
        name = this.name,
        profileUrl = if (this.profilePath != null) AppConstants.TMDB_CHARACTERS_BASE_URL + "/t/p/w200" + this.profilePath
        else null
    )
}