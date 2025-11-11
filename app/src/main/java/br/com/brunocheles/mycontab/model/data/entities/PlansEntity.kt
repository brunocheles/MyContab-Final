package br.com.brunocheles.mycontab.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plans_tb",
    indices = [
        Index(value = ["plan_user_id"], unique = false)
    ]
)
data class PlansEntity(
    @PrimaryKey(autoGenerate = true) val planId: Int,
    @ColumnInfo(name = "plan_name") val planName: String,
    @ColumnInfo(name = "plan_value") val planValue: Double,
    @ColumnInfo(name = "plan_image") val planImage: Int,
    @ColumnInfo(name = "plan_user_id") val planUserId: String
)
