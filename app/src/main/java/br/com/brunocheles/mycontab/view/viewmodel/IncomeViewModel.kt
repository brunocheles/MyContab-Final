package br.com.brunocheles.mycontab.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brunocheles.mycontab.model.components.Income
import br.com.brunocheles.mycontab.model.data.repositories.IncomeRepository
import br.com.brunocheles.mycontab.model.di.DataStoreManager
import br.com.brunocheles.mycontab.view.items.DateFilter
import br.com.brunocheles.mycontab.view.states.IncomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate


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

    private val today = LocalDate.now()

    private val _dateFilter = MutableStateFlow(
        DateFilter(month = today.monthValue, year = today.year)
    )

    private val _uiEvent = MutableSharedFlow<IncomeUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<IncomeUiState> = combine(
        dataStoreManager.user,
        _dateFilter
    ) { user, date ->
        Pair(user, date)
    }.flatMapLatest { (user, date) ->
        val userId = user?.userId

        if (userId == null) {
            flowOf(IncomeUiState(isLoading = false))
        } else {
            // Executa as duas queries (Mês e Ano) em paralelo e combina os resultados
            combine(
                incomeRepository.getMonthIncomesStream(userId, date.month, date.year),
                incomeRepository.getYearIncomesStream(userId, date.year)
            ) { monthList, yearList ->
                IncomeUiState(
                    incomeValuesMonth = monthList,
                    incomeValuesYear = yearList,
                    isLoading = false
                )
            }
                // Opcional: emitir estado de loading ao começar a troca
                .onStart { emit(IncomeUiState(isLoading = true)) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = IncomeUiState(isLoading = true)
    )

    fun updateDate(month: Int, year: Int) {
        // Nota: Verifique se sua UI manda mês 0-11 ou 1-12.
        // Se vier do Calendar do Java (0-11), some 1 aqui.
        // Se vier do LocalDate (1-12), use direto.
        // Assumindo que sua UI manda 0-11 (pelo código antigo `month + 1`):
        _dateFilter.value = DateFilter(month + 1, year)
    }

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