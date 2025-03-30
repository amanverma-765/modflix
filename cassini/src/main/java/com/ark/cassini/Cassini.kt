package com.ark.cassini

import co.touchlab.kermit.Logger
import com.ark.cassini.model.MediaCatalog
import com.ark.cassini.model.MediaInfo
import com.ark.cassini.platform.imdb.ImdbInfoExtractor
import com.ark.cassini.platform.vega.VegaCatalogScraper
import com.ark.cassini.platform.vega.VegaInfoScraper
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
    private val vegaInfoScraper = VegaInfoScraper(httpClient, imdbInfoExtractor)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            Logger.i("🔍 Refreshing Providers...")
            latestUrlProvider.refreshLatestProviders()
        }
    }

    suspend fun fetchVegaCatalog(): List<MediaCatalog> {
        return vegaCatalogScraper.getCatalog()
    }

    suspend fun fetchVegaInfo(url: String): MediaInfo? {
        return vegaInfoScraper.getInfo(url)
    }
}