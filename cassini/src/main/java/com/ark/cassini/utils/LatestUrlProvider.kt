package com.ark.cassini.utils

import co.touchlab.kermit.Logger
import com.ark.cassini.model.Provider
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.io.files.Path
import kotlinx.serialization.json.Json


internal class LatestUrlProvider(
    private val httpClient: HttpClient,
    private val platformPath: Path
) {

    suspend fun refreshLatestProviders() {
        val response = safeRequest<String> {
            httpClient.get(AppConstants.PROVIDER_URL)
        }
        if (response == null) {
            Logger.e("Failed to fetch latest providers")
            return
        }

        val providerList = Json.decodeFromString<List<Provider>>(response)
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
            .find { it.key == providerKey }
            ?.url
    }

}