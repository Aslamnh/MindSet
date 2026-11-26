package com.example.mind_set.ui.screens.profil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mind_set.data.model.EmergencyIntervention
import com.example.mind_set.data.model.JournalItem
import com.example.mind_set.data.repository.UserRepository
import com.example.mind_set.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfilScreen(
    currentUsername: String = "Alex",
    emergencyHistory: List<EmergencyIntervention> = emptyList(),
    journalHistory: List<JournalItem> = emptyList(),
    userRepository: UserRepository? = null,
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Semua", "Jurnal", "Kuis/Darurat", "Milestone")

    // Password Edit State
    var isEditingPassword by remember { mutableStateOf(false) }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessMessage by remember { mutableStateOf(false) }
    var isSavingPassword by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Dialog States
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Konversi data asli Room DB (emergency_logs) menjadi LogEntry UI
    val dynamicEmergencyEntries = remember(emergencyHistory) {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH.mm 'WIB'", Locale("id", "ID"))
        emergencyHistory.map { item ->
            LogEntry(
                date = formatter.format(Date(item.createdAt)),
                category = "DARURAT",
                type = "🚨 Intervensi Krisis Kilat",
                title = item.cbtTechniqueTitle,
                body = item.recommendationSteps.firstOrNull()
                    ?: "Berhasil menyelesaikan panduan penenangan darurat CBT.",
                tags = "Intensitas Level ${item.urgeLevel} • ${item.triggerCategory}",
                badge = "Tantangan Teratasi",
                isMilestone = false
            )
        }
    }

    // Konversi data asli Room DB (journals) menjadi LogEntry UI
    val dynamicJournalEntries = remember(journalHistory) {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH.mm 'WIB'", Locale("id", "ID"))
        journalHistory.map { item ->
            LogEntry(
                date = formatter.format(Date(item.createdAt)),
                category = "JURNAL",
                type = "📝 Refleksi Harian (${item.mood})",
                title = item.triggerText.take(40) + if (item.triggerText.length > 40) "..." else "",
                body = item.reframingText.ifBlank { item.aiResponse },
                tags = if (item.tags.isNotEmpty()) item.tags.joinToString(" • ") else "#JurnalHarian • #CBT",
                badge = item.status,
                isMilestone = false
            )
        }
    }

    // Milestone dinamis berdasarkan pencapaian asli pengguna
    val dynamicMilestoneEntries = remember(journalHistory, emergencyHistory) {
        val list = mutableListOf<LogEntry>()
        val formatter = SimpleDateFormat("dd MMM yyyy, HH.mm 'WIB'", Locale("id", "ID"))

        if (journalHistory.isNotEmpty()) {
            val firstJournal = journalHistory.last()
            list.add(
                LogEntry(
                    date = formatter.format(Date(firstJournal.createdAt)),
                    category = "MILESTONE",
                    type = "🧘 Pencapaian Langkah Pertama",
                    title = "Jurnal Pemulihan Pertama",
                    body = "Berhasil membuat refleksi harian pertama untuk mengenali pola pemicu kognitif.",
                    tags = "#LangkahAwal • #MilestoneCBT",
                    badge = "Milestone",
                    isMilestone = true
                )
            )
        }

        if (journalHistory.size >= 7) {
            val seventhJournal = journalHistory[journalHistory.size - 7]
            list.add(
                LogEntry(
                    date = formatter.format(Date(seventhJournal.createdAt)),
                    category = "MILESTONE",
                    type = "🧘 Pencapaian Fase 2",
                    title = "7 Hari Kesadaran Diri Terjaga",
                    body = "Konsisten mencatat refleksi harian dan mengidentifikasi pola pemicu emosional.",
                    tags = "#IdentitasDiri • #MilestoneCBT",
                    badge = "Milestone",
                    isMilestone = true
                )
            )
        }

        list
    }

    val filteredEntries = remember(selectedTab, dynamicEmergencyEntries, dynamicJournalEntries, dynamicMilestoneEntries) {
        val allEntries = dynamicEmergencyEntries + dynamicJournalEntries + dynamicMilestoneEntries
        when (selectedTab) {
            1 -> allEntries.filter { it.category == "JURNAL" }
            2 -> allEntries.filter { it.category == "DARURAT" }
            3 -> allEntries.filter { it.category == "MILESTONE" }
            else -> allEntries
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MindSetBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 1. Top Bar ───────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Mind-Set", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                Text(
                    text = "Profil & Riwayat Pemulihan",
                    style = MaterialTheme.typography.titleSmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifikasi", tint = MindSetNeutral)
            }
        }

        // ── 2. Kartu Profil Pengguna ─────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface),
            border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MindSetPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentUsername.isNotBlank()) currentUsername.take(2).uppercase() else "AP",
                            fontSize = 20.sp,
                            color = MindSetPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentUsername,
                            style = MaterialTheme.typography.titleMedium,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = MindSetPrimaryContainer) {
                            Text(
                                text = if (journalHistory.size >= 7) "🏆 Pejuang Pulih • Fase 2" else "🌱 Pejuang Pulih • Fase 1",
                                style = MaterialTheme.typography.labelSmall,
                                color = MindSetPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Akun Lokal Terenkripsi SHA-256",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = MindSetDivider)
                Spacer(Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Psychology, contentDescription = null, tint = MindSetPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Fokus: Neuroplastisitas & Rekognisi Kognitif",
                            style = MaterialTheme.typography.bodySmall,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Selangkah kecil menata ulang jalur impuls pikiran.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                val cleanStreakDays = journalHistory.size
                val totalReflections = journalHistory.size
                val totalCbtSessions = emergencyHistory.size + journalHistory.size

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileStat("$cleanStreakDays Hari", "Hari Bersih\nKumulatif")
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(MindSetDivider))
                    ProfileStat("$totalReflections", "Refleksi\nTotal")
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(MindSetDivider))
                    ProfileStat("$totalCbtSessions", "Sesi CBT\nTuntas")
                }

                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = MindSetTertiary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Progres Pemulihan Berkelanjutan",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetTertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // ── 3. Edit Profil (Ganti Kata Sandi) ────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface),
            border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isEditingPassword = !isEditingPassword },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = MindSetPrimary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Edit Profil & Ganti Kata Sandi",
                            style = MaterialTheme.typography.titleSmall,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ubah kata sandi rahasia untuk akun '$currentUsername'",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                    Icon(
                        imageVector = if (isEditingPassword) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        tint = MindSetNeutralGrey
                    )
                }

                if (isEditingPassword) {
                    Spacer(Modifier.height(14.dp))
                    HorizontalDivider(color = MindSetDivider)
                    Spacer(Modifier.height(14.dp))

                    if (passwordMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSuccessMessage) MindSetTertiaryLight else MindSetError.copy(alpha = 0.15f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = passwordMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSuccessMessage) MindSetNeutral else MindSetError,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    Text("Kata Sandi Lama", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = { oldPassword = it; passwordMessage = null },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••••••••", color = MindSetNeutralLight) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(10.dp))

                    Text("Kata Sandi Baru", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it; passwordMessage = null },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("••••••••••", color = MindSetNeutralLight) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle Password",
                                    tint = MindSetNeutralGrey
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (oldPassword.isBlank() || newPassword.isBlank()) {
                                passwordMessage = "Kata sandi tidak boleh kosong."
                                isSuccessMessage = false
                                return@Button
                            }
                            if (userRepository == null) {
                                passwordMessage = "Repository autentikasi belum tersedia."
                                isSuccessMessage = false
                                return@Button
                            }

                            isSavingPassword = true
                            coroutineScope.launch {
                                val success = userRepository.changePassword(
                                    username = currentUsername,
                                    oldPass = oldPassword,
                                    newPass = newPassword
                                )
                                isSavingPassword = false
                                if (success) {
                                    passwordMessage = "Kata sandi akun '$currentUsername' berhasil diperbarui!"
                                    isSuccessMessage = true
                                    oldPassword = ""
                                    newPassword = ""
                                } else {
                                    passwordMessage = "Gagal memperbarui. Kata sandi lama tidak cocok."
                                    isSuccessMessage = false
                                }
                            }
                        },
                        enabled = !isSavingPassword,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MindSetPrimary)
                    ) {
                        if (isSavingPassword) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("Simpan Kata Sandi Baru", color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ── 4. Riwayat & Log Pemulihan (Terhubung ke Room DB Jurnal & Darurat) ────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface),
            border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Riwayat & Log Pemulihan",
                            style = MaterialTheme.typography.titleSmall,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Catatan refleksi jurnal dan intervensi darurat CBT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MindSetPrimaryContainer
                    ) {
                        Text(
                            text = "${filteredEntries.size} Log",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(tabs) { index, tab ->
                        val isSelected = selectedTab == index
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            label = {
                                Text(
                                    text = tab,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MindSetPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = MindSetBackground,
                                labelColor = MindSetNeutralGrey
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

                Spacer(Modifier.height(14.dp))

                if (filteredEntries.isEmpty()) {
                    Text(
                        text = "Belum ada riwayat pada kategori ini.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutralGrey,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    )
                } else {
                    filteredEntries.forEachIndexed { idx, entry ->
                        LogEntryItem(entry)
                        if (idx < filteredEntries.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MindSetDivider
                            )
                        }
                    }
                }
            }
        }

        // ── 5. Pusat Bantuan Darurat ─────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showEmergencyDialog = true },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSecondaryLight),
            border = BorderStroke(1.dp, MindSetSecondary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MindSetSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📞", fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pusat Bantuan & Konseling Darurat",
                        style = MaterialTheme.typography.labelLarge,
                        color = MindSetNeutral,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hubungi layanan konselor mitra 24/7",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetNeutralGrey
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MindSetNeutral)
            }
        }

        // ── 6. Opsi Keluar Akun & Keluar Aplikasi (Quit) ──────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MindSetDivider)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MindSetNeutral, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Keluar Akun", style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
                onClick = {
                    (context as? Activity)?.finish()
                },
                modifier = Modifier.weight(1f).height(46.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MindSetError.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MindSetError.copy(alpha = 0.08f))
            ) {
                Icon(Icons.Filled.PowerSettingsNew, contentDescription = null, tint = MindSetError, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Tutup Aplikasi", style = MaterialTheme.typography.labelMedium, color = MindSetError, fontWeight = FontWeight.Bold)
            }
        }

        Text(
            text = "Mind-Set v2.4 • Ruangan Pemulihan Pribadimu",
            style = MaterialTheme.typography.labelSmall,
            color = MindSetNeutralGrey,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Keluar dari Akun?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Anda akan keluar dari akun '$currentUsername'. Seluruh catatan dan riwayat Anda tetap tersimpan aman di database lokal gawai ini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutralGrey
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MindSetPrimary)
                ) {
                    Text("Ya, Keluar Akun", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = MindSetNeutral)
                }
            }
        )
    }

    // Emergency Counseling Dialog
    if (showEmergencyDialog) {
        AlertDialog(
            onDismissRequest = { showEmergencyDialog = false },
            title = {
                Text(
                    text = "Pusat Bantuan & Konseling Darurat 24/7",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Jika Anda membutuhkan bantuan segera atau ingin berbicara dengan profesional:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MindSetSecondaryLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📞 Layanan Kemenkes Sehat Jiwa", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                            Text("Hotline 119 (Ext 8) / 1500-715", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text(
                        text = "Layanan ini gratis, rahasia, dan beroperasi 24 jam sehari.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetNeutralGrey
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEmergencyDialog = false
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MindSetPrimary)
                ) {
                    Text("Panggil Hotline 119", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmergencyDialog = false }) {
                    Text("Tutup", color = MindSetNeutral)
                }
            }
        )
    }
}

data class LogEntry(
    val date: String,
    val category: String,
    val type: String,
    val title: String,
    val body: String,
    val tags: String,
    val badge: String,
    val isMilestone: Boolean
)

@Composable
fun LogEntryItem(entry: LogEntry) {
    val dotColor = when {
        entry.category == "DARURAT" -> MindSetSecondary
        entry.isMilestone -> MindSetPrimary
        else -> MindSetTertiary
    }

    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(9.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MindSetNeutralGrey
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (entry.category == "DARURAT") MindSetSecondaryLight else MindSetPrimaryContainer
                ) {
                    Text(
                        text = entry.badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (entry.category == "DARURAT") MindSetNeutral else MindSetPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(
                text = entry.type,
                style = MaterialTheme.typography.labelSmall,
                color = if (entry.category == "DARURAT") MindSetError else MindSetPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = entry.title,
                style = MaterialTheme.typography.labelLarge,
                color = MindSetNeutral,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = entry.body,
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = entry.tags,
                style = MaterialTheme.typography.labelSmall,
                color = MindSetPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MindSetNeutral,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MindSetNeutralGrey,
            textAlign = TextAlign.Center
        )
    }
}
