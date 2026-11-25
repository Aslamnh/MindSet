package com.example.mind_set.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mind_set.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun setMode(isRegister: Boolean) {
        _uiState.update { it.copy(isRegisterMode = isRegister, username = "", password = "", errorMessage = null, successMessage = null) }
    }

    fun updateUsername(input: String) {
        _uiState.update { it.copy(username = input, errorMessage = null) }
    }

    fun updatePassword(input: String) {
        _uiState.update { it.copy(password = input, errorMessage = null) }
    }

    fun authenticate(onSuccess: (String) -> Unit) {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Username dan kata sandi tidak boleh kosong.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            if (state.isRegisterMode) {
                val success = userRepository.register(state.username, state.password)
                if (success) {
                    val registeredUser = state.username
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRegisterMode = false,
                            username = "",
                            password = "",
                            successMessage = "Akun '$registeredUser' berhasil dibuat! Silakan masuk."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Username sudah terdaftar. Gunakan yang lain."
                        )
                    }
                }
            } else {
                val success = userRepository.login(state.username, state.password)
                if (success) {
                    val loggedInUser = state.username
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess(loggedInUser)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Username atau kata sandi salah."
                        )
                    }
                }
            }
        }
    }

    companion object {
        fun provideFactory(userRepository: UserRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                        return LoginViewModel(userRepository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
