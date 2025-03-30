package com.ark.modflix.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ark.modflix.presentation.features.detail.screen.RootDetailScreen
import com.ark.modflix.presentation.features.download.screen.RootDownloadScreen
import com.ark.modflix.presentation.features.home.screen.RootHomeScreen
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
        modifier = modifier
    ) {

        composable<RootDestinations.Home> { RootHomeScreen() }

        composable<RootDestinations.Setting> { RootSettingScreen() }

        composable<RootDestinations.Player> { RootPlayerScreen() }

        composable<RootDestinations.Search> { RootSearchScreen() }

        composable<RootDestinations.WatchList> { RootWatchListScreen() }

        composable<RootDestinations.Detail> { RootDetailScreen() }

        composable<RootDestinations.Download> { RootDownloadScreen() }

    }
}