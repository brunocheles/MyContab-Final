package br.com.brunocheles.mycontab.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Query("SELECT * FROM groups_tb WHERE group_user_id = :userId OR group_user_id = :appUserId")
    fun getGroupsStream(userId: String, appUserId: String): Flow<List<GroupsEntity>>

    @Query("SELECT * FROM groups_tb WHERE id = :groupId")
    suspend fun getGroupById(groupId: Int): GroupsEntity

    @Insert
    suspend fun insertGroup(group: GroupsEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGroup(group: GroupsEntity)

    @Delete
    suspend fun deleteGroup(group: GroupsEntity)
}