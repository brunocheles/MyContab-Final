package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.entities.GroupsEntity
import br.com.brunocheles.mycontab.ui.theme.Gray
import br.com.brunocheles.mycontab.ui.theme.GreenMedium
import br.com.brunocheles.mycontab.ui.theme.Red
import br.com.brunocheles.mycontab.ui.theme.RedMedium
import br.com.brunocheles.mycontab.view.items.ShowValueItem

@Composable
fun TransactionItem(
    item: ShowValueItem?,
    isEdit: Boolean,
    onDelete: () -> Unit,
    groups: List<GroupsEntity?>
) {
    val group = remember(item?.groupId, groups) {
        groups.find { it!!.id == item?.groupId }
    }
    val color = if (item?.isExpense == true) RedMedium else GreenMedium

    item?.let {
        ListItem(
            modifier = Modifier.height(40.dp),
            leadingContent = {
                group?.let {
                    Icon(
                        painter = painterResource(it.groupIcon),
                        contentDescription = it.groupName,
                        modifier = Modifier,
                        tint = color
                    )
                }
            },
            headlineContent = {
                item.name?.let { text ->
                    Text(
                        text = text,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 12.sp,
                        color = color,
                        fontSize = 12.sp
                    )
                }
            },
            trailingContent = {
                Row {
                    Text(
                        text = "R$ ${"%.2f".format(item.value)}",
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 0.sp
                    )
                    when(isEdit) {
                        true -> {
                            Icon(
                                painter = painterResource(R.drawable.rounded_delete),
                                contentDescription = "delete",
                                modifier = Modifier.clickable(
                                    onClick = onDelete
                                ),
                                tint = Red
                            )
                        }
                        false -> {}
                    }
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent, // Fundo transparente no card
                headlineColor = Gray
            )
        )
    }
}