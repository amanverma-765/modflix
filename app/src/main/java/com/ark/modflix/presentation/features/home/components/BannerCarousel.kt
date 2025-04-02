package com.ark.modflix.presentation.features.home.components

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ark.cassini.model.MediaInfo
import com.ark.modflix.presentation.components.MediaBanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun BannerCarousel(
    banners: List<MediaInfo>,
    onWatchNowClicked: (pageUrl: String, posterUrl: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            coroutineScope.launch {
                val nextPage = (pagerState.currentPage + 1) % banners.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }
    // Carousel
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val banner = banners[page]
        MediaBanner(
            bannerInfo = banner,
            onWatchNowClicked = { onWatchNowClicked(banner.pageUrl, banner.posterUrl) },
        )
    }

}