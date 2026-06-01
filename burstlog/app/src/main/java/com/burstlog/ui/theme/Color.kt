package com.burstlog.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * BurstLog color palette.
 *
 * The app supports both dark and light themes. Dark is the default
 * because workouts often happen in low-light environments (gyms, mornings),
 * but the user can switch to light theme in Settings.
 */

// Dark theme palette
val DarkBackground = Color(0xFF0E0F12)
val DarkSurface = Color(0xFF15171C)
val DarkSurfaceVariant = Color(0xFF22252D)
val DarkPrimary = Color(0xFFFF6B35)     // Energetic orange for primary actions
val DarkPrimaryContainer = Color(0xFF4A1F0E)
val DarkOnPrimary = Color(0xFFFFFFFF)
val DarkSecondary = Color(0xFF4ECDC4)   // Cool teal for rest periods
val DarkOnSecondary = Color(0xFF002B28)
val DarkTertiary = Color(0xFFFFD166)    // Warm yellow for warnings
val DarkOnBackground = Color(0xFFE8E8E8)
val DarkOnSurface = Color(0xFFE8E8E8)
val DarkOutline = Color(0xFF45484F)

// Light theme palette
val LightBackground = Color(0xFFFAFAFA)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFEFEFEF)
val LightPrimary = Color(0xFFE85D2C)
val LightPrimaryContainer = Color(0xFFFFE0D4)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightSecondary = Color(0xFF1FA39A)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightTertiary = Color(0xFFE5A800)
val LightOnBackground = Color(0xFF1A1A1A)
val LightOnSurface = Color(0xFF1A1A1A)
val LightOutline = Color(0xFFCFCFCF)

// Shared accent colors for workout state
val WorkColor = Color(0xFFFF6B35)       // Same orange family for WORK state
val RestColor = Color(0xFF4ECDC4)       // Teal for REST state
val WarningColor = Color(0xFFFFD166)    // Yellow for the last few seconds
