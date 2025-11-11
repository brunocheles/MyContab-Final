package br.com.brunocheles.mycontab.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import br.com.brunocheles.mycontab.model.entities.GroupsEntity

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups_tb WHERE group_user_id = :userId")
    suspend fun getGroups(userId: String): List<GroupsEntity>

    @Query("SELECT * FROM groups_tb WHERE id = :groupId")
    suspend fun getGroupById(groupId: Int): GroupsEntity

    @Insert
    suspend fun insertGroup(group: GroupsEntity)

    @Delete
    suspend fun deleteGroup(group: GroupsEntity)
}