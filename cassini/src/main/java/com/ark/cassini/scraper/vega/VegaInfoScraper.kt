package com.ark.cassini.scraper.vega

import co.touchlab.kermit.Logger
import com.ark.cassini.model.MediaInfo
import com.ark.cassini.model.enums.MediaType
import com.ark.cassini.utils.safeRequest
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers


internal class VegaInfoScraper(private val httpClient: HttpClient) {

    suspend fun getInfo(pageUrl: String): MediaInfo? {
        try {
            // Extract base URL for referer
            val baseUrl = pageUrl.split("/").take(3).joinToString("/")

            val response = safeRequest<String> {
                httpClient.get(pageUrl) {
                    headers {
                        VegaHeaders.applyDefaultHeaders(this)
                        append("Referer", baseUrl)
                    }
                }
            }

            if (response == null) {
                Logger.e("Can't fetch movie info: response is null")
                return null
            }

            val document = Ksoup.parse(response)
            val infoContainer = document.select(".entry-content, .post-inner")

            // Extract title
            val title = infoContainer.select(".post-title, entry-title")
                .firstOrNull()?.text()?.replace("Download", "")?.trim()
                ?: run {
                    Logger.e("No title found", tag = "VegaInfoScraper")
                    return null
                }


            // Extract synopsis
            val synopsisHeader = infoContainer.select("h3")
                .firstOrNull { it.select("span").text().lowercase().contains("synopsis") }
            val synopsis = synopsisHeader?.nextElementSibling()?.text()
                ?: run {
                    Logger.e("No synopsis found", tag = "VegaInfoScraper")
                    null
                }


            // Determine content type
            val typeElement = infoContainer.select("h3 strong span")
            val type = if (typeElement.text().contains("Series"))
                MediaType.SERIES
            else MediaType.MOVIE


            // IMDB Header
            val imdbIdHeader = infoContainer.select("strong")
                .firstOrNull { it.select("a").attr("href").contains("imdb.com") }
            // Extract IMDB ID
            val imdbId = imdbIdHeader?.select("a")?.attr("href")
                ?.let { """\btt\d+\b""".toRegex().find(it)?.value } ?: ""


            // Extract extra details
            val details = linkedMapOf<String, String>()
            val infoHeader = infoContainer.select("h3")
                .firstOrNull { it.text().contains("Series Info:") || it.text().contains("Info:") }
            if (infoHeader != null) {
                val detailsParagraph = infoHeader.nextElementSibling()
                if (detailsParagraph?.tagName() == "p") {
                    val detailLines = detailsParagraph.html().split("<br>")
                    for (line in detailLines) {
                        val detailElement = Ksoup.parse(line).body()
                        val keyValueText = detailElement.text().trim()
                        val colonIndex = keyValueText.indexOf(":")
                        if (colonIndex > 0) {
                            val key = keyValueText.substring(0, colonIndex).trim()
                            val value = keyValueText.substring(colonIndex + 1).trim()
                            if (value.isNotBlank()) {
                                details[key] = value
                            }
                        }
                    }
                }
                // Fallback to strong tag extraction if the above didn't work
                if (details.isEmpty()) {
                    val detailsSection = infoContainer.select("p")
                        .filter {
                            it.select("strong").isNotEmpty() && !it.text().contains("Synopsis")
                        }

                    for (paragraph in detailsSection) {
                        val strongTags = paragraph.select("strong")
                        for (strongTag in strongTags) {
                            val keyText = strongTag.text().trim()
                            if (keyText.endsWith(":")) {
                                val key = keyText.removeSuffix(":").trim()
                                val valueNode = strongTag.nextSibling()
                                val value = valueNode?.toString()?.trim() ?: ""
                                if (value.isNotBlank()) {
                                    details[key] = value
                                }
                            }
                        }
                    }
                }
            }


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
                    val sizeMatch =
                        """\[\s*([\d.]+(?:MB|GB)(?:/[A-Z])?)\s*]""".toRegex().find(linkName)
                    postDownloadLinks.add(
                        MediaInfo.DownloadLink(
                            name = linkName,
                            size = sizeMatch?.groups?.get(1)?.value,
                            quality = qualityMatch?.value,
                            directLinks = directLinks.takeIf { it.isNotEmpty() }
                        )
                    )
                }
            }

            return MediaInfo(
                title = title,
                posterUrl = null,
                pageUrl = pageUrl,
                synopsis = synopsis,
                imdbId = imdbId,
                type = type,
                logoUrl = null,
                rating = null,
                details = details,
                bgUrl = null,
                creditsCast = null,
                downloadLinks = postDownloadLinks,
                runtime = null,
                releaseInfo = null,
                genres = null,
                trailers = null
            )
        } catch (e: Exception) {
            Logger.e("Error while scraping movie Info: ${e.message}", e, "VegaInfoScraper")
            return null
        }
    }
}