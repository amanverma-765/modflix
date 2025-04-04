package com.ark.cassini.scraper.vega

import co.touchlab.kermit.Logger
import com.ark.cassini.model.MediaCatalog
import com.ark.cassini.model.enums.VegaFilter
import com.ark.cassini.utils.LatestUrlProvider
import com.ark.cassini.utils.safeRequest
import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers

internal class VegaCatalogScraper(
    private val httpClient: HttpClient,
    private val latestUrlProvider: LatestUrlProvider
) {

    suspend fun getCatalog(
        searchQuery: String? = null,
        filter: VegaFilter? = null,
        page: Int = 1
    ): List<MediaCatalog>? {
        val baseUrl = latestUrlProvider.getProviderUrl("vega") ?: run {
            Logger.e("Can't fetch movies: provider not found")
            return emptyList()
        }

        val url = when {
            searchQuery != null -> {
                if (page > 1) "$baseUrl/page/$page/?s=$searchQuery"
                else "$baseUrl/?s=$searchQuery"
            }

            filter?.value != null -> {
                if (page > 1) "$baseUrl/${filter.value}/page/$page/"
                else "$baseUrl/${filter.value}/"
            }

            else -> {
                if (page > 1) "$baseUrl/page/$page/"
                else "$baseUrl/"
            }
        }

        Logger.i("Fetching from URL: $url")
        return fetchPosts(baseUrl, url)
    }

    private suspend fun fetchPosts(baseUrl: String, url: String): List<MediaCatalog>? {
        return try {
            val response = safeRequest<String> {
                httpClient.get(url) {
                    headers {
                        VegaHeaders.applyDefaultHeaders(this)
                        append("Referer", baseUrl)
                    }
                }
            }

            if (response == null) {
                Logger.e("Failed to fetch posts: response is null")
                return null
            }

            val document = Ksoup.parse(response)
            val posts = mutableListOf<MediaCatalog>()

            document.select(".blog-items, .post-list").first()
                ?.select("article")
                ?.forEach { element ->
                    val title =
                        element.select(".post-title, entry-title").text().replace("Download", "")
                            .let { fullTitle ->
                                val regex =
                                    """^(.*?)\s*\((\d{4})\)|^(.*?)\s*\((Season \d+)\)""".toRegex()
                                regex.find(fullTitle)?.value ?: fullTitle
                            }

                    val link = element.select("a").attr("href")

                    var image = element.select("a img").attr("data-lazy-src")
                    if (image.isEmpty()) {
                        image = element.select("a img").attr("data-src")
                    }
                    if (image.isEmpty()) {
                        image = element.select("a img").attr("src")
                    }
                    if (image.startsWith("//")) {
                        image = "https:$image"
                    }

                    val date = element.select(".post-date time").attr("datetime")

                    posts.add(MediaCatalog(title, link, image, date))
                }

            posts
        } catch (e: Exception) {
            println("Error fetching posts: ${e.message}")
            null
        }
    }
}