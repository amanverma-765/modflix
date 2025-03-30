package com.ark.modflix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ark.modflix.presentation.navigation.RootDestinations
import com.ark.modflix.presentation.navigation.RootNavHost
import com.ark.modflix.presentation.theme.ModFlixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ModFlixTheme {
                RootNavHost(startDestination = RootDestinations.Home)
            }
        }
    }
}