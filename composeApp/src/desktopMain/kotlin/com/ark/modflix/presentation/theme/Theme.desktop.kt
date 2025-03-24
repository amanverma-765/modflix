package com.ark.modflix.presentation.theme

import androidx.compose.runtime.Composable

@Composable
internal actual fun SystemAppearance(isDark: Boolean) {
    // Desktop platforms don't need specific system appearance handling
    // as window decorations are managed differently than on Android
    // If needed in the future, we could implement platform-specific window styling here
}