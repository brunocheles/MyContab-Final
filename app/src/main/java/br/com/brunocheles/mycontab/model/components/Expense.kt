package br.com.brunocheles.mycontab.model.components

data class Expense(
    val expenseId: Int,
    val expenseValue: Double,
    val expenseDesc: String,
    val expenseGroupId: Int,
    val expenseGroupIcon: Int,
    val expenseMonth: Int,
    val expenseYear: Int,
    val expenseDay: Int
)