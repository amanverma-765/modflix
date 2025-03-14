package com.ark.cassini.platform.vega

import co.touchlab.kermit.Logger
import com.ark.cassini.model.MovieInfo
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText


class VegaInfoScraper(private val httpClient: HttpClient) {

    suspend fun getInfo(pageUrl: String, imgUrl: String): MovieInfo? {
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

            // Extract title
            val title = infoContainer.select(".post-title, entry-title")
                .firstOrNull()?.text()?.replace("Download", "")?.trim()
                ?: run {
                    Logger.e("No title found", tag = "VegaInfoScraper")
                    return null
                }

            // Determine content type
            val type = if (infoContainer.select("h3 strong span").text().contains("Series"))
                MovieInfo.Type.SERIES
            else MovieInfo.Type.MOVIE

            // Extract synopsis
            val synopsisHeader = infoContainer.select("h3")
                .firstOrNull { it.select("span").text().lowercase().contains("synopsis") }
            val synopsis = synopsisHeader?.nextElementSibling()?.text()
                ?: run {
                    Logger.e("No synopsis found", tag = "VegaInfoScraper")
                    return null
                }

            // IMDB Header
            val imdbIdHeader = infoContainer.select("strong")
                .firstOrNull { it.select("a").attr("href").contains("imdb.com") }

            // Extract IMDB ID
            val imdbId = imdbIdHeader?.select("a")?.attr("href")
                ?.let { """\btt\d+\b""".toRegex().find(it)?.value } ?: ""

            // Extract IMDB rating
            val ratingRegex = """IMDb Rating:- (\d+\.\d+|\d+)/10""".toRegex()
            val rating = if (imdbIdHeader == null) null
            else ratingRegex.find(imdbIdHeader.text())?.groupValues?.get(1)?.toFloatOrNull()

            // Extract extra details
            val details = hashMapOf<String, String>()
            if (imdbIdHeader != null) {
                val extractedHtml = StringBuilder()
                var currentNode = imdbIdHeader.nextSibling()
                while (currentNode != null && currentNode != synopsisHeader) {
                    extractedHtml.append(currentNode.outerHtml())
                    currentNode = currentNode.nextSibling()
                }
                val detailSection = Ksoup.parse(extractedHtml.toString())
                val strongTextElements = detailSection.select("strong")
                for (strongTag in strongTextElements) {
                    val keyText = strongTag.text()
                    if (keyText.endsWith(":")) {
                        val key = keyText.removeSuffix(":").trim()
                        val value = strongTag.nextSibling()
                            ?.outerHtml()?.trim() ?: ""
                        if (value.isNotBlank()) {
                            details[key] = value
                        }
                    }
                }
            }

            // Extract links
            val postDownloadLinks = mutableListOf<MovieInfo.DownloadLink>()
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
                    val directLinks = mutableListOf<MovieInfo.DownloadLink.DirectLink>()
                    nextElement?.select("a")?.forEach { source ->
                        val sourceTitle = source.text()
                        val sourceLink = source.attr("href")
                        directLinks.add(MovieInfo.DownloadLink.DirectLink(sourceTitle, sourceLink))
                    }
                    val qualityMatch = """\d+p\b""".toRegex().find(linkName)
                    val quality = qualityMatch?.value
                    postDownloadLinks.add(
                        MovieInfo.DownloadLink(
                            name = linkName,
                            quality = quality,
                            directLinks = directLinks.takeIf { it.isNotEmpty() }
                        )
                    )
                }
            }

            return MovieInfo(
                title = title,
                imgUrl = imgUrl,
                synopsis = synopsis,
                imdbId = imdbId,
                rating = rating,
                type = type,
                downloadLinks = postDownloadLinks,
                details = details
            )

        } catch (e: Exception) {
            Logger.e("Error while scraping movie Info: ${e.message}", e, "VegaInfoScraper")
            return null
        }
    }
}