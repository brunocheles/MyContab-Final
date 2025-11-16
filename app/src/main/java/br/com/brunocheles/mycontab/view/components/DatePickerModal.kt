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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.ui.theme.Gray
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
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
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = Color.White
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Principal
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Light,
                currentYearContentColor = Principal,
                selectedDayContainerColor = Principal,
                todayContentColor = PrincipalLight,
                todayDateBorderColor = PrincipalLight,
                selectedDayContentColor = Color.White,
                selectedYearContainerColor = Principal,
                selectedYearContentColor = Color.White,

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
                tint = Gray
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) showModal = true
                }
            },
        shape = MyContabShapes.extraLarge,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Principal,
            focusedLabelColor = Principal,
            unfocusedLabelColor = Gray,
            unfocusedBorderColor = PrincipalLight,
            unfocusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
            focusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
            unfocusedTextColor = Gray.copy(alpha = 0.7f),
            focusedTextColor = Gray
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