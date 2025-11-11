package br.com.brunocheles.mycontab.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "groups_tb",
    indices = [
        Index(value = ["group_user_id"], unique = false)
    ]
)
data class GroupsEntity(
    @PrimaryKey(autoGenerate = true)    val id: Int,
    @ColumnInfo(name = "group_name")    val groupName: String,
    @ColumnInfo(name = "group_icon")    val groupIcon: Int,
    @ColumnInfo(name = "group_user_id") var groupUserId: String
)
