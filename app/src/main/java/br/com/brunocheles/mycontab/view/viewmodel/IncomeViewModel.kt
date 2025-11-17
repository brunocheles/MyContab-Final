package br.com.brunocheles.mycontab.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brunocheles.mycontab.model.components.Income
import br.com.brunocheles.mycontab.model.data.repositories.IncomeRepository
import br.com.brunocheles.mycontab.model.di.DataStoreManager
import br.com.brunocheles.mycontab.view.states.IncomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private val dataStoreManager: DataStoreManager
): ViewModel() {

    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<IncomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var activeLoadingJobs = 0
    private var currentUserId: String? = null
    private var currentMonth: Int? = null
    private var currentYear: Int? = null

    fun getAllIncomesMonth(userId:String, month: Int, year: Int) {

        currentUserId = userId
        currentMonth = month + 1
        currentYear = year

        activeLoadingJobs++
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {

                val result = incomeRepository.getAllMonthIncomes(currentUserId!!, currentMonth!!, currentYear!!)

                result.onSuccess { incomesList ->
                    _uiState.update {
                        it.copy(incomeValuesMonth = incomesList)
                    }
                    _uiEvent.emit(IncomeUiEvent.OnSuccess)
                }.onFailure { error ->
                    val errorMessage = error.message ?: "Erro desconhecido ao salvar."
                    _uiEvent.emit(IncomeUiEvent.ShowError(errorMessage))
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
            getAllIncomesMonth(currentUserId!!, currentMonth!!, currentYear!!)
        }
        // Se tiver o método de carregar o ano também, chame aqui:
        if (currentUserId != null && currentYear != null) {
            getAllIncomesYear(currentUserId!!, currentYear!!)
        }
    }

    fun getAllIncomesYear(userId: String,year: Int) {

        currentUserId = userId
        currentYear = year

        activeLoadingJobs++
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val result = incomeRepository.getAllYearIncomes(currentUserId!!, currentYear!!)

                result.onSuccess { incomesList ->
                    _uiState.update {
                        it.copy(incomeValuesYear = incomesList)
                    }
                    _uiEvent.emit(IncomeUiEvent.OnSuccess)
                }.onFailure { error ->
                    val errorMessage = error.message ?: "Erro desconhecido ao salvar."
                    _uiEvent.emit(IncomeUiEvent.ShowError(errorMessage))
                }
            } finally {
                activeLoadingJobs--

                if (activeLoadingJobs == 0) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun insertIncome(userId:String, income: Income) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = incomeRepository.insertIncome(userId, income)

            result.onSuccess {
                _uiEvent.emit(IncomeUiEvent.OnSuccess)
                refreshData()
            }.onFailure { error ->
                val errorMessage = error.message ?: "Erro desconhecido ao salvar."
                _uiEvent.emit(IncomeUiEvent.ShowError(errorMessage))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun updateIncome(userId:String, income: Income) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentList = _uiState.value.incomeValuesMonth.toMutableList()

            val index = currentList.indexOfFirst { it?.incomeId == income.incomeId }

            if (index != -1) {
                currentList[index] = income

                _uiState.update { it.copy(incomeValuesMonth = currentList) }
            }

            val result = incomeRepository.updateIncome(userId, income)

            result.onSuccess {
                _uiEvent.emit(IncomeUiEvent.OnSuccess)
            }.onFailure { error ->
                val errorMessage = error.message ?: "Erro desconhecido ao salvar."
                _uiEvent.emit(IncomeUiEvent.ShowError(errorMessage))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun deleteIncome(userId:String, income: Income) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentList = _uiState.value.incomeValuesMonth.toMutableList()

            val removed = currentList.removeIf { it?.incomeId == income.incomeId }

            if (removed) {
                _uiState.update { it.copy(incomeValuesMonth = currentList) }
            }

            val result = incomeRepository.deleteIncome(userId, income)

            result.onSuccess {
                _uiEvent.emit(IncomeUiEvent.OnSuccess)
            }.onFailure { error ->
                val errorMessage = error.message ?: "Erro desconhecido ao salvar."
                _uiEvent.emit(IncomeUiEvent.ShowError(errorMessage))
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}