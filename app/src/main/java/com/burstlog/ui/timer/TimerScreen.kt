package com.burstlog.ui.timer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.burstlog.timer.TimerPhase
import com.burstlog.ui.theme.RestColor
import com.burstlog.ui.theme.WarningColor
import com.burstlog.ui.theme.WorkColor

/**
 * Timer screen that drives a workout session.
 *
 * The screen takes the configuration from navigation arguments, starts the
 * engine via the ViewModel, and renders a big countdown number with the
 * current phase color (orange for WORK, teal for REST, yellow when the
 * last few seconds are ticking down).
 *
 * Pressing back is intercepted so users can't accidentally drop out of a
 * workout mid-session.
 */
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    workoutName: String,
    workSeconds: Int,
    restSeconds: Int,
    totalRounds: Int,
    onFinished: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Kick the timer off once on first composition.
    LaunchedEffect(Unit) {
        viewModel.startWorkout(workoutName, workSeconds, restSeconds, totalRounds)
    }

    // When the workout is DONE, give a 1.5-second pause for the user to see
    // the completion screen, then auto-navigate back to Home.
    LaunchedEffect(state.phase) {
        if (state.phase == TimerPhase.DONE) {
            kotlinx.coroutines.delay(1500)
            onFinished()
        }
    }

    // Capture the back gesture so the workout cannot end accidentally.
    BackHandler(enabled = state.phase != TimerPhase.DONE) {
        viewModel.stop()
    }

    // Color the background to reflect the current phase.
    val targetColor: Color = when {
        state.phase == TimerPhase.WORK && state.secondsRemaining in 1..3 -> WarningColor
        state.phase == TimerPhase.WORK -> WorkColor
        state.phase == TimerPhase.REST && state.secondsRemaining in 1..3 -> WarningColor
        state.phase == TimerPhase.REST -> RestColor
        else -> MaterialTheme.colorScheme.surface
    }
    val animatedColor by animateColorAsState(targetValue = targetColor, label = "phaseColor")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = animatedColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top: workout name + round counter
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = workoutName,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (state.phase != TimerPhase.DONE) {
                    Text(
                        text = "Round ${state.currentRound} / ${state.totalRounds}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Middle: phase label + huge countdown
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = phaseLabel(state.phase),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (state.phase == TimerPhase.DONE) {
                    Text(
                        text = "DONE!",
                        fontSize = 96.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = state.secondsRemaining.toString(),
                        fontSize = 200.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            // Bottom: controls
            if (state.phase != TimerPhase.DONE) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    ControlButton(
                        onClick = {
                            if (state.isPaused) viewModel.resume() else viewModel.pause()
                        }
                    ) {
                        Icon(
                            if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (state.isPaused) "Resume" else "Pause"
                        )
                    }
                    ControlButton(onClick = { viewModel.stop() }) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop")
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun ControlButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.20f))
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize()
        ) {
            content()
        }
    }
}

private fun phaseLabel(phase: TimerPhase): String = when (phase) {
    TimerPhase.IDLE -> "READY"
    TimerPhase.WORK -> "WORK"
    TimerPhase.REST -> "REST"
    TimerPhase.DONE -> "FINISHED"
}
