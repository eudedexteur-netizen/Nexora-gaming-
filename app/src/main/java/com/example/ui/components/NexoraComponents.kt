package com.example.ui.components

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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun NexoraSectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NexoraTextPrimary,
                    fontSize = 17.sp
                )
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NexoraTextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onActionClick() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = actionText,
                    color = NexoraCyan,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}

enum class MetricStatus {
    OPTIMAL,
    WARNING,
    CRITICAL,
    NEUTRAL
}

@Composable
fun NexoraMetricCard(
    title: String,
    value: String,
    unit: String = "",
    icon: ImageVector,
    status: MetricStatus = MetricStatus.NEUTRAL,
    subtext: String? = null,
    modifier: Modifier = Modifier
) {
    val statusColor = when (status) {
        MetricStatus.OPTIMAL -> NexoraSuccess
        MetricStatus.WARNING -> NexoraWarning
        MetricStatus.CRITICAL -> NexoraError
        MetricStatus.NEUTRAL -> NexoraCyan
    }

    Card(
        modifier = modifier
            .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = NexoraSurfaceVariant),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = NexoraTextSecondary,
                        fontSize = 12.sp
                    )
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NexoraTextPrimary,
                        fontSize = 20.sp
                    )
                )
                if (unit.isNotBlank()) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = NexoraTextSecondary,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            if (subtext != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NexoraTextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
fun NexoraTag(
    text: String,
    color: Color = NexoraCyan,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun NexoraHonestBanner(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NexoraPurple.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = NexoraPurple,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = NexoraTextPrimary,
                        fontSize = 13.sp
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NexoraTextSecondary,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                )
            }
        }
    }
}
