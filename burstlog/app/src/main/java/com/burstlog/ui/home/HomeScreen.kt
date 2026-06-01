package com.burstlog.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.burstlog.data.Preset
import com.burstlog.data.WorkoutRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Home screen.
 *
 * Shows three sections:
 *  - This week's stats card (total time + workout count)
 *  - Saved presets row
 *  - Full workout history list (with swipe-or-tap-delete)
 *
 * A floating action button starts a new workout setup flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartNewWorkout: () -> Unit,
    onPresetSelected: (Long) -> Unit,
    onRecordSelected: (Long) -> Unit,
    onOpenSettings: () -> Unit
) {
    val records by viewModel.records.collectAsState()
    val presets by viewModel.presets.collectAsState()
    val thisWeekSeconds by viewModel.thisWeekSeconds.collectAsState()
    val thisWeekCount by viewModel.thisWeekCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BurstLog", fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onStartNewWorkout,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Workout") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { StatsCard(seconds = thisWeekSeconds, count = thisWeekCount) }

            item {
                SectionHeader("Presets")
            }
            if (presets.isEmpty()) {
                item {
                    EmptyHint("Save a preset on the Setup screen for one-tap launches.")
                }
            } else {
                items(presets, key = { it.id }) { preset ->
                    PresetRow(
                        preset = preset,
                        onTap = { onPresetSelected(preset.id) },
                        onDelete = { viewModel.deletePreset(preset) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader("History")
            }
            if (records.isEmpty()) {
                item {
                    EmptyHint("No workouts yet. Tap 'New Workout' to start your first session.")
                }
            } else {
                items(records, key = { it.id }) { record ->
                    RecordRow(
                        record = record,
                        onTap = { onRecordSelected(record.id) },
                        onDelete = { viewModel.deleteRecord(record.id) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) } // breathing room for the FAB
        }
    }
}

@Composable
private fun StatsCard(seconds: Int, count: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatColumn(value = formatMinutes(seconds), label = "Total Time")
                StatColumn(value = count.toString(), label = "Workouts")
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun PresetRow(preset: Preset, onTap: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onTap
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = preset.totalRounds.toString(),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(preset.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${preset.workSeconds}s work / ${preset.restSeconds}s rest",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete preset")
            }
        }
    }
}

@Composable
private fun RecordRow(record: WorkoutRecord, onTap: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onTap
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(record.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${formatDate(record.startedAtMillis)} · ${formatMinutes(record.totalDurationSeconds)} · ${record.completedRounds}/${record.totalRounds} rounds",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete record")
            }
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

/** Format seconds as e.g. "12m 05s" or "1h 02m". */
private fun formatMinutes(seconds: Int): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "${h}h ${m.toString().padStart(2, '0')}m"
    else "${m}m ${s.toString().padStart(2, '0')}s"
}

private fun formatDate(millis: Long): String {
    val fmt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    return fmt.format(Date(millis))
}
