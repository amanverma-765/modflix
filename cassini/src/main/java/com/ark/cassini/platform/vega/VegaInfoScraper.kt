package com.ark.cassini.platform.vega

import co.touchlab.kermit.Logger
import com.ark.cassini.model.MediaInfo
import com.ark.cassini.model.enums.MediaType
import com.ark.cassini.model.mapper.MediaInfoMapper.toMediaInfo
import com.ark.cassini.platform.imdb.ImdbInfoExtractor
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText


internal class VegaInfoScraper(
    private val httpClient: HttpClient,
    private val imdbInfoExtractor: ImdbInfoExtractor
) {

    suspend fun getInfo(pageUrl: String): MediaInfo? {
        try {
            // Extract base URL for referer
            val baseUrl = pageUrl.split("/").take(3).joinToString("/")

            val response = httpClient.get(pageUrl) {
                headers {
                    Headers.applyDefaultHeaders(this)
                    append("Referer", baseUrl)
                }
            }

            val document = Ksoup.parse(response.bodyAsText())
            val infoContainer = document.select(".entry-content, .post-inner")

            // Determine content type
            val type = if (infoContainer.select("h3 strong span").text().contains("Series"))
                MediaType.SERIES
            else MediaType.MOVIE

            // IMDB Header
            val imdbIdHeader = infoContainer.select("strong")
                .firstOrNull { it.select("a").attr("href").contains("imdb.com") }
            // Extract IMDB ID
            val imdbId = imdbIdHeader?.select("a")?.attr("href")
                ?.let { """\btt\d+\b""".toRegex().find(it)?.value } ?: ""

            // Extract links
            val postDownloadLinks = mutableListOf<MediaInfo.DownloadLink>()
            val linkSections = infoContainer.select("hr")
            val linkElements = mutableListOf<Element>()

            linkSections.forEach { section ->
                val elements = section.nextElementSiblings()
                    .takeWhile { it.tagName() != "hr" }
                linkElements.addAll(elements)
            }

            linkElements.forEach { element ->
                if (element.tagName().startsWith("h")) {
                    val linkName = element.text().trim()
                    val nextElement = element.nextElementSibling()
                    val directLinks = mutableListOf<MediaInfo.DownloadLink.DirectLink>()
                    nextElement?.select("a")?.forEach { source ->
                        val sourceTitle = source.text()
                        val sourceLink = source.attr("href")
                        directLinks.add(MediaInfo.DownloadLink.DirectLink(sourceTitle, sourceLink))
                    }
                    val qualityMatch = """\d+p\b""".toRegex().find(linkName)
                    val quality = qualityMatch?.value
                    postDownloadLinks.add(
                        MediaInfo.DownloadLink(
                            name = linkName,
                            quality = quality,
                            directLinks = directLinks.takeIf { it.isNotEmpty() }
                        )
                    )
                }
            }

            val imdbInfo = imdbInfoExtractor.getImdbInfo(imdbId, type) ?: run {
                Logger.e("Failed to fetch IMDB info for $imdbId", tag = "VegaInfoScraper")
                return null
            }

            return imdbInfo.toMediaInfo(type, postDownloadLinks)

        } catch (e: Exception) {
            Logger.e("Error while scraping movie Info: ${e.message}", e, "VegaInfoScraper")
            return null
        }
    }
}