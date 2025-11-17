package br.com.brunocheles.mycontab.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "incomes_tb",
    indices = [
        Index(value = ["income_group_id"], unique = false),
        Index(value = ["income_user_id"], unique = false)
    ]
)
data class IncomesEntity(
    @PrimaryKey(autoGenerate = true) val incomeId: Int,
    @ColumnInfo(name = "income_value") val incomeValue: Double,
    @ColumnInfo(name = "income_description") val incomeDesc: String,
    @ColumnInfo(name = "income_group_id") val incomeGroupId: Int,
    @ColumnInfo(name = "income_group_icon") val incomeGroupIcon: String,
    @ColumnInfo(name = "income_user_id") val incomeUserId: String,
    @ColumnInfo(name = "income_month") val incomeMonth: Int,
    @ColumnInfo(name = "income_year") val incomeYear: Int,
    @ColumnInfo(name = "income_day") val incomeDay: Int
)