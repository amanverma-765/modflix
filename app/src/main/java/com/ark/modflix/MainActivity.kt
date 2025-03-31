package com.ark.modflix

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ark.modflix.presentation.components.ConnectivitySheet
import com.ark.modflix.presentation.navigation.RootDestinations
import com.ark.modflix.presentation.navigation.RootNavHost
import com.ark.modflix.presentation.theme.ModFlixTheme
import dev.jordond.connectivity.compose.rememberConnectivityState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            ModFlixTheme {
                App()
            }
        }
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val state = rememberConnectivityState { autoStart = true }
    ConnectivitySheet(connectivityState = state, onRetryClicked = { state.forceCheck() })
    RootNavHost(modifier = modifier, startDestination = RootDestinations.Home)
}