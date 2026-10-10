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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Game
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

@Composable
fun GamesScreen(
    viewModel: NexoraViewModel,
    modifier: Modifier = Modifier
) {
    val games by viewModel.games.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val onlyFavorites by viewModel.gameFilterOnlyFavorites.collectAsState()
    val selectedGame by viewModel.selectedGame.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val filteredGames = games.filter { game ->
        val matchesSearch = game.title.contains(searchQuery, ignoreCase = true) ||
                game.genre.contains(searchQuery, ignoreCase = true)
        val matchesFav = if (onlyFavorites) game.isFavorite else true
        matchesSearch && matchesFav
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NexoraDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Info & Notice
            item {
                Column {
                    Text(
                        text = "Bibliothèque de jeux",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NexoraTextPrimary,
                            fontSize = 22.sp
                        )
                    )
                    Text(
                        text = "Centralisez et suivez tous vos titres mobiles favoris",
                        style = MaterialTheme.typography.bodySmall.copy(color = NexoraTextSecondary)
                    )
                }
            }

            // Honest notice about demo catalogue and native permissions
            item {
                NexoraHonestBanner(
                    title = "Respect des restrictions Android",
                    description = "Les titres de démonstration servent à illustrer l'interface. Sur Android moderne, la détection des applications installées requiert la permission QUERY_ALL_PACKAGES soumise à des règles strictes sur Google Play. Vous pouvez ajouter et organiser manuellement tous vos jeux ici en toute confidentialité."
                )
            }

            // Search and filters bar
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Rechercher un jeu...", color = NexoraTextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = NexoraCyan)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Effacer", tint = NexoraTextSecondary)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NexoraSurface,
                            unfocusedContainerColor = NexoraSurface,
                            focusedBorderColor = NexoraCyan,
                            unfocusedBorderColor = NexoraBorder,
                            focusedTextColor = NexoraTextPrimary,
                            unfocusedTextColor = NexoraTextPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("games_search_field")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = onlyFavorites,
                                onClick = { viewModel.toggleFavoritesOnlyFilter() },
                                label = { Text("Favoris uniquement", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (onlyFavorites) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = null,
                                        tint = if (onlyFavorites) NexoraWarning else NexoraTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NexoraSurfaceElevated,
                                    selectedLabelColor = NexoraTextPrimary,
                                    containerColor = NexoraSurface,
                                    labelColor = NexoraTextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (onlyFavorites) NexoraWarning else NexoraBorder,
                                    selectedBorderColor = NexoraWarning,
                                    enabled = true,
                                    selected = onlyFavorites
                                )
                            )
                        }

                        Text(
                            text = "${filteredGames.size} jeux",
                            color = NexoraTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Games List
            if (filteredGames.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = NexoraSurfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = NexoraTextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Aucun jeu correspondant",
                                color = NexoraTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Ajoutez un jeu manuellement ou modifiez votre filtre.",
                                color = NexoraTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(filteredGames, key = { it.id }) { game ->
                    GameListItem(
                        game = game,
                        onItemClick = { viewModel.openGameDetails(game) },
                        onToggleFavorite = { viewModel.toggleFavoriteGame(game.id) },
                        onStartGameMode = { viewModel.openGameMode(game) }
                    )
                }
            }
        }

        // FAB to add game manually
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = NexoraCyan,
            contentColor = NexoraDarkBg,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("games_add_game_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter un jeu")
        }
    }

    // Add Game Dialog
    if (showAddDialog) {
        AddGameDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, genre, notes ->
                viewModel.addCustomGame(title, genre, notes)
                showAddDialog = false
            }
        )
    }

    // Game Details Sheet / Dialog
    if (selectedGame != null) {
        GameDetailDialog(
            game = selectedGame!!,
            onDismiss = { viewModel.closeGameDetails() },
            onStartSession = {
                viewModel.openGameMode(selectedGame)
                viewModel.closeGameDetails()
            },
            onDeleteGame = {
                viewModel.deleteGame(selectedGame!!.id)
            },
            onUpdateGame = { updated ->
                viewModel.updateGame(updated)
            },
            onOpenGraphicsGuide = {
                viewModel.openGraphicsGuide()
                viewModel.closeGameDetails()
            }
        )
    }
}

@Composable
fun GameListItem(
    game: Game,
    onItemClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onStartGameMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, NexoraBorder, RoundedCornerShape(14.dp))
            .clickable { onItemClick() },
        colors = CardDefaults.cardColors(containerColor = NexoraSurface),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail / Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NexoraSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                if (game.drawableRes != null) {
                    Image(
                        painter = painterResource(id = game.drawableRes),
                        contentDescription = game.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = NexoraCyan,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NexoraTextPrimary,
                            fontSize = 15.sp
                        ),
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = game.genre,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NexoraTextSecondary,
                            fontSize = 11.5.sp
                        )
                    )
                    if (game.isDemo) {
                        NexoraTag(text = "Catalogue démo", color = NexoraPurple)
                    }
                }

                if (game.totalPlayTimeMinutes > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Temps suivi : ${game.totalPlayTimeMinutes} min",
                        color = NexoraTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Actions: Star Favorite & Play Mode
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = if (game.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = if (game.isFavorite) "Retirer des favoris" else "Ajouter aux favoris",
                        tint = if (game.isFavorite) NexoraWarning else NexoraTextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NexoraCyan.copy(alpha = 0.15f))
                        .border(1.dp, NexoraCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { onStartGameMode() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Session",
                        tint = NexoraCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddGameDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, genre: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexoraSurface,
        titleContentColor = NexoraTextPrimary,
        textContentColor = NexoraTextSecondary,
        title = {
            Text("Ajouter un jeu à votre bibliothèque", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Ajoutez un jeu installé sur votre appareil pour suivre vos sessions et optimiser vos réglages.",
                    fontSize = 12.sp,
                    color = NexoraTextSecondary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nom du jeu *") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NexoraSurfaceVariant,
                        unfocusedContainerColor = NexoraSurfaceVariant,
                        focusedBorderColor = NexoraCyan,
                        unfocusedBorderColor = NexoraBorder,
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_game_title_input")
                )

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Genre (ex: Battle Royale, MOBA, FPS)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NexoraSurfaceVariant,
                        unfocusedContainerColor = NexoraSurfaceVariant,
                        focusedBorderColor = NexoraCyan,
                        unfocusedBorderColor = NexoraBorder,
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes personnelles (objectifs, sensibilité)") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, genre, notes)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Ajouter", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = NexoraTextSecondary)
            }
        }
    )
}

@Composable
fun GameDetailDialog(
    game: Game,
    onDismiss: () -> Unit,
    onStartSession: () -> Unit,
    onDeleteGame: () -> Unit,
    onUpdateGame: (Game) -> Unit,
    onOpenGraphicsGuide: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(game.title) }
    var notes by remember { mutableStateOf(game.customNotes) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexoraSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Modifier le jeu" else game.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = NexoraTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = NexoraTextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (isEditing) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre du jeu") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NexoraSurfaceVariant,
                            unfocusedContainerColor = NexoraSurfaceVariant,
                            focusedBorderColor = NexoraCyan,
                            unfocusedBorderColor = NexoraBorder,
                            focusedTextColor = NexoraTextPrimary,
                            unfocusedTextColor = NexoraTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes personnelles") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NexoraSurfaceVariant,
                            unfocusedContainerColor = NexoraSurfaceVariant,
                            focusedBorderColor = NexoraCyan,
                            unfocusedBorderColor = NexoraBorder,
                            focusedTextColor = NexoraTextPrimary,
                            unfocusedTextColor = NexoraTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                onUpdateGame(game.copy(title = title, customNotes = notes))
                                isEditing = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg)
                        ) {
                            Text("Enregistrer")
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        NexoraTag(text = game.genre, color = NexoraCyan)
                        if (game.isDemo) {
                            NexoraTag(text = "Exemple Démo", color = NexoraPurple)
                        }
                    }

                    if (game.customNotes.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NexoraSurfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Notes du joueur :", fontSize = 11.sp, color = NexoraTextMuted)
                                Text(game.customNotes, fontSize = 13.sp, color = NexoraTextPrimary)
                            }
                        }
                    }

                    Text(
                        text = "Temps de jeu enregistré : ${game.totalPlayTimeMinutes} minutes",
                        fontSize = 12.sp,
                        color = NexoraTextSecondary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Buttons
                    OutlinedButton(
                        onClick = onOpenGraphicsGuide,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NexoraCyan),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NexoraCyan))
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Consulter le Guide Graphique")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Modifier", color = NexoraCyan)
                        }

                        TextButton(onClick = { showDeleteConfirm = true }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = NexoraError, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Retirer de la liste", color = NexoraError)
                        }
                    }
                }

                if (showDeleteConfirm) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, NexoraError, RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                "Retirer de la bibliothèque ?",
                                color = NexoraError,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                "Cette action retire l'élément de NEXORA sans supprimer l'application de votre téléphone.",
                                color = NexoraTextSecondary,
                                fontSize = 11.5.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showDeleteConfirm = false }) {
                                    Text("Annuler", color = NexoraTextSecondary)
                                }
                                Button(
                                    onClick = {
                                        onDeleteGame()
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NexoraError)
                                ) {
                                    Text("Confirmer", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isEditing) {
                Button(
                    onClick = onStartSession,
                    colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Lancer la session", fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}
