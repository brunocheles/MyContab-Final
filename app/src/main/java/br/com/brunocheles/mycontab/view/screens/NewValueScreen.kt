package br.com.brunocheles.mycontab.view.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.components.Expense
import br.com.brunocheles.mycontab.model.components.Income
import br.com.brunocheles.mycontab.ui.theme.GreenDark
import br.com.brunocheles.mycontab.ui.theme.GreenMedium
import br.com.brunocheles.mycontab.ui.theme.LessBlack
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.PrincipalLight
import br.com.brunocheles.mycontab.ui.theme.RedMedium
import br.com.brunocheles.mycontab.view.components.CurrencyAmountInputVisualTransformation
import br.com.brunocheles.mycontab.view.components.DatePickerFieldToModal
import br.com.brunocheles.mycontab.view.components.GroupSelection
import br.com.brunocheles.mycontab.view.components.IconUtils
import br.com.brunocheles.mycontab.view.components.ValueType
import br.com.brunocheles.mycontab.view.items.IconOption
import br.com.brunocheles.mycontab.view.items.toGroup
import br.com.brunocheles.mycontab.view.viewmodel.AuthViewModel
import br.com.brunocheles.mycontab.view.viewmodel.ExpenseViewModel
import br.com.brunocheles.mycontab.view.viewmodel.GroupViewModel
import br.com.brunocheles.mycontab.view.viewmodel.IncomeViewModel
import java.time.LocalDate


@Composable
fun NewValueScreen(
    type: ValueType,
    onClose: () -> Unit,
    onManageGroupsClick: () -> Unit,
    authViewModel: AuthViewModel,
    groupViewModel: GroupViewModel = hiltViewModel(),
    incomeViewModel: IncomeViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val groupsUiState by groupViewModel.uiState.collectAsState()
    val user = authUiState.userLogged?.userId

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
    }

    val onConfirm: (Double, String, LocalDate, IconOption) -> Unit = { value, desc, date, group ->
        user?.let { userId ->
            when (type) {
                ValueType.INCOME -> {
                    incomeViewModel.insertIncome(
                        userId = userId,
                        income = Income(
                            incomeId = 0,
                            incomeValue = value,
                            incomeDesc = desc,
                            incomeGroupId = group.groupId,
                            incomeGroupIcon = group.groupIcon,
                            incomeMonth = date.monthValue,
                            incomeYear = date.year,
                            incomeDay = date.dayOfMonth
                        )
                    )
                }

                ValueType.EXPENSE -> {
                    expenseViewModel.insertExpense(
                        userId = userId,
                        expense = Expense(
                            expenseId = 0,
                            expenseValue = value,
                            expenseDesc = desc,
                            expenseGroupId = group.groupId,
                            expenseGroupIcon = group.groupIcon,
                            expenseMonth = date.monthValue,
                            expenseYear = date.year,
                            expenseDay = date.dayOfMonth
                        )
                    )
                }
            }
        }
        onClose()
    }

    // 4. Lógica de adicionar Grupo novo
    val onAddNewGroup: (IconOption) -> Unit = { group ->
        groupViewModel.insertGroup(group.toGroup())
    }

    NewValueContent(
        type = type,
        user = user, // Passa o ID do usuário (pode ser null, a UI trata)
        groupOptions = groupOptions,
        initialDate = LocalDate.of(
            authUiState.year,
            authUiState.month + 1,
            LocalDate.now().dayOfMonth
        ),
        onClose = onClose,
        onConfirm = onConfirm,
        onAddNewGroup = onAddNewGroup,
        onManageGroupsClick = onManageGroupsClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewValueContent(
    type: ValueType,
    user: String?,
    groupOptions: List<IconOption>,
    initialDate: LocalDate,
    onClose: () -> Unit,
    onConfirm: (Double, String, LocalDate, IconOption) -> Unit,
    onAddNewGroup: (IconOption) -> Unit,
    onManageGroupsClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val sheetState = rememberStandardBottomSheetState(skipHiddenState = false)

    var selectedDate by rememberSaveable { mutableStateOf(initialDate) }
    var newValue by rememberSaveable { mutableStateOf("000") }
    var hasValue by remember { mutableStateOf(true) }
    var newDesc by rememberSaveable { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var selectedGroupId by rememberSaveable { mutableStateOf<Int?>(null) }

    LaunchedEffect(groupOptions) {
        if (groupOptions.isNotEmpty() && selectedGroupId == null) {
            selectedGroupId = groupOptions.first().groupId
        }
    }

    val selectedGroup: IconOption? = selectedGroupId?.let { id ->
        groupOptions.firstOrNull { it.groupId == id }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Light),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ====== HEADER ======
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp),
                    )
                    Text(
                        text = "New ${type.name}".uppercase(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NewGray
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        IconButton(
                            modifier = Modifier.size(40.dp),
                            onClick = {
                                onClose()
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(R.drawable.rounded_close),
                                contentDescription = "close",
                                tint = NewGray
                            )
                        }
                    }
                }


                // ====== VALUE FIELD ======
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .fillMaxWidth(),
                )
                {
                    OutlinedTextField(
                        modifier = Modifier
                            .height(65.dp)
                            .weight(9f),
                        value = newValue,
                        onValueChange = { value ->
                            newValue = value.trimStart('0')
                            hasValue = newValue.isNotEmpty()
                        },
                        singleLine = true,
                        label = {
                            Text(
                                text = "${
                                    type.name.lowercase().replaceFirstChar { it.uppercase() }
                                } value"
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_attach_money),
                                contentDescription = "money icon",
                                tint = NewGray
                            )
                        },
                        shape = MyContabShapes.extraLarge,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Principal,
                            focusedLabelColor = Principal,
                            unfocusedLabelColor = NewGray,
                            unfocusedBorderColor = PrincipalLight,
                            unfocusedContainerColor = PrincipalLight.copy(
                                alpha = 0.1f
                            ),
                            focusedContainerColor = PrincipalLight.copy(
                                alpha = 0.1f
                            ),
                            unfocusedTextColor = NewGray,
                            focusedTextColor = LessBlack
                        ),
                        isError = !hasValue,
                        visualTransformation = CurrencyAmountInputVisualTransformation(),
                        textStyle = TextStyle(
                            fontSize = 26.sp,
                            textAlign = TextAlign.End
                        )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(65.dp)
                            .weight(2f)
                    ) {
                        OutlinedTextField(
                            value = ".",
                            onValueChange = { },
                            readOnly = true,
                            singleLine = true,
                            modifier = Modifier
                                .matchParentSize(),
                            leadingIcon = {
                                selectedGroup?.let {
                                    val iconResId = IconUtils.getIconIdByName(it.groupIcon)
                                    Icon(
                                        modifier = Modifier
                                            .padding(start = 10.dp)
                                            .size(30.dp),
                                        painter = painterResource(iconResId),
                                        contentDescription = it.description,
                                        tint = Principal
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = "Icon",
                                )
                            },
                            shape = MyContabShapes.extraLarge,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedLeadingIconColor = Principal,
                                focusedLeadingIconColor = Principal,
                                focusedBorderColor = Principal,
                                focusedLabelColor = Principal,
                                unfocusedLabelColor = NewGray,
                                unfocusedBorderColor = PrincipalLight,
                                unfocusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                                focusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                                unfocusedTextColor = NewGray.copy(alpha = 0.7f),
                                focusedTextColor = NewGray
                            )
                        )
                        Spacer(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable(
                                    onClick = {
                                        expanded = !expanded
                                    },
                                    interactionSource = interactionSource,
                                    indication = null
                                )
                        )
                    }
                }

                // ====== DESCRIPTION FIELD ======
                OutlinedTextField(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 5.dp)
                        .fillMaxWidth(),
                    value = newDesc,
                    onValueChange = { newDesc = it },
                    singleLine = true,
                    label = { Text("Description") },
                    placeholder = {
                        Text(
                            "${
                                type.name.lowercase().replaceFirstChar { it.uppercase() }
                            } Description")
                    },
                    shape = MyContabShapes.extraLarge,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Principal,
                        focusedLabelColor = Principal,
                        unfocusedLabelColor = NewGray,
                        unfocusedBorderColor = PrincipalLight,
                        unfocusedContainerColor = PrincipalLight.copy(
                            alpha = 0.1f
                        ),
                        focusedContainerColor = PrincipalLight.copy(
                            alpha = 0.1f
                        ),
                        unfocusedTextColor = NewGray,
                        focusedTextColor = LessBlack
                    )
                )
                // ====== MONTH PICKER ======
                DatePickerFieldToModal(
                    selectedDate = selectedDate,
                    onDateSelected = {
                        selectedDate = it
                    }
                )

                // ====== ERROR MESSAGE ======
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = RedMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // ====== ACTION BUTTONS ======
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 30.dp)
                    .height(50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                // Cancel Button
                TextButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .border(
                            2.dp,
                            RedMedium,
                            MyContabShapes.extraLarge
                        ),
                    onClick = onClose,
                    shape = MyContabShapes.extraLarge,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedMedium.copy(0.2f),
                        contentColor = RedMedium
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Confirm Button
                TextButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .border(
                            2.dp,
                            GreenDark,
                            MyContabShapes.extraLarge
                        ),
                    shape = MyContabShapes.extraLarge,
                    onClick = {
                        val missing = buildList {
                            if (newValue.isBlank() || newValue == "000") add("Value")
                            if (newDesc.isBlank()) add("Description")
                            if (selectedGroup == null) add("Group")
                        }

                        if (missing.isNotEmpty()) {
                            errorMessage = "Field(s) missing: ${missing.joinToString(", ")}"
                            hasValue = newValue.isNotBlank() && newValue != "000"
                            return@TextButton
                        }

                        // 2. Sucesso - Envia dados para o Wrapper
                        errorMessage = ""
                        hasValue = true
                        val value = newValue.toDouble() / 100.0

                        // Chamamos o Wrapper passando os dados limpos
                        selectedGroup?.let { group ->
                            onConfirm(value, newDesc, selectedDate, group)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenMedium.copy(0.2f),
                        contentColor = GreenDark
                    )
                ) {
                    Text("Confirm", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        AnimatedVisibility(
            expanded
        ) {
            selectedGroupId?.let {
                GroupSelection(
                    sheetState = sheetState,
                    onDismiss = {
                        expanded = false
                    },
                    selectedGroupId = it,
                    groups = groupOptions,
                    onConfirm = { selected ->
                        selectedGroupId = selected
                    },
                    user = user!!,
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
@Preview(showBackground = true)
fun NewValueScreenPreview() {
    NewValueContent(
        type = ValueType.EXPENSE,
        user = "MyContabUser",
        groupOptions = emptyList(),
        initialDate = LocalDate.now(),
        onClose = {},
        onConfirm = { _, _, _, _ -> },
        onAddNewGroup = {},
        onManageGroupsClick = {}
    )
}