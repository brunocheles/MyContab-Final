package br.com.brunocheles.mycontab.model.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_tb",
    indices = [Index(value = ["firebase_id"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    @ColumnInfo(name = "firebase_id") val firebaseId: String,
    @ColumnInfo(name = "user_name") val userName: String,
    @ColumnInfo(name = "user_config") val userConfig: String?,
    @ColumnInfo(name = "user_email") val userEmail: String?,
    @ColumnInfo(name = "user_photo") val userPhoto: String?
)
