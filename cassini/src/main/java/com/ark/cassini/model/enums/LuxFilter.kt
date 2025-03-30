package com.ark.cassini.model.enums


enum class LuxFilter(
    val title: String,
    val value: String
) {
    TRENDING("Trending", "category/featured"),
    PRIME("Amazon Prime", "category/web-series/amazon-prime-video"),
    NETFLIX("Netflix", "category/web-series/netflix"),
    DISNEY_PLUS("Disney+", "category/web-series/disney-plus-hotstar"),
    SONY_LIV("Sony Liv", "category/web-series/sonyliv"),
    ZEE5("Zee5", "category/web-series/zee5-originals"),
    JIO_CINEMA("Jio Cinema", "category/web-series/jio-studios"),
    VOOT("Voot", "category/web-series/voot-originals"),
    MX_PLAYER("MX Player", "category/web-series/mx-original"),
    SOUTH_INDIAN("South Indian", "category/hindi-dubbed-movies")
}