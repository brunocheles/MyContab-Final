package br.com.brunocheles.mycontab.model.data.repositories

import br.com.brunocheles.mycontab.model.dao.GroupDao
import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val groupDao: GroupDao
) {
    suspend fun getUserGroups(userId: String): List<GroupsEntity> =
        groupDao.getGroups(userId)

    suspend fun getCoreGroups(appUserId: String): List<GroupsEntity> =
        groupDao.getGroups(appUserId) // MyContab user

    suspend fun getAllGroupsForUser(userId: String, appUserId: String): List<GroupsEntity> {
        val userGroups = getUserGroups(userId)
        val coreGroups = getCoreGroups(appUserId)
        return (coreGroups + userGroups).distinctBy { it.groupName }
    }

    suspend fun getGroupById(groupId: Int): GroupsEntity =
        groupDao.getGroupById(groupId)

    suspend fun insert(group: GroupsEntity) =
        withContext(Dispatchers.IO) {
            groupDao.insertGroup(group)
        }

    suspend fun delete(group: GroupsEntity) =
        withContext(Dispatchers.IO) {
            groupDao.deleteGroup(group)
        }
}
