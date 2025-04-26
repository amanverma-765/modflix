package com.ark.cassini.scraper.vega

import co.touchlab.kermit.Logger
import com.ark.cassini.model.MediaInfo
import com.ark.cassini.model.enums.MediaType
import io.ktor.client.HttpClient

internal class VegaStreamScraper(
    private val httpClient: HttpClient
) {
    fun getMediaStreams(
        mediaType: MediaType,
        downloadLinks: List<MediaInfo.DownloadLink>
    ): String? {
        try {
            when (mediaType) {
                MediaType.SERIES -> {}
                MediaType.MOVIE -> {}
            }
            return ""
        } catch (e: Exception) {
            Logger.e("Error fetching media streams: ${e.message}")
            return null
        }
    }
}