package br.com.brunocheles.mycontab.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.brunocheles.mycontab.model.data.entities.ExpensesEntity

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses_tb WHERE expense_user_id = (:userId) AND expense_month = (:month) AND expense_year = (:year)")
    fun getAllMonthExpenses(userId: String?, month: Int, year: Int): List<ExpensesEntity>

    @Query("SELECT * FROM expenses_tb WHERE expense_user_id = (:userId) AND expense_year = (:year)")
    fun getAllYearExpenses(userId: String?, year: Int): List<ExpensesEntity>

    @Insert
    fun insertExpense(expense: ExpensesEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateExpense(income: ExpensesEntity)

    @Delete
    fun deleteExpense(expense: ExpensesEntity)
}