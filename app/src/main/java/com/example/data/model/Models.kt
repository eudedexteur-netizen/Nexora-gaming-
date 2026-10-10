package com.example.data.model

data class Game(
    val id: String,
    val title: String,
    val genre: String,
    val isFavorite: Boolean = false,
    val isDemo: Boolean = false,
    val packageName: String = "",
    val customNotes: String = "",
    val totalPlayTimeMinutes: Int = 0,
    val drawableRes: Int? = null
)

data class GameSession(
    val id: String,
    val gameId: String,
    val gameTitle: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val durationSeconds: Long,
    val startBattery: Int,
    val endBattery: Int,
    val startTempCelsius: Float? = null,
    val endTempCelsius: Float? = null,
    val avgPingMs: Int? = null,
    val pingJitterMs: Int? = null,
    val notes: String = "",
    val isDemo: Boolean = false
) {
    val batteryDrainPercent: Int
        get() = (startBattery - endBattery).coerceAtLeast(0)

    val drainPerHour: Float
        get() {
            if (durationSeconds <= 0) return 0f
            val hours = durationSeconds / 3600f
            return if (hours > 0f) batteryDrainPercent / hours else 0f
        }
}

data class DeviceMetrics(
    val batteryPercent: Int = -1,
    val isCharging: Boolean = false,
    val batteryTempCelsius: Float? = null,
    val availableRamMb: Long = -1,
    val totalRamMb: Long = -1,
    val availableStorageGb: Float = -1f,
    val totalStorageGb: Float = -1f,
    val networkType: String = "Inconnu",
    val pingMs: Int? = null,
    val pingJitterMs: Int? = null,
    val pingFailures: Int = 0,
    val testTarget: String = "Google DNS (8.8.8.8 : 53)",
    val timestamp: Long = System.currentTimeMillis()
)

data class TrainingRecord(
    val id: String,
    val type: TrainingType,
    val scoreMs: Long,
    val dateTimestamp: Long = System.currentTimeMillis()
)

enum class TrainingType {
    VISUAL_REACTION,
    TARGET_TAP
}

data class UserPreferences(
    val language: String = "fr",
    val darkTheme: Boolean = true,
    val breakReminderMinutes: Int = 45,
    val temperatureUnitCelsius: Boolean = true,
    val showDemoData: Boolean = true,
    val hasCompletedOnboarding: Boolean = false,
    val favoriteGameId: String = "",
    val isPlusActive: Boolean = false,
    val activePlusPlan: String = ""
)
