package br.com.brunocheles.mycontab.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.components.Expense
import br.com.brunocheles.mycontab.model.components.Income
import br.com.brunocheles.mycontab.model.items.User
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.GreenDark
import br.com.brunocheles.mycontab.ui.theme.GreenMedium
import br.com.brunocheles.mycontab.ui.theme.LessBlack
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.PrincipalLight
import br.com.brunocheles.mycontab.ui.theme.RedMedium
import br.com.brunocheles.mycontab.view.components.CurrencyAmountInputVisualTransformation
import br.com.brunocheles.mycontab.view.components.DatePickerFieldToModal
import br.com.brunocheles.mycontab.view.components.IconUtils
import br.com.brunocheles.mycontab.view.components.ValueType
import br.com.brunocheles.mycontab.view.states.AuthUiState
import br.com.brunocheles.mycontab.viewmodel.states.GroupsUiState
import java.time.LocalDate

@Composable
fun NewValueScreen(
    type: ValueType,
    onClose: () -> Unit,
    authUiState: AuthUiState,
    groupsUiState: GroupsUiState,
    onInsertIncome: (Income) -> Unit,
    onInsertExpense: (Expense) -> Unit
) {
    var selectedDate by rememberSaveable {
        mutableStateOf(LocalDate.of(
            authUiState.year,
            authUiState.month + 1,
            LocalDate.now().dayOfMonth)
        )
    }

    var newValue by rememberSaveable { mutableStateOf("000") }
    var hasValue by remember { mutableStateOf(true) }
    var newDesc by rememberSaveable { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }

    val groupOptions = groupsUiState.groups
        .filterNotNull()
        .map { group ->
            val iconResId = IconUtils.getIconIdByName(group.groupName)
            IconOption(
                painter = painterResource(iconResId),
                description = group.groupName,
                groupId = group.id,
                groupIcon = group.groupName
            )
        }

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
                            .size(40.dp)
                        ,
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
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .fillMaxWidth(),
                )
                {
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .weight(6f),
                        value = newValue,
                        onValueChange = { value ->
                            newValue = value.trimStart('0')
                            hasValue = newValue.isNotEmpty()
                        },
                        singleLine = true,
                        label = {
                            Text(
                                text = "${ type.name.lowercase().replaceFirstChar { it.uppercase() } } value"
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
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .height(70.dp)
                            .weight(1f)
                            .padding(start = 5.dp)

                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Light,
                                )
                                .width(52.dp)
                                .padding(top = 2.dp),
                            text = "Group",
                            textAlign = TextAlign.Center,
                            color = LessBlack,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W400
                        )
                        IconButton(
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .border(
                                    width = 1.dp,
                                    color = PrincipalLight,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .fillMaxWidth(),
                            onClick = { expanded = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = PrincipalLight.copy(alpha = 0.1f)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        {
                            selectedGroup?.let {
                                Icon(
                                    it.painter,
                                    contentDescription = it.description,
                                    tint = LessBlack
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        )
                        {
                            groupOptions.forEach { option ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedGroupId = option.groupId
                                        expanded = false
                                    },
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                option.painter,
                                                contentDescription = option.description
                                            )
                                            Text(
                                                modifier = Modifier.padding(start = 8.dp),
                                                text = option.description
                                            )
                                        }
                                    }
                                )
                            }
                        }
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
                            errorMessage = "Fied(s) missing: ${missing.joinToString(", ")}"
                            hasValue = newValue.isNotBlank() && newValue != "000"
                            return@TextButton
                        }

                        errorMessage = ""
                        hasValue = true

                        val value = newValue.toDouble() / 100.0

                        selectedGroup?.let {
                            when (type) {
                                ValueType.EXPENSE -> onInsertExpense(
                                    Expense(
                                        expenseId = 0,
                                        expenseValue = value,
                                        expenseDesc = newDesc,
                                        expenseGroupId = selectedGroup.groupId,
                                        expenseGroupIcon = selectedGroup.groupIcon,
                                        expenseMonth = selectedDate.monthValue,
                                        expenseYear = selectedDate.year,
                                        expenseDay = selectedDate.dayOfMonth
                                    )
                                )

                                ValueType.INCOME -> onInsertIncome(
                                    Income(
                                        incomeId = 0,
                                        incomeValue = value,
                                        incomeDesc = newDesc,
                                        incomeGroupId = selectedGroup.groupId,
                                        incomeGroupIcon = selectedGroup.groupIcon,
                                        incomeMonth = selectedDate.monthValue,
                                        incomeYear = selectedDate.year,
                                        incomeDay = selectedDate.dayOfMonth
                                    )
                                )
                            }
                        }

                        onClose()
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
    }
}

data class IconOption(
    val painter: Painter,
    val description: String,
    val groupId: Int,
    val groupIcon: String
)

@Composable
@Preview(showBackground = true, apiLevel = 35)
fun NewValueScreenPreview() {
    val fakeUser = User(
        username = "Teste",
        email = "teste@teste.com",
        userId = "testeUserId",
        userConfig = null
    )

    NewValueScreen(
        type = ValueType.INCOME,
        onClose = {},
        authUiState = AuthUiState(
            year = 2025,
            month = 7,
            isLoading = false,
            success = true,
            errorMessage = null,
            userLogged = fakeUser,
            actualScreen = 0
        ),
        groupsUiState = GroupsUiState(),
        onInsertIncome = {},
        onInsertExpense = {}
    )
}