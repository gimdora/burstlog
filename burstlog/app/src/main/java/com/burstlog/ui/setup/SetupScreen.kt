package com.burstlog.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.burstlog.BurstLogApplication
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

/**
 * Setup screen where the user configures a workout.
 *
 * Provides:
 *  - Workout name field
 *  - Work seconds, rest seconds, and rounds sliders
 *  - Save as Preset button
 *  - Start button (navigates to the Timer)
 *
 * If [presetIdToLoad] is non-null on first composition, the screen prefills
 * from that preset.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    viewModel: SetupViewModel,
    presetIdToLoad: Long?,
    onBack: () -> Unit,
    onStart: (name: String, work: Int, rest: Int, rounds: Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Load from preset on first composition only.
    val context = LocalContext.current
    val app = context.applicationContext as BurstLogApplication
    LaunchedEffect(presetIdToLoad) {
        if (presetIdToLoad != null) {
            val preset = app.repository.getPresetById(presetIdToLoad)
            if (preset != null) viewModel.loadFromPreset(preset)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::setName,
                label = { Text("Workout Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            NumberSlider(
                label = "Work Time",
                value = state.workSeconds,
                onChange = viewModel::setWorkSeconds,
                range = 5..120,
                suffix = "s"
            )

            NumberSlider(
                label = "Rest Time",
                value = state.restSeconds,
                onChange = viewModel::setRestSeconds,
                range = 0..120,
                suffix = "s"
            )

            NumberSlider(
                label = "Rounds",
                value = state.totalRounds,
                onChange = viewModel::setTotalRounds,
                range = 1..30,
                suffix = ""
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Estimated Duration", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = estimateDuration(state),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.saveAsPreset()
                        scope.launch { snackbarHostState.showSnackbar("Preset saved") }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Preset")
                }

                Button(
                    onClick = {
                        onStart(
                            state.name,
                            state.workSeconds,
                            state.restSeconds,
                            state.totalRounds
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start")
                }
            }
        }
    }
}

/** Reusable +/- slider that shows the current value in big text. */
@Composable
private fun NumberSlider(
    label: String,
    value: Int,
    onChange: (Int) -> Unit,
    range: IntRange,
    suffix: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "$value$suffix",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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
}

private fun estimateDuration(state: SetupUiState): String {
    val totalSeconds = state.workSeconds * state.totalRounds +
            state.restSeconds * (state.totalRounds - 1).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "About ${minutes}m ${seconds.toString().padStart(2, '0')}s total"
}
