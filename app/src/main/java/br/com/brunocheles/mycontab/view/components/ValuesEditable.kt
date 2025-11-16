package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.entities.GroupsEntity
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.NewRed
import br.com.brunocheles.mycontab.ui.theme.Red
import br.com.brunocheles.mycontab.view.items.ShowValueItem

@Composable
fun ValuesEditable(
    item: ShowValueItem?,
    groups: List<GroupsEntity?>,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    colorBg: Color
) {
    val group = remember(item?.groupId, groups) {
        groups.find { it!!.id == item?.groupId }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(
                color = colorBg.copy(alpha = 0.4f),
                shape = MyContabShapes.medium
            )
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    )
    {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        )
        {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                group?.let {
                    Icon(
                        painter = painterResource(it.groupIcon),
                        contentDescription = it.groupName,
                        tint = Light
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                }
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                item?.name?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Light,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                Text(
                    text = "R$%.2f".format(item?.value),
                    fontSize = 13.sp,
                    color = Light
                )
            }
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .size(26.dp)
                        .clickable(
                            onClick = {
                                onEditClick()
                            }
                        ),
                    painter = painterResource(R.drawable.rounded_edit),
                    contentDescription = "edit",
                    tint = Light
                )
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Icon(
                    modifier = Modifier
                        .size(26.dp)
                        .clickable(
                            onClick = {
                                onDeleteClick()
                            }
                        ),
                    painter = painterResource(R.drawable.rounded_delete),
                    contentDescription = "delete",
                    tint = NewRed
                )
            }
        }
    }
}

@Preview(showBackground = true, apiLevel = 35)
@Composable
fun ValuesEditablePreview() {
    ValuesEditable(
        ShowValueItem(
            value = 0.00,
            name = "teste",
            groupId = 1,
            day = 15,
            isExpense = false,
            id = 0,
            month = 11,
            year = 2025,
            groupIcon = 2
        ),
        groups = emptyList(),
        onEditClick = {},
        onDeleteClick = {},
        colorBg = Red
    )
}