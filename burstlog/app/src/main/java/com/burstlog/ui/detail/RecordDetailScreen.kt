package com.burstlog.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.burstlog.data.WorkoutRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Detail screen for a single workout record.
 *
 * Shows the full set of stored fields for the record and a Delete button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordDetailScreen(
    viewModel: RecordDetailViewModel,
    recordId: Long,
    onBack: () -> Unit
) {
    LaunchedEffect(recordId) { viewModel.load(recordId) }
    val record by viewModel.record.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.delete(onBack) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { padding ->
        val r = record
        if (r == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Text("Loading...", modifier = Modifier.padding(20.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(r.name, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = formatDate(r.startedAtMillis),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                DetailCard {
                    DetailRow("Work time", "${r.workSeconds}s")
                    DetailRow("Rest time", "${r.restSeconds}s")
                    DetailRow("Rounds", "${r.completedRounds} / ${r.totalRounds}")
                    DetailRow("Total duration", formatDuration(r.totalDurationSeconds))
                    DetailRow(
                        "Completion",
                        completionLabel(r)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

private fun completionLabel(r: WorkoutRecord): String {
    if (r.totalRounds <= 0) return "n/a"
    val pct = (r.completedRounds.toDouble() / r.totalRounds * 100).toInt()
    return "$pct%"
}

private fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "${m}m ${s.toString().padStart(2, '0')}s"
}

private fun formatDate(millis: Long): String {
    val fmt = SimpleDateFormat("EEEE, MMM d yyyy, h:mm a", Locale.getDefault())
    return fmt.format(Date(millis))
}
