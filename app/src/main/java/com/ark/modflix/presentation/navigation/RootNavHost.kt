package com.ark.modflix.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ark.cassini.model.enums.VegaFilter
import com.ark.modflix.presentation.features.detail.screen.RootDetailScreen
import com.ark.modflix.presentation.features.downloader.screen.RootDownloadScreen
import com.ark.modflix.presentation.features.home.screen.RootHomeScreen
import com.ark.modflix.presentation.features.listing.screen.RootMediaListScreen
import com.ark.modflix.presentation.features.player.screen.RootPlayerScreen
import com.ark.modflix.presentation.features.search.screen.RootSearchScreen
import com.ark.modflix.presentation.features.setting.screen.RootSettingScreen
import com.ark.modflix.presentation.features.watchlist.screen.RootWatchListScreen

@Composable
fun RootNavHost(
    modifier: Modifier = Modifier,
    startDestination: RootDestinations
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally(tween(500)) { it } },
        exitTransition = { slideOutHorizontally(tween(500)) { -it } },
        popEnterTransition = { slideInHorizontally(tween(500)) { -it } },
        popExitTransition = { slideOutHorizontally(tween(500)) { it } },
        modifier = modifier
    ) {

        composable<RootDestinations.Home> {
            RootHomeScreen(
                onBannerClicked = { pageUrl, posterUrl ->
                    navController.navigate(RootDestinations.Detail(pageUrl, posterUrl))
                },
                onSeeAllClicked = { category ->
                    navController.navigate(
                        RootDestinations.MediaList(category = category)
                    )
                }
            )
        }

        composable<RootDestinations.MediaList> { navBackStack ->
            val mediaList = navBackStack.toRoute<RootDestinations.MediaList>()
            val category = VegaFilter.valueOf(mediaList.category)
            RootMediaListScreen(
                category = category,
                onBannerClicked = { pageUrl, posterUrl ->
                    navController.navigate(RootDestinations.Detail(pageUrl, posterUrl))
                },
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable<RootDestinations.Setting> { RootSettingScreen() }

        composable<RootDestinations.Player> { RootPlayerScreen() }

        composable<RootDestinations.Search> { RootSearchScreen() }

        composable<RootDestinations.WatchList> { RootWatchListScreen() }

        composable<RootDestinations.Detail> { navBackStack ->
            val detail = navBackStack.toRoute<RootDestinations.Detail>()
            RootDetailScreen(
                pageUrl = detail.pageUrl,
                posterUrl = detail.posterUrl,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable<RootDestinations.Downloader> { RootDownloadScreen() }

    }
}