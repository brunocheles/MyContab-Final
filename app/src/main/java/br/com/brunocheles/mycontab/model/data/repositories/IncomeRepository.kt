package br.com.brunocheles.mycontab.model.data.repositories

import br.com.brunocheles.mycontab.model.dao.IncomeDao
import br.com.brunocheles.mycontab.model.entities.IncomesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IncomeRepository @Inject constructor(
    private val incomeDao: IncomeDao
) {

    suspend fun getAllMonthIncomes(uId: String?, month: Int, year: Int): List<IncomesEntity> =
        withContext(Dispatchers.IO) {
            incomeDao.getAllMonthIncomes(uId,month, year)
        }

    suspend fun getAllYearIncomes(uId: String?, year: Int): List<IncomesEntity> =
        withContext(Dispatchers.IO) {
            incomeDao.getAllYearIncomes(uId, year)
        }

    suspend fun update(income: IncomesEntity) =
        withContext(Dispatchers.IO) {
            incomeDao.updateIncome(income)
        }

    suspend fun insert(income: IncomesEntity) =
        withContext(Dispatchers.IO) {
            incomeDao.insertIncome(income)
        }

    suspend fun delete(income: IncomesEntity) =
        withContext(Dispatchers.IO) {
            incomeDao.deleteIncome(income)
        }
}
