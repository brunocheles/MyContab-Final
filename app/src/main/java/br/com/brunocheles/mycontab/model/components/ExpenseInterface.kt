package br.com.brunocheles.mycontab.model.components

interface ExpenseInterface {
    suspend fun getAllMonthExpenses(userId: String, month: Int, year: Int): Result<List<Expense>>
    suspend fun getAllYearExpenses(userId: String, year: Int): Result<List<Expense>>
    suspend fun insertExpense(userId: String, expense: Expense): Result<Unit>
    suspend fun updateExpense(userId: String, expense: Expense): Result<Unit>
    suspend fun deleteExpense(userId: String, expense: Expense): Result<Unit>
}