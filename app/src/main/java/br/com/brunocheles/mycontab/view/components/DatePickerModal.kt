package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.LessBlack
import br.com.brunocheles.mycontab.ui.theme.LessLight
import br.com.brunocheles.mycontab.ui.theme.LessWhite
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.NewLight
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.PrincipalLight
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
    fun DatePickerModal(
        initialDate: LocalDate? = null,
        onDateSelected: (LocalDate) -> Unit,
        onDismiss: () -> Unit
    ) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()
                ?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { onDismiss() },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val localDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            onDateSelected(localDate)
                        }
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Confirm",
                        color = LessWhite
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Cancel",
                        color = LessWhite
                    )
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Principal
            ),
            properties = DialogProperties(
                usePlatformDefaultWidth = true
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = NewLight,
                    currentYearContentColor = Principal,
                    selectedDayContainerColor = Principal,
                    todayContentColor = PrincipalLight,
                    todayDateBorderColor = PrincipalLight,
                    selectedDayContentColor = LessLight,
                    selectedYearContainerColor = Principal,
                    selectedYearContentColor = LessLight,
                    dayInSelectionRangeContentColor = LessBlack,
                    headlineContentColor = LessBlack,
                    subheadContentColor = NewGray,
                    dayContentColor = LessBlack,
                    yearContentColor = LessBlack,
                    navigationContentColor = LessBlack
                    )
            )
        }
    }

    @Composable
    fun DatePickerFieldToModal(
        selectedDate: LocalDate? = null,
        onDateSelected: (LocalDate) -> Unit = {}
    ) {
        var selectedDate by remember { mutableStateOf(selectedDate) }
        var showModal by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
            onValueChange = { },
            label = { Text("Date") },
            placeholder = { Text("DD/MM/YYYY") },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.rounded_date_range),
                    contentDescription = "Select date",
                    tint = NewGray
                )
            },
            readOnly = true, // Torna o campo não editável
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if (upEvent != null) showModal = true
                    }
                }, // Abre o modal ao clicar
            // --- FIM DA MELHORIA ---,
            shape = MyContabShapes.extraLarge,
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
        )

        if (showModal) {
            DatePickerModal(
                initialDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                    onDateSelected(date)
                    showModal = false
                },
                onDismiss = { showModal = false }
            )
        }
    }

@Preview
@Composable
fun DatePickerModalPreview() {
    DatePickerModal(
        onDateSelected = {},
        onDismiss = {}
    )
}