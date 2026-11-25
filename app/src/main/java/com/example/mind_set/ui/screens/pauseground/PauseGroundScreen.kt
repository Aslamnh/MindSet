package com.example.mind_set.ui.screens.pauseground

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mind_set.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PauseGroundScreen(onBack: () -> Unit) {
    var timeElapsed by remember { mutableStateOf(46) } // start at 46s for demo
    val totalTime = 240
    var isTimerRunning by remember { mutableStateOf(true) }
    var intensityRating by remember { mutableStateOf(4) }
    var selectedTrigger by remember { mutableStateOf("Kesepian/Sendirian") }

    // Countdown timer
    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timeElapsed < totalTime) {
            delay(1000L)
            timeElapsed++
        }
    }

    // Breathing phase (16s cycle: 4s inhale, 4s hold, 4s exhale, 4s rest)
    val breathPhase = timeElapsed % 16
    val breathText = when {
        breathPhase < 4  -> "Tarik Napas"
        breathPhase < 8  -> "Tahan"
        breathPhase < 12 -> "Hembuskan"
        else             -> "Istirahat"
    }
    val breathCount = (breathPhase % 4) + 1

    // Breathing circle animation
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val circleScale by infiniteTransition.animateFloat(
        initialValue   = 0.75f,
        targetValue    = 1.0f,
        animationSpec  = infiniteRepeatable(
            animation  = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circleScale"
    )

    fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }
    fun formatTotal(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MindSetBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top bar ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MindSetNeutral)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Pause & Ground", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MindSetNeutral)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MindSetError)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text  = "Intervensi Krisis Cepat (24/7 Siaga)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetError
                    )
                }
            }
        }

        // ── Main heading ──────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text  = "Tarik Napas... Kamu Berada di Ruang yang Aman",
                style = MaterialTheme.typography.headlineSmall,
                color = MindSetNeutral,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Dorongan ini bersifat sementara dan akan mereda dalam 15–20 menit. Kami di sini menemanimu melewati gelombang ini selangkah demi langkah.",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey
            )

            Spacer(Modifier.height(12.dp))

            // Timer badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = MindSetPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text  = "Box Breathing 4-4-4-4",
                    style = MaterialTheme.typography.labelMedium,
                    color = MindSetPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text  = "${formatTime(timeElapsed)} / ${formatTotal(totalTime)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Breathing circle ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow ring
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(circleScale * 1.1f)
                    .clip(CircleShape)
                    .background(MindSetPrimaryContainer.copy(alpha = 0.5f))
            )
            // Inner circle
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(circleScale)
                    .clip(CircleShape)
                    .background(MindSetPrimary),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text  = breathText,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text  = "${breathCount}dtk",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text      = "Tarik perlahan lewat hidung... rasakan udara sejuk masuk mengisi rongga dadamu.",
            style     = MaterialTheme.typography.bodySmall,
            color     = MindSetNeutralGrey,
            textAlign = TextAlign.Center,
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        )

        Spacer(Modifier.height(24.dp))

        // ── Questionnaire ─────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text  = "Kuesioner Singkat Intensitas",
                    style = MaterialTheme.typography.titleSmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(16.dp))

                // Question 1 - intensity
                Text(
                    text  = "1. Seberapa kuat dorongan/kecemasan saat ini?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Pengukuran objektif membantu menurunkan aktivasi sistem alarm patuh.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MindSetNeutralGrey
                )
                Spacer(Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val labels = listOf("1\nRingan", "2\nSedang", "3\nKuat", "4★\nSangat\nKuat", "5\nPuncak")
                    labels.forEachIndexed { index, label ->
                        val rating = index + 1
                        val isSelected = intensityRating == rating
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { intensityRating = rating }
                                .border(
                                    1.dp,
                                    if (isSelected) MindSetPrimary else MindSetDivider,
                                    RoundedCornerShape(8.dp)
                                ),
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MindSetPrimaryContainer else MindSetSurface
                        ) {
                            Text(
                                text      = label,
                                style     = MaterialTheme.typography.labelSmall,
                                color     = if (isSelected) MindSetPrimary else MindSetNeutralGrey,
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier  = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Question 2 - trigger
                Text(
                    text  = "2. Apa pemicu utama saat ini?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Pilih 1 atau lebih faktor yang sering memicu respons somatik tubuhmu:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MindSetNeutralGrey
                )
                Spacer(Modifier.height(10.dp))

                val triggers = listOf(
                    "😔 Kesepian/Sendirian",
                    "📚 Stres Tugas/Tekanan",
                    "🌙 Bosan Layar/Malam",
                    "📱 Pemicu Visual Konten",
                    "😴 Kelelahan Fisik"
                )
                triggers.forEach { trigger ->
                    val isSelected = selectedTrigger == trigger
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { selectedTrigger = trigger }
                            .border(
                                1.dp,
                                if (isSelected) MindSetPrimary else MindSetDivider,
                                RoundedCornerShape(8.dp)
                            ),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MindSetPrimaryContainer else MindSetSurface
                    ) {
                        Text(
                            text     = trigger,
                            style    = MaterialTheme.typography.bodySmall,
                            color    = if (isSelected) MindSetPrimary else MindSetNeutral,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Quick Steps ───────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text  = "Langkah Penanganan Cepat",
                    style = MaterialTheme.typography.titleSmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text  = "Rekomendasi Konsensus Berbasis CBT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MindSetNeutralGrey
                )
                Spacer(Modifier.height(14.dp))

                val steps = listOf(
                    Triple("1", "Basuh wajah dengan air dingin",
                        "Merangsang refleks selam mamalia (mammalian dive reflex) untuk menstabilkan detak jantung."),
                    Triple("2", "Pindah ruangan & beri jarak gadget",
                        "Segera berdiri, tinggalkan smartphone di meja selama 10 menit, dan jalan/perhatikan ke ruang terbuka/jendela."),
                    Triple("3", "Kontak Teman Pendamping / Konselor",
                        "Kamu tidak sendirian. Berbicara singkat dengan orang terpercaya membantu memutus putaran pikiran obsesif.")
                )

                steps.forEach { (num, title, desc) ->
                    Row(modifier = Modifier.padding(vertical = 6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MindSetPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(num, style = MaterialTheme.typography.labelMedium, color = MindSetPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(title, style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(2.dp))
                            Text(desc, style = MaterialTheme.typography.bodySmall, color = MindSetNeutralGrey)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Calming quote ─────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetPrimaryContainer)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text  = "REFLEKSI MENENANGKAN",
                    style = MaterialTheme.typography.labelSmall,
                    color = MindSetPrimary,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "\"Pikiranmu adalah gelombang di lautan kesadaran. Kamu tidak harus membuat mereka menjadi...\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Success button ────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Button(
                onClick  = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MindSetTertiary)
            ) {
                Text(
                    text  = "🎉 Saya Berhasil Melewati Dorongan Ini!",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(10.dp))

            TextButton(
                onClick  = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text  = "📞 Butuh Kontak Konselor Darurat 119",
                    style = MaterialTheme.typography.labelMedium,
                    color = MindSetError
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text      = "Layanan darurat terhubung langsung dengan Hotline Kemenkes RI (Bebas Pulsa)",
                style     = MaterialTheme.typography.labelSmall,
                color     = MindSetNeutralGrey,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}
