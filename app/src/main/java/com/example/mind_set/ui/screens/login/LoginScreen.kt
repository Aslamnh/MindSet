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
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

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
                text = if (uiState.isRegisterMode) "Daftar Akun Lokal" else "Login / Masuk",
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
                        text  = "AUTENTIKASI LOKAL & TERENKRIPSI",
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
                    text  = "Database lokal membedakan setiap akun dengan enkripsi hash SHA-256 yang aman di perangkat.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MindSetNeutralGrey
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Mode Switcher Tabs (Masuk vs Daftar) ─────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.setMode(false) },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!uiState.isRegisterMode) MindSetPrimary else MindSetSurface,
                    contentColor = if (!uiState.isRegisterMode) Color.White else MindSetNeutral
                )
            ) {
                Text("Masuk", fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { viewModel.setMode(true) },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isRegisterMode) MindSetPrimary else MindSetSurface,
                    contentColor = if (uiState.isRegisterMode) Color.White else MindSetNeutral
                )
            ) {
                Text("Daftar Akun Baru", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Error / Success Banners ──────────────────────────
        if (uiState.errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MindSetError.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Error, contentDescription = null, tint = MindSetError, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(uiState.errorMessage!!, style = MaterialTheme.typography.bodySmall, color = MindSetError)
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (uiState.successMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MindSetTertiaryLight)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MindSetTertiary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(uiState.successMessage!!, style = MaterialTheme.typography.bodySmall, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── Form fields ──────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            Text(
                text  = "Username / Identitas Akun",
                style = MaterialTheme.typography.labelMedium,
                color = MindSetNeutralGrey
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = uiState.username,
                onValueChange = { viewModel.updateUsername(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Contoh: alex_pratama", color = MindSetNeutralLight) },
                leadingIcon  = {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = MindSetNeutralGrey)
                },
                singleLine = true,
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
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
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

            Spacer(Modifier.height(24.dp))

            // ── Primary authenticate button ───────────────────
            Button(
                onClick  = {
                    viewModel.authenticate { loggedInUser ->
                        onLoginSuccess(loggedInUser)
                    }
                },
                enabled  = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MindSetPrimary)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text  = if (uiState.isRegisterMode) "Daftar & Buat Akun Lokal →" else "Masuk ke Ruang Tenang →",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Anonymous mode ────────────────────────────────
            OutlinedButton(
                onClick  = { onLoginSuccess("Tamu Anonim") },
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
                            text  = "Enkripsi Hash SHA-256 Lokal",
                            style = MaterialTheme.typography.labelMedium,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text  = "Kredensial akun lokal dienkripsi menggunakan fungsi hash kriptografi sebelum disimpan ke Room DB perangkat. Memastikan pemisahan akun aman tanpa server eksternal.",
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
