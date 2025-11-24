package br.com.brunocheles.mycontab.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.ui.theme.LessBlack
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.NewLight
import br.com.brunocheles.mycontab.ui.theme.RedMedium
import br.com.brunocheles.mycontab.view.components.EditGroupBottomSheet
import br.com.brunocheles.mycontab.view.components.IconUtils
import br.com.brunocheles.mycontab.view.items.IconOption
import br.com.brunocheles.mycontab.view.items.toGroup
import br.com.brunocheles.mycontab.view.viewmodel.AuthViewModel
import br.com.brunocheles.mycontab.view.viewmodel.GroupViewModel

@Composable
fun ManageGroupsScreen(
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val groupsUiState by groupViewModel.uiState.collectAsState()
    val user = authUiState.userLogged?.userId

    val systemUserId = groupViewModel.appUser

    val groupOptions = remember(groupsUiState.groups) {
        groupsUiState.groups
            .filterNotNull()
            .map { group ->
                IconOption(
                    description = group.groupName,
                    groupId = group.id,
                    groupIcon = group.groupIcon,
                    groupUserId = group.groupUserId,
                )
            }
            .sortedBy { it.groupId }
    }

    val onAddNewGroup: (Boolean, IconOption) -> Unit = { isEdit, group ->
        val groupFormatted = group.toGroup()
        when (isEdit) {
            true -> groupViewModel.updateGroup(groupFormatted)
            false -> groupViewModel.insertGroup(groupFormatted)
        }
    }

    val onDelete: (IconOption) -> Unit = { group ->
        val groupFormatted = group.toGroup()
        groupViewModel.deleteGroup(groupFormatted)
    }

    ManageGroupsContent(
        user = user,
        systemUserId = systemUserId,
        groupOptions = groupOptions,
        onBackClick = onBackClick,
        onAddNewGroup = onAddNewGroup,
        onDeleteClick = onDelete
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageGroupsContent(
    user: String?,
    systemUserId: String,
    groupOptions: List<IconOption>,
    onBackClick: () -> Unit,
    onAddNewGroup: (Boolean, IconOption) -> Unit,
    onDeleteClick: (IconOption) -> Unit
) {
    val sheetState = rememberStandardBottomSheetState(skipHiddenState = false)

    var addNewGroup by remember { mutableStateOf(false) }
    var selectedGroup by remember { mutableStateOf<IconOption?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = NewLight)
    )
    {
        Column {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 40.dp, end = 20.dp, bottom = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = {
                        onBackClick()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_arrow_back_ios_new),
                        contentDescription = "close",
                        tint = LessBlack
                    )
                }
                Text(
                    text = "Groups",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LessBlack
                )
                Box(
                    modifier = Modifier.size(40.dp)
                )
            }
            LazyColumn(
                modifier = Modifier.padding(
                    vertical = 20.dp,
                    horizontal = 20.dp
                )
            )
            {
                items(
                    items = groupOptions,
                    key = { it.groupId }
                ) { item ->
                    ShowGroupEditable(
                        icon = item,
                        systemUserId = systemUserId,
                        onDeleteClick = {
                            onDeleteClick(item)
                        },
                        onEditClick = {
                            addNewGroup = !addNewGroup
                            selectedGroup = item
                        }
                    )
                    HorizontalDivider(
                        color = NewGray.copy(alpha = 0.1f),
                        thickness = 0.5.dp
                    )
                }
                item {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = {
                                    addNewGroup = !addNewGroup
                                    selectedGroup = null
                                }
                            ),
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_add),
                                contentDescription = "AddGroup",
                                modifier = Modifier.size(24.dp),
                                tint = NewGray
                            )
                        },
                        headlineContent = {
                            Text(
                                text = "Add New Group",
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.SemiBold,
                                color = NewGray,
                                fontSize = 14.sp
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        )
                    )
                }
            }
        }
        AnimatedVisibility(
            addNewGroup
        ) {
            EditGroupBottomSheet(
                sheetState = sheetState,
                onDismiss = {
                    addNewGroup = false
                },
                item = selectedGroup,
                userId = user,
                onConfirm = { newGroup ->
                    onAddNewGroup(selectedGroup != null, newGroup)
                }
            )
        }
    }
}

@Composable
fun ShowGroupEditable(
    icon: IconOption?,
    systemUserId: String,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val iconResId = IconUtils.getIconIdByName(icon?.groupIcon)

    icon?.let {
        ListItem(
            modifier = Modifier
                .fillMaxWidth(),
            leadingContent = {
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        painter = painterResource(iconResId),
                        contentDescription = it.description,
                        modifier = Modifier.size(24.dp),
                        tint = NewGray
                    )
                }
            },
            headlineContent = {
                Text(
                    text = it.description,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                    color = NewGray,
                    fontSize = 14.sp
                )
            },
            trailingContent = {
                if (icon.groupUserId != systemUserId) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        IconButton(
                            modifier = Modifier,
                            shape = CircleShape,
                            onClick = {
                                onEditClick()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_edit_filled),
                                contentDescription = "edit",
                                tint = Color.Gray
                            )
                        }
                        Spacer(
                            modifier = Modifier.width(4.dp)
                        )
                        IconButton(
                            modifier = Modifier,
                            shape = CircleShape,
                            onClick = {
                                onDeleteClick()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.rounded_delete),
                                contentDescription = "delete",
                                tint = RedMedium
                            )
                        }
                    }
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}


@Preview
@Composable
fun ManageGroupsScreenPreview() {
    ManageGroupsContent(
        user = null,
        systemUserId = "MyContabUser",
        groupOptions = emptyList(),
        onBackClick = {},
        onAddNewGroup = { _, _ -> },
        onDeleteClick = {}
    )
}