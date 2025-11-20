package br.com.brunocheles.mycontab.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.data.repositories.GroupRepository
import br.com.brunocheles.mycontab.model.data.repositories.UserRepository
import br.com.brunocheles.mycontab.model.di.DataStoreManager
import br.com.brunocheles.mycontab.model.di.ResourceProvider
import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity
import br.com.brunocheles.mycontab.viewmodel.states.GroupsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val dataStoreManager: DataStoreManager,
    private val resourceProvider: ResourceProvider
) : ViewModel() {
    val appUser = resourceProvider.getString(R.string.app_user_name)
    private val coreGroupsList = listOf(
        GroupsEntity(0, "Wallet", appUser),
        GroupsEntity(0, "Card",  appUser),
        GroupsEntity(0, "Home",  appUser),
        GroupsEntity(0, "Trip", appUser),
        GroupsEntity(0, "Wage", appUser),
        GroupsEntity(0, "Vehicle", appUser),
        GroupsEntity(0, "Shopping", appUser)
    )
    private val _groups = MutableStateFlow<List<GroupsEntity>>(emptyList())
    val uiState: StateFlow<GroupsUiState> = _groups.map { groups -> GroupsUiState(groups = groups) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            GroupsUiState()
        )

    init {
        initializeSystemCoreGroups()
        observeUserFromDataStore()
    }

    // Inicializa os grupos que pertencem ao "sistema" (appUser)
    private fun initializeSystemCoreGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            val existingGroups = groupRepository.getAllGroupsForUser(appUser, appUser)
            if (existingGroups.isEmpty()) {
                println("✅ Criando grupos core do SISTEMA (userId=$appUser)")
                coreGroupsList.forEach { groupCoreItem ->
                    groupRepository.insert(groupCoreItem.copy(groupUserId = appUser))
                }
            } else {
                println("ℹ️ Grupos core do SISTEMA já existem.")
            }
        }
    }

    private fun observeUserFromDataStore() {
        viewModelScope.launch {
            dataStoreManager.user.collect { userData ->
                if (userData != null && userData.userId != null) {
                    // Usuário logou: carrega os grupos dele + os do sistema
                    refreshGroups(userData.userId)
                } else {
                    // Ninguém logado: carrega apenas os grupos do sistema (opcional, para modo visitante)
                    refreshGroups(appUser)
                }
            }
        }
    }

    /** * Cria os grupos core apenas na primeira execução. */
    private suspend fun initializeCoreGroups(userId: String) {
        val isFirstStart = dataStoreManager.isFirstStart.first()
        if (isFirstStart) {
            val existingGroups = groupRepository.getAllGroupsForUser(
                userId,
                appUser
            )
            if (existingGroups.isEmpty()) {
                println("✅ Criando grupos core para userId=$userId")
                coreGroupsList.forEach { groupCoreItem ->
                    groupRepository.insert(
                        groupCoreItem.copy(groupUserId = userId)
                    )
                }
            } else {
                println("ℹ️ Grupos core já existem para userId=$userId")
            }
            dataStoreManager.setFirstStart(false)
        }
    }

    fun getGroupById(groupId: Int): StateFlow<GroupsEntity?> {
        val state = MutableStateFlow<GroupsEntity?>(null)
        viewModelScope.launch(Dispatchers.IO) {
            val group = groupRepository.getGroupById(groupId)
            state.value = group
        }
        return state
    }

    fun getGroups(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allGroups = groupRepository.getAllGroupsForUser(userId, appUser)
            _groups.value = allGroups
        }
    }

    fun insertGroup(group: GroupsEntity) {
        viewModelScope.launch(Dispatchers.IO) { groupRepository.insert(group) }
    }

    fun deleteGroup(group: GroupsEntity) {
        viewModelScope.launch(Dispatchers.IO) { groupRepository.delete(group) }
    }

    fun refreshGroups(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _groups.value = groupRepository.getAllGroupsForUser(userId, appUser)
        }
    }
}