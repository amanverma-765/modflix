package com.ark.cassini.platform.vega

import co.touchlab.kermit.Logger
import com.ark.cassini.model.MovieCatalog
import com.ark.cassini.utils.LatestUrlProvider
import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText

class VegaCatalogScraper(
    private val httpClient: HttpClient,
    private val latestUrlProvider: LatestUrlProvider
) {

    suspend fun getCatalog(
        searchQuery: String? = null,
        filter: String? = null,
        page: Int = 1
    ): List<MovieCatalog> {
        val baseUrl = latestUrlProvider.getProviderUrl("Vega") ?: run {
            Logger.e("Can't fetch movies: provider not found")
            return emptyList()
        }

        val url = when {
            searchQuery != null -> "$baseUrl/page/$page/?s=$searchQuery"
            filter != null -> "$baseUrl/$filter/page/$page/"
            else -> baseUrl
        }

        Logger.i("Fetching from URL: $url")
        return fetchPosts(baseUrl, url)
    }

    private suspend fun fetchPosts(baseUrl: String, url: String): List<MovieCatalog> {
        return try {
            val response = httpClient.get(url) {
                headers {
                    append("Referer", baseUrl)
                    append(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
                    )
                }
            }
            val htmlContent = response.bodyAsText()
            val document = Ksoup.parse(htmlContent)
            val posts = mutableListOf<MovieCatalog>()

            document.select(".blog-items,.post-list").first()
                ?.select("article")
                ?.forEach { element ->
                    val title = element.select("a").attr("title").replace("Download", "")
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

                    posts.add(MovieCatalog(title, link, image))
                }

            posts
        } catch (e: Exception) {
            println("Error fetching posts: ${e.message}")
            emptyList()
        }
    }
}