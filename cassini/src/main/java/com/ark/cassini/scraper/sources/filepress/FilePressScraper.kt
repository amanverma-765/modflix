package com.ark.cassini.scraper.sources.filepress

import co.touchlab.kermit.Logger
import com.ark.cassini.utils.LatestUrlProvider
import com.ark.cassini.utils.safeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


@Serializable
private data class DataPayload(
    val id: String,
    val method: String,
    val captchaValue: String?
)


internal class FilePressScraper(
    private val httpClient: HttpClient,
    private val latestUrlProvider: LatestUrlProvider
) {
    companion object {
        private val CODE_REGEX = """https?://[^/]+/file/([a-zA-Z0-9]+)""".toRegex()
        private val URL_REGEX = """https?://[^\s\]")]+""".toRegex()

        // Keeping original typos as specified
        private val DOWNLOAD_METHODS = listOf(
            "publicDownlaod",
            "indexDownlaod",
            "cloudDownlaod",
            "cloudDownload",
            "cloudR2Downlaod"
        )

        private const val DOWNLOAD_ENDPOINT = "/api/file/downlaod/"
        private const val DOWNLOAD2_ENDPOINT = "/api/file/downlaod2/"
    }

    suspend fun getMediaStreams(filePressUrl: String): Map<String, String>? = coroutineScope {
        try {
            val code = extractCodeFromUrl(filePressUrl) ?: return@coroutineScope null

            val results = DOWNLOAD_METHODS.map { method ->
                async { getSource(code, method) }
            }.awaitAll()

            // Fixed version with explicit type parameters
            val streams = results.filterNotNull()
                .fold(mutableMapOf<String, String>()) { acc, map ->
                    acc.also { it.putAll(map) }
                }

            return@coroutineScope streams.ifEmpty { null }
        } catch (e: Exception) {
            Logger.e("Error getting stream from FilePress URL: ${e.message}")
            null
        }
    }

    private fun extractCodeFromUrl(url: String): String? {
        return CODE_REGEX.find(url)?.groups?.get(1)?.value.also {
            if (it == null) Logger.e("Invalid FilePress URL: $url")
        }
    }

    private suspend fun getSource(code: String, method: String): Map<String, String>? {
        try {
            // First Request
            val firstPayload = DataPayload(id = code, method = method, captchaValue = "")
            val initialResponse = makeInitialRequest(firstPayload) ?: return null

            // Parse response
            val downloadId = extractDownloadId(initialResponse) ?: return null

            // Second Request
            val streamUrl = makeSecondRequest(downloadId, method) ?: return null

            return mapOf(method to streamUrl)
        } catch (e: Exception) {
            Logger.e("Error getting stream from FilePress method: $method, ${e.message}")
            return null
        }
    }

    private suspend fun makeInitialRequest(payload: DataPayload): String? {

        val filePressUrl = latestUrlProvider.getProviderUrl("filepress") ?: return null

        return safeRequest<String> {
            httpClient.post("$filePressUrl$DOWNLOAD_ENDPOINT") {
                contentType(ContentType.Application.Json)
                setBody(payload)
                headers {
                    FilePressHeaders.applyDefaultHeaders(this)
                    append("Origin", filePressUrl)
                    append("Referer", "$filePressUrl/file/${payload.id}")
                }
            }
        }.also { response ->
            if (response == null) {
                Logger.e("Error getting stream from FilePress method: ${payload.method}")
            }

            val jsonResponse = response?.let { Json.parseToJsonElement(it).jsonObject }
            val statusCode = jsonResponse?.get("statusCode")?.jsonPrimitive?.int

            if (statusCode != 200) {
                Logger.e("Stream from FilePress method: ${payload.method} not available")
                return null
            }
        }
    }

    private fun extractDownloadId(response: String): String? {
        val jsonObject = Json.parseToJsonElement(response).jsonObject
        val dataElement = jsonObject["data"]

        return when {
            dataElement is JsonPrimitive && dataElement.isString -> dataElement.content
            dataElement is JsonObject -> dataElement["downloadId"]?.jsonPrimitive?.content
            else -> {
                Logger.e("Download ID not found in response: $response")
                null
            }
        }
    }

    private suspend fun makeSecondRequest(downloadId: String, method: String): String? {

        val filePressUrl = latestUrlProvider.getProviderUrl("filepress") ?: return null

        val secondPayload = DataPayload(id = downloadId, method = method, captchaValue = null)
        val finalResponse = httpClient.post("$filePressUrl$DOWNLOAD2_ENDPOINT") {
            contentType(ContentType.Application.Json)
            setBody(secondPayload)
            headers {
                FilePressHeaders.applyDefaultHeaders(this)
                append("Origin", filePressUrl)
                append("Referer", "$filePressUrl/download/$downloadId")
            }
        }

        // Find the first URL in the response
        return URL_REGEX.find(finalResponse.bodyAsText())?.value
    }
}