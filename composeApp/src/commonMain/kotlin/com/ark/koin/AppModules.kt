package com.ark.koin

import com.ark.core.data.HttpClientFactory
import com.ark.cassini.platform.vega.VegaCatalogScraper
import com.ark.cassini.utils.LatestUrlProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.createClient(engine = get()) }

    singleOf(::LatestUrlProvider)
    singleOf(::VegaCatalogScraper)
}