package com.ark.cassini

import co.touchlab.kermit.Logger
import com.ark.cassini.model.MediaCatalog
import com.ark.cassini.model.MediaInfo
import com.ark.cassini.platform.vega.VegaCatalogScraper
import com.ark.cassini.platform.vega.VegaInfoScraper
import com.ark.cassini.utils.LatestUrlProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class Cassini: KoinComponent {

    private val vegaCatalogScraper: VegaCatalogScraper by inject()
    private val vegaInfoScraper: VegaInfoScraper by inject()

    private val latestUrlProvider: LatestUrlProvider by inject()
    init {
        CoroutineScope(Dispatchers.IO).launch {
            Logger.i("🔍 Refreshing Providers...")
            latestUrlProvider.refreshLatestProviders()
        }
    }

    suspend fun fetchVegaCatalog(): List<MediaCatalog> {
        val catalog = vegaCatalogScraper.getCatalog()
        return catalog
    }

    suspend fun fetchVegaInfo(url: String): MediaInfo? {
        val info = vegaInfoScraper.getInfo(url)
        return info
    }
}