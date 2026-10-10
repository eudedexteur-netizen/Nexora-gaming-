package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

enum class GraphicsObjective {
    FLUIDITY,   // Max FPS, minimal lag/heat
    BALANCED,   // 60 FPS, good visuals
    VISUALS     // Max resolution & shadows
}

@Composable
fun GraphicsGuideScreen(
    viewModel: NexoraViewModel,
    modifier: Modifier = Modifier
) {
    val games by viewModel.games.collectAsState()
    val metrics by viewModel.deviceMetrics.collectAsState()

    var selectedGameId by remember { mutableStateOf(games.firstOrNull()?.id ?: "") }
    var objective by remember { mutableStateOf(GraphicsObjective.FLUIDITY) }

    val selectedGame = games.find { it.id == selectedGameId } ?: games.firstOrNull()

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
                        text = "Guide Graphique & Équilibre",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NexoraTextPrimary,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = "Conseils d'ajustement selon votre matériel",
                        style = MaterialTheme.typography.bodySmall.copy(color = NexoraTextSecondary)
                    )
                }

                IconButton(onClick = { viewModel.closeGraphicsGuide() }) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = NexoraTextSecondary)
                }
            }
        }

        // Selection 1: Game
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("1. Jeu concerné", fontWeight = FontWeight.Bold, color = NexoraCyan, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        games.take(4).forEach { game ->
                            val isChosen = selectedGame?.id == game.id
                            FilterChip(
                                selected = isChosen,
                                onClick = { selectedGameId = game.id },
                                label = { Text(game.title.take(14), fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NexoraSurfaceElevated,
                                    selectedLabelColor = NexoraCyan,
                                    containerColor = NexoraSurfaceVariant,
                                    labelColor = NexoraTextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Selection 2: Player Objective
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("2. Priorité de jeu souhaitée", fontWeight = FontWeight.Bold, color = NexoraCyan, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    ObjectiveSelectRow(
                        title = "Priorité à la fluidité (Compétitif)",
                        subtitle = "FPS maximum (60-90 Hz), latence d'affichage la plus faible, chauffe minimale.",
                        isSelected = objective == GraphicsObjective.FLUIDITY,
                        onClick = { objective = GraphicsObjective.FLUIDITY }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ObjectiveSelectRow(
                        title = "Équilibre Fluidité / Qualité",
                        subtitle = "60 FPS stables, textures moyennes, autonomie préservée.",
                        isSelected = objective == GraphicsObjective.BALANCED,
                        onClick = { objective = GraphicsObjective.BALANCED }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ObjectiveSelectRow(
                        title = "Priorité à la fidélité visuelle",
                        subtitle = "Graphismes élevés, reflets et ombres activés. Attention à la chauffe.",
                        isSelected = objective == GraphicsObjective.VISUALS,
                        onClick = { objective = GraphicsObjective.VISUALS }
                    )
                }
            }
        }

        // Recommendation Results Card
        item {
            NexoraSectionHeader(
                title = "Réglages recommandés pour ${selectedGame?.title ?: "votre jeu"}",
                subtitle = "À appliquer directement dans le menu Options du jeu"
            )

            val rec = when (objective) {
                GraphicsObjective.FLUIDITY -> RecommendationData(
                    fpsSetting = "Élevé / Extrême (60 ou 90 FPS)",
                    graphicsQuality = "Fluide / Bas",
                    shadows = "Désactivées",
                    antiAliasing = "Désactivé (améliore la lisibilité des contours à distance)",
                    thermalImpact = "Faible chauffe, pas de bridage",
                    batteryDrain = "~15% à 18% par heure",
                    enemyReadability = "Optimale (pas d'effets visuels parasites)"
                )
                GraphicsObjective.BALANCED -> RecommendationData(
                    fpsSetting = "Élevé (60 FPS fixe)",
                    graphicsQuality = "Moyen / HD",
                    shadows = "Basses",
                    antiAliasing = "Optionnel (2x)",
                    thermalImpact = "Chauffe modérée après 30 min",
                    batteryDrain = "~20% à 24% par heure",
                    enemyReadability = "Très bonne"
                )
                GraphicsObjective.VISUALS -> RecommendationData(
                    fpsSetting = "Standard (30 à 45 FPS)",
                    graphicsQuality = "Ultra / HD+",
                    shadows = "Activées",
                    antiAliasing = "Activé",
                    thermalImpact = "Élevée (risque de bridage thermique si jeu prolongé)",
                    batteryDrain = "~28% à 35% par heure",
                    enemyReadability = "Moyenne (ombres et particules denses)"
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Taux de rafraîchissement / FPS :", color = NexoraTextSecondary, fontSize = 12.sp)
                        Text(rec.fpsSetting, fontWeight = FontWeight.Bold, color = NexoraCyan, fontSize = 12.5.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Qualité graphique globale :", color = NexoraTextSecondary, fontSize = 12.sp)
                        Text(rec.graphicsQuality, fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 12.5.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Ombres & Éclairage :", color = NexoraTextSecondary, fontSize = 12.sp)
                        Text(rec.shadows, color = NexoraTextPrimary, fontSize = 12.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Anticrénelage (Anti-Aliasing) :", color = NexoraTextSecondary, fontSize = 12.sp)
                        Text(rec.antiAliasing, color = NexoraTextPrimary, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(NexoraBorder))
                    Spacer(modifier = Modifier.height(4.dp))

                    // Compromises
                    Text("Compromis techniques observés :", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 12.sp)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = NexoraWarning, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(rec.thermalImpact, color = NexoraTextSecondary, fontSize = 11.5.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.BatteryAlert, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Consommation estimée : ${rec.batteryDrain}", color = NexoraTextSecondary, fontSize = 11.5.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = NexoraPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Repérage des adversaires : ${rec.enemyReadability}", color = NexoraTextSecondary, fontSize = 11.5.sp)
                    }
                }
            }
        }

        // Mandatory Disclaimer Banner
        item {
            NexoraHonestBanner(
                title = "Application manuelle indispensable",
                description = "NEXORA ne modifie aucun fichier interne des jeux. Les jeux comme PUBG Mobile ou Free Fire interdisent toute modification externe de leurs fichiers de configuration. Ces réglages doivent être appliqués par vos soins dans les paramètres du jeu."
            )
        }
    }
}

data class RecommendationData(
    val fpsSetting: String,
    val graphicsQuality: String,
    val shadows: String,
    val antiAliasing: String,
    val thermalImpact: String,
    val batteryDrain: String,
    val enemyReadability: String
)

@Composable
fun ObjectiveSelectRow(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) NexoraSurfaceElevated else NexoraSurfaceVariant)
            .border(1.dp, if (isSelected) NexoraCyan else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = if (isSelected) NexoraCyan else NexoraTextPrimary, fontSize = 13.sp)
            Text(subtitle, color = NexoraTextSecondary, fontSize = 11.sp)
        }
        if (isSelected) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(18.dp))
        }
    }
}
