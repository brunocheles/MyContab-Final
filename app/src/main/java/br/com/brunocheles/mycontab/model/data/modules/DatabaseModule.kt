package br.com.brunocheles.mycontab.model.data.modules

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import br.com.brunocheles.mycontab.model.dao.ExpenseDao
import br.com.brunocheles.mycontab.model.dao.GroupDao
import br.com.brunocheles.mycontab.model.dao.IncomeDao
import br.com.brunocheles.mycontab.model.dao.PlanDao
import br.com.brunocheles.mycontab.model.dao.UserDao
import br.com.brunocheles.mycontab.model.data.AppDatabase
import br.com.brunocheles.mycontab.model.data.repositories.ExpenseRepository
import br.com.brunocheles.mycontab.model.data.repositories.GroupRepository
import br.com.brunocheles.mycontab.model.data.repositories.IncomeRepository
import br.com.brunocheles.mycontab.model.data.repositories.PlanRepository
import br.com.brunocheles.mycontab.model.data.repositories.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        val dbName = "mycontab.db"
        val dbFile = context.getDatabasePath(dbName)

        if (dbFile.exists()) {
            try {
                val database = SQLiteDatabase.openDatabase(
                    dbFile.path,
                    null,
                    SQLiteDatabase.OPEN_READONLY
                )
                database.close()
            } catch (_: Exception) {
                dbFile.delete()
            }
        }

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            dbName
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }
    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao =
        appDatabase.userDao()

    @Provides
    fun provideGroupDao(appDatabase: AppDatabase): GroupDao =
        appDatabase.groupDao()

    @Provides
    fun provideIncomeDao(appDatabase: AppDatabase): IncomeDao =
        appDatabase.incomeDao()

    @Provides
    fun provideExpenseDao(appDatabase: AppDatabase): ExpenseDao =
        appDatabase.expenseDao()

    @Provides
    fun providePlanDao(appDatabase: AppDatabase): PlanDao =
        appDatabase.planDao()

    @Provides
    fun provideGroupRepository(groupDao: GroupDao): GroupRepository =
        GroupRepository(groupDao)

    @Provides
    fun provideIncomeRepository(incomeDao: IncomeDao): IncomeRepository =
        IncomeRepository(incomeDao)

    @Provides
    fun provideExpenseRepository(expenseDao: ExpenseDao): ExpenseRepository =
        ExpenseRepository(expenseDao)

    @Provides
    fun providePlanRepository(planDao: PlanDao): PlanRepository =
        PlanRepository(planDao)
}