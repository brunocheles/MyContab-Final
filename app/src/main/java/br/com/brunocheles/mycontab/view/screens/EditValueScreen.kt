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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
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
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.entities.GroupsEntity
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
import br.com.brunocheles.mycontab.view.items.ShowValueItem
import br.com.brunocheles.mycontab.view.states.ExpenseUiState
import br.com.brunocheles.mycontab.view.states.IncomeUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditValueScreen(
    typeIndex: Int,
    incomeUiState: IncomeUiState,
    expenseUiState: ExpenseUiState,
    groups: List<GroupsEntity?>,
    onEdit: (ShowValueItem) -> Unit,
    onDelete: (ShowValueItem) -> Unit,
    onBackPressed: () -> Unit
) {
    var isEditValueOpen by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<ShowValueItem?>(null) }
    var selectedTabIndex by remember { mutableIntStateOf(typeIndex) }
    val tabTitles = listOf("Incomes", "Expenses")
    val sheetState = rememberStandardBottomSheetState(skipHiddenState = false)

    val incomesList = incomeUiState.incomeValuesMonth
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
    val expensesList = expenseUiState.expenseValuesMonth
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

    val currentList = if (selectedTabIndex == 0) incomesList else expensesList
    val totalAmount = currentList.sumOf { it.value!! }
    val totalLabel = if (selectedTabIndex == 0) "Total Incomes" else "Total Expenses"

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
                                    color = if(selectedTabIndex == index) Principal else Color.Transparent,
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
                    color = if(selectedTabIndex == 0) NewGreen else NewRed,
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
                            groups = groups,
                            transaction = transaction,
                            onClick = {
                                isEditValueOpen = true
                                selectedItem = transaction
                            },
                            onDeleteClick = { onDelete(transaction) }
                        )
                    }
                }
            }
        }
    }
    AnimatedVisibility(isEditValueOpen && selectedItem != null) {
        selectedItem?.let { item ->
            EditValueDialog(
                sheetState = sheetState,
                onDismiss = {
                    isEditValueOpen = false
                    selectedItem = null
                },
                valueItem = item,
                groups = groups,
                onConfirm = { updatedItem ->
                    onEdit(updatedItem)
                    isEditValueOpen = false
                    selectedItem = null
                }
            )
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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LessLight
    ) {
        EditValueScreen(
            typeIndex = 0,
            incomeUiState = IncomeUiState(),
            expenseUiState = ExpenseUiState(),
            groups = emptyList(),
            onEdit = {},
            onBackPressed = {},
            onDelete = {}
        )
    }
}
