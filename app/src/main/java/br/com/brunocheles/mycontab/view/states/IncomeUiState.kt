package br.com.brunocheles.mycontab.view.states

import br.com.brunocheles.mycontab.model.components.Income


data class IncomeUiState(
    val isLoading: Boolean = false,
    val incomeValuesMonth: List<Income?> = emptyList(),
    val incomeValuesYear: List<Income?> = emptyList()
)
