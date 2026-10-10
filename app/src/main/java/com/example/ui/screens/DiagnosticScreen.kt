package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.viewmodel.NexoraViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiagnosticScreen(
    viewModel: NexoraViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.deviceMetrics.collectAsState()
    val isDiagnosing by viewModel.isDiagnosing.collectAsState()
    val diagnosticStep by viewModel.diagnosticStep.collectAsState()
    val diagnosticProgress by viewModel.diagnosticProgress.collectAsState()
    val netResult by viewModel.lastNetworkResult.collectAsState()

    val animatedProgress by animateFloatAsState(targetValue = diagnosticProgress, label = "progress")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NexoraDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "Diagnostic Matériel & Réseau",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NexoraTextPrimary,
                        fontSize = 22.sp
                    )
                )
                Text(
                    text = "Mesures réelles pour comprendre les capacités et limites de l'appareil",
                    style = MaterialTheme.typography.bodySmall.copy(color = NexoraTextSecondary)
                )
            }
        }

        // Action card: Run diagnostic
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Test de stabilité système",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NexoraTextPrimary,
                                fontSize = 16.sp
                            )
                        )
                        val timeStr = SimpleDateFormat("HH:mm", Locale.FRANCE).format(Date(metrics.timestamp))
                        Text(
                            text = "Mesuré à $timeStr",
                            color = NexoraTextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isDiagnosing) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = diagnosticStep,
                                    color = NexoraCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${(animatedProgress * 100).toInt()}%",
                                    color = NexoraCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = NexoraCyan,
                                trackColor = NexoraBorder
                            )
                        }
                    } else {
                        Text(
                            text = "Interroge directement les capteurs du noyau Android et effectue 4 requêtes de mesure vers le résolveur public.",
                            color = NexoraTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.runFullDiagnostic() },
                        enabled = !isDiagnosing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NexoraCyan,
                            contentColor = NexoraDarkBg
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("diagnostic_run_button")
                    ) {
                        if (isDiagnosing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = NexoraDarkBg,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyse en cours...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lancer un diagnostic", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Real Network Test Details Card
        item {
            NexoraSectionHeader(
                title = "Qualité de la connexion",
                subtitle = "Cible de test : ${metrics.testTarget}"
            )

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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NetworkCheck,
                                contentDescription = null,
                                tint = NexoraCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Latence & Gigue Réseau",
                                fontWeight = FontWeight.Bold,
                                color = NexoraTextPrimary,
                                fontSize = 14.sp
                            )
                        }

                        val ping = netResult?.averageMs ?: metrics.pingMs
                        val pillColor = when {
                            ping == null -> NexoraTextMuted
                            ping < 50 -> NexoraSuccess
                            ping < 90 -> NexoraWarning
                            else -> NexoraError
                        }
                        NexoraTag(
                            text = if (ping != null) "$ping ms moyen" else "Non mesuré",
                            color = pillColor
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ping breakdown stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Latence min", color = NexoraTextSecondary, fontSize = 11.sp)
                            Text(
                                text = netResult?.minMs?.let { "$it ms" } ?: "--",
                                color = NexoraTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Column {
                            Text("Latence max", color = NexoraTextSecondary, fontSize = 11.sp)
                            Text(
                                text = netResult?.maxMs?.let { "$it ms" } ?: "--",
                                color = NexoraTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Column {
                            Text("Gigue (Jitter)", color = NexoraTextSecondary, fontSize = 11.sp)
                            Text(
                                text = netResult?.jitterMs?.let { "±$it ms" } ?: "--",
                                color = NexoraPurple,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Column {
                            Text("Échecs", color = NexoraTextSecondary, fontSize = 11.sp)
                            Text(
                                text = "${netResult?.failures ?: metrics.pingFailures} / 4",
                                color = if ((netResult?.failures ?: 0) > 0) NexoraError else NexoraSuccess,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Note de transparence : Cette mesure teste la réponse de votre liaison vers le résolveur public. Les serveurs de jeu réels peuvent être situés dans des régions différentes et présenter une latence différente.",
                        color = NexoraTextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Hardware details 2x2
        item {
            NexoraSectionHeader(
                title = "Capteurs & Ressources système",
                subtitle = "Interrogation directe des API Android sans simulation"
            )

            // Grid of 4 cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Battery
                    val bVal = if (metrics.batteryPercent >= 0) "${metrics.batteryPercent}%" else "Non disponible"
                    NexoraMetricCard(
                        title = "Niveau Batterie",
                        value = bVal,
                        unit = if (metrics.isCharging) "(Secteur)" else "",
                        icon = Icons.Default.BatteryFull,
                        status = if (metrics.batteryPercent > 20) MetricStatus.OPTIMAL else MetricStatus.WARNING,
                        subtext = "Source: BatteryManager",
                        modifier = Modifier.weight(1f)
                    )

                    // Battery Temperature
                    val tVal = metrics.batteryTempCelsius?.let { "${"%.1f".format(it)}°C" } ?: "Non disponible"
                    val tStatus = when {
                        metrics.batteryTempCelsius == null -> MetricStatus.NEUTRAL
                        metrics.batteryTempCelsius!! > 40f -> MetricStatus.CRITICAL
                        metrics.batteryTempCelsius!! > 37f -> MetricStatus.WARNING
                        else -> MetricStatus.OPTIMAL
                    }
                    NexoraMetricCard(
                        title = "Température",
                        value = tVal,
                        icon = Icons.Default.Thermostat,
                        status = tStatus,
                        subtext = "Source: Sonde thermique batterie",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // RAM
                    val ramVal = if (metrics.availableRamMb > 0) "${metrics.availableRamMb}" else "Non disponible"
                    val ramSub = if (metrics.totalRamMb > 0) "Sur ${metrics.totalRamMb} Mo totaux" else "Source: ActivityManager"
                    NexoraMetricCard(
                        title = "RAM Disponible",
                        value = ramVal,
                        unit = if (metrics.availableRamMb > 0) "Mo" else "",
                        icon = Icons.Default.Memory,
                        status = if (metrics.availableRamMb > 1200) MetricStatus.OPTIMAL else MetricStatus.WARNING,
                        subtext = ramSub,
                        modifier = Modifier.weight(1f)
                    )

                    // Storage
                    val stoVal = if (metrics.availableStorageGb >= 0f) "${metrics.availableStorageGb}" else "Non disponible"
                    val stoSub = if (metrics.totalStorageGb >= 0f) "Sur ${metrics.totalStorageGb} Go au total" else "Source: StatFs"
                    NexoraMetricCard(
                        title = "Stockage Libre",
                        value = stoVal,
                        unit = if (metrics.availableStorageGb >= 0f) "Go" else "",
                        icon = Icons.Default.SdStorage,
                        status = if (metrics.availableStorageGb > 5f) MetricStatus.OPTIMAL else MetricStatus.WARNING,
                        subtext = stoSub,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: Honest Recommendations Framework (4 quadrants required by spec)
        item {
            NexoraSectionHeader(
                title = "Analyse & Recommandations honnêtes",
                subtitle = "Comprendre sans fausses promesses"
            )

            RecommendationCard(
                observed = if (metrics.batteryTempCelsius != null && metrics.batteryTempCelsius!! > 38f) {
                    "Température de batterie élevée (${metrics.batteryTempCelsius}°C) détectée."
                } else if (metrics.availableRamMb in 1..1000) {
                    "Moins de 1 Go de mémoire vive libre détecté."
                } else {
                    "Comportement matériel nominal et liaison réseau mesurée stable."
                },
                meaning = "Sur Android, la chauffe entraîne la régulation thermique automatique (throttling) du GPU/CPU pour protéger le composant, réduisant la fréquence d'images par seconde.",
                actions = listOf(
                    "Retirez la coque épaisse du téléphone pendant les longues sessions de jeu.",
                    "Fermez manuellement les applications gourmandes en arrière-plan comme le streaming vidéo ou GPS.",
                    "Ne forcez pas la luminosité de l'écran à 100% si vous jouez en intérieur."
                ),
                limitations = "NEXORA ne peut pas modifier la vitesse de votre processeur ni contourner les limites physiques fixées par le fabricant."
            )
        }

        // Education card: Why fake cleaners are bad
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = NexoraCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pourquoi NEXORA ne propose pas de « Boost RAM » ?",
                            fontWeight = FontWeight.Bold,
                            color = NexoraTextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Sur le système Android moderne (Linux kernel), la RAM non utilisée est de la mémoire gaspillée. Les applications dites de « nettoyage » forcent la fermeture brutale des processus, qui redémarrent immédiatement en arrière-plan. Cela surcharge le processeur, augmente la chauffe et épuise la batterie au lieu d'améliorer vos jeux.",
                            color = NexoraTextSecondary,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(
    observed: String,
    meaning: String,
    actions: List<String>,
    limitations: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = NexoraSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Observed
            Column {
                Text(
                    text = "1. Ce que nous avons observé :",
                    fontWeight = FontWeight.Bold,
                    color = NexoraCyan,
                    fontSize = 12.5.sp
                )
                Text(
                    text = observed,
                    color = NexoraTextPrimary,
                    fontSize = 12.sp
                )
            }

            // Meaning
            Column {
                Text(
                    text = "2. Ce que cela peut signifier :",
                    fontWeight = FontWeight.Bold,
                    color = NexoraPurple,
                    fontSize = 12.5.sp
                )
                Text(
                    text = meaning,
                    color = NexoraTextSecondary,
                    fontSize = 12.sp
                )
            }

            // Actions
            Column {
                Text(
                    text = "3. Ce que vous pouvez essayer :",
                    fontWeight = FontWeight.Bold,
                    color = NexoraSuccess,
                    fontSize = 12.5.sp
                )
                actions.forEach { action ->
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("• ", color = NexoraSuccess, fontWeight = FontWeight.Bold)
                        Text(text = action, color = NexoraTextSecondary, fontSize = 11.5.sp)
                    }
                }
            }

            // Limitations
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NexoraSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "4. Limites de la mesure :",
                    fontWeight = FontWeight.Bold,
                    color = NexoraWarning,
                    fontSize = 11.sp
                )
                Text(
                    text = limitations,
                    color = NexoraTextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}
