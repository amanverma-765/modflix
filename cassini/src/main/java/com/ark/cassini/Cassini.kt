package com.ark.cassini

import co.touchlab.kermit.Logger
import com.ark.cassini.model.MediaCatalog
import com.ark.cassini.model.MediaInfo
import com.ark.cassini.model.StreamSource
import com.ark.cassini.model.enums.MediaType
import com.ark.cassini.model.enums.VegaFilter
import com.ark.cassini.model.mapper.MediaInfoMapper.toMediaInfo
import com.ark.cassini.scraper.imdb.ImdbInfoExtractor
import com.ark.cassini.scraper.vega.VegaCatalogScraper
import com.ark.cassini.scraper.vega.VegaSourceScraper
import com.ark.cassini.scraper.vega.VegaInfoScraper
import com.ark.cassini.utils.HttpClientFactory
import com.ark.cassini.utils.LatestUrlProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.io.files.Path

class Cassini(platformPath: Path) {

    private val httpClient = HttpClientFactory.createClient()

    private val latestUrlProvider = LatestUrlProvider(httpClient, platformPath)
    private val imdbInfoExtractor = ImdbInfoExtractor(httpClient)
    private val vegaCatalogScraper = VegaCatalogScraper(httpClient, latestUrlProvider)
    private val vegaInfoScraper = VegaInfoScraper(httpClient)
    private val vegaSourceScraper = VegaSourceScraper(httpClient)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            Logger.i("🔍 Refreshing Providers...")
            latestUrlProvider.refreshLatestProviders()
        }
    }

    suspend fun fetchVegaCatalog(
        searchQuery: String? = null,
        filter: VegaFilter? = null,
        page: Int = 1
    ): List<MediaCatalog>? {
        return vegaCatalogScraper.getCatalog(
            searchQuery = searchQuery,
            filter = filter,
            page = page
        )
    }

    suspend fun fetchVegaInfo(url: String): MediaInfo? {
        val origInfo = vegaInfoScraper.getInfo(pageUrl = url) ?: return null
        if (origInfo.imdbId?.isNotBlank() == true) {
            return imdbInfoExtractor.getImdbInfo(
                imdbId = origInfo.imdbId,
                mediaType = origInfo.type
            )?.toMediaInfo(
                pageUrl = origInfo.pageUrl,
                type = origInfo.type,
                postDownloadLinks = origInfo.downloadLinks,
                details = origInfo.details
            ) ?: origInfo
        }
        return origInfo
    }

    suspend fun fetchAllStreamSources(
        downloadPageUrls: List<String>,
        mediaType: MediaType
    ): StreamSource? {
        return vegaSourceScraper.fetchAllStreamSources(
            downloadPageUrls,
            mediaType
        )
    }
}