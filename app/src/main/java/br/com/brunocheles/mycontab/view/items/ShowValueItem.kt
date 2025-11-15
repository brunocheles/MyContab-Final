package br.com.brunocheles.mycontab.view.items

data class ShowValueItem(
    val value: Double,
    val name: String,
    val day: Int,
    val groupId: Int,
    val isExpense: Boolean
)
