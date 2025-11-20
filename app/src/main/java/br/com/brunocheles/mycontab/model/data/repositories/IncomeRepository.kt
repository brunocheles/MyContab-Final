package br.com.brunocheles.mycontab.model.data.repositories

import br.com.brunocheles.mycontab.model.components.Income
import br.com.brunocheles.mycontab.model.components.IncomeInterface
import br.com.brunocheles.mycontab.model.dao.IncomeDao
import br.com.brunocheles.mycontab.model.data.entities.IncomesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class IncomeRepository @Inject constructor(
    private val incomeDao: IncomeDao
): IncomeInterface {

    override suspend fun getAllMonthIncomes(userId: String, month: Int, year: Int): Result<List<Income>> {
        return withContext(Dispatchers.IO) {
            try {
                val entities: List<IncomesEntity> =
                    incomeDao.getAllMonthIncomes(userId, month, year)

                val incomes: List<Income> = entities.map { entity ->
                    entity.toIncome() // Usando sua função de mapeamento
                }

                Result.success(incomes)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getAllYearIncomes(userId: String, year: Int): Result<List<Income>> {
        return withContext(Dispatchers.IO) {
            try {
                val entities: List<IncomesEntity> = incomeDao.getAllYearIncomes(userId, year)

                val incomes: List<Income> = entities.map { entity ->
                    entity.toIncome() // Usando sua função de mapeamento
                }

                Result.success(incomes)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun insertIncome(userId: String, income: Income): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entity = income.toEntity(userId)

                incomeDao.insertIncome(entity)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun updateIncome(userId: String, income: Income): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entity = income.toEntity(userId)

                incomeDao.updateIncome(entity)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun deleteIncome(userId: String, income: Income): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entity = income.toEntity(userId)

                incomeDao.deleteIncome(entity)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun IncomesEntity.toIncome(): Income {
        return Income(
            incomeId = incomeId,
            incomeValue = incomeValue,
            incomeDesc = incomeDesc,
            incomeGroupId = incomeGroupId,
            incomeGroupIcon = incomeGroupIcon,
            incomeMonth = incomeMonth,
            incomeYear = incomeYear,
            incomeDay = incomeDay
        )
    }

    private fun Income.toEntity(userId: String): IncomesEntity {
        return IncomesEntity(
            // Você precisa do ID aqui se for fazer update/delete
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
}
