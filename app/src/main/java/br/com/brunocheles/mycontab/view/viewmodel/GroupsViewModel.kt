package br.com.brunocheles.mycontab.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity
import br.com.brunocheles.mycontab.model.data.repositories.GroupRepository
import br.com.brunocheles.mycontab.model.di.DataStoreManager
import br.com.brunocheles.mycontab.model.di.ResourceProvider
import br.com.brunocheles.mycontab.viewmodel.states.GroupsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    dataStoreManager: DataStoreManager,
    resourceProvider: ResourceProvider
) : ViewModel() {
    val appUser = resourceProvider.getString(R.string.app_user_name)
    private val coreGroupsList = listOf(
        GroupsEntity(0, "Wallet","Wallet",appUser),
        GroupsEntity(0, "Card","Card", appUser),
        GroupsEntity(0, "Home", "Home", appUser),
        GroupsEntity(0, "Trip","Trip", appUser),
        GroupsEntity(0, "Wage", "Wage",appUser),
        GroupsEntity(0, "Vehicle", "Vehicle",appUser),
        GroupsEntity(0, "Shopping", "Shopping",appUser)
    )

    init {
        // Inicialização única do sistema (mantida igual)
        initializeSystemCoreGroups()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<GroupsUiState> = dataStoreManager.user
        .flatMapLatest { userData ->
            // Se userData for null, usa apenas appUser. Se tiver ID, usa o ID.
            val currentUserId = userData?.userId ?: appUser

            // Conecta no fluxo do repositório
            groupRepository.getAllGroupsStream(userId = currentUserId, appUserId = appUser)
        }
        .map { groups ->
            GroupsUiState(groups = groups)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GroupsUiState(isLoading = true) // Pode adicionar isLoading no seu State se quiser
        )

    // Inicializa os grupos que pertencem ao "sistema" (appUser)
    fun insertGroup(group: GroupsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.insert(group)
        }
    }

    fun updateGroup(group: GroupsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.update(group)
        }
    }

    fun deleteGroup(group: GroupsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            groupRepository.delete(group)
        }
    }

    private fun initializeSystemCoreGroups() {
        viewModelScope.launch(Dispatchers.IO) {
            // Como mudamos para Flow, aqui usamos .first() para pegar o valor atual uma única vez
            val existingGroups = groupRepository.getAllGroupsStream(appUser, appUser).first()

            if (existingGroups.isEmpty()) {
                println("✅ Criando grupos core do SISTEMA")
                coreGroupsList.forEach { groupCoreItem ->
                    groupRepository.insert(groupCoreItem.copy(id = 0, groupUserId = appUser))
                }
            }
        }
    }
}