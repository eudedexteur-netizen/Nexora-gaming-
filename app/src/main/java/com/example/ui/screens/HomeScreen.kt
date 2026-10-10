package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Game
import com.example.data.model.GameSession
import com.example.ui.components.MetricStatus
import com.example.ui.components.NexoraHonestBanner
import com.example.ui.components.NexoraMetricCard
import com.example.ui.components.NexoraSectionHeader
import com.example.ui.components.NexoraTag
import com.example.ui.theme.NexoraBorder
import com.example.ui.theme.NexoraCyan
import com.example.ui.theme.NexoraDarkBg
import com.example.ui.theme.NexoraError
import com.example.ui.theme.NexoraPurple
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurface
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraSurfaceVariant
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.ui.theme.NexoraWarning
import com.example.viewmodel.NavTab
import com.example.viewmodel.NexoraViewModel

@Composable
fun HomeScreen(
    viewModel: NexoraViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.deviceMetrics.collectAsState()
    val games by viewModel.games.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val isSessionActive by viewModel.isSessionActive.asStateFlowWrapper()

    val favoriteGames = games.filter { it.isFavorite }.ifEmpty { games.take(3) }
    val lastSession = sessions.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NexoraDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraCyan.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bonjour Joueur,",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NexoraTextPrimary,
                                    fontSize = 18.sp
                                )
                            )
                            Text(
                                text = "Prêt pour une session de jeu optimale ?",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NexoraTextSecondary
                                )
                            )
                        }

                        NexoraTag(
                            text = if (isSessionActive) "Session en cours" else "Système prêt",
                            color = if (isSessionActive) NexoraSuccess else NexoraCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Action Button: "Préparer une partie"
                    Button(
                        onClick = { viewModel.openGameMode() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSessionActive) NexoraSuccess else NexoraCyan,
                            contentColor = NexoraDarkBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("home_prepare_game_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isSessionActive) "Afficher la session active" else "Préparer une partie",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Section: État de mon appareil
        item {
            NexoraSectionHeader(
                title = "État de mon appareil",
                subtitle = "Mesures directes du système",
                actionText = "Diagnostic complet",
                onActionClick = { viewModel.selectTab(NavTab.DIAGNOSTIC) }
            )

            // Grid 2x2 of immediate metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Battery
                val batteryVal = if (metrics.batteryPercent >= 0) "${metrics.batteryPercent}%" else "Non disponible"
                val batteryStatus = when {
                    metrics.batteryPercent < 20 -> MetricStatus.WARNING
                    metrics.batteryPercent > 80 -> MetricStatus.OPTIMAL
                    else -> MetricStatus.NEUTRAL
                }
                NexoraMetricCard(
                    title = "Batterie",
                    value = batteryVal,
                    unit = if (metrics.isCharging) "(En charge)" else "",
                    icon = Icons.Default.BatteryFull,
                    status = batteryStatus,
                    subtext = "Gestionnaire système",
                    modifier = Modifier.weight(1f)
                )

                // Battery Temp
                val tempVal = metrics.batteryTempCelsius?.let { "${"%.1f".format(it)}°C" } ?: "Non disponible"
                val tempStatus = when {
                    metrics.batteryTempCelsius == null -> MetricStatus.NEUTRAL
                    metrics.batteryTempCelsius!! > 40f -> MetricStatus.CRITICAL
                    metrics.batteryTempCelsius!! > 36f -> MetricStatus.WARNING
                    else -> MetricStatus.OPTIMAL
                }
                NexoraMetricCard(
                    title = "Température",
                    value = tempVal,
                    icon = Icons.Default.Thermostat,
                    status = tempStatus,
                    subtext = "Capteur batterie",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // RAM
                val ramVal = if (metrics.availableRamMb > 0) "${metrics.availableRamMb}" else "Non disponible"
                val ramUnit = if (metrics.availableRamMb > 0) "Mo libre" else ""
                NexoraMetricCard(
                    title = "Mémoire RAM",
                    value = ramVal,
                    unit = ramUnit,
                    icon = Icons.Default.Memory,
                    status = if (metrics.availableRamMb > 1000) MetricStatus.OPTIMAL else MetricStatus.WARNING,
                    subtext = if (metrics.totalRamMb > 0) "Sur ${metrics.totalRamMb} Mo total" else null,
                    modifier = Modifier.weight(1f)
                )

                // Network / Ping
                val pingVal = metrics.pingMs?.let { "${it} ms" } ?: metrics.networkType
                NexoraMetricCard(
                    title = "Connexion",
                    value = pingVal,
                    icon = Icons.Default.Wifi,
                    status = if (metrics.networkType.contains("Wi-Fi")) MetricStatus.OPTIMAL else MetricStatus.NEUTRAL,
                    subtext = metrics.networkType,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section: Reprendre un jeu (Favoris)
        item {
            NexoraSectionHeader(
                title = "Reprendre un jeu",
                subtitle = "Accès rapide à votre bibliothèque",
                actionText = "Tous les jeux (${games.size})",
                onActionClick = { viewModel.selectTab(NavTab.GAMES) }
            )

            if (favoriteGames.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NexoraSurfaceVariant)
                ) {
                    Text(
                        text = "Aucun jeu favori enregistré pour l'instant.",
                        color = NexoraTextSecondary,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favoriteGames) { game ->
                        GameQuickCard(
                            game = game,
                            onPlayClick = {
                                viewModel.openGameMode(game)
                            },
                            onDetailsClick = {
                                viewModel.openGameDetails(game)
                            }
                        )
                    }
                }
            }
        }

        // Section: Quick Utilities (Training & Graphics Guide)
        item {
            NexoraSectionHeader(
                title = "Boîte à outils du joueur",
                subtitle = "Exercices de précision et réglages graphiques"
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Reflex training button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NexoraPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.openTraining() }
                        .testTag("home_tool_training"),
                    colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = NexoraPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Entraînement",
                            fontWeight = FontWeight.Bold,
                            color = NexoraTextPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Réflexes & Visée tactile",
                            color = NexoraTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Graphics Advisor button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, NexoraCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.openGraphicsGuide() }
                        .testTag("home_tool_graphics"),
                    colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = NexoraCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Guide Graphique",
                            fontWeight = FontWeight.Bold,
                            color = NexoraTextPrimary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Compromis FPS / Chauffe",
                            color = NexoraTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Section: Conseil du jour (Honest, realistic advice)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(NexoraWarning.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = NexoraWarning,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Conseil d'expert NEXORA",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NexoraTextPrimary,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Évitez de recharger votre smartphone pendant une partie intensive. La charge génère une chaleur cumulative qui déclenche le bridage thermique (thermal throttling) du processeur et fait chuter le taux de FPS.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NexoraTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Aucune application ne peut forcer le matériel au-delà de sa limite thermique sans risque.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NexoraTextMuted,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }
            }
        }

        // Section: Ma dernière session
        item {
            NexoraSectionHeader(
                title = "Ma dernière session",
                subtitle = "Historique récent",
                actionText = "Toutes les sessions",
                onActionClick = { viewModel.selectTab(NavTab.SESSIONS) }
            )

            if (lastSession == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NexoraSurfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timeline,
                            contentDescription = null,
                            tint = NexoraTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aucune session enregistrée",
                            color = NexoraTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Lancez votre première session pour suivre l'évolution de la batterie et de la stabilité.",
                            color = NexoraTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Button(
                            onClick = { viewModel.openGameMode() },
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Démarrer ma 1ère session", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                LastSessionCard(
                    session = lastSession,
                    onViewDetails = {
                        viewModel.selectSessionForDetail(lastSession)
                        viewModel.selectTab(NavTab.SESSIONS)
                    }
                )
            }
        }
    }
}

@Composable
fun GameQuickCard(
    game: Game,
    onPlayClick: () -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp))
            .clickable { onDetailsClick() },
        colors = CardDefaults.cardColors(containerColor = NexoraSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column {
            // Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(NexoraSurfaceElevated)
            ) {
                if (game.drawableRes != null) {
                    Image(
                        painter = painterResource(id = game.drawableRes),
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = NexoraCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                if (game.isDemo) {
                    Box(modifier = Modifier.padding(6.dp)) {
                        NexoraTag(text = "Démo", color = NexoraPurple)
                    }
                }
            }

            // Info & button
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = game.title,
                    fontWeight = FontWeight.Bold,
                    color = NexoraTextPrimary,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Text(
                    text = game.genre,
                    color = NexoraTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onPlayClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexoraCyan.copy(alpha = 0.15f),
                        contentColor = NexoraCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Session", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LastSessionCard(
    session: GameSession,
    onViewDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp))
            .clickable { onViewDetails() },
        colors = CardDefaults.cardColors(containerColor = NexoraSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = session.gameTitle,
                        fontWeight = FontWeight.Bold,
                        color = NexoraTextPrimary,
                        fontSize = 15.sp
                    )
                    val minutes = session.durationSeconds / 60
                    Text(
                        text = "Durée : ${minutes} min",
                        color = NexoraTextSecondary,
                        fontSize = 12.sp
                    )
                }
                if (session.isDemo) {
                    NexoraTag(text = "Données de démo", color = NexoraPurple)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Battery drain
                Column {
                    Text(text = "Consommation", color = NexoraTextSecondary, fontSize = 11.sp)
                    Text(
                        text = "-${session.batteryDrainPercent}%",
                        color = NexoraWarning,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Latency
                Column {
                    Text(text = "Latence moy.", color = NexoraTextSecondary, fontSize = 11.sp)
                    Text(
                        text = session.avgPingMs?.let { "${it} ms" } ?: "--",
                        color = NexoraCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Temp delta
                Column {
                    Text(text = "Température", color = NexoraTextSecondary, fontSize = 11.sp)
                    val tempDiff = if (session.startTempCelsius != null && session.endTempCelsius != null) {
                        "+${"%.1f".format(session.endTempCelsius - session.startTempCelsius)}°C"
                    } else "--"
                    Text(
                        text = tempDiff,
                        color = NexoraTextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Consulter le rapport",
                    color = NexoraCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = NexoraCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// Utility extension for StateFlow wrapped in composable
@Composable
private fun <T> kotlinx.coroutines.flow.StateFlow<T>.asStateFlowWrapper() = this.collectAsState()
