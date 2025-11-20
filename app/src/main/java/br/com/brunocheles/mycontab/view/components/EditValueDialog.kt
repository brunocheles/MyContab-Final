package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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
import br.com.brunocheles.mycontab.model.data.entities.GroupsEntity
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.PrincipalLight
import br.com.brunocheles.mycontab.view.items.ShowValueItem
import br.com.brunocheles.mycontab.view.screens.IconOption
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditValueDialog(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    valueItem: ShowValueItem,
    groups: List<GroupsEntity?>,
    onConfirm: (ShowValueItem) -> Unit
) {
    var valueDesc by remember { mutableStateOf("") }
    var newValue by rememberSaveable { mutableStateOf("000") }
    var hasValue by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var selectedDay by remember { mutableIntStateOf(valueItem.day ?: LocalDate.now().dayOfMonth) }
    var selectedYear by remember { mutableIntStateOf(valueItem.year ?: LocalDate.now().year) }
    var selectedMonth by remember {
        mutableIntStateOf(
            valueItem.month ?: LocalDate.now().monthValue
        )
    }
    valueDesc = valueItem.name.toString()

    var selectedDate by rememberSaveable {
        mutableStateOf(LocalDate.of(
            selectedYear,
            selectedMonth,
            selectedDay)
        )
    }

    val actualValue = String.format(Locale.US, "%.2f", valueItem.value)

    newValue = actualValue.replace(".", "")

    val groupOptions = groups
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

    var selectedGroupId by rememberSaveable { mutableStateOf(valueItem.groupId) }

    val selectedGroup: IconOption? = selectedGroupId?.let { id ->
        groupOptions.firstOrNull { it.groupId == id }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = Light
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        )
        {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(9f)
                            .height(65.dp),
                        value = valueDesc,
                        onValueChange = { valueDesc = it },
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
                            unfocusedTextColor = NewGray.copy(alpha = 0.7f),
                            focusedTextColor = NewGray
                        )
                    )
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .weight(2f)
                            .size(70.dp)
                            .padding(start = 5.dp)

                    ) {
                        Text(
                            modifier = Modifier
                                .background(
                                    color = Light,
                                )
                                .width(52.dp)
                                .padding(top = 2.dp),
                            text = "Group",
                            textAlign = TextAlign.Center,
                            color = NewGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.W400
                        )
                        IconButton(
                            modifier = Modifier
                                .padding(top = 1.dp)
                                .border(
                                    width = 1.dp,
                                    color = PrincipalLight,
                                    shape = MyContabShapes.extraLarge
                                )
                                .size(50.dp),
                            onClick = { expanded = true },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = PrincipalLight.copy(alpha = 0.1f)
                            ),
                            shape = MyContabShapes.extraLarge
                        )
                        {
                            selectedGroup?.let {
                                Icon(it.painter, contentDescription = it.description)
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
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    value = newValue,
                    onValueChange = { value ->
                        newValue = value.trimStart('0')
                        hasValue = newValue.isNotEmpty()
                    },
                    singleLine = true,
                    label = { Text("Value") },
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
                        unfocusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                        focusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                        unfocusedTextColor = NewGray.copy(alpha = 0.7f),
                        focusedTextColor = NewGray
                    ),
                    isError = !hasValue,
                    visualTransformation = CurrencyAmountInputVisualTransformation(),
                    textStyle = TextStyle(
                        fontSize = 26.sp,
                        textAlign = TextAlign.End
                    )
                )

                DatePickerFieldToModal(
                    selectedDate = selectedDate
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    horizontalArrangement = Arrangement.End
                )
                {
                    TextButton(
                        modifier = Modifier.height(40.dp),
                        shape = RectangleShape,
                        contentPadding = PaddingValues(5.dp),
                        onClick = {
                            onDismiss()
                        }
                    )
                    {
                        Text(
                            modifier = Modifier
                                .height(30.dp)
                                .wrapContentHeight(Alignment.CenterVertically),
                            text = "Cancel",
                            fontSize = 14.sp,
                            color = Principal,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                    TextButton(
                        modifier = Modifier.height(40.dp),
                        shape = RectangleShape,
                        contentPadding = PaddingValues(5.dp),
                        onClick = {
                            onConfirm(
                                ShowValueItem(
                                    id = valueItem.id,
                                    value = if (newValue.isNotEmpty()) newValue.toDouble() / 100 else valueItem.value?.div(
                                        100
                                    ),
                                    name = valueDesc.ifEmpty { valueItem.name },
                                    groupId = if (selectedGroupId != null) selectedGroupId else valueItem.groupId,
                                    groupIcon = if (selectedGroup != null) selectedGroup.groupIcon else valueItem.groupIcon,
                                    month = selectedMonth,
                                    year = selectedYear,
                                    day = selectedDay,
                                    isExpense = valueItem.isExpense
                                )
                            )
                            onDismiss()
                        }
                    )
                    {
                        Text(
                            modifier = Modifier
                                .height(30.dp)
                                .wrapContentHeight(Alignment.CenterVertically),
                            text = "Confirm",
                            fontSize = 14.sp,
                            color = Principal,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, apiLevel = 35)
@Composable
fun EditValueDialogPreview() {
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded, // Set to Expanded for preview visibility
        confirmValueChange = { true }
    )
    EditValueDialog(
        sheetState = sheetState,
        onDismiss = {},
        valueItem = ShowValueItem(
            id = 1,
            value = 132.55,
            name = "Teste",
            groupId = 1,
            groupIcon = "teste",
            month = 10,
            year = 2025,
            day = 15,
            isExpense = false
        ),
        groups = emptyList(),
        onConfirm = {}
    )
}