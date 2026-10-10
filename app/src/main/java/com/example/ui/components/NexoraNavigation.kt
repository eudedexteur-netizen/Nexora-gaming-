package com.example.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.DeviceMetrics
import com.example.ui.theme.NexoraBorder
import com.example.ui.theme.NexoraCyan
import com.example.ui.theme.NexoraDarkBg
import com.example.ui.theme.NexoraPurple
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurface
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.viewmodel.NavTab

@Composable
fun NexoraTopBar(
    metrics: DeviceMetrics,
    isSessionActive: Boolean,
    onGameModeClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NexoraDarkBg)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NexoraSurfaceElevated)
                    .border(1.dp, NexoraCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_nexora_logo),
                    contentDescription = "Logo NEXORA",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "NEXORA",
                        fontWeight = FontWeight.Black,
                        color = NexoraCyan,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "GAMING",
                        fontWeight = FontWeight.Bold,
                        color = NexoraPurple,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = "Joue mieux. Comprends ton tél.",
                    color = NexoraTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Quick status badge & Action
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Live status pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(NexoraSurface)
                    .border(1.dp, NexoraBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (metrics.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                        contentDescription = "Batterie",
                        tint = if (metrics.batteryPercent > 20) NexoraSuccess else NexoraCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (metrics.batteryPercent >= 0) "${metrics.batteryPercent}%" else "--",
                        color = NexoraTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Game mode button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSessionActive) NexoraSuccess.copy(alpha = 0.2f) else NexoraCyan.copy(alpha = 0.15f))
                    .border(
                        1.dp,
                        if (isSessionActive) NexoraSuccess else NexoraCyan,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onGameModeClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_bar_game_mode_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Mode Partie",
                        tint = if (isSessionActive) NexoraSuccess else NexoraCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSessionActive) "En jeu" else "Mode Partie",
                        color = if (isSessionActive) NexoraSuccess else NexoraCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun NexoraBottomNav(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = NexoraBorder, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        containerColor = NexoraSurface,
        tonalElevation = 6.dp
    ) {
        val items = listOf(
            Triple(NavTab.HOME, "Accueil", Icons.Default.Dashboard),
            Triple(NavTab.GAMES, "Mes jeux", Icons.Default.SportsEsports),
            Triple(NavTab.DIAGNOSTIC, "Diagnostic", Icons.Default.Speed),
            Triple(NavTab.SESSIONS, "Sessions", Icons.Default.History),
            Triple(NavTab.PROFILE, "Profil", Icons.Default.Person)
        )

        items.forEach { (tab, label, icon) ->
            val isSelected = currentTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NexoraDarkBg,
                    selectedTextColor = NexoraCyan,
                    indicatorColor = NexoraCyan,
                    unselectedIconColor = NexoraTextSecondary,
                    unselectedTextColor = NexoraTextSecondary
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
