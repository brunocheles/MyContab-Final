package br.com.brunocheles.mycontab.view.states

import br.com.brunocheles.mycontab.model.components.Expense
import br.com.brunocheles.mycontab.model.data.entities.ExpensesEntity


data class ExpenseUiState(
    val isLoading: Boolean = false,
    val expenseValuesMonth: List<Expense?> = emptyList(),
    val expenseValuesYear: List<Expense?> = emptyList()
)
