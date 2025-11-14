package br.com.brunocheles.mycontab.model.data.repositories

import br.com.brunocheles.mycontab.model.components.Expense
import br.com.brunocheles.mycontab.model.components.ExpenseInterface
import br.com.brunocheles.mycontab.model.dao.ExpenseDao
import br.com.brunocheles.mycontab.model.data.entities.ExpensesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) : ExpenseInterface {

    override suspend fun getAllMonthExpenses(userId: String, month: Int, year: Int): Result<List<Expense>> {
        return withContext(Dispatchers.IO) {
            try {
                val entities: List<ExpensesEntity> =
                    expenseDao.getAllMonthExpenses(userId, month, year)

                val expenses: List<Expense> = entities.map { entity ->
                    entity.toExpense() // Usando sua função de mapeamento
                }

                Result.success(expenses)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getAllYearExpenses(userId: String, year: Int): Result<List<Expense>> {
        return withContext(Dispatchers.IO) {
            try {
                val entities: List<ExpensesEntity> = expenseDao.getAllYearExpenses(userId, year)

                val expenses: List<Expense> = entities.map { entity ->
                    entity.toExpense() // Usando sua função de mapeamento
                }

                Result.success(expenses)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun insertExpense(userId: String, expense: Expense): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entity = expense.toEntity(userId)

                expenseDao.insertExpense(entity)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateExpense(userId: String, expense: Expense): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entity = expense.toEntity(userId)

                expenseDao.updateExpense(entity)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteExpense(userId: String, expense: Expense): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entity = expense.toEntity(userId)

                expenseDao.deleteExpense(entity)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun ExpensesEntity.toExpense(): Expense {
        return Expense(
            expenseId = expenseId,
            expenseValue = expenseValue,
            expenseDesc = expenseDesc,
            expenseGroupId = expenseGroupId,
            expenseGroupIcon = expenseGroupIcon,
            expenseMonth = expenseMonth,
            expenseYear = expenseYear,
            expenseDay = expenseDay
        )
    }

    private fun Expense.toEntity(userId: String): ExpensesEntity {
        return ExpensesEntity(
            // Você precisa do ID aqui se for fazer update/delete
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
}
