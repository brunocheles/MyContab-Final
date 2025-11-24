package br.com.brunocheles.mycontab.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.brunocheles.mycontab.model.data.entities.ExpensesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses_tb WHERE expense_user_id = (:userId) AND expense_month = (:month) AND expense_year = (:year)")
    fun getMonthExpensesStream(userId: String, month: Int, year: Int): Flow<List<ExpensesEntity>>

    @Query("SELECT * FROM expenses_tb WHERE expense_user_id = (:userId) AND expense_year = (:year)")
    fun getYearExpensesStream(userId: String, year: Int): Flow<List<ExpensesEntity>>

    @Insert
    fun insertExpense(expense: ExpensesEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateExpense(income: ExpensesEntity)

    @Delete
    fun deleteExpense(expense: ExpensesEntity)
}