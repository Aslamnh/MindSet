package com.example.mind_set.ui.screens.pauseground

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
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

    // State Timer Box Breathing (4 menit = 240 detik)
    var timeElapsed by remember { mutableIntStateOf(0) }
    val totalTime = 240
    var isTimerRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timeElapsed < totalTime) {
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
                },
                actions = {
                    FilledTonalIconButton(
                        onClick = { isTimerRunning = !isTimerRunning },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MindSetPrimaryContainer,
                            contentColor = MindSetPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (isTimerRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isTimerRunning) "Jeda Timer" else "Lanjut Timer"
                        )
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
            // ── 1. Header Penenang ───────────────────────────────────────
            Column {
                Text(
                    text = "Kamu Berada di Ruang yang Aman",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Dorongan ini hanyalah gelombang sementara dan akan mereda dalam 15–20 menit. Ikuti ritme napas di bawah ini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutralGrey,
                    lineHeight = 18.sp
                )
            }

            // ── 2. Kartu Box Breathing ───────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MindSetSurface),
                border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MindSetPrimaryContainer
                        ) {
                            Text(
                                text = "Box Breathing 4-4-4-4",
                                style = MaterialTheme.typography.labelMedium,
                                color = MindSetPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "${formatTime(timeElapsed)} / ${formatTime(totalTime)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(172.dp)
                                .scale(circleScale * 1.08f)
                                .clip(CircleShape)
                                .background(MindSetPrimaryContainer.copy(alpha = 0.55f))
                        )
                        Box(
                            modifier = Modifier
                                .size(138.dp)
                                .scale(circleScale)
                                .clip(CircleShape)
                                .background(MindSetPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = breathText,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${breathCount} dtk",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = breathGuide,
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutralGrey,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            // ── 3. Kuesioner Singkat CBT ─────────────────────────────────
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
                            val isSelected = uiState.urgeLevel == rating
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.updateUrgeLevel(rating) },
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
                            val isSelected = uiState.selectedTrigger == trigger
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectTrigger(trigger) },
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

                    Button(
                        onClick = { viewModel.submitEmergencyAssessment() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MindSetSecondary,
                            contentColor = MindSetNeutral
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MindSetNeutral
                            )
                        } else {
                            Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Tampilkan Panduan CBT Personal",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ── 4. Hasil Rekomendasi CBT Dinamis ─────────────────────────
            val intervention = uiState.currentIntervention
            AnimatedVisibility(
                visible = intervention != null,
                enter = fadeIn() + expandVertically()
            ) {
                intervention?.let { result ->
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
                                Text(
                                    text = result.cbtTechniqueTitle,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MindSetPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(8.dp))
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

                            if (uiState.retryCount > 0) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "Panduan Alternatif #${uiState.retryCount} • Fokus Regulasi Intensif",
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
            }

            // ── 5. Refleksi Menenangkan ──────────────────────────────────
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

            // ── 6. Aksi Penyelesaian, Coba Panduan Lain, & Hotline ───────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (uiState.currentIntervention == null) {
                            viewModel.submitEmergencyAssessment()
                        }
                        onBack()
                    },
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

                if (uiState.currentIntervention != null) {
                    OutlinedButton(
                        onClick = { viewModel.retryWithAlternativeGuidance() },
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
                            text = "Belum Mempan? Coba Panduan Lain",
                            style = MaterialTheme.typography.labelLarge,
                            color = MindSetPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (uiState.retryCount >= 2) {
                    Button(
                        onClick = { dialHotline119() },
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
                        onClick = { dialHotline119() },
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
    }
}