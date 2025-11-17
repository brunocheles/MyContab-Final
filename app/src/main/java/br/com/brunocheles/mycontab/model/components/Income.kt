package br.com.brunocheles.mycontab.model.components

data class Income(
    val incomeId: Int,
    val incomeValue: Double,
    val incomeDesc: String,
    val incomeGroupId: Int,
    val incomeGroupIcon: String,
    val incomeMonth: Int,
    val incomeYear: Int,
    val incomeDay: Int
)
