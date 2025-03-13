package com.ark.cassini.model

enum class VegaFilter(
    val title: String,
    val value: String
) {
    TRENDING("Trending", "featured"),
    ANIME("Anime", "anime-series"),
    K_DRAMA("K-Drama", "korean-series"),
    PRIME("Amazon Prime", "web-series/amazon-prime-video"),
    NETFLIX("Netflix", "web-series/netflix"),
    DISNEY_PLUS("Disney+", "web-series/disney-plus-hotstar"),
    MINI_TV("Mini TV", "web-series/mini-tv"),
}