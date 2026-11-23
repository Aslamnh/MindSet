package com.example.mind_set.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mind_set.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

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
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MindSetNeutral
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Login / Masuk",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MindSetNeutral,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = "Privacy",
                tint = MindSetTertiary
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Calming header card ──────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetPrimaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌿", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text  = "PERJALANAN PEMULIHAN",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "Ruang Tenang Pribadimu",
                    style = MaterialTheme.typography.titleLarge,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "Ambil napas sejenak. Di sini tidak ada tuntutan, target tergesa, atau penilaian. Mulai langkahmu dengan tempo yang paling ramah untuk jiwamu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MindSetNeutralGrey
                )
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🌬️", fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text  = "Hembuskan napas perlahan sebelum melanjutkan...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Form fields ──────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            Text(
                text  = "Identitas Aman atau Email",
                style = MaterialTheme.typography.labelMedium,
                color = MindSetNeutralGrey
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("nama@email.com", color = MindSetNeutralLight) },
                leadingIcon  = {
                    Icon(Icons.Filled.Email, contentDescription = null, tint = MindSetNeutralGrey)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine      = true,
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MindSetPrimary,
                    unfocusedBorderColor = MindSetDivider,
                    focusedContainerColor   = MindSetSurface,
                    unfocusedContainerColor = MindSetSurface
                )
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text  = "Kata Sandi Rahasia",
                style = MaterialTheme.typography.labelMedium,
                color = MindSetNeutralGrey
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("••••••••••", color = MindSetNeutralLight) },
                leadingIcon  = {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = MindSetNeutralGrey)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password",
                            tint = MindSetNeutralGrey
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine           = true,
                shape  = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MindSetPrimary,
                    unfocusedBorderColor = MindSetDivider,
                    focusedContainerColor   = MindSetSurface,
                    unfocusedContainerColor = MindSetSurface
                )
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(checkedColor = MindSetPrimary)
                    )
                    Text(
                        text  = "Ingat saya di ponsel ini",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutral
                    )
                }
                Text(
                    text  = "Bantuan Sandi?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { }
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Primary login button ──────────────────────────
            Button(
                onClick  = onLoginSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MindSetPrimary)
            ) {
                Text(
                    text  = "Masuk ke Ruang Tenang →",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Divider ───────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MindSetDivider)
                Text(
                    text     = "  AKSES LEMBUT & CEPAT  ",
                    style    = MaterialTheme.typography.labelSmall,
                    color    = MindSetNeutralGrey,
                    letterSpacing = 1.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MindSetDivider)
            }

            Spacer(Modifier.height(16.dp))

            // ── Biometric + Google ────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick  = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape  = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MindSetDivider)
                ) {
                    Text("👆", fontSize = 18.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text  = "Sidik Jari",
                        style = MaterialTheme.typography.labelLarge,
                        color = MindSetNeutral
                    )
                }
                OutlinedButton(
                    onClick  = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape  = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MindSetDivider)
                ) {
                    Text("G", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text  = "Akun Sandar",
                        style = MaterialTheme.typography.labelLarge,
                        color = MindSetNeutral
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Anonymous mode ────────────────────────────────
            OutlinedButton(
                onClick  = onLoginSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape  = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MindSetSecondary),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MindSetSecondaryLight)
            ) {
                Text("🙈", fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text  = "Mode Tamu Anonim (Discreet Mode)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text      = "Jelajahi modul relaksasi tanpa perlu mendaftarkan email sekarang.",
                style     = MaterialTheme.typography.bodySmall,
                color     = MindSetNeutralGrey,
                textAlign = TextAlign.Center,
                modifier  = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // ── Privacy card ──────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = CardDefaults.cardColors(containerColor = MindSetTertiaryLight),
                border   = androidx.compose.foundation.BorderStroke(1.dp, MindSetTertiary.copy(alpha = 0.4f))
            ) {
                Row(modifier = Modifier.padding(14.dp)) {
                    Icon(
                        Icons.Filled.Shield,
                        contentDescription = null,
                        tint = MindSetTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text  = "Privasi Terapeutik 100% Terjaga",
                            style = MaterialTheme.typography.labelMedium,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text  = "Jurnal emosi, rekaman reframing, dan data pernapasanmu dienkripsi kuat secara lokal di gawai ini. Tidak ada data yang dibagikan ke pihak ketiga atau pengiklan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MindSetNeutralGrey
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
