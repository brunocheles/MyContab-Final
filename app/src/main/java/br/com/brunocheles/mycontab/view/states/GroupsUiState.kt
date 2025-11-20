package br.com.brunocheles.mycontab.viewmodel.states

import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity


data class GroupsUiState (
    val groups: List<GroupsEntity?> = emptyList()
)