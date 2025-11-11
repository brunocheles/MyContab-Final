package br.com.brunocheles.mycontab.model.data.repositories

import br.com.brunocheles.mycontab.model.dao.ExpenseDao
import br.com.brunocheles.mycontab.model.data.entities.ExpensesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {

    suspend fun getAllMonthExpenses(uId: String?, month: Int, year: Int): List<ExpensesEntity> =
        withContext(Dispatchers.IO) {
            expenseDao.getAllMonthExpenses(uId,month, year)
        }

    suspend fun getAllYearExpenses(uId: String?, year: Int): List<ExpensesEntity> =
        withContext(Dispatchers.IO) {
            expenseDao.getAllYearExpenses(uId, year)
        }

    suspend fun update(expense: ExpensesEntity) =
        withContext(Dispatchers.IO) {
            expenseDao.updateExpense(expense)
        }

    suspend fun insert(expense: ExpensesEntity) =
        withContext(Dispatchers.IO) {
            expenseDao.insertExpense(expense)
        }

    suspend fun delete(expense: ExpensesEntity) =
        withContext(Dispatchers.IO) {
            expenseDao.deleteExpense(expense)
        }
}
