package com.ark.cassini.scraper.sources.hubcloud

import co.touchlab.kermit.Logger
import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

class HubCloudScraper(private val httpClient: HttpClient) {
   suspend fun getMediaStreams(vCloudUrl: String): Map<String, String>? {
        try {
            val response = httpClient.get(vCloudUrl) {
                headers { HubCloudHeaders.applyDefaultHeaders(this) }
            }
            if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.MovedPermanently) {
                Logger.e("Error fetching media streams from hubCloud: ${response.status}")
                return null
            }

            val linkRegex = Regex("""var\s+url\s*=\s*'([^']+)'""")
            val matches = linkRegex.findAll(response.bodyAsText())
            val hubCloudLink = matches.mapNotNull { it.groups[1]?.value }
                .filter { it.contains("hub") }.firstOrNull() ?: run {
                    Logger.e("No hub cloud link found in response")
                    return null
                }

            val streams = extractHubCloudLinks(hubCloudLink)
            if (streams.isNullOrEmpty()) {
                Logger.e("No streams found at hub cloud link")
                return null
            }

            return streams
        } catch (e: Exception) {
            Logger.e("Error fetching media streams from hubCloud: ${e.message}")
            return null
        }
    }

   private suspend fun extractHubCloudLinks(hubCloudLink: String): Map<String, String>? {
        val mediaLinks = mutableMapOf<String, String>()
        val response = httpClient.get(hubCloudLink) {
            headers { HubCloudHeaders.applyDefaultHeaders(this) }
        }
        if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.MovedPermanently) {
            Logger.e("Error extracting links from hubCloud: ${response.status}")
            return null
        }

        val doc = Ksoup.parse(response.bodyAsText())
        val downloadElements = doc.select(".fa-file-download").map { it.parent() }
        for (element in downloadElements) {
            val link = element?.attr("href")
            if (link?.isNotEmpty() == true) {
                val label = element.text()
                mediaLinks[label] = link
            }
        }
        return mediaLinks.ifEmpty { null }
    }
}