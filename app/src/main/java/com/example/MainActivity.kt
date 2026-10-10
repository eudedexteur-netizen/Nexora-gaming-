package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.NexoraBottomNav
import com.example.ui.components.NexoraTopBar
import com.example.ui.screens.DiagnosticScreen
import com.example.ui.screens.GameModeScreen
import com.example.ui.screens.GamesScreen
import com.example.ui.screens.GraphicsGuideScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingDialog
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SessionsScreen
import com.example.ui.screens.SubscriptionModal
import com.example.ui.screens.TrainingScreen
import com.example.ui.theme.NexoraDarkBg
import com.example.ui.theme.NexoraTheme
import com.example.viewmodel.NavTab
import com.example.viewmodel.NexoraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NexoraApp()
        }
    }
}

@Composable
fun NexoraApp(viewModel: NexoraViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsState()
    val showGameMode by viewModel.showGameMode.collectAsState()
    val showTraining by viewModel.showTraining.collectAsState()
    val showGraphicsGuide by viewModel.showGraphicsGuide.collectAsState()
    val selectedGame by viewModel.selectedGame.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()
    val showPlusModal by viewModel.showPlusModal.collectAsState()

    val metrics by viewModel.deviceMetrics.collectAsState()
    val isSessionActive by viewModel.isSessionActive.collectAsState()
    val userPrefs by viewModel.userPreferences.collectAsState()

    // Back handling for navigation stack
    BackHandler(enabled = showGameMode || showTraining || showGraphicsGuide || selectedGame != null || currentTab != NavTab.HOME) {
        when {
            showGameMode -> viewModel.closeGameMode()
            showTraining -> viewModel.closeTraining()
            showGraphicsGuide -> viewModel.closeGraphicsGuide()
            selectedGame != null -> viewModel.closeGameDetails()
            currentTab != NavTab.HOME -> viewModel.selectTab(NavTab.HOME)
        }
    }

    NexoraTheme(darkTheme = userPrefs.darkTheme) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(NexoraDarkBg),
            topBar = {
                NexoraTopBar(
                    metrics = metrics,
                    isSessionActive = isSessionActive,
                    onGameModeClick = { viewModel.openGameMode() },
                    onProfileClick = { viewModel.selectTab(NavTab.PROFILE) }
                )
            },
            bottomBar = {
                // Show bottom nav unless in modal subscreen
                if (!showGameMode && !showTraining && !showGraphicsGuide) {
                    NexoraBottomNav(
                        currentTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    showGameMode -> {
                        GameModeScreen(viewModel = viewModel)
                    }
                    showTraining -> {
                        TrainingScreen(viewModel = viewModel)
                    }
                    showGraphicsGuide -> {
                        GraphicsGuideScreen(viewModel = viewModel)
                    }
                    else -> {
                        when (currentTab) {
                            NavTab.HOME -> HomeScreen(viewModel = viewModel)
                            NavTab.GAMES -> GamesScreen(viewModel = viewModel)
                            NavTab.DIAGNOSTIC -> DiagnosticScreen(viewModel = viewModel)
                            NavTab.SESSIONS -> SessionsScreen(viewModel = viewModel)
                            NavTab.PROFILE -> ProfileScreen(viewModel = viewModel)
                        }
                    }
                }

                // Onboarding Dialog for first launch
                if (showOnboarding) {
                    OnboardingDialog(
                        onComplete = { favGame ->
                            viewModel.completeOnboarding(favGame)
                        }
                    )
                }

                // Freemium Subscription Preview Modal
                if (showPlusModal) {
                    SubscriptionModal(
                        viewModel = viewModel,
                        onDismiss = { viewModel.closePlusModal() }
                    )
                }
            }
        }
    }
}
