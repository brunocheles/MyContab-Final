package br.com.brunocheles.mycontab.model.components

interface IncomeInterface {
    suspend fun getAllMonthIncomes(userId: String, month: Int, year: Int): Result<List<Income>>
    suspend fun getAllYearIncomes(userId: String, year: Int): Result<List<Income>>
    suspend fun insertIncome(userId: String, income: Income): Result<Unit>
    suspend fun updateIncome(userId: String, income: Income): Result<Unit>
    suspend fun deleteIncome(userId: String, income: Income): Result<Unit>
}