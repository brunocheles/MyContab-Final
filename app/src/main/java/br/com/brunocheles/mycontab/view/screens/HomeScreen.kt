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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.ui.theme.Gray
import br.com.brunocheles.mycontab.ui.theme.Green
import br.com.brunocheles.mycontab.ui.theme.GreenDark
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.Red
import br.com.brunocheles.mycontab.ui.theme.RedDark
import br.com.brunocheles.mycontab.view.animations.AnimationController
import br.com.brunocheles.mycontab.view.components.MonthPickerDialog
import br.com.brunocheles.mycontab.view.components.ShowValue
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
    var isIncomesOpen by remember { mutableStateOf(false) }
    var isExpensesOpen by remember { mutableStateOf(false) }
    var isMonthChangeOpen by remember { mutableStateOf(false) }

    val selectedMonth = uiState.month
    val selectedYear = uiState.year

    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    val months = DateFormatSymbols(Locale.US).months.filter { it.isNotEmpty() }

    val transitionFAB = updateTransition(targetState = isFABOpen, label = "")
    val transitionIncomes = updateTransition(targetState = isIncomesOpen, label = "")
    val transitionExpenses = updateTransition(targetState = isExpensesOpen, label = "")
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
    val rotationIncome = animationController.animateFloatWithTransition(
        transition = transitionIncomes,
        valueForTrue = 180f
    )
    val rotationExpense = animationController.animateFloatWithTransition(
        transition = transitionExpenses,
        valueForTrue = 180f
    )
    val rotationMonth = animationController.animateFloatWithTransition(
        transition = transitionMonth,
        valueForTrue = 180f
    )

    val sumIncomes = incomeUiState.incomeValuesMonth.sumOf { it?.incomeValue ?: 0.0 }
    val sumExpenses = expenseUiState.expenseValuesMonth.sumOf { it?.expenseValue ?: 0.0 }
    val sumValues = incomeUiState.incomeValuesMonth.sumOf { it?.incomeValue ?: 0.0 } -
            expenseUiState.expenseValuesMonth.sumOf { it?.expenseValue ?: 0.0 }

    val incomesGroupedByDate = incomeUiState.incomeValuesMonth
        .filterNotNull()
        .sortedWith(compareBy { it.incomeDay })
        .groupBy { income ->
            "%02d".format(income.incomeDay)
        }

    val expensesGroupedByDate = expenseUiState.expenseValuesMonth
        .filterNotNull()
        .sortedWith(compareBy { it.expenseDay })
        .groupBy { income ->
            "%02d".format(income.expenseDay)
        }

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
            Box(
                modifier = Modifier.background(
                    color = Principal,
                    shape = RoundedCornerShape(bottomStartPercent = 15, bottomEndPercent = 15)
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
                            .padding(bottom = 40.dp),
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
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Gray
                            )
                            Text(
                                text = "R$%.2f".format(sumValues),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Gray
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                )
                {
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(12.dp))
                            .clickable(
                                onClick = {
                                    isIncomesOpen = !isIncomesOpen
                                }
                            )
                            .background(
                                color = Green.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Box (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            contentAlignment = Alignment.Center
                        )
                        {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.Start
                                ){
                                    Text(
                                        text = "incomes".uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Gray.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "R$%.2f".format(sumIncomes),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Gray
                                    )
                                }
                                Icon(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .rotate(rotationIncome),
                                    painter = painterResource(R.drawable.rounded_arrow_drop_down),
                                    contentDescription = "drop_down",
                                    tint = Gray
                                )
                            }
                        }
                        AnimatedVisibility(visible = isIncomesOpen)
                        {
                            Column {
                                LazyColumn(
                                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                                )
                                {
//                                    items(incomeUiState.incomeValuesMonth) { income ->
//                                        ShowValue(
//                                            layoutDirection = LayoutDirection.Ltr,
//                                            item = ShowValueItem(
//                                                value = income?.incomeValue,
//                                                name = income?.incomeDesc,
//                                                groupId = income?.incomeGroupId
//                                            ),
//                                            groups = groupsUiState.groups // 👈 passa aqui
//                                        )
//                                    }
                                    incomesGroupedByDate.forEach { (day, incomesOfDay) ->
                                        stickyHeader {
                                            Surface(
                                                color = Color.Transparent,
                                                tonalElevation = 4.dp,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    modifier = Modifier
                                                        .padding(vertical = 6.dp, horizontal = 12.dp),
                                                    text = day,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        items(incomesOfDay) { income ->
                                            ShowValue(
                                                layoutDirection = LayoutDirection.Rtl,
                                                item = ShowValueItem(
                                                    value = income.incomeValue,
                                                    name = income.incomeDesc,
                                                    groupId = income.incomeGroupId
                                                ),
                                                groups = groupsUiState.groups
                                            )
                                        }
                                    }
                                }
                                Button (
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 1.dp, horizontal = 10.dp),
                                    onClick = {
                                        onEditValueClick(ValueType.INCOME)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GreenDark.copy(alpha = 0.2f),
                                        contentColor = Gray
                                    ),
                                    shape = MyContabShapes.medium
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Edit Incomes",
                                            fontSize = 11.sp
                                        )
                                        Icon(
                                            painter = painterResource(R.drawable.rounded_edit_arrow_up),
                                            contentDescription = "edit income icon"
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 6.dp))
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(12.dp))
                            .clickable(
                                onClick = {
                                    isExpensesOpen = !isExpensesOpen
                                }
                            )
                            .background(
                                color = Red.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Box (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            contentAlignment = Alignment.Center
                        )
                        {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .rotate(rotationExpense),
                                    painter = painterResource(R.drawable.rounded_arrow_drop_down),
                                    contentDescription = "drop_down",
                                    tint = Gray
                                )
                                Column(
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "expenses".uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Gray.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "R$%.2f".format(sumExpenses),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Gray
                                    )
                                }
                            }

                        }
                        AnimatedVisibility(visible = isExpensesOpen) {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                LazyColumn(
                                    modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
                                ) {
//                                    items(expenseUiState.expenseValuesMonth) { expense ->
//                                        ShowValue(
//                                            layoutDirection = LayoutDirection.Rtl,
//                                            item = ShowValueItem(
//                                                value = expense?.expenseValue,
//                                                name = expense?.expenseDesc,
//                                                groupId = expense?.expenseGroupId
//                                            ),
//                                            groups = groupsUiState.groups // 👈 passa aqui
//                                        )
//                                    }

                                    expensesGroupedByDate.forEach { (day, expensesOfDay) ->
                                        stickyHeader {
                                            Surface(
                                                color = Color.Transparent,
                                                tonalElevation = 4.dp,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    modifier = Modifier
                                                        .padding(vertical = 6.dp, horizontal = 12.dp),
                                                    text = day,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        items(expensesOfDay) { expense ->
                                            ShowValue(
                                                layoutDirection = LayoutDirection.Ltr,
                                                item = ShowValueItem(
                                                    value = expense.expenseValue,
                                                    name = expense.expenseDesc,
                                                    groupId = expense.expenseGroupId
                                                ),
                                                groups = groupsUiState.groups
                                            )
                                        }
                                    }
                                }
                                Button (
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 1.dp, horizontal = 10.dp),
                                    onClick = {
                                        onEditValueClick(ValueType.EXPENSE)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RedDark.copy(alpha = 0.2f),
                                        contentColor = Gray
                                    ),
                                    shape = MyContabShapes.medium
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Edit Expenses",
                                            fontSize = 11.sp
                                        )
                                        Icon(
                                            painter = painterResource(R.drawable.rounded_edit_arrow_down),
                                            contentDescription = "edit expense icon"
                                        )
                                    }
                                }
                            }
                        }
                    }
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
                shape = MyContabShapes.extraLarge,
                containerColor = Principal
            ) {
//                AnimatedVisibility(!isFABOpen) {
//                    Icon(
//                        painter = painterResource(R.drawable.rounded_add),
//                        tint = Gray,
//                        contentDescription = "",
//                        modifier = Modifier
//                            .size(30.dp)
//                            .rotate(rotationFAB)
//                    )
//                }
//                AnimatedVisibility(isFABOpen) {
//                    Icon(
//                        painter = painterResource(R.drawable.rounded_close),
//                        tint = Gray,
//                        contentDescription = "",
//                        modifier = Modifier
//                            .size(30.dp)
//                            .rotate(rotationFAB)
//                    )
//                }
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
        enabled = true,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(top = 5.dp, bottom = 5.dp, end = 5.dp, start = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Green.copy(0.8f)
        )
    ) {
        Text(
            text = "New Income",
            modifier = Modifier,
            color = Gray.copy(alpha = 0.9f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textDecoration = null,
            textAlign = TextAlign.Center
        )
        Icon(
            modifier = Modifier,
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
        enabled = true,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(top = 5.dp, bottom = 5.dp, end = 5.dp, start = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Red.copy(0.8f)
        )
    ) {
        Text(
            text = "New Expense",
            modifier = Modifier,
            color = Gray,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            textDecoration = null,
            textAlign = TextAlign.Center
        )
        Icon(
            modifier = Modifier.rotate(180f),
            painter = painterResource(R.drawable.rounded_arrow_shape_up_stack),
            tint = Gray,
            contentDescription = "expense"
        )
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