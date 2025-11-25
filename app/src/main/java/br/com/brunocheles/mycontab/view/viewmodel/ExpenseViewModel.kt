package br.com.brunocheles.mycontab.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brunocheles.mycontab.model.components.Expense
import br.com.brunocheles.mycontab.model.data.repositories.ExpenseRepository
import br.com.brunocheles.mycontab.model.di.DataStoreManager
import br.com.brunocheles.mycontab.view.states.ExpenseUiState
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

sealed class ExpenseUiEvent {
    // Evento para quando salvar der certo
    data object OnSuccess : ExpenseUiEvent()

    // Evento para mostrar erros
    data class ShowError(val message: String) : ExpenseUiEvent()
}

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    dataStoreManager: DataStoreManager
) : ViewModel() {


    private val _uiEvent = MutableSharedFlow<ExpenseUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ExpenseUiState> = combine(
        dataStoreManager.user,
        dataStoreManager.savedDate
    ) { user, date ->
        Pair(user, date)
    }.flatMapLatest { (user, date) ->
        val userId = user?.userId

        if (userId == null) {
            flowOf(ExpenseUiState(isLoading = false))
        } else {
            flow {
                emit(ExpenseUiState(isLoading = true))

                delay(100)

                val dbMonth = date.month + 1

                emitAll(
                    combine(
                        expenseRepository.getMonthExpensesStream(userId, dbMonth, date.year),
                        expenseRepository.getYearExpensesStream(userId, date.year)
                    ) { monthList, yearList ->
                        ExpenseUiState(
                            expenseValuesMonth = monthList,
                            expenseValuesYear = yearList,
                            isLoading = false
                        )
                    }
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpenseUiState(isLoading = true)
    )

    fun insertExpense(userId: String, expense: Expense) {
        viewModelScope.launch {
            // Loading será tratado pelo stateIn ou onStart se quiser algo muito reativo,
            // mas para insert rápido, geralmente nem precisa mostrar loading full screen.
            expenseRepository.insertExpense(userId, expense)
                .onSuccess {
                    _uiEvent.emit(ExpenseUiEvent.OnSuccess)
                }
                .onFailure { error ->
                    _uiEvent.emit(ExpenseUiEvent.ShowError(error.message ?: "Erro ao inserir"))
                }
        }
    }

    fun updateExpense(userId: String, expense: Expense) {
        viewModelScope.launch {
            // Não precisamos atualizar a lista manualmente! O Room fará isso.
            expenseRepository.updateExpense(userId, expense)
                .onSuccess {
                    _uiEvent.emit(ExpenseUiEvent.OnSuccess)
                }
                .onFailure {
                    _uiEvent.emit(ExpenseUiEvent.ShowError("Erro ao atualizar"))
                }
        }
    }

    fun deleteExpense(userId: String, expense: Expense) {
        viewModelScope.launch {
            expenseRepository.deleteExpense(userId, expense)
                .onSuccess {
                    _uiEvent.emit(ExpenseUiEvent.OnSuccess)
                }
                .onFailure {
                    _uiEvent.emit(ExpenseUiEvent.ShowError("Erro ao deletar"))
                }
        }
    }
}