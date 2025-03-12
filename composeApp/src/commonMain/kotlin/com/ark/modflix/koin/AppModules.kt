package com.ark.modflix.koin

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect val platformModule: Module

val sharedModule = module {
    single { "Hello from shared module" }
}