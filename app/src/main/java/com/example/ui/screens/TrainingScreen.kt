package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TrainingType
import com.example.ui.components.NexoraHonestBanner
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun TrainingScreen(
    viewModel: NexoraViewModel,
    modifier: Modifier = Modifier
) {
    var selectedExerciseTab by remember { mutableIntStateOf(0) }
    val records by viewModel.trainingRecords.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NexoraDarkBg)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Outils d'entraînement",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NexoraTextPrimary,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "Précision tactile et réflexes moteurs",
                    style = MaterialTheme.typography.bodySmall.copy(color = NexoraTextSecondary)
                )
            }

            IconButton(onClick = { viewModel.closeTraining() }) {
                Icon(Icons.Default.Close, contentDescription = "Fermer", tint = NexoraTextSecondary)
            }
        }

        TabRow(
            selectedTabIndex = selectedExerciseTab,
            containerColor = NexoraSurface,
            contentColor = NexoraCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedExerciseTab]),
                    color = NexoraCyan
                )
            }
        ) {
            Tab(
                selected = selectedExerciseTab == 0,
                onClick = { selectedExerciseTab = 0 },
                text = { Text("A. Réaction Visuelle", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedExerciseTab == 1,
                onClick = { selectedExerciseTab = 1 },
                text = { Text("B. Cibles Tactiles", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedExerciseTab == 0) {
            VisualReactionExercise(
                onRecordScore = { score ->
                    viewModel.recordTrainingResult(TrainingType.VISUAL_REACTION, score)
                },
                historyScores = records.filter { it.type == TrainingType.VISUAL_REACTION }.map { it.scoreMs }
            )
        } else {
            TargetTapExercise(
                onRecordScore = { score ->
                    viewModel.recordTrainingResult(TrainingType.TARGET_TAP, score)
                },
                historyScores = records.filter { it.type == TrainingType.TARGET_TAP }.map { it.scoreMs }
            )
        }
    }
}

enum class ReactionState {
    IDLE,
    WAITING_COLOR,
    READY_TO_TAP,
    TOO_EARLY,
    RESULT
}

@Composable
fun VisualReactionExercise(
    onRecordScore: (Long) -> Unit,
    historyScores: List<Long>
) {
    var state by remember { mutableStateOf(ReactionState.IDLE) }
    var startTime by remember { mutableLongStateOf(0L) }
    var currentScore by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()

    val bestScore = historyScores.minOrNull()
    val avgScore = if (historyScores.isNotEmpty()) historyScores.average().toLong() else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NexoraHonestBanner(
            title = "Précision de la mesure",
            description = "Mesure le délai de réaction de l'écran tactile à l'interaction visuelle. Ces résultats restent privés et locaux."
        )

        // Interactive Tap Box
        val (bgColor, stateText, subText) = when (state) {
            ReactionState.IDLE -> Triple(
                NexoraSurface,
                "Touchez pour commencer",
                "Attendez que l'écran devienne vert/cyan avant de toucher !"
            )
            ReactionState.WAITING_COLOR -> Triple(
                NexoraError.copy(alpha = 0.4f),
                "Attendez...",
                "Ne touchez pas tant que la couleur n'a pas changé !"
            )
            ReactionState.READY_TO_TAP -> Triple(
                NexoraSuccess,
                "TOUCHEZ MAINTENANT !",
                "Appuyez aussi vite que possible !"
            )
            ReactionState.TOO_EARLY -> Triple(
                NexoraWarning.copy(alpha = 0.5f),
                "Trop tôt !",
                "Vous avez cliqué avant le signal. Réessayez."
            )
            ReactionState.RESULT -> Triple(
                NexoraSurfaceElevated,
                "$currentScore ms",
                "Temps de réaction enregistré. Touchez pour recommencer."
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .border(2.dp, if (state == ReactionState.READY_TO_TAP) Color.White else NexoraBorder, RoundedCornerShape(16.dp))
                .clickable {
                    when (state) {
                        ReactionState.IDLE, ReactionState.RESULT, ReactionState.TOO_EARLY -> {
                            state = ReactionState.WAITING_COLOR
                            scope.launch {
                                val delayMs = Random.nextLong(1500, 3800)
                                delay(delayMs)
                                if (state == ReactionState.WAITING_COLOR) {
                                    startTime = System.currentTimeMillis()
                                    state = ReactionState.READY_TO_TAP
                                }
                            }
                        }
                        ReactionState.WAITING_COLOR -> {
                            state = ReactionState.TOO_EARLY
                        }
                        ReactionState.READY_TO_TAP -> {
                            val elapsed = System.currentTimeMillis() - startTime
                            currentScore = elapsed
                            state = ReactionState.RESULT
                            onRecordScore(elapsed)
                        }
                    }
                }
                .testTag("reaction_interactive_box"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = stateText,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = if (state == ReactionState.RESULT) 38.sp else 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subText,
                    color = NexoraTextPrimary.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }

        // Stats Footer
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NexoraSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Meilleur", color = NexoraTextSecondary, fontSize = 11.sp)
                    Text(bestScore?.let { "$it ms" } ?: "--", fontWeight = FontWeight.Bold, color = NexoraCyan, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Moyenne", color = NexoraTextSecondary, fontSize = 11.sp)
                    Text(avgScore?.let { "$it ms" } ?: "--", fontWeight = FontWeight.Bold, color = NexoraPurple, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Essais réalisés", color = NexoraTextSecondary, fontSize = 11.sp)
                    Text("${historyScores.size}", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun TargetTapExercise(
    onRecordScore: (Long) -> Unit,
    historyScores: List<Long>
) {
    var isRunning by remember { mutableStateOf(false) }
    var targetCountRemaining by remember { mutableIntStateOf(5) }
    var startTimestamp by remember { mutableLongStateOf(0L) }
    var lastCompletedScore by remember { mutableLongStateOf(0L) }

    // Target positions (x, y as fraction 0.1 to 0.8)
    var targetX by remember { mutableStateOf(0.4f) }
    var targetY by remember { mutableStateOf(0.4f) }

    val best = historyScores.minOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        NexoraHonestBanner(
            title = "Exercice de précision tactile",
            description = "Touchez les 5 cibles successives le plus vite possible pour mesurer votre réactivité de pointage."
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(NexoraSurface)
                .border(1.dp, NexoraBorder, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (!isRunning) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (lastCompletedScore > 0L) {
                        Text(
                            text = "Score : $lastCompletedScore ms",
                            color = NexoraSuccess,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Moyenne : ${lastCompletedScore / 5} ms par cible",
                            color = NexoraTextSecondary,
                            fontSize = 13.sp
                        )
                    } else {
                        Text(
                            text = "5 Cibles aléatoires",
                            color = NexoraTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Touchez chaque point dès qu'il apparaît",
                            color = NexoraTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            isRunning = true
                            targetCountRemaining = 5
                            startTimestamp = System.currentTimeMillis()
                            targetX = Random.nextFloat() * 0.7f + 0.15f
                            targetY = Random.nextFloat() * 0.7f + 0.15f
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (lastCompletedScore > 0L) "Recommencer" else "Démarrer l'exercice", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Active Target Arena
                Box(modifier = Modifier.fillMaxSize()) {
                    // Counter in corner
                    Text(
                        text = "Cibles restantes : $targetCountRemaining",
                        color = NexoraTextMuted,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    )

                    // Target Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.TopStart)
                            .offset(
                                x = (targetX * 240).dp,
                                y = (targetY * 340).dp
                            )
                            .clip(CircleShape)
                            .background(NexoraCyan)
                            .border(3.dp, Color.White, CircleShape)
                            .clickable {
                                val nextRemaining = targetCountRemaining - 1
                                if (nextRemaining <= 0) {
                                    val total = System.currentTimeMillis() - startTimestamp
                                    lastCompletedScore = total
                                    isRunning = false
                                    onRecordScore(total)
                                } else {
                                    targetCountRemaining = nextRemaining
                                    targetX = Random.nextFloat() * 0.7f + 0.15f
                                    targetY = Random.nextFloat() * 0.7f + 0.15f
                                }
                            }
                            .testTag("target_tap_bubble"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$targetCountRemaining",
                            color = NexoraDarkBg,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NexoraSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Meilleur temps (5 cibles)", color = NexoraTextSecondary, fontSize = 11.sp)
                    Text(best?.let { "$it ms" } ?: "--", fontWeight = FontWeight.Bold, color = NexoraCyan, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Historique", color = NexoraTextSecondary, fontSize = 11.sp)
                    Text("${historyScores.size} sessions", fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 16.sp)
                }
            }
        }
    }
}
