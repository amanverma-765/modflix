package com.ark.cassini.scraper.sources.hubcloud

import io.ktor.http.HeadersBuilder


internal object HubCloudHeaders {
    fun applyDefaultHeaders(headers: HeadersBuilder) {
        headers.apply {
            append("Accept", "application/json, text/plain, */*")
            append("Accept-Language", "en-GB,en;q=0.9")
            append("Content-Type", "application/json")
            append("Priority", "u=1, i")
            append("Sec-Ch-Ua", "\"Chromium\";v=\"134\", \"Not:A-Brand\";v=\"24\", \"Brave\";v=\"134\"")
            append("Sec-Ch-Ua-Mobile", "?1")
            append("Sec-Ch-Ua-Platform", "\"Android\"")
            append("Sec-Fetch-Dest", "empty")
            append("Sec-Fetch-Mode", "cors")
            append("Sec-Fetch-Site", "same-origin")
            append("Sec-Gpc", "1")
            append("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Mobile Safari/537.36")
        }
    }
}