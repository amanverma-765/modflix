package com.ark.cassini.utils

import co.touchlab.kermit.Logger
import com.ark.cassini.model.Provider
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.io.files.Path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement


internal class LatestUrlProvider(
    private val httpClient: HttpClient,
    private val platformPath: Path
) {

    suspend fun refreshLatestProviders() {
        val response = httpClient.get(AppConstants.PROVIDER_URL)
        if (response.status != HttpStatusCode.OK) {
            Logger.e("Failed to fetch latest providers: ${response.status}")
            return
        }

        val json = Json { ignoreUnknownKeys = true }
        val jsonObject = json.decodeFromString<JsonObject>(response.bodyAsText())

        @Serializable
        data class Website(
            val name: String,
            val url: String
        )

        val providerList = jsonObject.map { (key, element) ->
            val site = json.decodeFromJsonElement<Website>(element)
            Provider(
                name = site.name,
                value = key,
                url = site.url
            )
        }

        val store: KStore<List<Provider>> =
            storeOf(file = Path("${platformPath}websites.json"))
        store.set(providerList)

    }

    private suspend fun getAllProviders(): List<Provider> {
        val store: KStore<List<Provider>> =
            storeOf(file = Path("${platformPath}websites.json"))
        return store.get() ?: emptyList()
    }

    suspend fun getProviderUrl(providerKey: String): String? {
        return getAllProviders()
            .find { it.value == providerKey }
            ?.url
    }

}