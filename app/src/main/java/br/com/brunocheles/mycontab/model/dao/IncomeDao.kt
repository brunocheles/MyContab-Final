package br.com.brunocheles.mycontab.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.brunocheles.mycontab.model.data.entities.IncomesEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface IncomeDao {
    @Query("SELECT * FROM incomes_tb WHERE income_user_id = (:userId) AND income_month = (:month) AND income_year = (:year)")
    fun getMonthIncomesStream(userId: String?, month: Int, year: Int): Flow<List<IncomesEntity>>

    @Query("SELECT * FROM incomes_tb WHERE income_user_id = (:userId) AND income_year = (:year)")
    fun getYearIncomesStream(userId: String?, year: Int): Flow<List<IncomesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIncome(income: IncomesEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateIncome(income: IncomesEntity)

    @Delete
    fun deleteIncome(income: IncomesEntity)
}