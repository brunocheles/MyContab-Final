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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity
import br.com.brunocheles.mycontab.ui.theme.LessBlack
import br.com.brunocheles.mycontab.ui.theme.LessLight
import br.com.brunocheles.mycontab.ui.theme.LessWhite
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.NewGreen
import br.com.brunocheles.mycontab.ui.theme.NewLight
import br.com.brunocheles.mycontab.ui.theme.NewRed
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.view.components.EditValueDialog
import br.com.brunocheles.mycontab.view.components.TransactionItem
import br.com.brunocheles.mycontab.view.items.IconOption
import br.com.brunocheles.mycontab.view.items.ShowValueItem
import br.com.brunocheles.mycontab.view.items.toExpense
import br.com.brunocheles.mycontab.view.items.toGroup
import br.com.brunocheles.mycontab.view.items.toIncome
import br.com.brunocheles.mycontab.view.viewmodel.AuthViewModel
import br.com.brunocheles.mycontab.view.viewmodel.ExpenseViewModel
import br.com.brunocheles.mycontab.view.viewmodel.GroupViewModel
import br.com.brunocheles.mycontab.view.viewmodel.IncomeViewModel

@Composable
fun EditValueScreen(
    typeIndex: Int,
    onBackPressed: () -> Unit,
    onManageGroupsClick: () -> Unit,
    authViewModel: AuthViewModel,
    incomeViewModel: IncomeViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    groupViewModel: GroupViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val incomeUiState by incomeViewModel.uiState.collectAsState()
    val expenseUiState by expenseViewModel.uiState.collectAsState()
    val groupsUiState by groupViewModel.uiState.collectAsState()
    val user = authUiState.userLogged?.userId
    val groups = groupsUiState.groups

    val incomesList = remember(incomeUiState.incomeValuesMonth) {
        incomeUiState.incomeValuesMonth
            .filterNotNull()
            .map {
                ShowValueItem(
                    id = it.incomeId,
                    value = it.incomeValue,
                    name = it.incomeDesc,
                    day = it.incomeDay,
                    month = it.incomeMonth,
                    year = it.incomeYear,
                    groupId = it.incomeGroupId,
                    groupIcon = it.incomeGroupIcon,
                    isExpense = false,
                )
            }
    }
    val expensesList = remember(expenseUiState.expenseValuesMonth) {
        expenseUiState.expenseValuesMonth
            .filterNotNull()
            .map {
                ShowValueItem(
                    id = it.expenseId,
                    value = it.expenseValue,
                    name = it.expenseDesc,
                    day = it.expenseDay,
                    month = it.expenseMonth,
                    year = it.expenseYear,
                    groupId = it.expenseGroupId,
                    groupIcon = it.expenseGroupIcon,
                    isExpense = true
                )
            }
    }

    val onEdit: (ShowValueItem) -> Unit = { value ->
        user?.let { userId ->
            if (value.isExpense) {
                expenseViewModel.updateExpense(userId, value.toExpense())
            } else {
                incomeViewModel.updateIncome(userId, value.toIncome())
            }
        }
    }

    val onDelete: (ShowValueItem) -> Unit = { value ->
        user?.let { userId ->
            if (value.isExpense) {
                expenseViewModel.deleteExpense(userId, value.toExpense())
            } else {
                incomeViewModel.deleteIncome(userId, value.toIncome())
            }
        }
    }

    val onAddNewGroup: (IconOption) -> Unit = { group ->
        groupViewModel.insertGroup(group.toGroup())
    }

    EditValueContent(
        typeIndex = typeIndex,
        onBackPressed = onBackPressed,
        onManageGroupsClick = onManageGroupsClick,
        user = user,
        incomesList = incomesList,
        expensesList = expensesList,
        groupsList = groups,
        onEdit = onEdit,
        onAddNewGroup = onAddNewGroup,
        onDelete = onDelete
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditValueContent(
    typeIndex: Int,
    onBackPressed: () -> Unit,
    onManageGroupsClick: () -> Unit,
    user: String?,
    incomesList: List<ShowValueItem>,
    expensesList: List<ShowValueItem>,
    groupsList: List<GroupsEntity?>,
    onEdit: (ShowValueItem) -> Unit,
    onAddNewGroup: (IconOption) -> Unit,
    onDelete: (ShowValueItem) -> Unit
) {
    val sheetState = rememberStandardBottomSheetState(skipHiddenState = false)

    var selectedTabIndex by remember { mutableIntStateOf(typeIndex) }
    val tabTitles = listOf("Incomes", "Expenses")

    val currentList by remember {
        derivedStateOf { if (selectedTabIndex == 0) incomesList else expensesList }
    }
    val totalLabel by remember {
        derivedStateOf { if (selectedTabIndex == 0) "Total Incomes" else "Total Expenses" }
    }
    val totalAmount by remember(currentList) {
        derivedStateOf { currentList.sumOf { it.value ?: 0.0 } }
    }

    var isEditValueOpen by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<ShowValueItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = LessLight)
    )
    {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 20.dp, top = 40.dp, end = 20.dp, bottom = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.size(40.dp),
                    onClick = {
                        onBackPressed()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.rounded_arrow_back_ios_new),
                        contentDescription = "close",
                        tint = LessBlack
                    )
                }
                Text(
                    text = "Month Transactions",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LessBlack
                )
                Box(
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // 1. Abas (Tabs)
                PrimaryTabRow(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clip(CircleShape),
                    selectedTabIndex = selectedTabIndex,
                    containerColor = LessWhite,
                    contentColor = NewGray,
                    indicator = {
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier
                                .tabIndicatorOffset(selectedTabIndex, matchContentSize = false),
                            width = Dp.Unspecified,
                            color = Color.Transparent
                        )
                    },
                    divider = {

                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            modifier = Modifier
                                .background(
                                    color = if (selectedTabIndex == index) Principal else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clip(CircleShape),
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontWeight = FontWeight.Bold) },
                            selectedContentColor = LessBlack,
                            unselectedContentColor = NewGray.copy(alpha = 0.8f)
                        )
                    }
                }

                // 2. Resumo (Total)
                Text(
                    text = "$totalLabel: R$${"%.2f".format(totalAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedTabIndex == 0) NewGreen else NewRed,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                // 3. Lista de Transações
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    items(currentList, key = { it.id!! }) { transaction ->
                        TransactionListItem(
                            groups = groupsList,
                            transaction = transaction,
                            onClick = {
                                isEditValueOpen = !isEditValueOpen
                                selectedItem = transaction
                            },
                            onDeleteClick = { onDelete(transaction) }
                        )
                    }
                }
            }
        }
    }
    AnimatedVisibility(isEditValueOpen) {
        selectedItem?.let { item ->
            if (user != null) {
                EditValueDialog(
                    sheetState = sheetState,
                    onDismiss = {
                        isEditValueOpen = false
                    },
                    valueItem = item,
                    groups = groupsList,
                    onConfirm = { updatedItem ->
                        onEdit(updatedItem)
                        isEditValueOpen = false
                    },
                    user = user,
                    onAddNewGroup = { group ->
                        onAddNewGroup(group)
                    },
                    onManageGroupsClick = {
                        onManageGroupsClick()
                    }
                )
            }
        }
    }
}

@Composable
fun TransactionListItem(
    groups: List<GroupsEntity?>,
    transaction: ShowValueItem,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = NewLight
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier.clickable(
                onClick = onClick
            )
        ) {
            TransactionItem(
                item = transaction,
                height = 60.dp,
                fontSize = 16.sp,
                groups = groups,
                isEdit = true,
                onDelete = onDeleteClick
            )
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
}


@Preview(showBackground = true)
@Composable
fun EditValueScreenPreview() {
    EditValueContent(
        typeIndex = 0,
        onBackPressed = {},
        onManageGroupsClick = {},
        user = "MyContabUser",
        incomesList = emptyList(),
        expensesList = emptyList(),
        groupsList = emptyList(),
        onEdit = {},
        onAddNewGroup = {},
        onDelete = {}
    )
}
