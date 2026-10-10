package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.data.model.Game
import com.example.data.model.GameSession
import com.example.data.model.TrainingRecord
import com.example.data.model.TrainingType
import com.example.data.model.UserPreferences
import org.json.JSONArray
import org.json.JSONObject

class NexoraPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nexora_gaming_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_GAMES = "games_list"
        private const val KEY_SESSIONS = "sessions_list"
        private const val KEY_TRAINING = "training_records"
        private const val KEY_PREFS_INIT = "is_initialized"
        private const val KEY_LANGUAGE = "pref_language"
        private const val KEY_DARK_THEME = "pref_dark_theme"
        private const val KEY_BREAK_REMINDER = "pref_break_reminder"
        private const val KEY_TEMP_UNIT = "pref_temp_unit_celsius"
        private const val KEY_SHOW_DEMO = "pref_show_demo_data"
        private const val KEY_ONBOARDING_DONE = "pref_onboarding_done"
        private const val KEY_FAV_GAME = "pref_favorite_game_id"
        private const val KEY_PLUS_ACTIVE = "pref_is_plus_active"
        private const val KEY_PLUS_PLAN = "pref_plus_plan"
    }

    init {
        if (!prefs.getBoolean(KEY_PREFS_INIT, false)) {
            seedDefaultData()
            prefs.edit().putBoolean(KEY_PREFS_INIT, true).apply()
        }
    }

    private fun seedDefaultData() {
        val initialGames = listOf(
            Game(
                id = "demo_mlbb",
                title = "Mobile Legends: Bang Bang",
                genre = "MOBA 5v5",
                isFavorite = true,
                isDemo = true,
                customNotes = "Tournoi de fin de semaine",
                totalPlayTimeMinutes = 145,
                drawableRes = R.drawable.thumb_moba_arena
            ),
            Game(
                id = "demo_freefire",
                title = "Free Fire",
                genre = "Battle Royale",
                isFavorite = false,
                isDemo = true,
                customNotes = "Optimisé pour téléphones légers",
                totalPlayTimeMinutes = 80,
                drawableRes = R.drawable.thumb_tactical_fps
            ),
            Game(
                id = "demo_pubgm",
                title = "PUBG Mobile",
                genre = "Tactical Shooter",
                isFavorite = true,
                isDemo = true,
                customNotes = "Privilégier 60 FPS fluides",
                totalPlayTimeMinutes = 210,
                drawableRes = R.drawable.thumb_tactical_fps
            ),
            Game(
                id = "demo_codm",
                title = "Call of Duty Mobile",
                genre = "FPS Compétitif",
                isFavorite = false,
                isDemo = true,
                customNotes = "Sensibilité tactile personnalisée",
                totalPlayTimeMinutes = 95,
                drawableRes = R.drawable.thumb_moba_arena
            )
        )
        saveGames(initialGames)

        // Seed demo sessions
        val now = System.currentTimeMillis()
        val oneHour = 3600_000L
        val initialSessions = listOf(
            GameSession(
                id = "demo_session_1",
                gameId = "demo_pubgm",
                gameTitle = "PUBG Mobile",
                startTimestamp = now - (3 * oneHour),
                endTimestamp = now - (2 * oneHour),
                durationSeconds = 2700, // 45 min
                startBattery = 88,
                endBattery = 74,
                startTempCelsius = 31.5f,
                endTempCelsius = 38.2f,
                avgPingMs = 38,
                pingJitterMs = 6,
                notes = "Match serré sur Erangel, bonne stabilité Wi-Fi",
                isDemo = true
            ),
            GameSession(
                id = "demo_session_2",
                gameId = "demo_mlbb",
                gameTitle = "Mobile Legends: Bang Bang",
                startTimestamp = now - (26 * oneHour),
                endTimestamp = now - (25 * oneHour),
                durationSeconds = 1800, // 30 min
                startBattery = 65,
                endBattery = 54,
                startTempCelsius = 29.8f,
                endTempCelsius = 34.0f,
                avgPingMs = 45,
                pingJitterMs = 12,
                notes = "Ranked 5v5, légère chauffe vers 20 min",
                isDemo = true
            ),
            GameSession(
                id = "demo_session_3",
                gameId = "demo_freefire",
                gameTitle = "Free Fire",
                startTimestamp = now - (48 * oneHour),
                endTimestamp = now - (47 * oneHour),
                durationSeconds = 1500, // 25 min
                startBattery = 92,
                endBattery = 85,
                startTempCelsius = 28.0f,
                endTempCelsius = 32.2f,
                avgPingMs = 52,
                pingJitterMs = 15,
                notes = "Session rapide en 4G",
                isDemo = true
            )
        )
        saveSessions(initialSessions)
    }

    // --- Games ---

    fun getGames(): List<Game> {
        val raw = prefs.getString(KEY_GAMES, null) ?: return emptyList()
        val list = mutableListOf<Game>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Game(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        genre = obj.optString("genre", "Jeu"),
                        isFavorite = obj.optBoolean("isFavorite", false),
                        isDemo = obj.optBoolean("isDemo", false),
                        packageName = obj.optString("packageName", ""),
                        customNotes = obj.optString("customNotes", ""),
                        totalPlayTimeMinutes = obj.optInt("totalPlayTimeMinutes", 0),
                        drawableRes = if (obj.has("drawableRes")) obj.getInt("drawableRes") else null
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveGames(games: List<Game>) {
        val array = JSONArray()
        for (g in games) {
            val obj = JSONObject()
            obj.put("id", g.id)
            obj.put("title", g.title)
            obj.put("genre", g.genre)
            obj.put("isFavorite", g.isFavorite)
            obj.put("isDemo", g.isDemo)
            obj.put("packageName", g.packageName)
            obj.put("customNotes", g.customNotes)
            obj.put("totalPlayTimeMinutes", g.totalPlayTimeMinutes)
            g.drawableRes?.let { obj.put("drawableRes", it) }
            array.put(obj)
        }
        prefs.edit().putString(KEY_GAMES, array.toString()).apply()
    }

    // --- Sessions ---

    fun getSessions(): List<GameSession> {
        val raw = prefs.getString(KEY_SESSIONS, null) ?: return emptyList()
        val list = mutableListOf<GameSession>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    GameSession(
                        id = obj.getString("id"),
                        gameId = obj.getString("gameId"),
                        gameTitle = obj.getString("gameTitle"),
                        startTimestamp = obj.getLong("startTimestamp"),
                        endTimestamp = obj.getLong("endTimestamp"),
                        durationSeconds = obj.getLong("durationSeconds"),
                        startBattery = obj.getInt("startBattery"),
                        endBattery = obj.getInt("endBattery"),
                        startTempCelsius = if (obj.has("startTempCelsius")) obj.getDouble("startTempCelsius").toFloat() else null,
                        endTempCelsius = if (obj.has("endTempCelsius")) obj.getDouble("endTempCelsius").toFloat() else null,
                        avgPingMs = if (obj.has("avgPingMs")) obj.getInt("avgPingMs") else null,
                        pingJitterMs = if (obj.has("pingJitterMs")) obj.getInt("pingJitterMs") else null,
                        notes = obj.optString("notes", ""),
                        isDemo = obj.optBoolean("isDemo", false)
                    )
                )
            }
        } catch (_: Exception) {}
        return list.sortedByDescending { it.startTimestamp }
    }

    fun saveSessions(sessions: List<GameSession>) {
        val array = JSONArray()
        for (s in sessions) {
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("gameId", s.gameId)
            obj.put("gameTitle", s.gameTitle)
            obj.put("startTimestamp", s.startTimestamp)
            obj.put("endTimestamp", s.endTimestamp)
            obj.put("durationSeconds", s.durationSeconds)
            obj.put("startBattery", s.startBattery)
            obj.put("endBattery", s.endBattery)
            s.startTempCelsius?.let { obj.put("startTempCelsius", it.toDouble()) }
            s.endTempCelsius?.let { obj.put("endTempCelsius", it.toDouble()) }
            s.avgPingMs?.let { obj.put("avgPingMs", it) }
            s.pingJitterMs?.let { obj.put("pingJitterMs", it) }
            obj.put("notes", s.notes)
            obj.put("isDemo", s.isDemo)
            array.put(obj)
        }
        prefs.edit().putString(KEY_SESSIONS, array.toString()).apply()
    }

    fun addSession(session: GameSession) {
        val current = getSessions().toMutableList()
        current.add(0, session)
        saveSessions(current)
    }

    // --- Training Records ---

    fun getTrainingRecords(): List<TrainingRecord> {
        val raw = prefs.getString(KEY_TRAINING, null) ?: return emptyList()
        val list = mutableListOf<TrainingRecord>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TrainingRecord(
                        id = obj.getString("id"),
                        type = TrainingType.valueOf(obj.getString("type")),
                        scoreMs = obj.getLong("scoreMs"),
                        dateTimestamp = obj.optLong("dateTimestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list.sortedByDescending { it.dateTimestamp }
    }

    fun addTrainingRecord(record: TrainingRecord) {
        val current = getTrainingRecords().toMutableList()
        current.add(0, record)
        val array = JSONArray()
        for (r in current.take(50)) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("type", r.type.name)
            obj.put("scoreMs", r.scoreMs)
            obj.put("dateTimestamp", r.dateTimestamp)
            array.put(obj)
        }
        prefs.edit().putString(KEY_TRAINING, array.toString()).apply()
    }

    // --- Preferences ---

    fun getUserPreferences(): UserPreferences {
        return UserPreferences(
            language = prefs.getString(KEY_LANGUAGE, "fr") ?: "fr",
            darkTheme = prefs.getBoolean(KEY_DARK_THEME, true),
            breakReminderMinutes = prefs.getInt(KEY_BREAK_REMINDER, 45),
            temperatureUnitCelsius = prefs.getBoolean(KEY_TEMP_UNIT, true),
            showDemoData = prefs.getBoolean(KEY_SHOW_DEMO, true),
            hasCompletedOnboarding = prefs.getBoolean(KEY_ONBOARDING_DONE, false),
            favoriteGameId = prefs.getString(KEY_FAV_GAME, "") ?: "",
            isPlusActive = prefs.getBoolean(KEY_PLUS_ACTIVE, false),
            activePlusPlan = prefs.getString(KEY_PLUS_PLAN, "") ?: ""
        )
    }

    fun saveUserPreferences(prefsObj: UserPreferences) {
        prefs.edit()
            .putString(KEY_LANGUAGE, prefsObj.language)
            .putBoolean(KEY_DARK_THEME, prefsObj.darkTheme)
            .putInt(KEY_BREAK_REMINDER, prefsObj.breakReminderMinutes)
            .putBoolean(KEY_TEMP_UNIT, prefsObj.temperatureUnitCelsius)
            .putBoolean(KEY_SHOW_DEMO, prefsObj.showDemoData)
            .putBoolean(KEY_ONBOARDING_DONE, prefsObj.hasCompletedOnboarding)
            .putString(KEY_FAV_GAME, prefsObj.favoriteGameId)
            .putBoolean(KEY_PLUS_ACTIVE, prefsObj.isPlusActive)
            .putString(KEY_PLUS_PLAN, prefsObj.activePlusPlan)
            .apply()
    }

    // --- Clean / Reset ---

    fun deleteDemoData() {
        val nonDemoGames = getGames().filter { !it.isDemo }
        saveGames(nonDemoGames)
        val nonDemoSessions = getSessions().filter { !it.isDemo }
        saveSessions(nonDemoSessions)
        prefs.edit().putBoolean(KEY_SHOW_DEMO, false).apply()
    }

    fun clearAllData() {
        prefs.edit().clear().apply()
        seedDefaultData()
    }

    fun exportDataAsJson(): String {
        val root = JSONObject()
        root.put("app", "NEXORA Gaming")
        root.put("version", "1.0.0")
        root.put("exportTimestamp", System.currentTimeMillis())

        val gamesArr = JSONArray()
        for (g in getGames()) {
            val obj = JSONObject()
            obj.put("title", g.title)
            obj.put("genre", g.genre)
            obj.put("isFavorite", g.isFavorite)
            obj.put("playTimeMinutes", g.totalPlayTimeMinutes)
            gamesArr.put(obj)
        }
        root.put("games", gamesArr)

        val sessArr = JSONArray()
        for (s in getSessions()) {
            val obj = JSONObject()
            obj.put("gameTitle", s.gameTitle)
            obj.put("durationMinutes", s.durationSeconds / 60)
            obj.put("batteryDrain", s.batteryDrainPercent)
            obj.put("avgPingMs", s.avgPingMs ?: "N/A")
            obj.put("notes", s.notes)
            obj.put("isDemo", s.isDemo)
            sessArr.put(obj)
        }
        root.put("sessions", sessArr)

        val trainingArr = JSONArray()
        for (t in getTrainingRecords()) {
            val obj = JSONObject()
            obj.put("type", t.type.name)
            obj.put("scoreMs", t.scoreMs)
            trainingArr.put(obj)
        }
        root.put("training", trainingArr)

        return root.toString(2)
    }
}
