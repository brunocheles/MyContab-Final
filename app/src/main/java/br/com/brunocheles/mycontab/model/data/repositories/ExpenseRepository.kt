package br.com.brunocheles.mycontab.model.data.repositories

import br.com.brunocheles.mycontab.model.components.Expense
import br.com.brunocheles.mycontab.model.dao.ExpenseDao
import br.com.brunocheles.mycontab.model.data.entities.ExpensesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {

    fun getMonthExpensesStream(userId: String, month: Int, year: Int): Flow<List<Expense>> {
        return expenseDao.getMonthExpensesStream(userId, month, year)
            .map { entities ->
                entities.map { it.toExpense() }
            }
            .flowOn(Dispatchers.IO)
    }

    // 🔥 Stream reativo para o Ano
    fun getYearExpensesStream(userId: String, year: Int): Flow<List<Expense>> {
        return expenseDao.getYearExpensesStream(userId, year)
            .map { entities ->
                entities.map { it.toExpense() }
            }
            .flowOn(Dispatchers.IO)
    }

    // Escritas continuam suspend e retornando Result
    suspend fun insertExpense(userId: String, expense: Expense): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                expenseDao.insertExpense(expense.toEntity(userId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateExpense(userId: String, expense: Expense): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                expenseDao.updateExpense(expense.toEntity(userId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteExpense(userId: String, expense: Expense): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                expenseDao.deleteExpense(expense.toEntity(userId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Mappers (Mantidos iguais)
    private fun ExpensesEntity.toExpense(): Expense = Expense(
        expenseId = expenseId,
        expenseValue = expenseValue,
        expenseDesc = expenseDesc,
        expenseGroupId = expenseGroupId,
        expenseGroupIcon = expenseGroupIcon,
        expenseMonth = expenseMonth,
        expenseYear = expenseYear,
        expenseDay = expenseDay
    )

    private fun Expense.toEntity(userId: String): ExpensesEntity = ExpensesEntity(
        expenseId = expenseId,
        expenseValue = expenseValue,
        expenseDesc = expenseDesc,
        expenseGroupId = expenseGroupId,
        expenseGroupIcon = expenseGroupIcon,
        expenseMonth = expenseMonth,
        expenseYear = expenseYear,
        expenseDay = expenseDay,
        expenseUserId = userId
    )
}
