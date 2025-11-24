package br.com.brunocheles.mycontab.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.PrincipalLight
import br.com.brunocheles.mycontab.view.items.IconOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroupBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    item: IconOption? = null,
    userId: String?,
    onConfirm: (IconOption) -> Unit
) {
    var groupDesc by remember(item) { mutableStateOf(item?.description ?: "") }
    var selectedGroupIcon by remember(item) { mutableStateOf(item?.groupIcon ?: "Wallet") }

    // Controle do Dialog de Ícones
    var showIconPicker by remember { mutableStateOf(false) }

    // Título dinâmico
    val title = if (item == null) "Add New Group" else "Edit Group"
    val isFormValid = groupDesc.isNotBlank()

    // Pega o ID do recurso baseado no nome
    val iconResId = IconUtils.getIconIdByName(selectedGroupIcon)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = Light
    ) {
        Column(
            modifier = Modifier
                .padding(15.dp)
                .navigationBarsPadding()
        )
        {
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
            )
            {
                OutlinedTextField(
                    modifier = Modifier
                        .height(65.dp)
                        .weight(9f),
                    value = groupDesc,
                    onValueChange = { groupDesc = it },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.rounded_description),
                            contentDescription = "GroupDesc",
                            tint = NewGray
                        )
                    },
                    label = { Text(text = "Description") },
                    shape = MyContabShapes.extraLarge,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedLeadingIconColor = NewGray,
                        focusedLeadingIconColor = NewGray,
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
                            Icon(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .size(30.dp),
                                painter = painterResource(iconResId),
                                contentDescription = "GroupIcon"
                            )
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
                            .clip(MyContabShapes.extraLarge)
                            .clickable(
                                onClick = {
                                    showIconPicker = true
                                }
                            )
                    )
                }
            }

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
                    enabled = isFormValid,
                    onClick = {
                        onConfirm(
                            IconOption(
                                description = groupDesc,
                                groupId = item?.groupId ?: 0,
                                groupIcon = selectedGroupIcon,
                                groupUserId = userId!!
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
        AnimatedVisibility(showIconPicker) {
            IconPickerDialog(
                onDismiss = {
                    showIconPicker = false
                },
                onIconSelected = { iconName ->
                    selectedGroupIcon = iconName
                    showIconPicker = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun EditGroupBottomSheetPreview() {
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded, // Set to Expanded for preview visibility
        confirmValueChange = { true }
    )
    EditGroupBottomSheet(
        sheetState = sheetState,
        onDismiss = {},
        userId = "MyContabUser",
        item = IconOption(
            description = "Wallet",
            groupId = 0,
            groupIcon = "Wallet",
            groupUserId = "MyContabUser",

            ),
        onConfirm = { _ -> }
    )
}