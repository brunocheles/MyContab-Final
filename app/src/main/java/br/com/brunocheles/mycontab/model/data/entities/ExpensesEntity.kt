package br.com.brunocheles.mycontab.model.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses_tb",
    indices = [
        Index(value = ["expense_group_id"], unique = false),
        Index(value = ["expense_user_id"], unique = false)
    ],
)
data class ExpensesEntity(
    @PrimaryKey(autoGenerate = true) val expenseId: Int,
    @ColumnInfo(name = "expense_value") val expenseValue: Double,
    @ColumnInfo(name = "expense_description") val expenseDesc: String,
    @ColumnInfo(name = "expense_group_id") val expenseGroupId: Int,
    @ColumnInfo(name = "expense_group_icon") val expenseGroupIcon: String,
    @ColumnInfo(name = "expense_user_id") val expenseUserId: String,
    @ColumnInfo(name = "expense_month") val expenseMonth: Int,
    @ColumnInfo(name = "expense_year") val expenseYear: Int,
    @ColumnInfo(name = "expense_day") val expenseDay: Int
)