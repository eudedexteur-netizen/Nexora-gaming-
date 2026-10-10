package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class PaymentMethodType {
    WAVE,
    MOBILE_MONEY,
    BANK_CARD
}

enum class MobileMoneyOperator(val displayName: String, val brandColor: Color) {
    ORANGE_MONEY("Orange Money", Color(0xFFFF7900)),
    MTN_MOMO("MTN MoMo", Color(0xFFFFCC00)),
    MOOV_MONEY("Moov Money", Color(0xFF007A3D))
}

enum class SimulationStage {
    CONFIGURATION,  // Plan & phone input
    SENDING_PROMPT, // Communicating with simulated API
    AWAITING_PIN,   // User sees simulated Push / USSD confirmation modal
    SUCCESS_RECEIPT // Transaction confirmed, Plus activated in demo
}

@Composable
fun SubscriptionModal(
    viewModel: NexoraViewModel,
    onDismiss: () -> Unit
) {
    val userPrefs by viewModel.userPreferences.collectAsState()
    val scope = rememberCoroutineScope()

    var selectedPlan by remember { mutableStateOf("monthly") } // "monthly" or "yearly"
    var paymentMethod by remember { mutableStateOf(PaymentMethodType.WAVE) }
    var selectedOperator by remember { mutableStateOf(MobileMoneyOperator.ORANGE_MONEY) }
    var phoneNumber by remember { mutableStateOf("77 842 19 03") }
    var countryPrefix by remember { mutableStateOf("+221") }

    // Simulation states
    var simulationStage by remember { mutableStateOf(SimulationStage.CONFIGURATION) }
    var simulatedProgress by remember { mutableStateOf(0.1f) }
    var simulatedStepText by remember { mutableStateOf("") }
    var enteredSecretPin by remember { mutableStateOf("") }
    var generatedTransactionRef by remember { mutableStateOf("") }
    var showGdprPreSimulationDialog by remember { mutableStateOf(false) }

    val amountEur = if (selectedPlan == "monthly") "2,49 €" else "19,99 €"
    val amountFcfa = if (selectedPlan == "monthly") "1 500 FCFA" else "12 000 FCFA"
    val planLabel = if (selectedPlan == "monthly") "Formule Mensuelle" else "Formule Annuelle (-33%)"

    val providerName = when (paymentMethod) {
        PaymentMethodType.WAVE -> "Wave"
        PaymentMethodType.MOBILE_MONEY -> selectedOperator.displayName
        PaymentMethodType.BANK_CARD -> "Carte Bancaire"
    }

    val providerColor = when (paymentMethod) {
        PaymentMethodType.WAVE -> Color(0xFF1DC3F4)
        PaymentMethodType.MOBILE_MONEY -> selectedOperator.brandColor
        PaymentMethodType.BANK_CARD -> NexoraPurple
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexoraSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = NexoraPurple,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NEXORA Plus",
                        fontWeight = FontWeight.Bold,
                        color = NexoraTextPrimary,
                        fontSize = 18.sp
                    )
                }
                NexoraTag(text = "Simulation Démo", color = NexoraCyan)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Fermer", tint = NexoraTextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (simulationStage) {
                    // STAGE 1 : Configuration & Input
                    SimulationStage.CONFIGURATION -> {
                        // Status banner if already active
                        if (userPrefs.isPlusActive) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = NexoraSuccess.copy(alpha = 0.15f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSuccess),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Abonnement Plus Démo Actif",
                                            fontWeight = FontWeight.Bold,
                                            color = NexoraSuccess,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "Fonctionnalités avancées déverrouillées (${userPrefs.activePlusPlan}).",
                                            color = NexoraTextPrimary,
                                            fontSize = 11.sp
                                        )
                                    }
                                    TextButton(onClick = { viewModel.deactivatePlusDemo() }) {
                                        Text("Réinitialiser", color = NexoraError, fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Testez le parcours complet de paiement Wave et Mobile Money sans débit réel de votre compte.",
                            color = NexoraTextSecondary,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        )

                        // Visual Payment Options Header Showcase
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NexoraPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Options de paiement directes :",
                                        fontWeight = FontWeight.Bold,
                                        color = NexoraTextPrimary,
                                        fontSize = 12.sp
                                    )
                                    NexoraTag(text = "0% de frais", color = NexoraSuccess)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    PaymentShowcaseChip(
                                        name = "Wave",
                                        subtitle = "Instantané",
                                        color = Color(0xFF1DC3F4),
                                        modifier = Modifier.weight(1f)
                                    )
                                    PaymentShowcaseChip(
                                        name = "Orange",
                                        subtitle = "#144#",
                                        color = Color(0xFFFF7900),
                                        modifier = Modifier.weight(1f)
                                    )
                                    PaymentShowcaseChip(
                                        name = "MTN MoMo",
                                        subtitle = "*133#",
                                        color = Color(0xFFFFCC00),
                                        modifier = Modifier.weight(1f)
                                    )
                                    PaymentShowcaseChip(
                                        name = "Moov",
                                        subtitle = "*155#",
                                        color = Color(0xFF007A3D),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // 1. Plan Selector
                        Text("1. Formule d'abonnement :", fontWeight = FontWeight.Bold, color = NexoraCyan, fontSize = 13.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PlanOptionCard(
                                title = "Mensuel",
                                priceEur = "2,49 €",
                                priceFcfa = "1 500 FCFA",
                                period = "par mois",
                                isSelected = selectedPlan == "monthly",
                                onClick = { selectedPlan = "monthly" },
                                modifier = Modifier.weight(1f)
                            )

                            PlanOptionCard(
                                title = "Annuel",
                                priceEur = "19,99 €",
                                priceFcfa = "12 000 FCFA",
                                period = "par an",
                                badge = "-33%",
                                isSelected = selectedPlan == "yearly",
                                onClick = { selectedPlan = "yearly" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // 2. Payment Method Selector
                        Text("2. Moyen de paiement :", fontWeight = FontWeight.Bold, color = NexoraCyan, fontSize = 13.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PaymentTabButton(
                                title = "Wave",
                                subtitle = "Sans frais",
                                color = Color(0xFF1DC3F4),
                                isSelected = paymentMethod == PaymentMethodType.WAVE,
                                onClick = { paymentMethod = PaymentMethodType.WAVE },
                                modifier = Modifier.weight(1f)
                            )

                            PaymentTabButton(
                                title = "Mobile Money",
                                subtitle = "Orange, MTN, Moov",
                                color = Color(0xFFFF7900),
                                isSelected = paymentMethod == PaymentMethodType.MOBILE_MONEY,
                                onClick = { paymentMethod = PaymentMethodType.MOBILE_MONEY },
                                modifier = Modifier.weight(1f)
                            )

                            PaymentTabButton(
                                title = "Carte Bancaire",
                                subtitle = "Visa / Master",
                                color = NexoraPurple,
                                isSelected = paymentMethod == PaymentMethodType.BANK_CARD,
                                onClick = { paymentMethod = PaymentMethodType.BANK_CARD },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // 3. Provider Configuration
                        when (paymentMethod) {
                            PaymentMethodType.WAVE -> {
                                WaveConfigCard(
                                    countryPrefix = countryPrefix,
                                    onCountryPrefixChange = { countryPrefix = it },
                                    phoneNumber = phoneNumber,
                                    onPhoneNumberChange = { phoneNumber = it },
                                    amountFcfa = amountFcfa
                                )
                            }
                            PaymentMethodType.MOBILE_MONEY -> {
                                MobileMoneyConfigCard(
                                    selectedOperator = selectedOperator,
                                    onOperatorChange = { selectedOperator = it },
                                    countryPrefix = countryPrefix,
                                    onCountryPrefixChange = { countryPrefix = it },
                                    phoneNumber = phoneNumber,
                                    onPhoneNumberChange = { phoneNumber = it },
                                    amountFcfa = amountFcfa
                                )
                            }
                            PaymentMethodType.BANK_CARD -> {
                                BankCardConfigCard(amountEur = amountEur)
                            }
                        }

                        // RGPD Assurance Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, NexoraCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .clickable { showGdprPreSimulationDialog = true }
                                .testTag("gdpr_assurance_card"),
                            colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(NexoraCyan.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = NexoraCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Garantie de non-stockage & RGPD",
                                        fontWeight = FontWeight.Bold,
                                        color = NexoraTextPrimary,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Vos données saisies ne sont ni stockées ni transmises à des tiers.",
                                        color = NexoraTextSecondary,
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                                Text(
                                    text = "Notice",
                                    color = NexoraCyan,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Transparent Disclaimer
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = NexoraCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Environnement bac à sable : cette simulation démontre la fluidité de la validation Mobile Money sans aucun frais bancaire.",
                                    color = NexoraTextMuted,
                                    fontSize = 10.5.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    // STAGE 2 : Sending push request to gateway (Simulated API communication)
                    SimulationStage.SENDING_PROMPT -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CircularProgressIndicator(
                                color = providerColor,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                            )

                            Text(
                                text = "Connexion à la passerelle $providerName...",
                                fontWeight = FontWeight.Bold,
                                color = NexoraTextPrimary,
                                fontSize = 15.sp
                            )

                            Text(
                                text = simulatedStepText,
                                color = NexoraCyan,
                                fontSize = 12.sp
                            )

                            LinearProgressIndicator(
                                progress = { simulatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = providerColor,
                                trackColor = NexoraBorder
                            )
                        }
                    }

                    // STAGE 3 : Simulated Push / USSD validation modal (Authentic user interaction)
                    SimulationStage.AWAITING_PIN -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, providerColor, RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = NexoraDarkBg)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Push header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(providerColor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.NotificationsActive,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Notification $providerName",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                    }
                                    NexoraTag(text = "Appareil sécurisé", color = NexoraSuccess)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Demande d'autorisation de paiement",
                                    fontWeight = FontWeight.Bold,
                                    color = NexoraTextPrimary,
                                    fontSize = 14.sp
                                )

                                Card(
                                    colors = CardDefaults.cardColors(containerColor = NexoraSurface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Marchand :", color = NexoraTextSecondary, fontSize = 11.5.sp)
                                            Text("NEXORA Gaming", fontWeight = FontWeight.Bold, color = NexoraCyan, fontSize = 12.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Montant :", color = NexoraTextSecondary, fontSize = 11.5.sp)
                                            Text(amountFcfa, fontWeight = FontWeight.Black, color = NexoraTextPrimary, fontSize = 14.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Compte débité :", color = NexoraTextSecondary, fontSize = 11.5.sp)
                                            Text("$countryPrefix $phoneNumber", color = NexoraTextPrimary, fontSize = 11.5.sp)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text("Frais de service :", color = NexoraTextSecondary, fontSize = 11.5.sp)
                                            Text("0 FCFA (Gratuit)", color = NexoraSuccess, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Text(
                                    text = "Entrez un code PIN fictif (ex: 1234) pour simuler la validation sur votre téléphone :",
                                    color = NexoraTextSecondary,
                                    fontSize = 11.sp
                                )

                                OutlinedTextField(
                                    value = enteredSecretPin,
                                    onValueChange = { if (it.length <= 4) enteredSecretPin = it },
                                    label = { Text("Code Secret (Simulation)") },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    leadingIcon = {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = providerColor)
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = NexoraSurfaceVariant,
                                        unfocusedContainerColor = NexoraSurfaceVariant,
                                        focusedBorderColor = providerColor,
                                        unfocusedBorderColor = NexoraBorder,
                                        focusedTextColor = NexoraTextPrimary,
                                        unfocusedTextColor = NexoraTextPrimary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .testTag("simulation_pin_input")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { simulationStage = SimulationStage.CONFIGURATION },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NexoraTextSecondary)
                                    ) {
                                        Text("Rejeter")
                                    }

                                    Button(
                                        onClick = {
                                            val prefixTag = when (paymentMethod) {
                                                PaymentMethodType.WAVE -> "WAVE-SN"
                                                PaymentMethodType.MOBILE_MONEY -> when (selectedOperator) {
                                                    MobileMoneyOperator.ORANGE_MONEY -> "OM-SN"
                                                    MobileMoneyOperator.MTN_MOMO -> "MOMO-CI"
                                                    MobileMoneyOperator.MOOV_MONEY -> "MOOV-BF"
                                                }
                                                PaymentMethodType.BANK_CARD -> "VISA-3DS"
                                            }
                                            generatedTransactionRef = "$prefixTag-${Random.nextInt(100000, 999999)}"
                                            viewModel.activatePlusDemo(planLabel)
                                            simulationStage = SimulationStage.SUCCESS_RECEIPT
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = providerColor, contentColor = Color.Black),
                                        modifier = Modifier.weight(1.4f)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Autoriser", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // STAGE 4 : Success receipt & Unlocked features
                    SimulationStage.SUCCESS_RECEIPT -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, NexoraSuccess, RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = NexoraSurface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(NexoraSuccess.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = NexoraSuccess,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Text(
                                    text = "Paiement Validé avec Succès !",
                                    fontWeight = FontWeight.Bold,
                                    color = NexoraTextPrimary,
                                    fontSize = 16.sp
                                )

                                Text(
                                    text = "Votre statut NEXORA Plus est maintenant activé en mode démonstration.",
                                    color = NexoraTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                // Official simulated receipt
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        ReceiptRow("Référence", generatedTransactionRef)
                                        ReceiptRow("Service", "NEXORA Plus ($planLabel)")
                                        ReceiptRow("Opérateur", providerName)
                                        ReceiptRow("Montant", amountFcfa)
                                        ReceiptRow("Frais prélevés", "0 FCFA")
                                        val nowStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date())
                                        ReceiptRow("Horodatage", nowStr)
                                        ReceiptRow("Statut", "Confirmé (Mode Test)")
                                    }
                                }

                                Text(
                                    text = "Les fonctionnalités avancées sont actives dans votre session.",
                                    color = NexoraCyan,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            when (simulationStage) {
                SimulationStage.CONFIGURATION -> {
                    Button(
                        onClick = {
                            // Show RGPD explanatory information modal before launching simulation
                            showGdprPreSimulationDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = providerColor, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("launch_payment_simulation_button")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Lancer la simulation $providerName ($amountFcfa)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
                SimulationStage.SENDING_PROMPT -> {
                    // Progressing automatically
                }
                SimulationStage.AWAITING_PIN -> {
                    // Handled inside the push card
                }
                SimulationStage.SUCCESS_RECEIPT -> {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Accéder à NEXORA Plus", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            if (simulationStage == SimulationStage.CONFIGURATION) {
                TextButton(onClick = onDismiss) {
                    Text("Continuer en Gratuit (0 €)", color = NexoraTextSecondary, fontSize = 12.sp)
                }
            }
        }
    )

    // Pre-simulation GDPR & Data Safety Explanatory Modal
    if (showGdprPreSimulationDialog) {
        GdprExplanatoryModal(
            phoneNumber = phoneNumber,
            countryPrefix = countryPrefix,
            providerName = providerName,
            amountFcfa = amountFcfa,
            onDismiss = { showGdprPreSimulationDialog = false },
            onConfirm = {
                showGdprPreSimulationDialog = false
                simulationStage = SimulationStage.SENDING_PROMPT
                simulatedProgress = 0.2f
                simulatedStepText = "Initialisation de la requête vers l'API $providerName..."

                scope.launch {
                    delay(600)
                    simulatedProgress = 0.6f
                    simulatedStepText = "Génération du prompt de confirmation pour $countryPrefix $phoneNumber..."
                    delay(700)
                    simulatedProgress = 1.0f
                    simulatedStepText = "Notification push transmise à votre smartphone !"
                    delay(500)
                    simulationStage = SimulationStage.AWAITING_PIN
                }
            }
        )
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = NexoraTextSecondary, fontSize = 11.sp)
        Text(value, fontWeight = FontWeight.SemiBold, color = NexoraTextPrimary, fontSize = 11.5.sp)
    }
}

@Composable
fun WaveConfigCard(
    countryPrefix: String,
    onCountryPrefixChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    amountFcfa: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1DC3F4).copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1DC3F4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "Wave",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Paiement direct Wave ($amountFcfa)",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1DC3F4),
                    fontSize = 12.5.sp
                )
            }

            Text(
                text = "Entrez le numéro du compte Wave pour déclencher la simulation d'autorisation :",
                color = NexoraTextSecondary,
                fontSize = 11.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CountryPrefixChip("SN (+221)", "+221", countryPrefix == "+221") { onCountryPrefixChange("+221") }
                CountryPrefixChip("CI (+225)", "+225", countryPrefix == "+225") { onCountryPrefixChange("+225") }
                CountryPrefixChip("ML (+223)", "+223", countryPrefix == "+223") { onCountryPrefixChange("+223") }
                CountryPrefixChip("BF (+226)", "+226", countryPrefix == "+226") { onCountryPrefixChange("+226") }
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                placeholder = { Text("Numéro Wave", color = NexoraTextMuted, fontSize = 12.sp) },
                prefix = { Text("$countryPrefix ", color = Color(0xFF1DC3F4), fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = NexoraSurfaceVariant,
                    unfocusedContainerColor = NexoraSurfaceVariant,
                    focusedBorderColor = Color(0xFF1DC3F4),
                    unfocusedBorderColor = NexoraBorder,
                    focusedTextColor = NexoraTextPrimary,
                    unfocusedTextColor = NexoraTextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wave_phone_input")
            )
        }
    }
}

@Composable
fun MobileMoneyConfigCard(
    selectedOperator: MobileMoneyOperator,
    onOperatorChange: (MobileMoneyOperator) -> Unit,
    countryPrefix: String,
    onCountryPrefixChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    amountFcfa: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, selectedOperator.brandColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Opérateur Mobile Money ($amountFcfa) :",
                fontWeight = FontWeight.Bold,
                color = selectedOperator.brandColor,
                fontSize = 12.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MobileMoneyOperator.values().forEach { op ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedOperator == op) op.brandColor.copy(alpha = 0.2f) else NexoraSurfaceVariant)
                            .border(1.dp, if (selectedOperator == op) op.brandColor else NexoraBorder, RoundedCornerShape(8.dp))
                            .clickable { onOperatorChange(op) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = op.displayName,
                            fontWeight = if (selectedOperator == op) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedOperator == op) op.brandColor else NexoraTextPrimary,
                            fontSize = 10.5.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CountryPrefixChip("SN (+221)", "+221", countryPrefix == "+221") { onCountryPrefixChange("+221") }
                CountryPrefixChip("CI (+225)", "+225", countryPrefix == "+225") { onCountryPrefixChange("+225") }
                CountryPrefixChip("CM (+237)", "+237", countryPrefix == "+237") { onCountryPrefixChange("+237") }
                CountryPrefixChip("GN (+224)", "+224", countryPrefix == "+224") { onCountryPrefixChange("+224") }
            }

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                placeholder = { Text("Numéro Mobile Money", color = NexoraTextMuted, fontSize = 12.sp) },
                prefix = { Text("$countryPrefix ", color = selectedOperator.brandColor, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = NexoraSurfaceVariant,
                    unfocusedContainerColor = NexoraSurfaceVariant,
                    focusedBorderColor = selectedOperator.brandColor,
                    unfocusedBorderColor = NexoraBorder,
                    focusedTextColor = NexoraTextPrimary,
                    unfocusedTextColor = NexoraTextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("momo_phone_input")
            )
        }
    }
}

@Composable
fun BankCardConfigCard(amountEur: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NexoraPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CreditCard, contentDescription = null, tint = NexoraPurple, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Carte bancaire internationale ($amountEur)",
                    fontWeight = FontWeight.Bold,
                    color = NexoraPurple,
                    fontSize = 12.5.sp
                )
            }
            Text(
                text = "Protocole 3D-Secure simulé avec code d'autorisation instantané.",
                color = NexoraTextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun PlanOptionCard(
    title: String,
    priceEur: String,
    priceFcfa: String,
    period: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    badge: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(
                1.dp,
                if (isSelected) NexoraCyan else NexoraBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) NexoraSurfaceElevated else NexoraSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (badge != null) {
                NexoraTag(text = badge, color = NexoraPurple)
                Spacer(modifier = Modifier.height(2.dp))
            }
            Text(title, fontWeight = FontWeight.SemiBold, color = NexoraTextSecondary, fontSize = 11.sp)
            Text(
                text = priceFcfa,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) NexoraCyan else NexoraTextPrimary,
                fontSize = 13.sp
            )
            Text("($priceEur)", color = NexoraTextMuted, fontSize = 10.sp)
            Text(period, color = NexoraTextMuted, fontSize = 9.5.sp)
        }
    }
}

@Composable
fun PaymentTabButton(
    title: String,
    subtitle: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) color.copy(alpha = 0.2f) else NexoraSurfaceVariant)
            .border(1.dp, if (isSelected) color else NexoraBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else NexoraTextPrimary,
                fontSize = 11.sp
            )
            Text(
                text = subtitle,
                color = NexoraTextMuted,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun CountryPrefixChip(
    label: String,
    prefix: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) NexoraCyan.copy(alpha = 0.2f) else NexoraSurfaceVariant)
            .border(1.dp, if (isSelected) NexoraCyan else NexoraBorder, RoundedCornerShape(6.dp))
            .clickable { onSelect() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) NexoraCyan else NexoraTextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun GdprExplanatoryModal(
    phoneNumber: String,
    countryPrefix: String,
    providerName: String,
    amountFcfa: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexoraSurface,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(NexoraCyan.copy(alpha = 0.15f))
                        .border(1.dp, NexoraCyan.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Sécurité RGPD",
                        tint = NexoraCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Protection des données & RGPD",
                        fontWeight = FontWeight.Bold,
                        color = NexoraTextPrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Garantie de non-stockage & non-transmission",
                        color = NexoraTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Avant de lancer la simulation de paiement pour $providerName ($amountFcfa), voici nos engagements stricts de confidentialité :",
                    color = NexoraTextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = NexoraSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GdprBulletItem(
                            title = "1. Non-stockage absolu des données",
                            description = "Le numéro saisi ($countryPrefix $phoneNumber) et les codes PIN simulés restent temporairement dans la mémoire volatile (RAM) de l'appareil et ne sont jamais enregistrés sur disque ni dans une base de données."
                        )

                        GdprBulletItem(
                            title = "2. Aucune transmission à des tiers",
                            description = "Aucune information n'est communiquée aux serveurs de Wave, Orange, MTN, ni à aucun prestataire bancaire ou réseau publicitaire tiers."
                        )

                        GdprBulletItem(
                            title = "3. Conformité RGPD & Minimisation",
                            description = "Conformément au Règlement Général sur la Protection des Données (RGPD), cette démonstration applique les principes stricts de minimisation et de respect de la vie privée (Privacy by Design)."
                        )

                        GdprBulletItem(
                            title = "4. Zéro débit réel garanti",
                            description = "Il s'agit d'une simulation interactive éducative conçue pour valider l'expérience utilisateur sans aucun impact financier."
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = NexoraCyan, contentColor = NexoraDarkBg),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gdpr_confirm_simulation_button")
            ) {
                Text("J'ai compris, lancer la simulation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Annuler et modifier les informations", color = NexoraTextSecondary, fontSize = 12.sp)
            }
        }
    )
}

@Composable
fun GdprBulletItem(
    title: String,
    description: String
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = NexoraSuccess,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = NexoraTextPrimary,
                fontSize = 11.5.sp
            )
        }
        Text(
            text = description,
            color = NexoraTextSecondary,
            fontSize = 10.5.sp,
            lineHeight = 14.sp,
            modifier = Modifier.padding(start = 20.dp, top = 2.dp)
        )
    }
}

@Composable
fun PaymentShowcaseChip(
    name: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(vertical = 6.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 10.5.sp
            )
            Text(
                text = subtitle,
                color = NexoraTextMuted,
                fontSize = 8.5.sp
            )
        }
    }
}
