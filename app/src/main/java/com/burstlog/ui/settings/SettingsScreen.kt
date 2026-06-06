package com.burstlog.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.burstlog.settings.AppSettings
import com.burstlog.settings.ThemeMode
import com.burstlog.settings.VoiceType

/**
 * Settings screen. Every change is persisted immediately through DataStore.
 *
 * Sections:
 *  - Voice cues (which trainer voice + per-phase countdown window)
 *  - Vibration (start cue + per-phase vibration window)
 *  - Theme (dark / light / follow system)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            VoiceSection(
                settings = settings,
                onVoiceChange = viewModel::setVoice,
                onWorkCountdownChange = viewModel::setWorkCountdownStart,
                onRestCountdownChange = viewModel::setRestCountdownStart
            )

            VibrationSection(
                settings = settings,
                onStartToggle = viewModel::setVibrateOnStart,
                onWorkVibChange = viewModel::setWorkVibCountdown,
                onRestVibChange = viewModel::setRestVibCountdown
            )

            ThemeSection(
                settings = settings,
                onThemeChange = viewModel::setTheme
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ------------------- Sections -------------------

@Composable
private fun VoiceSection(
    settings: AppSettings,
    onVoiceChange: (VoiceType) -> Unit,
    onWorkCountdownChange: (Int) -> Unit,
    onRestCountdownChange: (Int) -> Unit
) {
    SectionCard(title = "Voice Cues") {
        Text(
            text = "Trainer voice",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        VoiceChips(current = settings.voiceType, onSelect = onVoiceChange)

        Spacer(modifier = Modifier.height(16.dp))
        SliderRow(
            label = "Work countdown starts at",
            value = settings.workCountdownStartSeconds,
            range = 0..30,
            suffix = "s remaining",
            onChange = onWorkCountdownChange
        )
        SliderRow(
            label = "Rest countdown starts at",
            value = settings.restCountdownStartSeconds,
            range = 0..30,
            suffix = "s remaining",
            onChange = onRestCountdownChange
        )
        HintText("Set to 0 to disable countdown voice for that phase.")
    }
}

@Composable
private fun VibrationSection(
    settings: AppSettings,
    onStartToggle: (Boolean) -> Unit,
    onWorkVibChange: (Int) -> Unit,
    onRestVibChange: (Int) -> Unit
) {
    SectionCard(title = "Vibration") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Vibrate at the start of each interval",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = settings.vibrateOnIntervalStart,
                onCheckedChange = onStartToggle
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        SliderRow(
            label = "Work vibration window",
            value = settings.workVibrationCountdownSeconds,
            range = 0..30,
            suffix = "s remaining",
            onChange = onWorkVibChange
        )
        SliderRow(
            label = "Rest vibration window",
            value = settings.restVibrationCountdownSeconds,
            range = 0..30,
            suffix = "s remaining",
            onChange = onRestVibChange
        )
    }
}

@Composable
private fun ThemeSection(
    settings: AppSettings,
    onThemeChange: (ThemeMode) -> Unit
) {
    SectionCard(title = "Appearance") {
        ThemeModeOption(
            label = "Dark",
            selected = settings.themeMode == ThemeMode.DARK,
            onSelect = { onThemeChange(ThemeMode.DARK) }
        )
        ThemeModeOption(
            label = "Light",
            selected = settings.themeMode == ThemeMode.LIGHT,
            onSelect = { onThemeChange(ThemeMode.LIGHT) }
        )
        ThemeModeOption(
            label = "Follow system",
            selected = settings.themeMode == ThemeMode.SYSTEM,
            onSelect = { onThemeChange(ThemeMode.SYSTEM) }
        )
    }
}

// ------------------- Reusable widgets -------------------

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun VoiceChips(current: VoiceType, onSelect: (VoiceType) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        VoiceType.values().forEach { voice ->
            FilterChip(
                selected = current == voice,
                onClick = { onSelect(voice) },
                label = { Text(voiceLabel(voice)) }
            )
        }
    }
}

private fun voiceLabel(v: VoiceType): String = when (v) {
    VoiceType.MALE -> "Male"
    VoiceType.FEMALE -> "Female"
    VoiceType.WHISTLE -> "Whistle"
    VoiceType.NONE -> "None"
}

@Composable
private fun SliderRow(
    label: String,
    value: Int,
    range: IntRange,
    suffix: String,
    onChange: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text(
                "$value $suffix",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = (range.last - range.first - 1).coerceAtLeast(0)
        )
    }
}

@Composable
private fun ThemeModeOption(label: String, selected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun HintText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}
