package br.com.brunocheles.mycontab.view.items

import br.com.brunocheles.mycontab.model.components.Expense
import br.com.brunocheles.mycontab.model.components.Income

data class ShowValueItem(
    val id: Int? = 0,
    val value: Double?,
    val name: String?,
    val day: Int?,
    val month: Int?,
    val year: Int?,
    val groupId: Int?,
    val groupIcon: Int?,
    val isExpense: Boolean
)

fun ShowValueItem.toExpense(): Expense {
    return Expense(
        expenseId = id!!,
        expenseValue = value!!,
        expenseDesc = name!!,
        expenseGroupId = groupId!!,
        expenseGroupIcon = groupIcon!!,
        expenseMonth = month!!,
        expenseYear = year!!,
        expenseDay = day!!
    )
}

fun ShowValueItem.toIncome(): Income {
    return Income(
        incomeId = id!!,
        incomeValue = value!!,
        incomeDesc = name!!,
        incomeGroupId = groupId!!,
        incomeGroupIcon = groupIcon!!,
        incomeMonth = month!!,
        incomeYear = year!!,
        incomeDay = day!!
    )
}