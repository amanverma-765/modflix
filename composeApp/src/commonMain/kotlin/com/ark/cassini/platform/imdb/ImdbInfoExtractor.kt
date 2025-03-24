package com.ark.cassini.platform.imdb

import co.touchlab.kermit.Logger
import com.ark.cassini.model.ImdbInfo
import com.ark.cassini.model.enums.MediaType
import com.ark.core.utils.AppConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

class ImdbInfoExtractor(private val httpClient: HttpClient) {

    suspend fun getImdbInfo(imdbId: String, mediaType: MediaType): ImdbInfo? {
        val infoUrl = AppConstants.IMDB_BASE_URL_1 + "/${mediaType.value}/$imdbId.json"
        val imdbResponse = httpClient.get(infoUrl)
        if (imdbResponse.status != HttpStatusCode.OK) {
            Logger.e("Failed to fetch IMDB info: ${imdbResponse.status}")
            return null
        }
        return imdbResponse.body<ImdbInfo>()
    }
}