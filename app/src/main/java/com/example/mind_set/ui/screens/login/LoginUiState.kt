package com.example.mind_set.ui.screens.login

data class LoginUiState(
    val isRegisterMode: Boolean = false,
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
