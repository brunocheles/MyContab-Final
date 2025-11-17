package br.com.brunocheles.mycontab.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brunocheles.mycontab.model.components.Expense
import br.com.brunocheles.mycontab.model.data.repositories.ExpenseRepository
import br.com.brunocheles.mycontab.model.di.DataStoreManager
import br.com.brunocheles.mycontab.view.states.ExpenseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val dataStoreManager: DataStoreManager
): ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ExpenseUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var activeLoadingJobs = 0
    private var currentUserId: String? = null
    private var currentMonth: Int? = null
    private var currentYear: Int? = null


    fun getAllExpensesMonth(userId:String, month: Int, year: Int) {

        currentUserId = userId
        currentMonth = month + 1
        currentYear = year

        activeLoadingJobs++
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {

                val result = expenseRepository.getAllMonthExpenses(currentUserId!!, currentMonth!!, currentYear!!)

                result.onSuccess { expensesList ->
                    _uiState.update {
                        it.copy(expenseValuesMonth = expensesList)
                    }
                    _uiEvent.emit(ExpenseUiEvent.OnSuccess)
                }.onFailure { error ->
                    val errorMessage = error.message ?: "Erro desconhecido ao salvar."
                    _uiEvent.emit(ExpenseUiEvent.ShowError(errorMessage))
                }
            } finally {
                activeLoadingJobs--

                if (activeLoadingJobs == 0) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    private fun refreshData() {
        if (currentUserId != null && currentMonth != null && currentYear != null) {
            getAllExpensesMonth(currentUserId!!, currentMonth!!, currentYear!!)
        }
        // Se tiver o método de carregar o ano também, chame aqui:
        if (currentUserId != null && currentYear != null) {
            getAllExpensesYear(currentUserId!!, currentYear!!)
        }
    }

    fun getAllExpensesYear(userId: String,year: Int) {

        currentUserId = userId
        currentYear = year

        activeLoadingJobs++
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val result = expenseRepository.getAllYearExpenses(currentUserId!!, currentYear!!)

                result.onSuccess { expensesList ->
                    _uiState.update {
                        it.copy(expenseValuesYear = expensesList)
                    }
                    _uiEvent.emit(ExpenseUiEvent.OnSuccess)
                }.onFailure { error ->
                    val errorMessage = error.message ?: "Erro desconhecido ao salvar."
                    _uiEvent.emit(ExpenseUiEvent.ShowError(errorMessage))
                }
            } finally {
                activeLoadingJobs--

                if (activeLoadingJobs == 0) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun insertExpense(userId:String, expense: Expense) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = expenseRepository.insertExpense(userId, expense)

            result.onSuccess {
                _uiEvent.emit(ExpenseUiEvent.OnSuccess)
                refreshData()
            }.onFailure { error ->
                val errorMessage = error.message ?: "Erro desconhecido ao salvar."
                _uiEvent.emit(ExpenseUiEvent.ShowError(errorMessage))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun updateExpense(userId:String, expense: Expense) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentList = _uiState.value.expenseValuesMonth.toMutableList()

            val index = currentList.indexOfFirst { it?.expenseId == expense.expenseId }

            if (index != -1) {
                currentList[index] = expense

                _uiState.update { it.copy(expenseValuesMonth = currentList) }
            }

            val result = expenseRepository.updateExpense(userId, expense)

            result.onSuccess {
                _uiEvent.emit(ExpenseUiEvent.OnSuccess)
            }.onFailure { error ->
                val errorMessage = error.message ?: "Erro desconhecido ao salvar."
                _uiEvent.emit(ExpenseUiEvent.ShowError(errorMessage))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun deleteExpense(userId:String, expense: Expense) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentList = _uiState.value.expenseValuesMonth.toMutableList()

            val removed = currentList.removeIf { it?.expenseId == expense.expenseId }

            if (removed) {
                _uiState.update { it.copy(expenseValuesMonth = currentList) }
            }

            val result = expenseRepository.deleteExpense(userId, expense)

            result.onSuccess {
                _uiEvent.emit(ExpenseUiEvent.OnSuccess)
                refreshData()
            }.onFailure { error ->
                val errorMessage = error.message ?: "Erro desconhecido ao salvar."
                _uiEvent.emit(ExpenseUiEvent.ShowError(errorMessage))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}