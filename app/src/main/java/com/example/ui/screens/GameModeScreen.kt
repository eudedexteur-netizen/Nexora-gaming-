package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Game
import com.example.ui.components.NexoraHonestBanner
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
import com.example.viewmodel.NexoraViewModel

@Composable
fun GameModeScreen(
    viewModel: NexoraViewModel,
    modifier: Modifier = Modifier
) {
    val isSessionActive by viewModel.isSessionActive.collectAsState()
    val activeGame by viewModel.activeSessionGame.collectAsState()
    val elapsedSeconds by viewModel.sessionElapsedSeconds.collectAsState()
    val metrics by viewModel.deviceMetrics.collectAsState()
    val games by viewModel.games.collectAsState()

    var checkBattery by remember { mutableStateOf(metrics.batteryPercent > 20) }
    var checkThermal by remember { mutableStateOf(true) }
    var checkNetwork by remember { mutableStateOf(true) }
    var checkDnd by remember { mutableStateOf(false) }
    var checkGraphics by remember { mutableStateOf(false) }

    var sessionUserNotes by remember { mutableStateOf("") }

    val formattedTime = remember(elapsedSeconds) {
        val h = elapsedSeconds / 3600
        val m = (elapsedSeconds % 3600) / 60
        val s = elapsedSeconds % 60
        if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NexoraDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mode Partie & Préparation",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NexoraTextPrimary,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Optimisez votre session et suivez vos performances",
                        style = MaterialTheme.typography.bodySmall.copy(color = NexoraTextSecondary)
                    )
                }

                IconButton(onClick = { viewModel.closeGameMode() }) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = NexoraTextSecondary)
                }
            }
        }

        // Active Session Tracker Card
        if (isSessionActive) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, NexoraSuccess, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SESSION EN COURS",
                                fontWeight = FontWeight.Black,
                                color = NexoraSuccess,
                                fontSize = 13.sp,
                                letterSpacing = 1.sp
                            )
                            NexoraTag(text = activeGame?.title ?: "Jeu", color = NexoraCyan)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NexoraTextPrimary,
                                fontSize = 42.sp,
                                letterSpacing = 2.sp
                            )
                        )

                        Text(
                            text = "Temps de jeu réel comptabilisé",
                            color = NexoraTextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live metrics snapshot
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NexoraSurfaceElevated, RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Batterie actuelle", color = NexoraTextSecondary, fontSize = 11.sp)
                                Text("${metrics.batteryPercent}%", color = NexoraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Température", color = NexoraTextSecondary, fontSize = 11.sp)
                                val t = metrics.batteryTempCelsius?.let { "${"%.1f".format(it)}°C" } ?: "--"
                                Text(t, color = NexoraTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Réseau", color = NexoraTextSecondary, fontSize = 11.sp)
                                Text(metrics.networkType.take(8), color = NexoraCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = sessionUserNotes,
                            onValueChange = { sessionUserNotes = it },
                            placeholder = { Text("Note facultative (ex: Victoire, sensation de chauffe...)", color = NexoraTextMuted, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = NexoraSurfaceVariant,
                                unfocusedContainerColor = NexoraSurfaceVariant,
                                focusedBorderColor = NexoraCyan,
                                unfocusedBorderColor = NexoraBorder,
                                focusedTextColor = NexoraTextPrimary,
                                unfocusedTextColor = NexoraTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.cancelActiveSession() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NexoraError),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Annuler")
                            }

                            Button(
                                onClick = {
                                    viewModel.stopAndSaveGameSession(sessionUserNotes)
                                    viewModel.closeGameMode()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NexoraSuccess, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(2f)
                                    .testTag("game_mode_stop_session_button")
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Terminer la session", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Game Selection
        if (!isSessionActive) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "1. Choisir le jeu cible",
                            fontWeight = FontWeight.Bold,
                            color = NexoraCyan,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        games.forEach { game ->
                            val isSelected = activeGame?.id == game.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) NexoraSurfaceElevated else Color.Transparent)
                                    .border(
                                        1.dp,
                                        if (isSelected) NexoraCyan else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.selectGameForMode(game) }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = game.title,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) NexoraCyan else NexoraTextPrimary,
                                        fontSize = 13.sp
                                    )
                                    Text(game.genre, color = NexoraTextSecondary, fontSize = 11.sp)
                                }

                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Pre-game Checklist
        if (!isSessionActive) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "2. Checklist de préparation avant partie",
                            fontWeight = FontWeight.Bold,
                            color = NexoraCyan,
                            fontSize = 14.sp
                        )

                        ChecklistRow(
                            checked = checkBattery,
                            onCheckedChange = { checkBattery = it },
                            title = "Batterie suffisante (${metrics.batteryPercent}%)",
                            subtitle = if (metrics.batteryPercent < 20) "Avertissement : Moins de 20%, risque d'extinction" else "Niveau de charge suffisant sans chargeur branché"
                        )

                        ChecklistRow(
                            checked = checkThermal,
                            onCheckedChange = { checkThermal = it },
                            title = "Téléphone correctement aéré",
                            subtitle = "Retirez la coque épaisse pour retarder le bridage thermique"
                        )

                        ChecklistRow(
                            checked = checkNetwork,
                            onCheckedChange = { checkNetwork = it },
                            title = "Connexion vérifiée (${metrics.networkType})",
                            subtitle = "Privilégiez la bande 5 GHz du Wi-Fi ou une 4G/5G stable"
                        )

                        ChecklistRow(
                            checked = checkDnd,
                            onCheckedChange = { checkDnd = it },
                            title = "Mode Ne Pas Déranger activé",
                            subtitle = "Évite les popups et appels qui masquent l'écran en plein jeu"
                        )

                        ChecklistRow(
                            checked = checkGraphics,
                            onCheckedChange = { checkGraphics = it },
                            title = "Réglages graphiques adaptés dans le jeu",
                            subtitle = "Privilégiez la fluidité (FPS élevés) sur la qualité des ombres"
                        )
                    }
                }
            }

            // Start Session Button
            item {
                Button(
                    onClick = { viewModel.startGameSession() },
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("game_mode_start_session_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Démarrer le suivi de session",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Honest explanation of Android sandbox capabilities
        item {
            NexoraHonestBanner(
                title = "Fonctionnalités natives & système",
                description = "Les fonctionnalités de suspension absolue des notifications ou de superposition flottante en jeu nécessitent des permissions système spéciales sur Android (Accès Ne Pas Déranger et Affichage par-dessus d'autres applications). NEXORA privilégie une approche non invasive sans altérer le système."
            )
        }
    }
}

@Composable
fun ChecklistRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = NexoraCyan,
                checkmarkColor = NexoraDarkBg,
                uncheckedColor = NexoraBorder
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, color = NexoraTextPrimary, fontSize = 13.sp)
            Text(subtitle, color = NexoraTextSecondary, fontSize = 11.sp)
        }
    }
}
