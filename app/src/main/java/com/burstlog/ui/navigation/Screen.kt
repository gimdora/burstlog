package com.burstlog.ui.navigation

/**
 * Type-safe definitions for every navigation route in the app.
 *
 * Using a sealed class keeps route strings in one place and avoids typos.
 */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Setup : Screen("setup?presetId={presetId}") {
        fun create(presetId: Long? = null): String =
            if (presetId != null) "setup?presetId=$presetId" else "setup"
        const val ARG_PRESET_ID = "presetId"
    }
    data object Timer : Screen("timer/{name}/{work}/{rest}/{rounds}") {
        fun create(name: String, work: Int, rest: Int, rounds: Int): String =
            "timer/${name.ifBlank { "Workout" }}/$work/$rest/$rounds"
        const val ARG_NAME = "name"
        const val ARG_WORK = "work"
        const val ARG_REST = "rest"
        const val ARG_ROUNDS = "rounds"
    }
    data object Settings : Screen("settings")
    data object RecordDetail : Screen("record/{recordId}") {
        fun create(recordId: Long): String = "record/$recordId"
        const val ARG_RECORD_ID = "recordId"
    }
}
