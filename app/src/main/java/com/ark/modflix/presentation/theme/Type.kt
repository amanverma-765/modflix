package com.ark.modflix.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.ark.modflix.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val poppinsFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Poppins"),
        fontProvider = provider,
    )
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    // Display styles
    displayLarge = baseline.displayLarge.copy(fontFamily = poppinsFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = poppinsFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = poppinsFontFamily),

    // Headline styles
    headlineLarge = baseline.headlineLarge.copy(fontFamily = poppinsFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = poppinsFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = poppinsFontFamily),

    // Title styles
    titleLarge = baseline.titleLarge.copy(fontFamily = poppinsFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = poppinsFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = poppinsFontFamily),

    // Body styles
    bodyLarge = baseline.bodyLarge.copy(fontFamily = poppinsFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = poppinsFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = poppinsFontFamily),

    // Label styles
    labelLarge = baseline.labelLarge.copy(fontFamily = poppinsFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = poppinsFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = poppinsFontFamily),
)