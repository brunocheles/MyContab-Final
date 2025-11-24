package br.com.brunocheles.mycontab.model.data.repositories

import br.com.brunocheles.mycontab.model.components.Income
import br.com.brunocheles.mycontab.model.dao.IncomeDao
import br.com.brunocheles.mycontab.model.data.entities.IncomesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IncomeRepository @Inject constructor(
    private val incomeDao: IncomeDao
) {

    fun getMonthIncomesStream(userId: String, month: Int, year: Int): Flow<List<Income>> {
        return incomeDao.getMonthIncomesStream(userId, month, year)
            .map { entities ->
                entities.map { it.toIncome() }
            }
            .flowOn(Dispatchers.IO)
    }

    // 🔥 Stream reativo para o Ano
    fun getYearIncomesStream(userId: String, year: Int): Flow<List<Income>> {
        return incomeDao.getYearIncomesStream(userId, year)
            .map { entities ->
                entities.map { it.toIncome() }
            }
            .flowOn(Dispatchers.IO)
    }

    // Escritas continuam suspend e retornando Result
    suspend fun insertIncome(userId: String, income: Income): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                incomeDao.insertIncome(income.toEntity(userId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateIncome(userId: String, income: Income): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                incomeDao.updateIncome(income.toEntity(userId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteIncome(userId: String, income: Income): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                incomeDao.deleteIncome(income.toEntity(userId))
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // Mappers (Mantidos iguais)
    private fun IncomesEntity.toIncome(): Income = Income(
        incomeId = incomeId,
        incomeValue = incomeValue,
        incomeDesc = incomeDesc,
        incomeGroupId = incomeGroupId,
        incomeGroupIcon = incomeGroupIcon,
        incomeMonth = incomeMonth,
        incomeYear = incomeYear,
        incomeDay = incomeDay
    )

    private fun Income.toEntity(userId: String): IncomesEntity = IncomesEntity(
        incomeId = incomeId,
        incomeValue = incomeValue,
        incomeDesc = incomeDesc,
        incomeGroupId = incomeGroupId,
        incomeGroupIcon = incomeGroupIcon,
        incomeMonth = incomeMonth,
        incomeYear = incomeYear,
        incomeDay = incomeDay,
        incomeUserId = userId
    )
}