package br.com.brunocheles.mycontab.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.brunocheles.mycontab.model.data.entities.IncomesEntity


@Dao
interface IncomeDao {
    @Query("SELECT * FROM incomes_tb WHERE income_user_id = (:userId) AND income_month = (:month) AND income_year = (:year)")
    fun getAllMonthIncomes(userId: String?, month: Int, year: Int): List<IncomesEntity>

    @Query("SELECT * FROM incomes_tb WHERE income_user_id = (:userId) AND income_year = (:year)")
    fun getAllYearIncomes(userId: String?, year: Int): List<IncomesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIncome(income: IncomesEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateIncome(income: IncomesEntity)

    @Delete
    fun deleteIncome(income: IncomesEntity)
}