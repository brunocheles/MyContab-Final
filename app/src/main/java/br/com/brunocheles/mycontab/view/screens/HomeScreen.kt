package br.com.brunocheles.mycontab.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.NewGreen
import br.com.brunocheles.mycontab.ui.theme.NewLight
import br.com.brunocheles.mycontab.ui.theme.NewRed
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.view.components.MonthPickerDialog
import br.com.brunocheles.mycontab.view.components.TransactionItem
import br.com.brunocheles.mycontab.view.components.ValueType
import br.com.brunocheles.mycontab.view.items.ShowValueItem
import br.com.brunocheles.mycontab.view.viewmodel.AuthViewModel
import br.com.brunocheles.mycontab.view.viewmodel.ExpenseViewModel
import br.com.brunocheles.mycontab.view.viewmodel.GroupViewModel
import br.com.brunocheles.mycontab.view.viewmodel.IncomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Month
import java.util.Locale
import java.util.SortedMap

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    incomeViewModel: IncomeViewModel = hiltViewModel(),
    groupViewModel: GroupViewModel = hiltViewModel(),
    onNewValueClick: (ValueType) -> Unit,
    onEditValueClick: (Int) -> Unit,
    locale: Locale = Locale.getDefault()
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val incomeUiState by incomeViewModel.uiState.collectAsState()
    val expenseUiState by expenseViewModel.uiState.collectAsState()
    val groupsUiState by groupViewModel.uiState.collectAsState()

    val isLoadingData = incomeUiState.isLoading || expenseUiState.isLoading

    val selectedMonth = authUiState.month
    val selectedYear = authUiState.year

    // 2. Callback para Atualizar Data (Sincroniza todos os ViewModels)
    val onConfirmMonthYear: (Int, Int) -> Unit = { year, month ->
        authViewModel.updateDate(year, month) // Se você tiver isso no Auth ou Control
    }

    // 3. Cálculos Pesados (Feitos aqui com remember para não travar a UI)
    val sumIncomes = remember(incomeUiState.incomeValuesMonth) {
        incomeUiState.incomeValuesMonth.sumOf { it?.incomeValue ?: 0.0 }
    }
    val sumExpenses = remember(expenseUiState.expenseValuesMonth) {
        expenseUiState.expenseValuesMonth.sumOf { it?.expenseValue ?: 0.0 }
    }
    val sumValues = remember(sumIncomes, sumExpenses) {
        sumIncomes - sumExpenses
    }

    // 4. Preparação da Lista Agrupada
    val sortedGroupedValues = remember(incomeUiState.incomeValuesMonth, expenseUiState.expenseValuesMonth) {
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

        (incomesMapped + expensesMapped)
            .filter { it.day != null }
            .groupBy { it.day!! }
            .toSortedMap(compareByDescending { it })
    }

    // 5. Chama a UI Pura
    HomeContent(
        selectedMonth = selectedMonth,
        selectedYear = selectedYear,
        sumIncomes = sumIncomes,
        sumExpenses = sumExpenses,
        balance = sumValues,
        transactions = sortedGroupedValues,
        groups = groupsUiState.groups,
        onNewValueClick = onNewValueClick,
        onEditValueClick = onEditValueClick,
        onConfirmMonthYear = onConfirmMonthYear,
        locale = locale,
        isLoadingData = isLoadingData
    )
}

@Composable
fun HomeContent(
    selectedMonth: Int,
    selectedYear: Int,
    sumIncomes: Double,
    sumExpenses: Double,
    balance: Double,
    transactions: SortedMap<Int, List<ShowValueItem>>,
    groups: List<GroupsEntity?>,
    onNewValueClick: (ValueType) -> Unit,
    onEditValueClick: (Int) -> Unit,
    onConfirmMonthYear: (Int, Int) -> Unit,
    locale: Locale,
    isLoadingData: Boolean
) {
    val scope = rememberCoroutineScope()
    var isLocalLoading by remember { mutableStateOf(false) }
    val showLoadingOverlay = isLoadingData || isLocalLoading

    var isFABOpen by remember { mutableStateOf(false) }
    var isMonthChangeOpen by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    val currentMonthName = remember(selectedMonth, locale) {
        try {
            Month.of(selectedMonth + 1)
                .getDisplayName(java.time.format.TextStyle.FULL, locale)
                .replaceFirstChar { it.titlecase(locale) }
        } catch (e: Exception) {
            "Error"
        }
    }

    // Animação da seta do mês
    val transitionMonth = updateTransition(targetState = isMonthChangeOpen, label = "MonthArrow")
    val rotationMonth by transitionMonth.animateFloat(label = "Rotation") { isOpen ->
        if (isOpen) 180f else 0f
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
            Card(
                shape = RoundedCornerShape(bottomStartPercent = 15, bottomEndPercent = 15),
                elevation = CardDefaults.cardElevation(5.dp),
                colors = CardDefaults.cardColors(
                    containerColor = NewLight
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
                                    text = currentMonthName,
                                    fontSize = 18.sp,
                                    color = NewGray,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    modifier = Modifier
                                        .rotate(rotationMonth),
                                    painter = painterResource(R.drawable.rounded_arrow_drop_down),
                                    contentDescription = "drop_down",
                                    tint = NewGray
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
                            color = NewGray,
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
                                color = NewGray
                            )
                            Text(
                                text = "R$%.2f".format(balance),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (balance > 0) NewGreen else if (balance < 0) NewRed else NewGray
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
                                onEditValueClick(0)
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
                                onEditValueClick(1)
                            }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.padding(vertical = 12.dp))
                }
                item {
                    RecentActivityCard(
                        transactions = transactions,
                        month = selectedMonth,
                        groups = groups,
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
            AnimatedVisibility(
                visible = isFABOpen,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 }, // Começa na metade da altura para baixo
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                ) + fadeIn(),

                // O menu desliza descendo e faz fade out
                exit = slideOutVertically(
                    targetOffsetY = { it / 2 },
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
                ) + fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    FloatingActionMenus(
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
            }

            FloatingActionButton(
                onClick = { isFABOpen = !isFABOpen },
                shape = CircleShape,
                containerColor = Principal
            ) {
                val rotationFab by animateFloatAsState(targetValue = if (isFABOpen) 45f else 0f, label = "rotation")
                Icon(
                    painter = painterResource(R.drawable.rounded_add),
                    tint = NewGray,
                    contentDescription = "",
                    modifier = Modifier
                        .rotate(rotationFab)
                )
            }
        }
        AnimatedVisibility(
            visible = showLoadingOverlay,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.matchParentSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(NewLight) // 🔥 Use uma opacidade forte para cobrir tudo
                    .clickable(enabled = false) {}, // Bloqueia cliques por baixo
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Principal,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(48.dp)
                )
            }
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
                isMonthChangeOpen = false

                scope.launch {
                    isLocalLoading = true

                    delay(350)

                    onConfirmMonthYear(newYear, newMonth)

                    delay(150)

                    isLocalLoading = false
                }
            }
        )
    }
}

@Composable
private fun FloatingActionMenus(
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit
) {
    Button(
        onClick = {
            onIncomeClick()
        },
        elevation = ButtonDefaults.buttonElevation(4.dp),
        enabled = true,
        shape = CircleShape,
        contentPadding = PaddingValues(start = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NewGreen
        )
    ) {
        Text(
            text = "New Income",
            modifier = Modifier.padding(start = 4.dp),
            color = NewGray,
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
            tint = NewGray,
            contentDescription = "income"
        )
    }

    Spacer(modifier = Modifier.padding(vertical = 5.dp))

    Button(
        onClick = {
            onExpenseClick()
        },
        elevation = ButtonDefaults.buttonElevation(4.dp),
        enabled = true,
        shape = CircleShape,
        contentPadding = PaddingValues(start = 10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NewRed
        )
    ) {
        Text(
            text = "New Expense",
            modifier = Modifier.padding(start = 4.dp),
            color = NewGray,
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
            tint = NewGray,
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
        modifier = modifier,
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = NewLight
        )
    ) {
        Column(
            Modifier
                .clickable(onClick = onClick)
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
    transactions: SortedMap<Int, List<ShowValueItem>>,
    month: Int,
    groups: List<GroupsEntity?>,
    modifier: Modifier = Modifier
) {
    val showMonth = month + 1
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
                color = NewGray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .background(color = NewGray.copy(0.2f))
            )
            LazyColumn(
                modifier = Modifier
                    .padding(top = 5.dp, end = 5.dp)
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
            ) {
                transactions.forEach { (day, transactions) ->
//                    TransactionItem(transaction, groups = groups)
                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = NewLight,
                            tonalElevation = 4.dp
                        ) {
                            Row(
                                // Padding maior para "respirar"
                                modifier = Modifier.padding(start = 8.dp, top = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    // Ícone para dar o contexto de data
                                    painter = painterResource(R.drawable.rounded_date_range), // Use seu ícone de calendário
                                    contentDescription = "Data",
                                    tint = Principal, // Cor de destaque do seu tema
                                    modifier = Modifier.size(16.dp) // Ícone pequeno
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "%02d/%02d".format(day, showMonth),
                                    fontWeight = FontWeight.Bold,
                                    color = NewGray, // Cor do texto
                                    style = MaterialTheme.typography.titleSmall // Estilo de fonte
                                )
                            }
                        }
                    }
                    items(
                        items = transactions,
                        key = {Pair(it.isExpense, it.id)}
                    ) { transaction ->
                        TransactionItem(
                            item = transaction,
                            height = 34.dp,
                            fontSize = 14.sp,
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
@Preview(showBackground = true)
fun HomeScreenPreview() {
    // 1. Dados falsos de Grupos
    val mockGroups = listOf(
        GroupsEntity(id = 1, groupName = "Casa", groupIcon = "Home", groupUserId = "1"),
        GroupsEntity(id = 2, groupName = "Lazer", groupIcon = "Trip", groupUserId = "1"),
        GroupsEntity(id = 3, groupName = "Mercado", groupIcon = "Shopping", groupUserId = "1")
    )

    // 2. Dados falsos de Transações (Map<Dia, Lista>)
    val mockTransactions = sortedMapOf(
        12 to listOf(
            ShowValueItem(
                id = 1, value = 150.50, name = "Compra Semanal",
                day = 12, month = 10, year = 2025,
                groupId = 3, groupIcon = "Shopping", isExpense = true
            ),
            ShowValueItem(
                id = 2, value = 2500.00, name = "Salário",
                day = 12, month = 10, year = 2025,
                groupId = 1, groupIcon = "Wage", isExpense = false
            )
        ),
        10 to listOf(
            ShowValueItem(
                id = 3, value = 45.00, name = "Uber",
                day = 10, month = 10, year = 2025,
                groupId = 2, groupIcon = "Trip", isExpense = true
            )
        )
    )
    HomeContent(
        selectedMonth = 10,
        selectedYear = 2025,
        sumIncomes = 5000.00,
        sumExpenses = 4500.25,
        balance = 5000.00 - 4500.25,
        transactions = mockTransactions,
        groups = mockGroups,
        onNewValueClick = {},
        onEditValueClick = {},
        onConfirmMonthYear = {_,_ -> },
        locale = Locale.ROOT,
        isLoadingData = false
    )
}