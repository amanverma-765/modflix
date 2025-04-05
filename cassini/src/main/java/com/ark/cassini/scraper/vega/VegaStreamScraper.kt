package com.ark.cassini.scraper.vega

import com.ark.cassini.model.enums.MediaType
import io.ktor.client.HttpClient

internal class VegaStreamScraper(
    private val httpClient: HttpClient
) {

    fun getMediaStreams(mediaType: MediaType, url: String) {

    }
}