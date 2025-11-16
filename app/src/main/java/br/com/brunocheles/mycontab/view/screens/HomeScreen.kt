package br.com.brunocheles.mycontab.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.entities.GroupsEntity
import br.com.brunocheles.mycontab.ui.theme.Gray
import br.com.brunocheles.mycontab.ui.theme.Green
import br.com.brunocheles.mycontab.ui.theme.LessLight
import br.com.brunocheles.mycontab.ui.theme.NewGreen
import br.com.brunocheles.mycontab.ui.theme.NewLight
import br.com.brunocheles.mycontab.ui.theme.NewRed
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.Red
import br.com.brunocheles.mycontab.view.animations.AnimationController
import br.com.brunocheles.mycontab.view.components.MonthPickerDialog
import br.com.brunocheles.mycontab.view.components.TransactionItem
import br.com.brunocheles.mycontab.view.components.ValueType
import br.com.brunocheles.mycontab.view.items.ShowValueItem
import br.com.brunocheles.mycontab.view.states.AuthUiState
import br.com.brunocheles.mycontab.view.states.ExpenseUiState
import br.com.brunocheles.mycontab.view.states.IncomeUiState
import br.com.brunocheles.mycontab.viewmodel.states.GroupsUiState
import java.text.DateFormatSymbols
import java.util.Locale

@Composable
fun HomeScreen(
    uiState: AuthUiState,
    expenseUiState: ExpenseUiState,
    incomeUiState: IncomeUiState,
    groupsUiState: GroupsUiState,
    onNewValueClick: (ValueType) -> Unit,
    onEditValueClick: (ValueType) -> Unit,
    onConfirmMonthYear: (Int, Int) -> Unit,
    locale: Locale = Locale.ROOT
) {
    var isFABOpen by remember { mutableStateOf(false) }
    var isMonthChangeOpen by remember { mutableStateOf(false) }

    val selectedMonth = uiState.month
    val selectedYear = uiState.year

    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    val months = DateFormatSymbols(Locale.US).months.filter { it.isNotEmpty() }

    val transitionFAB = updateTransition(targetState = isFABOpen, label = "")
    val transitionMonth = updateTransition(targetState = isMonthChangeOpen, label = "")
    val animationController = remember { AnimationController() }

    val rotationFAB = animationController.animateFloatWithTransition(
        transition = transitionFAB,
        valueForTrue = 45f
    )
    val menuScaleFAB = animationController.animateFloatWithTransition(
        transition = transitionFAB,
        valueForTrue = 1f
    )
    val rotationMonth = animationController.animateFloatWithTransition(
        transition = transitionMonth,
        valueForTrue = 180f
    )

    val sumIncomes = incomeUiState.incomeValuesMonth.sumOf { it?.incomeValue ?: 0.0 }
    val sumExpenses = expenseUiState.expenseValuesMonth.sumOf { it?.expenseValue ?: 0.0 }
    val sumValues = incomeUiState.incomeValuesMonth.sumOf { it?.incomeValue ?: 0.0 } -
            expenseUiState.expenseValuesMonth.sumOf { it?.expenseValue ?: 0.0 }

    val incomesMapped = incomeUiState.incomeValuesMonth.filterNotNull().map { income ->
        ShowValueItem(
            value = income.incomeValue,
            name = income.incomeDesc,
            day = income.incomeDay,
            month = income.incomeMonth,
            year = income.incomeYear,
            groupId = income.incomeGroupId,
            isExpense = false,
            id = income.incomeId,
            groupIcon = income.incomeGroupIcon
        )
    }

// 2. Mapeie os 'expenses' para 'ShowValueItem'
    val expensesMapped = expenseUiState.expenseValuesMonth.filterNotNull().map { expense ->
        ShowValueItem(
            value = expense.expenseValue,
            name = expense.expenseDesc,
            day = expense.expenseDay,
            month = expense.expenseMonth,
            year = expense.expenseYear,
            groupId = expense.expenseGroupId,
            isExpense = true,
            id = expense.expenseId,
            groupIcon = expense.expenseGroupIcon
        )
    }

    val allMonthValues = (incomesMapped + expensesMapped)

    val groupedByDate: Map<Int?, List<ShowValueItem>> = allMonthValues.groupBy { item ->
        item.day
    }

    val sortedGroupedValues: Map<Int, List<ShowValueItem>> = groupedByDate
        .toSortedMap(compareByDescending { it })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                enabled = isFABOpen,
                indication = null,
                interactionSource = interactionSource,
                onClick = {
                    isFABOpen = !isFABOpen
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        )
        {
            Card(
                shape = RoundedCornerShape(bottomStartPercent = 15, bottomEndPercent = 15),
                elevation = CardDefaults.cardElevation(5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LessLight
                )
            )
            {
                Column(
                    modifier = Modifier.padding(
                        start = 30.dp,
                        end = 30.dp,
                        top = 40.dp,
                        bottom = 30.dp
                    )
                )
                {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 30.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Box(
                            modifier = Modifier.width(40.dp)
                        )
                        TextButton(
                            modifier = Modifier.background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(percent = 25)
                            ),
                            onClick = {
                                isMonthChangeOpen = !isMonthChangeOpen
                            }
                        )
                        {
                            Row(
                                modifier = Modifier.wrapContentWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                Text(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .height(28.dp)
                                        .wrapContentHeight(Alignment.CenterVertically),
                                    text = months[selectedMonth].replaceFirstChar {
                                        it.titlecase(
                                            locale
                                        )
                                    },
                                    fontSize = 18.sp,
                                    color = Gray,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    modifier = Modifier
                                        .rotate(rotationMonth),
                                    painter = painterResource(R.drawable.rounded_arrow_drop_down),
                                    contentDescription = "drop_down",
                                    tint = Gray
                                )
                            }
                        }
                        Text(
                            modifier = Modifier
                                .width(40.dp)
                                .height(28.dp)
                                .wrapContentHeight(Alignment.CenterVertically),
                            text = selectedYear.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray,
                            textAlign = TextAlign.End
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "balance".uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Gray
                            )
                            Text(
                                text = "R$%.2f".format(sumValues),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (sumValues > 0) NewGreen else if (sumValues < 0) NewRed else Gray
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxSize()
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    )
                    {
                        SummaryCard(
                            title = "Incomes",
                            amount = sumIncomes,
                            color = NewGreen,
                            icon = painterResource(R.drawable.rounded_arrow_shape_up_stack),
                            iconModifier = Modifier,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onEditValueClick(ValueType.INCOME)
                            }
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                        SummaryCard(
                            title = "Expenses",
                            amount = sumExpenses,
                            color = NewRed,
                            icon = painterResource(R.drawable.rounded_arrow_shape_up_stack),
                            iconModifier = Modifier.rotate(180f),
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onEditValueClick(ValueType.EXPENSE)
                            }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(vertical = 12.dp))
                }
                item {
                    RecentActivityCard(
                        transactions = sortedGroupedValues,
                        groups = groupsUiState.groups,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 20.dp)
                .wrapContentSize(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        )
        {
            if (isFABOpen) {
                FloatingActionMenus(
                    actionMenuScale = menuScaleFAB,
                    onIncomeClick = {
                        onNewValueClick(ValueType.INCOME)
                        isFABOpen = !isFABOpen
                    },
                    onExpenseClick = {
                        onNewValueClick(ValueType.EXPENSE)
                        isFABOpen = !isFABOpen
                    }
                )
                Spacer(modifier = Modifier.padding(vertical = 10.dp))
            }

            FloatingActionButton(
                onClick = {
                    isFABOpen = !isFABOpen
                },
                modifier = Modifier,
                shape = CircleShape,
                containerColor = Principal
            ) {
                Icon(
                    painter = painterResource(R.drawable.rounded_add),
                    tint = Gray,
                    contentDescription = "",
                    modifier = Modifier
                        .rotate(rotationFAB)
                )
            }
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
        }
    }
    AnimatedVisibility(
        visible = isMonthChangeOpen
    ) {
        MonthPickerDialog(
            onDismiss = {
                isMonthChangeOpen = false
            },
            currentYear = selectedYear,
            currentMonth = selectedMonth,
            onConfirm = { newYear, newMonth ->
                onConfirmMonthYear(newYear, newMonth)
            }
        )
    }
}

@Composable
private fun FloatingActionMenus(
    actionMenuScale: Float,
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit
) {
    Button(
        onClick = {
            onIncomeClick()
        },
        modifier = Modifier.scale(actionMenuScale),
        elevation = ButtonDefaults.buttonElevation(4.dp),
        enabled = true,
        shape = CircleShape,
        contentPadding = PaddingValues(start = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Green
        )
    ) {
        Text(
            text = "New Income",
            modifier = Modifier.padding(start = 4.dp),
            color = Gray,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textDecoration = null,
            textAlign = TextAlign.Center
        )
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .padding(8.dp),
            painter = painterResource(R.drawable.rounded_arrow_shape_up_stack),
            tint = Gray.copy(alpha = 0.9f),
            contentDescription = "income"
        )
    }

    Spacer(modifier = Modifier.padding(vertical = 5.dp))

    Button(
        onClick = {
            onExpenseClick()
        },
        modifier = Modifier.scale(actionMenuScale),
        elevation = ButtonDefaults.buttonElevation(4.dp),
        enabled = true,
        shape = CircleShape,
        contentPadding = PaddingValues(start = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Red
        )
    ) {
        Text(
            text = "New Expense",
            modifier = Modifier.padding(start = 4.dp),
            color = Gray,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textDecoration = null,
            textAlign = TextAlign.Center
        )
        Icon(
            modifier = Modifier
                .rotate(180f)
                .clip(CircleShape)
                .padding(8.dp),
            painter = painterResource(R.drawable.rounded_arrow_shape_up_stack),
            tint = Gray,
            contentDescription = "expense"
        )
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    color: Color,
    icon: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iconModifier: Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = NewLight
        )
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = iconModifier
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "R$${"%.2f".format(amount)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun RecentActivityCard(
    transactions: Map<Int, List<ShowValueItem>>,
    groups: List<GroupsEntity?>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = NewLight
        )
    ) {
        Column(Modifier.padding(vertical = 8.dp)) { // Padding vertical menor para a lista
            Text(
                text = "Month Transactions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Gray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .background(color = Gray.copy(0.2f))
            )
            LazyColumn(
                modifier = Modifier
                    .padding(top = 5.dp, end = 5.dp)
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                transactions.forEach { (day, transactions) ->
//                    TransactionItem(transaction, groups = groups)
                    stickyHeader {
                        Surface(
                            color = Color.Transparent,
                            tonalElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(vertical = 6.dp, horizontal = 12.dp),
                                text = "%02d".format(day),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    items(
                        items = transactions
                    ) { transaction ->
                        TransactionItem(
                            item = transaction,
                            groups = groups,
                            isEdit = false,
                            onDelete = {}
                        )
                    }
                }

            }
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreen(
        uiState = AuthUiState(),
        expenseUiState = ExpenseUiState(),
        incomeUiState = IncomeUiState(),
        groupsUiState = GroupsUiState(),
        onNewValueClick = {},
        onEditValueClick = {},
        onConfirmMonthYear = { _, _ -> }
    )
}