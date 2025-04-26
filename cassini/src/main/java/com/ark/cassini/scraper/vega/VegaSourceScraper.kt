package com.ark.cassini.scraper.vega

import co.touchlab.kermit.Logger
import com.ark.cassini.model.StreamSource
import com.ark.cassini.model.enums.MediaType
import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class VegaSourceScraper(private val httpClient: HttpClient) {

    suspend fun fetchAllStreamSources(
        downloadPageUrls: List<String>,
        mediaType: MediaType
    ): StreamSource? {
        try {
            val allEpisodes = mutableListOf<StreamSource.Episode>()
            val allMovieLinks = mutableMapOf<StreamSource.Source, String>()

            downloadPageUrls.forEach { url ->
                val baseUrl = url.split("/").take(3).joinToString("/")
                Logger.d("Fetching from URL: $url")
                val response = httpClient.get(url) {
                    headers {
                        VegaHeaders.applyDefaultHeaders(this)
                        append("Referer", baseUrl)
                    }
                }
                if (response.status == HttpStatusCode.OK || response.status.value !in 300..399) {
                    val responseBody = response.bodyAsText()
                    when (mediaType) {
                        MediaType.MOVIE -> {
                            val movieLinks = extractMovieLinks(responseBody)
                            allMovieLinks.putAll(movieLinks)
                        }

                        MediaType.SERIES -> {
                            val episodes = extractEpisodeLinks(responseBody)
                            allEpisodes.addAll(episodes)
                        }
                    }
                }
            }

            // Create the StreamSource without using ifEmpty
            val streamSource = StreamSource(
                type = mediaType,
                episodes = if (allEpisodes.isEmpty()) null else allEpisodes,
                movieLinks = if (allMovieLinks.isEmpty()) null else allMovieLinks
            )

            return streamSource
        } catch (e: Exception) {
            Logger.e("Error fetching stream sources: ${e.message}", e)
            return null
        }
    }


    private fun extractEpisodeLinks(response: String): List<StreamSource.Episode> {
        val episodes = mutableListOf<StreamSource.Episode>()

        val doc = Ksoup.parse(response)
        val container = doc.selectFirst(".entry-content, .entry-inner")
        container?.select(".unili-content, .code-block-1")?.remove()

        val headers = container?.select("h4")

        headers?.forEach { header ->
            val title = header.text().replace("-", "").replace(":", "").trim()
            val button1 = header.nextElementSibling()?.selectFirst(
                ".btn-outline[style='background:linear-gradient(135deg,#ed0b0b,#f2d152); color: white;']"
            )
            val link1 = button1?.parent()?.attr("href")
            val button2 = header.nextElementSibling()?.selectFirst(
                ".btn-outline[style='background:linear-gradient(135deg,rgb(252,185,0) 0%,rgb(0,0,0)); color: #fdf8f2;']"
            )
            val link2 = button2?.parent()?.attr("href")

            if (title.isNotEmpty()) {
                link1?.let {
                    episodes.add(
                        StreamSource.Episode(
                            title,
                            mapOf(StreamSource.Source.VCLOUD to it)
                        )
                    )
                }
                link2?.let {
                    episodes.add(
                        StreamSource.Episode(
                            title,
                            mapOf(StreamSource.Source.FILEPRESS to it)
                        )
                    )
                }
            }
        }
        return episodes
    }


    private fun extractMovieLinks(response: String): Map<StreamSource.Source, String> {
        val movieLinks = mutableMapOf<StreamSource.Source, String>()

        val doc = Ksoup.parse(response)
        val container = doc.selectFirst("#content")
        val links = container?.select("p a")
        links?.forEach { link ->
            val button1 = link.selectFirst(
                ".btn-outline[style='background:linear-gradient(135deg,#ed0b0b,#f2d152); color: white;']"
            )
            val link1 = button1?.parent()?.attr("href")
            val button2 = link.selectFirst(
                ".btn-outline[style='background:linear-gradient(135deg,rgb(252,185,0) 0%,rgb(0,0,0)); color: #fdf8f2;']"
            )
            val link2 = button2?.parent()?.attr("href")

            link1?.let { movieLinks[StreamSource.Source.VCLOUD] = it }
            link2?.let { movieLinks[StreamSource.Source.FILEPRESS] = it }
        }
        return movieLinks
    }
}