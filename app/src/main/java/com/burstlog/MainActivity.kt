package com.burstlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.burstlog.settings.ThemeMode
import com.burstlog.ui.navigation.BurstLogNavGraph
import com.burstlog.ui.theme.BurstLogTheme

/**
 * The single activity that hosts all Compose UI.
 *
 * Resolves the user's chosen theme mode from settings and applies it to the
 * whole UI tree. Edge-to-edge is enabled so the timer can use the full screen
 * for maximum visibility during workouts.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = applicationContext as BurstLogApplication

        setContent {
            val settings by app.settingsRepository.settings.collectAsState(
                initial = com.burstlog.settings.AppSettings()
            )
            val systemDark = isSystemInDarkTheme()
            val useDark = when (settings.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> systemDark
            }

            BurstLogTheme(darkTheme = useDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BurstLogNavGraph()
                }
            }
        }
    }
}
