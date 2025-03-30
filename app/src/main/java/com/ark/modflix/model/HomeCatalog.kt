package com.ark.modflix.model

import com.ark.cassini.model.MediaCatalog

data class HomeCatalog(
//    val lastWatched: List<MediaCatalog>,
    val latest: List<MediaCatalog>,
    val trending: List<MediaCatalog>,
    val netflix: List<MediaCatalog>,
    val amazonPrime: List<MediaCatalog>,
    val disneyPlus: List<MediaCatalog>,
    val kDrama: List<MediaCatalog>,
    val anime: List<MediaCatalog>
)