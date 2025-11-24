package br.com.brunocheles.mycontab.model.data.repositories

import br.com.brunocheles.mycontab.model.dao.GroupDao
import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val groupDao: GroupDao
) {
    fun getAllGroupsStream(userId: String, appUserId: String): Flow<List<GroupsEntity>> {
        return groupDao.getGroupsStream(userId, appUserId)
            .map { list ->
                // Aplica sua lógica de negócio aqui dentro do fluxo
                // Sempre que o banco mudar, essa linha roda sozinha
                list.distinctBy { it.groupName }
                    // Opcional: Ordenar por nome ou ID para garantir consistência visual
                    .sortedBy { it.groupName }
            }
            .flowOn(Dispatchers.IO) // Garante que o cálculo pesado rode em IO
    }

    suspend fun getGroupById(groupId: Int): GroupsEntity? =
        groupDao.getGroupById(groupId)

    suspend fun insert(group: GroupsEntity) = groupDao.insertGroup(group)

    suspend fun update(group: GroupsEntity) = groupDao.updateGroup(group)

    suspend fun delete(group: GroupsEntity) = groupDao.deleteGroup(group)
}
