package br.com.brunocheles.mycontab.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brunocheles.mycontab.model.components.Income
import br.com.brunocheles.mycontab.model.data.repositories.IncomeRepository
import br.com.brunocheles.mycontab.model.di.DataStoreManager
import br.com.brunocheles.mycontab.view.states.IncomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


sealed class IncomeUiEvent {
    // Evento para quando salvar der certo
    data object OnSuccess : IncomeUiEvent()

    // Evento para mostrar erros
    data class ShowError(val message: String) : IncomeUiEvent()
}

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository,
    dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<IncomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<IncomeUiState> = combine(
        dataStoreManager.user,
        dataStoreManager.savedDate
    ) { user, date ->
        Pair(user, date)
    }.flatMapLatest { (user, date) ->
        val userId = user?.userId

        if (userId == null) {
            flowOf(IncomeUiState(isLoading = false))
        } else {
            flow {
                emit(IncomeUiState(isLoading = true))

                delay(100)

                val dbMonth = date.month + 1

                emitAll(
                    combine(
                        incomeRepository.getMonthIncomesStream(userId, dbMonth, date.year),
                        incomeRepository.getYearIncomesStream(userId, date.year)
                    ) { monthList, yearList ->
                        IncomeUiState(
                            incomeValuesMonth = monthList,
                            incomeValuesYear = yearList,
                            isLoading = false
                        )
                    }
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncomeUiState(isLoading = true)
    )

    fun insertIncome(userId: String, income: Income) {
        viewModelScope.launch {
            // Loading será tratado pelo stateIn ou onStart se quiser algo muito reativo,
            // mas para insert rápido, geralmente nem precisa mostrar loading full screen.
            incomeRepository.insertIncome(userId, income)
                .onSuccess {
                    _uiEvent.emit(IncomeUiEvent.OnSuccess)
                }
                .onFailure { error ->
                    _uiEvent.emit(IncomeUiEvent.ShowError(error.message ?: "Erro ao inserir"))
                }
        }
    }

    fun updateIncome(userId: String, income: Income) {
        viewModelScope.launch {
            // Não precisamos atualizar a lista manualmente! O Room fará isso.
            incomeRepository.updateIncome(userId, income)
                .onSuccess {
                    _uiEvent.emit(IncomeUiEvent.OnSuccess)
                }
                .onFailure {
                    _uiEvent.emit(IncomeUiEvent.ShowError("Erro ao atualizar"))
                }
        }
    }

    fun deleteIncome(userId: String, income: Income) {
        viewModelScope.launch {
            incomeRepository.deleteIncome(userId, income)
                .onSuccess {
                    _uiEvent.emit(IncomeUiEvent.OnSuccess)
                }
                .onFailure {
                    _uiEvent.emit(IncomeUiEvent.ShowError("Erro ao deletar"))
                }
        }
    }
}