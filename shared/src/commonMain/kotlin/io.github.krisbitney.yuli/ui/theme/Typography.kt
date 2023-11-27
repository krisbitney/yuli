package io.github.krisbitney.yuli.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
expect fun font(res: String, weight: FontWeight, style: FontStyle): Font

@Composable
fun getTypography(): Typography {
    val nunitoFontFamily = FontFamily(
        font("nunito_black", FontWeight.Black, FontStyle.Normal),
        font("nunito_black_italic", FontWeight.Black, FontStyle.Italic),
        font("nunito_bold", FontWeight.Bold, FontStyle.Normal),
        font("nunito_bold_italic", FontWeight.Bold, FontStyle.Italic),
        font("nunito_extrabold", FontWeight.ExtraBold, FontStyle.Normal),
        font("nunito_extrabold_italic", FontWeight.ExtraBold, FontStyle.Italic),
        font("nunito_extralight", FontWeight.ExtraLight, FontStyle.Normal),
        font("nunito_extralight_italic", FontWeight.ExtraLight, FontStyle.Italic),
        font("nunito_italic", FontWeight.Normal, FontStyle.Italic),
        font("nunito_light", FontWeight.Light, FontStyle.Normal),
        font("nunito_light_italic", FontWeight.Light, FontStyle.Italic),
        font("nunito_medium", FontWeight.Medium, FontStyle.Normal),
        font("nunito_medium_italic", FontWeight.Medium, FontStyle.Italic),
        font("nunito_regular", FontWeight.Normal, FontStyle.Normal),
        font("nunito_semibold", FontWeight.SemiBold, FontStyle.Normal),
        font("nunito_semibold_italic", FontWeight.SemiBold, FontStyle.Italic),
    )

    return Typography(
        displayLarge = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Black,
            fontSize = 30.sp, // Adjusted to reflect the username size
            lineHeight = 38.sp,
            letterSpacing = (-0.015).sp // Slightly tightened
        ),
        displayMedium = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp, // Adjusted for secondary headers
            lineHeight = 30.sp,
            letterSpacing = (-0.015).sp
        ),
        displaySmall = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp, // Adjusted as a smaller header
            lineHeight = 26.sp,
            letterSpacing = (-0.015).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp, // Similar to menu items
            lineHeight = 24.sp,
            letterSpacing = 0.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp, // For less prominent headers
            lineHeight = 22.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp, // For tertiary headers or important labels
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp, // Titles in navigation
            lineHeight = 22.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp, // Subtitles or secondary navigation
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),
        titleSmall = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp, // Smaller information such as counts
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp, // Main content text
            lineHeight = 20.sp,
            letterSpacing = 0.15.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp, // For less emphasized content
            lineHeight = 18.sp,
            letterSpacing = 0.15.sp
        ),
        bodySmall = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp, // Smallest readable text
            lineHeight = 16.sp,
            letterSpacing = 0.15.sp
        ),
        labelLarge = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp, // For labels that require attention
            lineHeight = 20.sp,
            letterSpacing = 0.sp
        ),
        labelMedium = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp, // Standard labels
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        ),
        labelSmall = TextStyle(
            fontFamily = nunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp, // Smaller labels
            lineHeight = 16.sp,
            letterSpacing = 0.15.sp
        )
    )
}