package com.example.mind_set.ui.screens.pauseground

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mind_set.ui.emergency.EmergencyViewModel
import com.example.mind_set.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PauseGroundScreen(
    viewModel: EmergencyViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // State Timer Box Breathing (4 menit = 240 detik) — alat bantu sekunder setelah saran muncul
    var isBreathingExpanded by remember { mutableStateOf(false) }
    var timeElapsed by remember { mutableIntStateOf(0) }
    val totalTime = 240
    var isTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isTimerRunning, isBreathingExpanded) {
        while (isBreathingExpanded && isTimerRunning && timeElapsed < totalTime) {
            delay(1000L)
            timeElapsed++
        }
    }

    // Siklus 16 detik: 4s Tarik, 4s Tahan, 4s Hembuskan, 4s Istirahat
    val breathPhase = timeElapsed % 16
    val (breathText, breathGuide) = when {
        breathPhase < 4  -> "Tarik Napas" to "Tarik perlahan lewat hidung selama 4 detik..."
        breathPhase < 8  -> "Tahan" to "Tahan napas dengan rileks, bahu tetap turun..."
        breathPhase < 12 -> "Hembuskan" to "Hembuskan perlahan lewat mulut hingga paru-paru kosong..."
        else             -> "Istirahat" to "Jeda sejenak sebelum siklus napas berikutnya..."
    }
    val breathCount = (breathPhase % 4) + 1

    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val circleScale by infiniteTransition.animateFloat(
        initialValue = 0.78f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circleScale"
    )

    fun formatTime(seconds: Int): String = "%02d:%02d".format(seconds / 60, seconds % 60)

    fun dialHotline119() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"))
        context.startActivity(intent)
    }

    Scaffold(
        containerColor = MindSetBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MindSetBackground),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MindSetNeutral
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = "Pause & Ground",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MindSetNeutral
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(MindSetError)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "Intervensi Krisis Cepat • 24/7 Siaga",
                                style = MaterialTheme.typography.labelSmall,
                                color = MindSetError
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ════════════════════════════════════════════════════════════════
            // PHASE 3: Sesi Selesai — Pesan Penutup & Kembali ke Beranda
            // ════════════════════════════════════════════════════════════════
            if (uiState.isSessionCompleted) {
                SessionCompletedSection(
                    closingMessage = uiState.closingMessage,
                    onBackToHome = onBack
                )
            } else {
                // ── 1. Header Penenang ─────────────────────────────────────
                Column {
                    Text(
                        text = "Kamu Berada di Ruang yang Aman",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MindSetNeutral,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Dorongan ini hanyalah gelombang sementara. Isi evaluasi singkat di bawah untuk mendapatkan saran CBT personal dari AI.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutralGrey,
                        lineHeight = 18.sp
                    )
                }

                // ── 2. Fokus Utama: Kuesioner Singkat CBT ─────────────────
                AnimatedVisibility(
                    visible = !uiState.isReflectionPhase,
                    exit = fadeOut() + shrinkVertically()
                ) {
                    AssessmentCard(
                        urgeLevel = uiState.urgeLevel,
                        selectedTrigger = uiState.selectedTrigger,
                        additionalNote = uiState.additionalNote,
                        hasIntervention = uiState.currentIntervention != null,
                        isLoading = uiState.isLoading,
                        onUrgeLevelChange = { viewModel.updateUrgeLevel(it) },
                        onTriggerSelect = { viewModel.selectTrigger(it) },
                        onAdditionalNoteChange = { viewModel.updateAdditionalNote(it) },
                        onSubmit = { viewModel.submitEmergencyAssessment() }
                    )
                }

                // ── 3. Fokus Utama: Hasil Rekomendasi CBT AI ──────────────
                val intervention = uiState.currentIntervention
                AnimatedVisibility(
                    visible = intervention != null && !uiState.isReflectionPhase,
                    enter = fadeIn() + expandVertically()
                ) {
                    intervention?.let { result ->
                        InterventionResultCard(
                            result = result,
                            retryCount = uiState.retryCount
                        )
                    }
                }

                // ── 4. Alat Bantu Sekunder: Box Breathing (muncul setelah saran ada) ──
                AnimatedVisibility(
                    visible = intervention != null && !uiState.isReflectionPhase,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    BreathingCard(
                        isExpanded = isBreathingExpanded,
                        onToggleExpand = { isBreathingExpanded = !isBreathingExpanded },
                        isTimerRunning = isTimerRunning,
                        onToggleTimer = { isTimerRunning = !isTimerRunning },
                        onResetTimer = {
                            timeElapsed = 0
                            isTimerRunning = true
                        },
                        breathText = breathText,
                        breathGuide = breathGuide,
                        breathCount = breathCount,
                        circleScale = circleScale,
                        timeElapsed = timeElapsed,
                        totalTime = totalTime,
                        formatTime = ::formatTime
                    )
                }

                // ── 5. Refleksi Menenangkan (kutipan) ─────────────────────
                AnimatedVisibility(
                    visible = !uiState.isReflectionPhase,
                    exit = fadeOut() + shrinkVertically()
                ) {
                    CalmingReflectionCard()
                }

                // ════════════════════════════════════════════════════════════
                // PHASE 2: Form Refleksi Detail Krisis (muncul saat retryCount >= 3)
                // ════════════════════════════════════════════════════════════
                AnimatedVisibility(
                    visible = uiState.isReflectionPhase,
                    enter = fadeIn() + expandVertically()
                ) {
                    ReflectionFormCard(
                        reflectionText = uiState.reflectionText,
                        isLoading = uiState.isLoading,
                        retryCount = uiState.retryCount,
                        onReflectionTextChange = { viewModel.updateReflectionText(it) },
                        onSubmitReflection = { viewModel.submitReflectionAndClose() }
                    )
                }

                // ── Error Message ─────────────────────────────────────────
                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MindSetError.copy(alpha = 0.08f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.ErrorOutline,
                                contentDescription = null,
                                tint = MindSetError,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MindSetError
                            )
                        }
                    }
                }

                // ── 6. Aksi Penyelesaian & Hotline ────────────────────────
                ActionButtonsSection(
                    uiState = uiState,
                    onSuccess = {
                        viewModel.markSessionSuccess()
                        onBack()
                    },
                    onRetry = { viewModel.retryWithAlternativeGuidance() },
                    onDialHotline = ::dialHotline119
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// SUB-COMPOSABLES
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun BreathingCard(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    isTimerRunning: Boolean,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    breathText: String,
    breathGuide: String,
    breathCount: Int,
    circleScale: Float,
    timeElapsed: Int,
    totalTime: Int,
    formatTime: (Int) -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetSurface),
        border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MindSetPrimaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Air,
                            contentDescription = null,
                            tint = MindSetPrimary,
                            modifier = Modifier.padding(8.dp).size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Alat Bantu: Box Breathing 4-4-4-4",
                            style = MaterialTheme.typography.titleSmall,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isExpanded) "Ketuk untuk menyembunyikan" else "Opsional • Ketuk untuk menenangkan napas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Tutup" else "Buka",
                    tint = MindSetNeutralGrey
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${formatTime(timeElapsed)} / ${formatTime(totalTime)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalIconButton(
                                onClick = onToggleTimer,
                                modifier = Modifier.size(34.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MindSetPrimaryContainer,
                                    contentColor = MindSetPrimary
                                )
                            ) {
                                Icon(
                                    imageVector = if (isTimerRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (isTimerRunning) "Jeda Timer" else "Lanjut Timer",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            FilledTonalIconButton(
                                onClick = onResetTimer,
                                modifier = Modifier.size(34.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MindSetBackground,
                                    contentColor = MindSetNeutralGrey
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Ulang Timer",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Box(
                        modifier = Modifier.size(156.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(148.dp)
                                .scale(circleScale * 1.08f)
                                .clip(CircleShape)
                                .background(MindSetPrimaryContainer.copy(alpha = 0.55f))
                        )
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(circleScale)
                                .clip(CircleShape)
                                .background(MindSetPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = breathText,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$breathCount dtk",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = breathGuide,
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutralGrey,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AssessmentCard(
    urgeLevel: Int,
    selectedTrigger: String,
    additionalNote: String,
    hasIntervention: Boolean,
    isLoading: Boolean,
    onUrgeLevelChange: (Int) -> Unit,
    onTriggerSelect: (String) -> Unit,
    onAdditionalNoteChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetSurface),
        border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Evaluasi Kondisi Saat Ini",
                style = MaterialTheme.typography.titleSmall,
                color = MindSetNeutral,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Bantu sistem menyesuaikan langkah intervensi CBT untukmu.",
                style = MaterialTheme.typography.labelSmall,
                color = MindSetNeutralGrey
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "1. Seberapa kuat dorongan saat ini?",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutral,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))

            val scaleLabels = listOf("Ringan", "Sedang", "Kuat", "Tinggi", "Puncak")
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                scaleLabels.forEachIndexed { index, label ->
                    val rating = index + 1
                    val isSelected = urgeLevel == rating
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onUrgeLevelChange(rating) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MindSetPrimary else MindSetBackground,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) MindSetPrimary else MindSetDivider
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$rating",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MindSetNeutral
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = if (isSelected) Color.White.copy(alpha = 0.9f) else MindSetNeutralGrey
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text = "2. Apa pemicu utama yang kamu rasakan?",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutral,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))

            val triggerOptions = listOf(
                "😔 Kesepian",
                "📚 Stres Tugas",
                "🌙 Bosan",
                "📱 Pemicu Visual",
                "😴 Lelah"
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                triggerOptions.forEach { trigger ->
                    val isSelected = selectedTrigger == trigger
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTriggerSelect(trigger) },
                        label = {
                            Text(
                                text = trigger,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MindSetPrimaryContainer,
                            selectedLabelColor = MindSetPrimary,
                            containerColor = MindSetBackground,
                            labelColor = MindSetNeutral
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = MindSetDivider,
                            selectedBorderColor = MindSetPrimary
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "3. Ceritakan singkat kondisimu (opsional)",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutral,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = additionalNote,
                onValueChange = onAdditionalNoteChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
                placeholder = {
                    Text(
                        text = "Misal: \"Baru selesai kerja, sendirian di kamar, rasanya ingin buka konten lagi...\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutralLight
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MindSetPrimary,
                    unfocusedBorderColor = MindSetDivider,
                    focusedContainerColor = MindSetBackground.copy(alpha = 0.5f),
                    unfocusedContainerColor = MindSetBackground.copy(alpha = 0.3f)
                ),
                textStyle = MaterialTheme.typography.bodySmall.copy(color = MindSetNeutral)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onSubmit,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MindSetSecondary,
                    contentColor = MindSetNeutral
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MindSetNeutral
                    )
                } else {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (hasIntervention) "Perbarui Panduan CBT AI" else "Dapatkan Panduan CBT AI",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun InterventionResultCard(
    result: com.example.mind_set.data.model.EmergencyIntervention,
    retryCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetSurface),
        border = BorderStroke(1.5.dp, MindSetPrimary.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (result.sourceType == "GEMINI_AI") MindSetPrimaryContainer else MindSetBackground
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (result.sourceType == "GEMINI_AI") Icons.Filled.AutoAwesome else Icons.Filled.Shield,
                            contentDescription = null,
                            tint = if (result.sourceType == "GEMINI_AI") MindSetPrimary else MindSetNeutralGrey,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (result.sourceType == "GEMINI_AI") "Gemini AI" else "CBT Offline",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (result.sourceType == "GEMINI_AI") MindSetPrimary else MindSetNeutralGrey,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MindSetPrimaryContainer
                ) {
                    Text(
                        text = result.urgencyStatus,
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = result.cbtTechniqueTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MindSetPrimary,
                fontWeight = FontWeight.Bold
            )

            if (retryCount > 0) {
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Panduan Alternatif #$retryCount • Fokus Regulasi Intensif",
                    style = MaterialTheme.typography.labelSmall,
                    color = MindSetTertiary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))

            result.recommendationSteps.forEachIndexed { idx, stepText ->
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MindSetPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stepText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutral,
                        lineHeight = 18.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun CalmingReflectionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetPrimaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "REFLEKSI MENENANGKAN",
                style = MaterialTheme.typography.labelSmall,
                color = MindSetPrimary,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "\"Pikiranmu hanyalah gelombang di permukaan lautan kesadaran. Kamu tidak harus mengikuti setiap gelombang yang datang.\"",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutral,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        }
    }
}

/**
 * Phase 2: Form Refleksi Detail Krisis
 * Muncul setelah 3x retry panduan CBT tidak berhasil.
 */
@Composable
private fun ReflectionFormCard(
    reflectionText: String,
    isLoading: Boolean,
    retryCount: Int,
    onReflectionTextChange: (String) -> Unit,
    onSubmitReflection: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetSurface),
        border = BorderStroke(1.5.dp, MindSetSecondary.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = null,
                    tint = MindSetSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Refleksi Detail Krisis",
                    style = MaterialTheme.typography.titleSmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Kamu sudah mencoba $retryCount panduan dan dorongan masih terasa. " +
                        "Tidak apa-apa — mari kita coba cara lain. Ceritakan dengan kata-katamu sendiri " +
                        "apa yang sedang kamu rasakan atau alami saat ini.",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey,
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(14.dp))

            OutlinedTextField(
                value = reflectionText,
                onValueChange = onReflectionTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                placeholder = {
                    Text(
                        text = "Contoh: \"Saya merasa sangat bosan dan kesepian malam ini. " +
                                "Pikiran saya terus kembali ke konten itu karena tidak ada hal lain " +
                                "yang bisa mengalihkan...\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutralLight
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MindSetPrimary,
                    unfocusedBorderColor = MindSetDivider,
                    focusedContainerColor = MindSetBackground.copy(alpha = 0.5f),
                    unfocusedContainerColor = MindSetBackground.copy(alpha = 0.3f)
                ),
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = MindSetNeutral,
                    lineHeight = 20.sp
                )
            )

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = onSubmitReflection,
                enabled = reflectionText.isNotBlank() && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MindSetPrimary,
                    disabledContainerColor = MindSetPrimary.copy(alpha = 0.4f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Icon(
                        Icons.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Kirim & Selesaikan Sesi",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Ceritamu akan disimpan secara aman di perangkatmu dan tidak dibagikan tanpa izin.",
                style = MaterialTheme.typography.labelSmall,
                color = MindSetNeutralGrey,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Phase 3: Sesi Selesai — Pesan Penutup CBT Empatik
 */
@Composable
private fun SessionCompletedSection(
    closingMessage: String,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Ikon check besar
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MindSetTertiaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Sesi Selesai",
                tint = MindSetTertiary,
                modifier = Modifier.size(48.dp)
            )
        }

        Text(
            text = "Sesi Selesai",
            style = MaterialTheme.typography.headlineSmall,
            color = MindSetNeutral,
            fontWeight = FontWeight.Bold
        )

        // Pesan penutup empatik
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetPrimaryContainer.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = MindSetPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "PESAN UNTUK KAMU",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetPrimary,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = closingMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MindSetNeutral,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Tombol kembali ke Beranda
        Button(
            onClick = onBackToHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MindSetTertiary)
        ) {
            Icon(
                Icons.Filled.Home,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Kembali ke Beranda",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Refleksimu telah disimpan dengan aman.",
            style = MaterialTheme.typography.labelSmall,
            color = MindSetNeutralGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Bottom action buttons: Berhasil, Retry / Refleksi, Hotline
 */
@Composable
private fun ActionButtonsSection(
    uiState: com.example.mind_set.ui.emergency.EmergencyUiState,
    onSuccess: () -> Unit,
    onRetry: () -> Unit,
    onDialHotline: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Tombol "Saya Berhasil"
        if (!uiState.isReflectionPhase) {
            Button(
                onClick = onSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MindSetTertiary)
            ) {
                Text(
                    text = "🎉 Saya Berhasil Melewati Dorongan Ini!",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tombol "Belum Mempan? Coba Panduan Lain" (hanya tampil saat retryCount < 3)
        if (uiState.currentIntervention != null && !uiState.isReflectionPhase) {
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, MindSetPrimary)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = MindSetPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (uiState.retryCount >= 2)
                        "Masih Belum Mempan? Langkah Terakhir..."
                    else
                        "Belum Mempan? Coba Panduan Lain",
                    style = MaterialTheme.typography.labelLarge,
                    color = MindSetPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Hotline 119
        if (uiState.retryCount >= 2 || uiState.isReflectionPhase) {
            Button(
                onClick = onDialHotline,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MindSetError.copy(alpha = 0.12f),
                    contentColor = MindSetError
                )
            ) {
                Text(
                    text = "📞 Dorongan Masih Kuat? Hubungi Konselor 119",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            TextButton(
                onClick = onDialHotline,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "📞 Hubungi Layanan Konseling Darurat 119",
                    style = MaterialTheme.typography.labelMedium,
                    color = MindSetError,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Text(
            text = "Terhubung langsung dengan Layanan SEJIWA / Kemenkes RI (Bebas Pulsa)",
            style = MaterialTheme.typography.labelSmall,
            color = MindSetNeutralGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
