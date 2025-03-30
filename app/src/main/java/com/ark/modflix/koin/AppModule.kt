package com.ark.modflix.koin

import com.ark.cassini.Cassini
import com.ark.modflix.presentation.features.home.logic.HomeViewModel
import com.ark.modflix.utils.getPlatformPath
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {
    single { Cassini(platformPath = getPlatformPath()) }
    viewModelOf(::HomeViewModel)
}