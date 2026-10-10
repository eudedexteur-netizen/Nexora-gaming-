package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameSession
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    viewModel: NexoraViewModel,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.sessions.collectAsState()
    val games by viewModel.games.collectAsState()
    val selectedSession by viewModel.selectedSessionForDetail.collectAsState()
    val comparison by viewModel.sessionComparison.collectAsState()

    var selectedGameFilter by remember { mutableStateOf("Tous") }
    var selectedPeriodFilter by remember { mutableStateOf("Toutes") }
    var showExportDialog by remember { mutableStateOf(false) }

    // Session selection for compare mode
    var compareSelectMode by remember { mutableStateOf(false) }
    var firstCompareSession by remember { mutableStateOf<GameSession?>(null) }

    val filteredSessions = sessions.filter { session ->
        val matchesGame = selectedGameFilter == "Tous" || session.gameTitle == selectedGameFilter
        val matchesPeriod = when (selectedPeriodFilter) {
            "7 derniers jours" -> session.startTimestamp >= (System.currentTimeMillis() - 7 * 86400_000L)
            "Aujourd'hui" -> session.startTimestamp >= (System.currentTimeMillis() - 86400_000L)
            else -> true
        }
        matchesGame && matchesPeriod
    }

    val demoCount = sessions.count { it.isDemo }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NexoraDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title & Export
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Historique des sessions",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NexoraTextPrimary,
                            fontSize = 22.sp
                        )
                    )
                    Text(
                        text = "Suivi de la batterie, de la chauffe et de la latence",
                        style = MaterialTheme.typography.bodySmall.copy(color = NexoraTextSecondary)
                    )
                }

                IconButton(
                    onClick = { showExportDialog = true },
                    modifier = Modifier.testTag("sessions_export_button")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = "Exporter", tint = NexoraCyan)
                }
            }
        }

        // Demo data alert banner
        if (demoCount > 0) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NexoraPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = NexoraPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$demoCount session(s) de démonstration incluses pour illustrer l'historique.",
                                color = NexoraTextSecondary,
                                fontSize = 11.5.sp
                            )
                        }

                        TextButton(onClick = { viewModel.deleteDemoData() }) {
                            Text("Supprimer démo", color = NexoraPurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Filter and Compare mode toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedPeriodFilter == "7 derniers jours",
                        onClick = {
                            selectedPeriodFilter = if (selectedPeriodFilter == "7 derniers jours") "Toutes" else "7 derniers jours"
                        },
                        label = { Text("7 derniers jours", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NexoraSurfaceElevated,
                            selectedLabelColor = NexoraCyan,
                            containerColor = NexoraSurface,
                            labelColor = NexoraTextSecondary
                        )
                    )
                }

                OutlinedButton(
                    onClick = {
                        compareSelectMode = !compareSelectMode
                        firstCompareSession = null
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (compareSelectMode) NexoraCyan else NexoraTextSecondary
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.CompareArrows, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (compareSelectMode) "Annuler sélection" else "Comparer 2 sessions", fontSize = 11.sp)
                }
            }
        }

        // Evolution Graph Card (Only if >= 2 sessions)
        if (filteredSessions.size >= 2) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Évolution de la batterie consommée (%)",
                                fontWeight = FontWeight.Bold,
                                color = NexoraTextPrimary,
                                fontSize = 13.sp
                            )
                            NexoraTag(text = "Dernières sessions", color = NexoraCyan)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Canvas Line Graph
                        val drainPoints = filteredSessions.take(7).reversed().map { it.batteryDrainPercent.toFloat() }
                        NexoraSessionDrainChart(
                            values = drainPoints,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Graphique généré uniquement à partir des sessions de la liste.",
                            color = NexoraTextMuted,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
        }

        // Sessions List
        if (filteredSessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = NexoraSurfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Timeline, contentDescription = null, tint = NexoraTextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Aucune session trouvée", color = NexoraTextPrimary, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Lancez une session via le Mode Partie pour enregistrer vos données.",
                            color = NexoraTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(filteredSessions, key = { it.id }) { session ->
                val isSelectedForCompare = firstCompareSession?.id == session.id

                SessionItemCard(
                    session = session,
                    isSelected = isSelectedForCompare,
                    isCompareMode = compareSelectMode,
                    onClick = {
                        if (compareSelectMode) {
                            if (firstCompareSession == null) {
                                firstCompareSession = session
                            } else if (firstCompareSession?.id != session.id) {
                                viewModel.compareSessions(firstCompareSession!!, session)
                                compareSelectMode = false
                                firstCompareSession = null
                            }
                        } else {
                            viewModel.selectSessionForDetail(session)
                        }
                    },
                    onDelete = {
                        viewModel.deleteSession(session.id)
                    }
                )
            }
        }
    }

    // Session Detail Dialog
    if (selectedSession != null) {
        SessionDetailDialog(
            session = selectedSession!!,
            onDismiss = { viewModel.selectSessionForDetail(null) },
            onDelete = {
                viewModel.deleteSession(selectedSession!!.id)
                viewModel.selectSessionForDetail(null)
            }
        )
    }

    // Session Comparison Dialog
    if (comparison != null) {
        SessionComparisonDialog(
            sessionA = comparison!!.first,
            sessionB = comparison!!.second,
            onDismiss = { viewModel.clearSessionComparison() }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        val exportText = viewModel.getExportJson()
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            containerColor = NexoraSurface,
            title = {
                Text("Exportation des données locales", fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Format JSON structuré et respectueux de la vie privée (aucune donnée transmise à un serveur distant) :",
                        color = NexoraTextSecondary,
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(NexoraDarkBg, RoundedCornerShape(8.dp))
                            .border(1.dp, NexoraBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = exportText.take(1500) + if (exportText.length > 1500) "\n..." else "",
                            fontSize = 11.sp,
                            color = NexoraCyan
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg)
                ) {
                    Text("Fermer")
                }
            }
        )
    }
}

@Composable
fun SessionItemCard(
    session: GameSession,
    isSelected: Boolean,
    isCompareMode: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isSelected) NexoraCyan else NexoraBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NexoraSurfaceElevated else NexoraSurface
        ),
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
                    val dateStr = SimpleDateFormat("dd MMM yyyy à HH:mm", Locale.FRANCE).format(Date(session.startTimestamp))
                    Text(
                        text = dateStr,
                        color = NexoraTextSecondary,
                        fontSize = 11.5.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (session.isDemo) {
                        NexoraTag(text = "Démo", color = NexoraPurple)
                    }
                    if (isCompareMode) {
                        NexoraTag(
                            text = if (isSelected) "Session 1" else "Sélectionner",
                            color = if (isSelected) NexoraCyan else NexoraTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Durée", color = NexoraTextSecondary, fontSize = 11.sp)
                    Text("${session.durationSeconds / 60} min", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 13.sp)
                }
                Column {
                    Text("Batterie", color = NexoraTextSecondary, fontSize = 11.sp)
                    Text("-${session.batteryDrainPercent}% (${session.startBattery}% → ${session.endBattery}%)", fontWeight = FontWeight.Bold, color = NexoraWarning, fontSize = 13.sp)
                }
                Column {
                    Text("Latence moy.", color = NexoraTextSecondary, fontSize = 11.sp)
                    Text(session.avgPingMs?.let { "$it ms" } ?: "--", fontWeight = FontWeight.Bold, color = NexoraCyan, fontSize = 13.sp)
                }
            }

            if (session.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Note : ${session.notes}",
                    color = NexoraTextMuted,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun NexoraSessionDrainChart(
    values: List<Float>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (values.isEmpty()) return@Canvas

        val maxVal = (values.maxOrNull() ?: 20f).coerceAtLeast(10f)
        val stepX = size.width / (values.size - 1).coerceAtLeast(1)

        val path = Path()
        values.forEachIndexed { i, v ->
            val x = i * stepX
            val y = size.height - ((v / maxVal) * (size.height - 20f))
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        // Draw line
        drawPath(
            path = path,
            color = Color(0xFF31D7F5),
            style = Stroke(width = 4f)
        )

        // Draw points
        values.forEachIndexed { i, v ->
            val x = i * stepX
            val y = size.height - ((v / maxVal) * (size.height - 20f))
            drawCircle(
                color = Color(0xFF0B1020),
                radius = 6f,
                center = Offset(x, y)
            )
            drawCircle(
                color = Color(0xFF31D7F5),
                radius = 4f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun SessionDetailDialog(
    session: GameSession,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexoraSurface,
        title = {
            Column {
                Text(session.gameTitle, fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 18.sp)
                val dateStr = SimpleDateFormat("EEEE dd MMMM yyyy à HH:mm", Locale.FRANCE).format(Date(session.startTimestamp))
                Text(dateStr, color = NexoraTextSecondary, fontSize = 12.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (session.isDemo) {
                    NexoraTag(text = "Données de démonstration", color = NexoraPurple)
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = NexoraSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Durée totale :", color = NexoraTextSecondary, fontSize = 12.sp)
                            Text("${session.durationSeconds / 60} minutes", color = NexoraTextPrimary, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Évolution batterie :", color = NexoraTextSecondary, fontSize = 12.sp)
                            Text("${session.startBattery}% → ${session.endBattery}% (-${session.batteryDrainPercent}%)", color = NexoraWarning, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Rythme estimé :", color = NexoraTextSecondary, fontSize = 12.sp)
                            Text("${"%.1f".format(session.drainPerHour)}% par heure", color = NexoraTextPrimary)
                        }
                        if (session.startTempCelsius != null && session.endTempCelsius != null) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Température batterie :", color = NexoraTextSecondary, fontSize = 12.sp)
                                Text("${session.startTempCelsius}°C → ${session.endTempCelsius}°C", color = NexoraTextPrimary)
                            }
                        }
                        if (session.avgPingMs != null) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Latence moyenne :", color = NexoraTextSecondary, fontSize = 12.sp)
                                Text("${session.avgPingMs} ms (gigue ±${session.pingJitterMs ?: 0} ms)", color = NexoraCyan)
                            }
                        }
                    }
                }

                if (session.notes.isNotBlank()) {
                    Text("Commentaire :", color = NexoraTextMuted, fontSize = 11.sp)
                    Text(session.notes, color = NexoraTextPrimary, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg)
            ) {
                Text("Fermer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("Supprimer", color = NexoraError)
            }
        }
    )
}

@Composable
fun SessionComparisonDialog(
    sessionA: GameSession,
    sessionB: GameSession,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexoraSurface,
        title = {
            Text("Comparaison de sessions", fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Métrique", modifier = Modifier.weight(1.2f), color = NexoraTextMuted, fontSize = 11.sp)
                    Text("Session A", modifier = Modifier.weight(1f), color = NexoraCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text("Session B", modifier = Modifier.weight(1f), color = NexoraPurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                ComparisonRow(
                    metric = "Jeu",
                    valA = sessionA.gameTitle.take(12),
                    valB = sessionB.gameTitle.take(12)
                )

                ComparisonRow(
                    metric = "Durée",
                    valA = "${sessionA.durationSeconds / 60}m",
                    valB = "${sessionB.durationSeconds / 60}m"
                )

                ComparisonRow(
                    metric = "Batterie perdue",
                    valA = "-${sessionA.batteryDrainPercent}%",
                    valB = "-${sessionB.batteryDrainPercent}%"
                )

                ComparisonRow(
                    metric = "Rythme / heure",
                    valA = "${"%.1f".format(sessionA.drainPerHour)}%",
                    valB = "${"%.1f".format(sessionB.drainPerHour)}%"
                )

                ComparisonRow(
                    metric = "Latence moy.",
                    valA = sessionA.avgPingMs?.let { "${it}ms" } ?: "--",
                    valB = sessionB.avgPingMs?.let { "${it}ms" } ?: "--"
                )

                val tempA = if (sessionA.startTempCelsius != null && sessionA.endTempCelsius != null) {
                    "+${"%.1f".format(sessionA.endTempCelsius - sessionA.startTempCelsius)}°C"
                } else "--"
                val tempB = if (sessionB.startTempCelsius != null && sessionB.endTempCelsius != null) {
                    "+${"%.1f".format(sessionB.endTempCelsius - sessionB.startTempCelsius)}°C"
                } else "--"

                ComparisonRow(
                    metric = "Delta Température",
                    valA = tempA,
                    valB = tempB
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg)
            ) {
                Text("Terminer")
            }
        }
    )
}

@Composable
fun ComparisonRow(metric: String, valA: String, valB: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(metric, modifier = Modifier.weight(1.2f), color = NexoraTextSecondary, fontSize = 12.sp)
        Text(valA, modifier = Modifier.weight(1f), color = NexoraTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Text(valB, modifier = Modifier.weight(1f), color = NexoraTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}
