package com.ark.cassini.model.enums

enum class VegaFilter(
    val title: String,
    val value: String?
) {
    LATEST("Featured", null),
    TRENDING("Trending Now", "featured"),
    NETFLIX("Netflix", "web-series/netflix"),
    PRIME("Amazon Prime", "web-series/amazon-prime-video"),
    DISNEY_PLUS("Disney+", "web-series/disney-plus-hotstar"),
    ANIME("Anime", "anime-series"),
    K_DRAMA("K-Drama", "korean-series"),
//    MINI_TV("Mini TV", "web-series/amazon-prime-video/minitv"),
}