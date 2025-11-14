package br.com.brunocheles.mycontab.view.components

import android.widget.NumberPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import br.com.brunocheles.mycontab.ui.theme.Gray
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.Principal
import java.text.DateFormatSymbols
import java.time.LocalDate
import java.util.Locale


@Composable
fun MonthPickerDialog(
    onDismiss: () -> Unit,
    currentYear: Int,
    currentMonth: Int,
    onConfirm: (Int, Int) -> Unit,
    minYear: Int = 1950,
    maxYear: Int = LocalDate.now().year + 50
) {
    val years: List<Int> = (minYear..maxYear).toList()
    val months = DateFormatSymbols(Locale.US).shortMonths.filter { it.isNotEmpty() }
    var selectedYear by remember { mutableIntStateOf(currentYear) }
    var selectedMonth by remember { mutableIntStateOf(currentMonth) }

    Dialog(onDismiss)
    {
        Box(
            modifier = Modifier
                .clip(MyContabShapes.small)
                .background(
                    shape = MyContabShapes.small,
                    color = Light
                )
        )
        {
            Column {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                )
                {
                    Box(
                        modifier = Modifier
                            .background(
                                shape = RoundedCornerShape(
                                    topStart = 20.dp,
                                    bottomStart = 20.dp
                                ),
                                color = Light
                            )
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .clipToBounds(),
                        contentAlignment = Alignment.Center
                    )
                    {
                        AndroidView(
                            modifier = Modifier
                                .wrapContentSize(),
                            factory = { context ->
                                NumberPicker(context).apply {
                                    textSize = 60f
                                    minValue = years.first()
                                    maxValue = years.last()
                                    value = selectedYear
                                    selectionDividerHeight = 0
                                    descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                                    setOnValueChangedListener { _, _, newVal ->
                                        selectedYear = newVal
                                    }
                                }
                            },
                            update = { picker ->
                                picker.minValue = years.first()
                                picker.maxValue = years.last()
                                picker.value = selectedYear
                            }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                color = Light
                            )
                            .padding(end = 15.dp),
                        contentAlignment = Alignment.Center
                    )
                    {
                        LazyVerticalGrid(
                            modifier = Modifier.padding(top = 10.dp),
                            columns = GridCells.Fixed(4),
                            userScrollEnabled = false,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(4.dp)
                        )
                        {
                            itemsIndexed(months) { index, month ->
                                Box(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .clip(MyContabShapes.extraLarge)
                                        .background(
                                            if (index == selectedMonth) Principal else Color.Transparent
                                        )
                                        .clickable {
                                            selectedMonth = index
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = month,
                                        color = if (index == selectedMonth) Color.White else Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .background(
                            color = Principal
                        )
                        .fillMaxWidth()
                        .padding(
                            end = 15.dp
                        ),
                    horizontalArrangement = Arrangement.End
                )
                {
                    TextButton(
                        modifier = Modifier,
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
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    TextButton(
                        modifier = Modifier,
                        shape = RectangleShape,
                        contentPadding = PaddingValues(5.dp),
                        onClick = {
                            onConfirm(selectedYear, selectedMonth)
                            onDismiss()
                        }
                    )
                    {
                        Text(
                            modifier = Modifier
                                .wrapContentHeight(Alignment.CenterVertically),
                            text = "Confirm",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MonthPickerDialogPreview() {
    MonthPickerDialog(
        onDismiss = { },
        currentYear = 2025,
        currentMonth = 2,
        onConfirm = { _, _ -> },
        minYear = 1950,
        maxYear = 2070
    )
}