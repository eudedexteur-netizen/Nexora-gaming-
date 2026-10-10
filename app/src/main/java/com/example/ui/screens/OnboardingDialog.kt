package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NexoraTag
import com.example.ui.theme.NexoraBorder
import com.example.ui.theme.NexoraCyan
import com.example.ui.theme.NexoraDarkBg
import com.example.ui.theme.NexoraPurple
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurface
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraSurfaceVariant
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

@Composable
fun OnboardingDialog(
    onComplete: (favGame: String) -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var selectedFavorite by remember { mutableStateOf("PUBG Mobile") }

    AlertDialog(
        onDismissRequest = { /* Force completion */ },
        containerColor = NexoraSurface,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bienvenue sur NEXORA",
                        fontWeight = FontWeight.Black,
                        color = NexoraCyan,
                        fontSize = 18.sp
                    )
                    NexoraTag(text = "Étape $step/3", color = NexoraPurple)
                }
                Text(
                    text = "« Joue mieux. Comprends ton téléphone. »",
                    color = NexoraTextSecondary,
                    fontSize = 11.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (step) {
                    1 -> {
                        // Étape 1 : Les 3 phrases de présentation
                        Text(
                            text = "NEXORA Gaming a été conçu autour de trois piliers simples et honnêtes :",
                            color = NexoraTextPrimary,
                            fontSize = 13.sp
                        )

                        OnboardingBullet(
                            icon = Icons.Default.SportsEsports,
                            title = "Centralisez vos jeux",
                            desc = "Organisez votre catalogue sans encombrement et suivez chaque temps de jeu."
                        )

                        OnboardingBullet(
                            icon = Icons.Default.Speed,
                            title = "Comprenez les mesures réelles",
                            desc = "Surveillez la batterie, la température et la stabilité réseau sans faux nettoyeurs de RAM."
                        )

                        OnboardingBullet(
                            icon = Icons.Default.Security,
                            title = "Consultez vos sessions",
                            desc = "Gardez l'historique complet de votre confort de jeu, stocké 100% sur votre appareil."
                        )
                    }
                    2 -> {
                        // Étape 2 : Préférence jeu favori
                        Text(
                            text = "Quel est votre type de jeu principal ?",
                            color = NexoraTextPrimary,
                            fontSize = 13.sp
                        )

                        val games = listOf("PUBG Mobile", "Mobile Legends", "Free Fire", "Call of Duty Mobile")
                        games.forEach { g ->
                            val isSel = selectedFavorite == g
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) NexoraSurfaceElevated else NexoraSurfaceVariant)
                                    .border(1.dp, if (isSel) NexoraCyan else NexoraBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedFavorite = g }
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(g, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = if (isSel) NexoraCyan else NexoraTextPrimary, fontSize = 13.sp)
                                if (isSel) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    3 -> {
                        // Étape 3 : Engagements de transparence & RGPD
                        Text(
                            text = "Nos engagements de transparence :",
                            fontWeight = FontWeight.Bold,
                            color = NexoraTextPrimary,
                            fontSize = 13.sp
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = NexoraSurfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("• Aucune création de compte obligatoire.", color = NexoraTextSecondary, fontSize = 12.sp)
                                Text("• Aucune promesse impossible d'augmentation magique de FPS.", color = NexoraTextSecondary, fontSize = 12.sp)
                                Text("• Aucun identifiant ou mot de passe de jeu n'est demandé.", color = NexoraTextSecondary, fontSize = 12.sp)
                                Text("• Données enregistrées localement sur votre téléphone.", color = NexoraTextSecondary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step < 3) {
                        step++
                    } else {
                        onComplete(selectedFavorite)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (step < 3) "Continuer" else "Commencer l'expérience", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun OnboardingBullet(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(NexoraSurfaceElevated, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, color = NexoraTextPrimary, fontSize = 12.5.sp)
            Text(desc, color = NexoraTextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
        }
    }
}
