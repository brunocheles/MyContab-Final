package br.com.brunocheles.mycontab.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brunocheles.mycontab.model.data.repositories.UserRepository
import br.com.brunocheles.mycontab.model.di.DataStoreManager
import br.com.brunocheles.mycontab.view.states.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed interface AuthUiEvent {
    data class ShowToast(val message: String) : AuthUiEvent
    data object NavigateToLoading : AuthUiEvent
//    data object RegisterSuccess : AuthUiEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiEvent = Channel<AuthUiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _viewState = MutableStateFlow(AuthUiState())

    val isAuthChecked: StateFlow<Boolean> = dataStoreManager.user
        .map { true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<AuthUiState> = combine(
        _viewState,
        dataStoreManager.user,
        dataStoreManager.savedDate // Novo fluxo criado no passo 1
    ) { viewState, sessionUser, savedDate ->

        // 1. Resolve o Usuário
        val finalUser = if (sessionUser?.userId != null) {
            sessionUser
        } else {
            null
        }
        // 2. Retorna o Estado Unificado
        viewState.copy(
            userLogged = finalUser,
            success = finalUser != null,
            // AQUI ESTÁ A MÁGICA: A UI recebe a data atualizada do DataStore
            year = savedDate.year,
            month = savedDate.month
        )
    }.flatMapLatest { state ->
        // Se tiver usuário, conectamos no Room para ter dados em tempo real (foto, config)
        val uid = state.userLogged?.userId
        if (uid != null) {
            userRepository.getUserStream(uid).map { roomUser ->
                state.copy(userLogged = roomUser ?: state.userLogged)
            }
        } else {
            flowOf(state)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AuthUiState(isLoading = true)
    )
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userRepository.loginWithGoogle(idToken)

            result.onSuccess { user ->
                dataStoreManager.saveUserSession(user)
                // O combine lá em cima vai atualizar o uiState.userLogged automaticamente
                _viewState.update { it.copy(isLoading = false) }
            }.onFailure { e ->
                _viewState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userRepository.loginWithEmail(email, password)

            result.onSuccess { user ->
                dataStoreManager.saveUserSession(user)
                // O combine lá em cima vai atualizar o uiState.userLogged automaticamente
                _viewState.update { it.copy(isLoading = false) }
            }.onFailure { e ->
                _viewState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun resetState() {
        _viewState.update { AuthUiState() } // Limpa erros, loadings e flags de registro
    }

    fun registerWithEmail(email: String, username: String, password: String) {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = userRepository.registerWithEmail(email, username, password)

            result.onSuccess {
                _uiEvent.send(AuthUiEvent.ShowToast("Conta criada! Faça Login."))
                _viewState.update { it.copy(isLoading = false, isRegistered = true) }
            }.onFailure { e ->
                _viewState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            dataStoreManager.logout() // Isso dispara a atualização do uiState para null
            _uiEvent.send(AuthUiEvent.NavigateToLoading)
            resetState()
        }
    }

    fun updateDate(year: Int, month: Int) {
        viewModelScope.launch {
            dataStoreManager.saveDate(year, month)
        }
    }
}
