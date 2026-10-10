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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@Composable
fun ProfileScreen(
    viewModel: NexoraViewModel,
    modifier: Modifier = Modifier
) {
    val prefs by viewModel.userPreferences.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NexoraDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(NexoraSurfaceElevated)
                            .border(1.5.dp, NexoraCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = NexoraCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Joueur NEXORA",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NexoraTextPrimary,
                                    fontSize = 17.sp
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (prefs.isPlusActive) {
                                NexoraTag(text = "NEXORA Plus", color = NexoraPurple)
                            } else {
                                NexoraTag(text = "Gratuit", color = NexoraCyan)
                            }
                        }
                        Text(
                            text = if (prefs.isPlusActive) "Statut Plus actif (${prefs.activePlusPlan})" else "Profil 100% local — Aucune connexion requise",
                            color = NexoraTextSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        // Section: NEXORA Plus (Subscription preview & simulation)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (prefs.isPlusActive) NexoraSuccess else NexoraPurple.copy(alpha = 0.5f),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { viewModel.openPlusModal() }
                    .testTag("profile_plus_card"),
                colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (prefs.isPlusActive) NexoraSuccess else NexoraPurple,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (prefs.isPlusActive) "NEXORA Plus (Actif)" else "NEXORA Plus",
                                fontWeight = FontWeight.Bold,
                                color = NexoraTextPrimary,
                                fontSize = 16.sp
                            )
                        }
                        NexoraTag(
                            text = if (prefs.isPlusActive) "Actif (Démo)" else "1 500 FCFA / 2,49 €",
                            color = if (prefs.isPlusActive) NexoraSuccess else NexoraPurple
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (prefs.isPlusActive)
                            "Formule active : ${prefs.activePlusPlan}. Toutes les fonctionnalités avancées sont opérationnelles."
                        else
                            "Analyses historiques étendues, sauvegardes et exports. Simulation de paiement direct par Wave et Mobile Money (Orange, MTN, Moov).",
                        color = NexoraTextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Visual payment methods showcase
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PaymentBrandBadge(name = "Wave", color = Color(0xFF1DC3F4))
                        PaymentBrandBadge(name = "Orange", color = Color(0xFFFF7900))
                        PaymentBrandBadge(name = "MTN MoMo", color = Color(0xFFFFCC00))
                        PaymentBrandBadge(name = "Moov", color = Color(0xFF007A3D))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (prefs.isPlusActive) "Gérer ou modifier l'abonnement simulé" else "Simuler un paiement Wave / Mobile Money",
                        color = NexoraCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Section: Préférences & Confort
        item {
            NexoraSectionHeader(
                title = "Préférences de jeu",
                subtitle = "Personnalisation de l'affichage et du confort"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Dark theme toggle
                    PreferenceRow(
                        icon = Icons.Default.Brightness4,
                        title = "Thème sombre gaming",
                        subtitle = if (prefs.darkTheme) "Activé (Bleu nuit #0B1020)" else "Mode clair standard",
                        trailing = {
                            Switch(
                                checked = prefs.darkTheme,
                                onCheckedChange = { viewModel.updateTheme(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = NexoraCyan,
                                    checkedTrackColor = NexoraSurfaceElevated,
                                    uncheckedThumbColor = NexoraTextSecondary,
                                    uncheckedTrackColor = NexoraSurfaceVariant
                                )
                            )
                        }
                    )

                    // Break reminder
                    PreferenceRow(
                        icon = Icons.Default.Notifications,
                        title = "Rappel de pause en session",
                        subtitle = if (prefs.breakReminderMinutes > 0) "${prefs.breakReminderMinutes} minutes" else "Désactivé",
                        trailing = {
                            val options = listOf(30, 45, 60, 0)
                            val currentIndex = options.indexOf(prefs.breakReminderMinutes).let { if (it >= 0) it else 1 }
                            val nextMinutes = options[(currentIndex + 1) % options.size]
                            TextButton(onClick = { viewModel.updateBreakReminder(nextMinutes) }) {
                                Text(
                                    text = if (prefs.breakReminderMinutes > 0) "${prefs.breakReminderMinutes}m" else "Off",
                                    color = NexoraCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )

                    // Temperature unit
                    PreferenceRow(
                        icon = Icons.Default.Thermostat,
                        title = "Unité de température",
                        subtitle = if (prefs.temperatureUnitCelsius) "Celsius (°C)" else "Fahrenheit (°F)",
                        trailing = {
                            TextButton(onClick = { viewModel.updateTemperatureUnit(!prefs.temperatureUnitCelsius) }) {
                                Text(
                                    text = if (prefs.temperatureUnitCelsius) "°C" else "°F",
                                    color = NexoraCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                }
            }
        }

        // Section: Outils d'entraînement et Guide
        item {
            NexoraSectionHeader(
                title = "Outils d'entraînement et d'optimisation",
                subtitle = "Exercices moteurs et recommandations d'affichage"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    PreferenceClickableRow(
                        icon = Icons.Default.Speed,
                        title = "Outils d'entraînement réflexes",
                        subtitle = "Test de réaction visuelle & cibles tactiles",
                        onClick = { viewModel.openTraining() }
                    )

                    PreferenceClickableRow(
                        icon = Icons.Default.Tune,
                        title = "Guide Graphique des jeux",
                        subtitle = "Arbitrage Fluidité (FPS) vs Chauffe thermique",
                        onClick = { viewModel.openGraphicsGuide() }
                    )
                }
            }
        }

        // Section: Données & Confidentialité (GDPR & Integrity)
        item {
            NexoraSectionHeader(
                title = "Gestion des données & Vie privée",
                subtitle = "Contrôle total sans collecte clandestine"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    PreferenceClickableRow(
                        icon = Icons.Default.Security,
                        title = "Politique de confidentialité (RGPD)",
                        subtitle = "Stockage 100% sur l'appareil, 0 traceur publicitaire",
                        onClick = { showPrivacyDialog = true }
                    )

                    PreferenceClickableRow(
                        icon = Icons.Default.Shield,
                        title = "Conditions d'utilisation",
                        subtitle = "Engagements d'honnêteté technique",
                        onClick = { showTermsDialog = true }
                    )

                    PreferenceClickableRow(
                        icon = Icons.Default.CleaningServices,
                        title = "Supprimer les données de démo",
                        subtitle = "Conserver uniquement vos sessions et jeux réels",
                        onClick = { viewModel.deleteDemoData() }
                    )

                    PreferenceClickableRow(
                        icon = Icons.Default.Delete,
                        title = "Réinitialiser toutes les données",
                        subtitle = "Efface l'historique et restaure la configuration",
                        onClick = { showResetConfirm = true },
                        isDestructive = true
                    )
                }
            }
        }

        // App Information Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NEXORA Gaming v1.0.0",
                    color = NexoraTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "« Joue mieux. Comprends ton téléphone. »",
                    color = NexoraTextMuted,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                TextButton(onClick = { showHelpDialog = true }) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Aide & Support technique", color = NexoraCyan, fontSize = 11.5.sp)
                }
            }
        }
    }

    // Privacy Dialog (GDPR)
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = NexoraSurface,
            title = {
                Text("Protection des données personnelles (RGPD)", fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "1. Minimisation des données :",
                        fontWeight = FontWeight.Bold,
                        color = NexoraCyan,
                        fontSize = 12.sp
                    )
                    Text(
                        "Toutes vos données (jeux suivis, sessions, temps de réaction) sont enregistrées exclusivement dans le stockage local de votre smartphone (SharedPreferences).",
                        color = NexoraTextSecondary,
                        fontSize = 11.5.sp
                    )
                    Text(
                        "2. Aucun identifiant de jeu collecté :",
                        fontWeight = FontWeight.Bold,
                        color = NexoraPurple,
                        fontSize = 12.sp
                    )
                    Text(
                        "NEXORA ne demande JAMAIS vos identifiants ou mots de passe de jeux (Mobile Legends, PUBG, Riot, Google Play).",
                        color = NexoraTextSecondary,
                        fontSize = 11.5.sp
                    )
                    Text(
                        "3. Pas de publicité intrusive :",
                        fontWeight = FontWeight.Bold,
                        color = NexoraSuccess,
                        fontSize = 12.sp
                    )
                    Text(
                        "Aucun SDK de pistage publicitaire ou de profilage n'est intégré à l'application.",
                        color = NexoraTextSecondary,
                        fontSize = 11.5.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg)
                ) {
                    Text("Fermer")
                }
            }
        )
    }

    // Terms Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            containerColor = NexoraSurface,
            title = {
                Text("Conditions d'utilisation", fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Engagements d'honnêteté NEXORA :",
                        fontWeight = FontWeight.Bold,
                        color = NexoraCyan,
                        fontSize = 12.sp
                    )
                    Text(
                        "• Aucune promesse trompeuse d'augmentation magique de FPS ou de réduction artificielle du ping.\n• Le respect absolu de l'intégrité de votre matériel sans forcer les limites thermiques du fabricant.\n• Respect des conditions des éditeurs de jeux tiers : aucune modification de mémoire ou d'exécutable binaire.",
                        color = NexoraTextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showTermsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg)
                ) {
                    Text("Compris")
                }
            }
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = NexoraSurface,
            title = {
                Text("Aide & Contact", fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Besoin d'un éclairage technique ?",
                        fontWeight = FontWeight.Bold,
                        color = NexoraCyan,
                        fontSize = 13.sp
                    )
                    Text(
                        "NEXORA a été conçu pour donner le contrôle aux joueurs Android sans jargon technique incompréhensible.\n\nPour signaler un problème ou suggérer un jeu, contactez l'équipe à support@nexora-gaming.fr",
                        color = NexoraTextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHelpDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg)
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Reset Confirm Dialog
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            containerColor = NexoraSurface,
            title = {
                Text("Réinitialiser les données locales ?", fontWeight = FontWeight.Bold, color = NexoraError)
            },
            text = {
                Text(
                    "Toutes vos sessions enregistrées et vos jeux ajoutés manuellement seront supprimés et les valeurs de démonstration réinitialisées. Cette opération est irréversible.",
                    color = NexoraTextSecondary,
                    fontSize = 12.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllData()
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraError)
                ) {
                    Text("Réinitialiser", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Annuler", color = NexoraTextSecondary)
                }
            }
        )
    }
}

@Composable
fun PreferenceRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(NexoraSurfaceElevated, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, color = NexoraTextPrimary, fontSize = 13.sp)
                Text(subtitle, color = NexoraTextSecondary, fontSize = 11.sp)
            }
        }
        trailing()
    }
}

@Composable
fun PreferenceClickableRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(if (isDestructive) NexoraError.copy(alpha = 0.1f) else NexoraSurfaceElevated, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isDestructive) NexoraError else NexoraCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    title,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) NexoraError else NexoraTextPrimary,
                    fontSize = 13.sp
                )
                Text(subtitle, color = NexoraTextSecondary, fontSize = 11.sp)
            }
        }
        Icon(
            Icons.Default.ArrowForward,
            contentDescription = null,
            tint = NexoraTextMuted,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun PaymentBrandBadge(name: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = name,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 9.5.sp
        )
    }
}
