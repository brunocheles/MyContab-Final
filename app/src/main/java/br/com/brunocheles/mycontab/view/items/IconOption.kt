package br.com.brunocheles.mycontab.view.items

import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity


data class IconOption(
    val description: String,
    val groupId: Int,
    val groupIcon: String,
    val groupUserId: String
)

fun IconOption.toGroup(): GroupsEntity {
    return GroupsEntity(
        id = groupId,
        groupName = description,
        groupIcon = groupIcon,
        groupUserId = groupUserId
    )
}