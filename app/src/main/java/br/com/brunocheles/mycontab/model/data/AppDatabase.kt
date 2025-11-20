package br.com.brunocheles.mycontab.model.data

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.brunocheles.mycontab.model.dao.ExpenseDao
import br.com.brunocheles.mycontab.model.dao.GroupDao
import br.com.brunocheles.mycontab.model.dao.IncomeDao
import br.com.brunocheles.mycontab.model.dao.PlanDao
import br.com.brunocheles.mycontab.model.dao.UserDao
import br.com.brunocheles.mycontab.model.data.entities.ExpensesEntity
import br.com.brunocheles.mycontab.model.data.entities.UserEntity
import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity
import br.com.brunocheles.mycontab.model.data.entities.IncomesEntity
import br.com.brunocheles.mycontab.model.data.entities.PlansEntity

@Database(
    entities = [
        UserEntity::class,
        GroupsEntity::class,
        IncomesEntity::class,
        ExpensesEntity::class,
        PlansEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun groupDao(): GroupDao
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun planDao(): PlanDao
}