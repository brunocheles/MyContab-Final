package br.com.brunocheles.mycontab.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brunocheles.mycontab.model.di.DataStoreManager
import br.com.brunocheles.mycontab.model.items.User
import br.com.brunocheles.mycontab.model.data.repositories.UserRepository
import br.com.brunocheles.mycontab.view.states.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface AuthUiEvent {
    data class ShowToast(val message: String) : AuthUiEvent
    data object NavigateToLoading : AuthUiEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<AuthUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _isAuthChecked = MutableStateFlow(false)
    val isAuthChecked: StateFlow<Boolean> = _isAuthChecked

    // 🔹 Controle interno de tela atual (índice da tab ou seção do app)
    private val _currentScreen = MutableStateFlow(0)
    val currentScreen: StateFlow<Int> = _currentScreen.asStateFlow()

    init {
        checkUserSession()
    }

    // 🔹 Verifica se já há sessão salva
    private fun checkUserSession() {
        viewModelScope.launch {
            dataStoreManager.user.collect { user ->
                _uiState.update { it.copy(userLogged = user, success = user != null) }
                _isAuthChecked.value = true
            }
        }
    }

    // 🔹 Login com Google
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = null) }
            val result = userRepository.loginWithGoogle(idToken)
            handleAuthResult(result)
        }
    }

    // 🔹 Login com Apple
    fun loginWithApple(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = null) }
            val result = userRepository.loginWithApple(idToken)
            handleAuthResult(result)
        }
    }

    // 🔹 Login com E-mail/Senha
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = null) }
            val result = userRepository.loginWithEmail(email, password)
            handleAuthResult(result)
        }
    }

    // 🔹 Registro com E-mail/Senha
    fun registerWithEmail(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, success = null) }
            val result = userRepository.registerWithEmail(email, username,password)

            result.onSuccess {
                _uiEvent.send(AuthUiEvent.ShowToast("Conta criada com sucesso!"))
            }
            result.onFailure {
                _uiEvent.send(AuthUiEvent.ShowToast("Erro ao criar conta!"))
            }

            handleAuthResult(result)
        }
    }

    // 🔹 Handler genérico para todos os logins
    private fun handleAuthResult(result: Result<User>) {
        result.fold(
            onSuccess = { user ->
                viewModelScope.launch {
                    dataStoreManager.saveUserSession(user)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success = true,
                            userLogged = user,
                            errorMessage = null
                        )
                    }
                }
            },
            onFailure = { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                        errorMessage = e.message ?: "Erro na autenticação"
                    )
                }
            }
        )
    }

    // 🔹 Atualiza a data salva (ex: usada para filtragem ou tela principal)
    fun updateDate(year: Int, month: Int) {
        viewModelScope.launch {
            dataStoreManager.saveDate(year, month)
            _uiState.update {
                it.copy(
                    year = year,
                    month = month
                )
            }
        }
    }

    // 🔹 Atualiza o índice da tela atual (ex: tab selecionada no Scaffold)
    fun updateScreen(index: Int) {
        _currentScreen.value = index
    }

    // 🔹 Logout completo
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            dataStoreManager.logout()
            _uiState.update { AuthUiState() }
            _uiEvent.trySend(AuthUiEvent.NavigateToLoading)
        }
    }

    // 🔹 Atualiza manualmente o usuário no estado
    fun updateUser(user: User?) {
        _uiState.update { it.copy(userLogged = user) }
    }

    // 🔹 Reset do estado (útil após navegação)
    fun resetState() {
        _uiState.update { it.copy(success = null, errorMessage = null, isLoading = false) }
    }
}
