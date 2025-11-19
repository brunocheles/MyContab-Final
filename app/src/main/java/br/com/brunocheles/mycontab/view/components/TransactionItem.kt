package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.entities.GroupsEntity
import br.com.brunocheles.mycontab.ui.theme.Green
import br.com.brunocheles.mycontab.ui.theme.GreenMedium
import br.com.brunocheles.mycontab.ui.theme.LessBlack
import br.com.brunocheles.mycontab.ui.theme.NewGreen
import br.com.brunocheles.mycontab.ui.theme.NewRed
import br.com.brunocheles.mycontab.ui.theme.Red
import br.com.brunocheles.mycontab.ui.theme.RedMedium
import br.com.brunocheles.mycontab.view.items.ShowValueItem

@Composable
fun TransactionItem(
    item: ShowValueItem?,
    height: Dp,
    fontSize: TextUnit,
    isEdit: Boolean,
    onDelete: () -> Unit,
    groups: List<GroupsEntity?>
) {
    val group = remember(item?.groupId, groups) {
        groups.find { it!!.id == item?.groupId }
    }
    val color = if (item?.isExpense == true) NewRed else NewGreen

    item?.let {
        val iconResId = IconUtils.getIconIdByName(it.groupIcon)
        ListItem(
            modifier = Modifier
                .height(height)
                .clip(ListItemDefaults.shape),
            leadingContent = {
                Box(
                    modifier = Modifier.height(height),
                    contentAlignment = Alignment.CenterStart
                ) {
                    group?.let {
                        Icon(
                            painter = painterResource(iconResId),
                            contentDescription = it.groupName,
                            modifier = Modifier,
                            tint = color
                        )
                    }
                }
            },
            headlineContent = {
                Box(
                    modifier = Modifier.height(height),
                    contentAlignment = Alignment.CenterStart
                ) {
                    item.name?.let { text ->
                        Text(
                            text = text,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 12.sp,
                            color = color,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(height)
                ) {
                    Text(
                        text = "R$ ${"%.2f".format(item.value)}",
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize,
                        letterSpacing = 0.sp
                    )
                    when(isEdit) {
                        true -> {
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                painter = painterResource(R.drawable.rounded_delete),
                                contentDescription = "delete",
                                modifier = Modifier.clickable(
                                    onClick = onDelete
                                ),
                                tint = RedMedium
                            )
                        }
                        false -> {}
                    }
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent, // Fundo transparente no card
                headlineColor = LessBlack
            )
        )
    }
}