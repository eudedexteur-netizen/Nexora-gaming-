package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.NexoraPreferences
import com.example.data.model.DeviceMetrics
import com.example.data.model.Game
import com.example.data.model.GameSession
import com.example.data.model.TrainingRecord
import com.example.data.model.TrainingType
import com.example.data.model.UserPreferences
import com.example.data.system.DeviceDiagnosticsService
import com.example.data.system.NetworkTestResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavTab {
    HOME,
    GAMES,
    DIAGNOSTIC,
    SESSIONS,
    PROFILE
}

class NexoraViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = NexoraPreferences(application)
    private val diagnosticsService = DeviceDiagnosticsService(application)

    // Navigation State
    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    // Sub-screens / dialogs
    private val _showGameMode = MutableStateFlow(false)
    val showGameMode: StateFlow<Boolean> = _showGameMode.asStateFlow()

    private val _showTraining = MutableStateFlow(false)
    val showTraining: StateFlow<Boolean> = _showTraining.asStateFlow()

    private val _showGraphicsGuide = MutableStateFlow(false)
    val showGraphicsGuide: StateFlow<Boolean> = _showGraphicsGuide.asStateFlow()

    private val _selectedGame = MutableStateFlow<Game?>(null)
    val selectedGame: StateFlow<Game?> = _selectedGame.asStateFlow()

    private val _showOnboarding = MutableStateFlow(false)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    private val _showPlusModal = MutableStateFlow(false)
    val showPlusModal: StateFlow<Boolean> = _showPlusModal.asStateFlow()

    // Games State
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _gameFilterOnlyFavorites = MutableStateFlow(false)
    val gameFilterOnlyFavorites: StateFlow<Boolean> = _gameFilterOnlyFavorites.asStateFlow()

    // Sessions State
    private val _sessions = MutableStateFlow<List<GameSession>>(emptyList())
    val sessions: StateFlow<List<GameSession>> = _sessions.asStateFlow()

    private val _selectedSessionForDetail = MutableStateFlow<GameSession?>(null)
    val selectedSessionForDetail: StateFlow<GameSession?> = _selectedSessionForDetail.asStateFlow()

    private val _sessionComparison = MutableStateFlow<Pair<GameSession, GameSession>?>(null)
    val sessionComparison: StateFlow<Pair<GameSession, GameSession>?> = _sessionComparison.asStateFlow()

    // Diagnostics State
    private val _deviceMetrics = MutableStateFlow(diagnosticsService.getImmediateMetrics())
    val deviceMetrics: StateFlow<DeviceMetrics> = _deviceMetrics.asStateFlow()

    private val _isDiagnosing = MutableStateFlow(false)
    val isDiagnosing: StateFlow<Boolean> = _isDiagnosing.asStateFlow()

    private val _diagnosticStep = MutableStateFlow("Prêt")
    val diagnosticStep: StateFlow<String> = _diagnosticStep.asStateFlow()

    private val _diagnosticProgress = MutableStateFlow(0f)
    val diagnosticProgress: StateFlow<Float> = _diagnosticProgress.asStateFlow()

    private val _lastNetworkResult = MutableStateFlow<NetworkTestResult?>(null)
    val lastNetworkResult: StateFlow<NetworkTestResult?> = _lastNetworkResult.asStateFlow()

    // Active Game Session (Mode Partie)
    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _activeSessionGame = MutableStateFlow<Game?>(null)
    val activeSessionGame: StateFlow<Game?> = _activeSessionGame.asStateFlow()

    private val _sessionElapsedSeconds = MutableStateFlow(0L)
    val sessionElapsedSeconds: StateFlow<Long> = _sessionElapsedSeconds.asStateFlow()

    private var activeSessionStartTime = 0L
    private var activeSessionStartBattery = 0
    private var activeSessionStartTemp: Float? = null
    private var sessionTimerJob: Job? = null

    // Training state
    private val _trainingRecords = MutableStateFlow<List<TrainingRecord>>(emptyList())
    val trainingRecords: StateFlow<List<TrainingRecord>> = _trainingRecords.asStateFlow()

    // User Preferences
    private val _userPreferences = MutableStateFlow(prefs.getUserPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()

    init {
        loadData()
        if (!_userPreferences.value.hasCompletedOnboarding) {
            _showOnboarding.value = true
        }
    }

    fun loadData() {
        _games.value = prefs.getGames()
        _sessions.value = prefs.getSessions()
        _trainingRecords.value = prefs.getTrainingRecords()
        _userPreferences.value = prefs.getUserPreferences()
        refreshImmediateMetrics()
    }

    fun refreshImmediateMetrics() {
        _deviceMetrics.value = diagnosticsService.getImmediateMetrics()
    }

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
        // Close sub screens if open
        _showGameMode.value = false
        _showTraining.value = false
        _showGraphicsGuide.value = false
        _selectedGame.value = null
    }

    // --- Games Management ---

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavoritesOnlyFilter() {
        _gameFilterOnlyFavorites.value = !_gameFilterOnlyFavorites.value
    }

    fun toggleFavoriteGame(gameId: String) {
        val updated = _games.value.map {
            if (it.id == gameId) it.copy(isFavorite = !it.isFavorite) else it
        }
        _games.value = updated
        prefs.saveGames(updated)
    }

    fun addCustomGame(title: String, genre: String, notes: String) {
        if (title.isBlank()) return
        val newGame = Game(
            id = "custom_" + UUID.randomUUID().toString().take(8),
            title = title.trim(),
            genre = if (genre.isBlank()) "Jeu Android" else genre.trim(),
            customNotes = notes.trim(),
            isFavorite = false,
            isDemo = false
        )
        val updated = _games.value + newGame
        _games.value = updated
        prefs.saveGames(updated)
    }

    fun updateGame(updatedGame: Game) {
        val updated = _games.value.map {
            if (it.id == updatedGame.id) updatedGame else it
        }
        _games.value = updated
        prefs.saveGames(updated)
        if (_selectedGame.value?.id == updatedGame.id) {
            _selectedGame.value = updatedGame
        }
    }

    fun deleteGame(gameId: String) {
        val updated = _games.value.filter { it.id != gameId }
        _games.value = updated
        prefs.saveGames(updated)
        if (_selectedGame.value?.id == gameId) {
            _selectedGame.value = null
        }
    }

    fun openGameDetails(game: Game) {
        _selectedGame.value = game
    }

    fun closeGameDetails() {
        _selectedGame.value = null
    }

    // --- Diagnostic Suite ---

    fun runFullDiagnostic() {
        if (_isDiagnosing.value) return
        viewModelScope.launch {
            _isDiagnosing.value = true
            _diagnosticProgress.value = 0.05f
            _diagnosticStep.value = "Vérification des capteurs système et batterie..."

            delay(300)
            refreshImmediateMetrics()
            _diagnosticProgress.value = 0.25f
            _diagnosticStep.value = "Analyse de la mémoire vive et de l'espace disque..."

            delay(300)
            _diagnosticProgress.value = 0.40f
            _diagnosticStep.value = "Test de latence réseau vers la cible de test..."

            val netResult = diagnosticsService.performNetworkPingTest { step, progress ->
                _diagnosticStep.value = step
                _diagnosticProgress.value = progress
            }

            _lastNetworkResult.value = netResult
            _diagnosticProgress.value = 1.0f
            _diagnosticStep.value = "Diagnostic terminé !"

            // Update metrics with ping results
            _deviceMetrics.value = _deviceMetrics.value.copy(
                pingMs = netResult.averageMs,
                pingJitterMs = netResult.jitterMs,
                pingFailures = netResult.failures,
                timestamp = System.currentTimeMillis()
            )

            delay(400)
            _isDiagnosing.value = false
        }
    }

    // --- Active Game Session (Mode Partie) ---

    fun openGameMode(game: Game? = null) {
        _activeSessionGame.value = game ?: _games.value.firstOrNull { it.isFavorite } ?: _games.value.firstOrNull()
        _showGameMode.value = true
    }

    fun closeGameMode() {
        _showGameMode.value = false
    }

    fun selectGameForMode(game: Game) {
        _activeSessionGame.value = game
    }

    fun startGameSession() {
        if (_isSessionActive.value) return
        val game = _activeSessionGame.value ?: return

        refreshImmediateMetrics()
        val currentMetrics = _deviceMetrics.value
        activeSessionStartTime = System.currentTimeMillis()
        activeSessionStartBattery = currentMetrics.batteryPercent
        activeSessionStartTemp = currentMetrics.batteryTempCelsius
        _sessionElapsedSeconds.value = 0L
        _isSessionActive.value = true

        sessionTimerJob?.cancel()
        sessionTimerJob = viewModelScope.launch {
            while (_isSessionActive.value) {
                delay(1000)
                _sessionElapsedSeconds.value++
            }
        }
    }

    fun stopAndSaveGameSession(userNotes: String = "") {
        if (!_isSessionActive.value) return
        sessionTimerJob?.cancel()
        _isSessionActive.value = false

        val game = _activeSessionGame.value ?: return
        refreshImmediateMetrics()
        val currentMetrics = _deviceMetrics.value
        val endTime = System.currentTimeMillis()
        val duration = _sessionElapsedSeconds.value

        val newSession = GameSession(
            id = "sess_" + UUID.randomUUID().toString().take(8),
            gameId = game.id,
            gameTitle = game.title,
            startTimestamp = activeSessionStartTime,
            endTimestamp = endTime,
            durationSeconds = duration,
            startBattery = if (activeSessionStartBattery > 0) activeSessionStartBattery else currentMetrics.batteryPercent,
            endBattery = if (currentMetrics.batteryPercent > 0) currentMetrics.batteryPercent else activeSessionStartBattery,
            startTempCelsius = activeSessionStartTemp,
            endTempCelsius = currentMetrics.batteryTempCelsius,
            avgPingMs = currentMetrics.pingMs,
            pingJitterMs = currentMetrics.pingJitterMs,
            notes = userNotes.trim(),
            isDemo = false
        )

        prefs.addSession(newSession)
        // update game play time
        val updatedGames = _games.value.map {
            if (it.id == game.id) {
                it.copy(totalPlayTimeMinutes = it.totalPlayTimeMinutes + (duration / 60).toInt())
            } else it
        }
        _games.value = updatedGames
        prefs.saveGames(updatedGames)

        _sessions.value = prefs.getSessions()
        _selectedSessionForDetail.value = newSession
    }

    fun cancelActiveSession() {
        sessionTimerJob?.cancel()
        _isSessionActive.value = false
        _sessionElapsedSeconds.value = 0L
    }

    // --- Sessions History Management ---

    fun selectSessionForDetail(session: GameSession?) {
        _selectedSessionForDetail.value = session
    }

    fun deleteSession(sessionId: String) {
        val updated = _sessions.value.filter { it.id != sessionId }
        _sessions.value = updated
        prefs.saveSessions(updated)
        if (_selectedSessionForDetail.value?.id == sessionId) {
            _selectedSessionForDetail.value = null
        }
    }

    fun compareSessions(s1: GameSession, s2: GameSession) {
        _sessionComparison.value = Pair(s1, s2)
    }

    fun clearSessionComparison() {
        _sessionComparison.value = null
    }

    // --- Reflex / Aim Training ---

    fun openTraining() {
        _showTraining.value = true
    }

    fun closeTraining() {
        _showTraining.value = false
    }

    fun recordTrainingResult(type: TrainingType, scoreMs: Long) {
        val record = TrainingRecord(
            id = UUID.randomUUID().toString(),
            type = type,
            scoreMs = scoreMs,
            dateTimestamp = System.currentTimeMillis()
        )
        prefs.addTrainingRecord(record)
        _trainingRecords.value = prefs.getTrainingRecords()
    }

    // --- Graphics Guide ---

    fun openGraphicsGuide() {
        _showGraphicsGuide.value = true
    }

    fun closeGraphicsGuide() {
        _showGraphicsGuide.value = false
    }

    // --- Onboarding & Preferences ---

    fun completeOnboarding(favGameTitle: String = "") {
        _showOnboarding.value = false
        val current = _userPreferences.value.copy(
            hasCompletedOnboarding = true,
            favoriteGameId = favGameTitle
        )
        _userPreferences.value = current
        prefs.saveUserPreferences(current)
    }

    fun updateBreakReminder(minutes: Int) {
        val current = _userPreferences.value.copy(breakReminderMinutes = minutes)
        _userPreferences.value = current
        prefs.saveUserPreferences(current)
    }

    fun updateTemperatureUnit(celsius: Boolean) {
        val current = _userPreferences.value.copy(temperatureUnitCelsius = celsius)
        _userPreferences.value = current
        prefs.saveUserPreferences(current)
    }

    fun updateTheme(dark: Boolean) {
        val current = _userPreferences.value.copy(darkTheme = dark)
        _userPreferences.value = current
        prefs.saveUserPreferences(current)
    }

    fun openPlusModal() {
        _showPlusModal.value = true
    }

    fun closePlusModal() {
        _showPlusModal.value = false
    }

    fun activatePlusDemo(planName: String) {
        val current = _userPreferences.value.copy(
            isPlusActive = true,
            activePlusPlan = planName
        )
        _userPreferences.value = current
        prefs.saveUserPreferences(current)
    }

    fun deactivatePlusDemo() {
        val current = _userPreferences.value.copy(
            isPlusActive = false,
            activePlusPlan = ""
        )
        _userPreferences.value = current
        prefs.saveUserPreferences(current)
    }

    fun deleteDemoData() {
        prefs.deleteDemoData()
        loadData()
    }

    fun resetAllData() {
        prefs.clearAllData()
        loadData()
    }

    fun getExportJson(): String {
        return prefs.exportDataAsJson()
    }
}
