package com.ark.cassini.scraper.imdb

import com.ark.cassini.model.ImdbInfo
import com.ark.cassini.model.enums.MediaType
import com.ark.cassini.utils.AppConstants
import com.ark.cassini.utils.safeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class ImdbInfoExtractor(private val httpClient: HttpClient) {
    suspend fun getImdbInfo(imdbId: String, mediaType: MediaType): ImdbInfo? {
        try {
            val infoUrl = AppConstants.IMDB_BASE_URL_1 + "/${mediaType.value}/$imdbId.json"
            val imdbResponse = safeRequest<ImdbInfo> {
                httpClient.get(infoUrl)
            }
            return imdbResponse
        } catch (e: Exception) {
            return null
        }
    }
}