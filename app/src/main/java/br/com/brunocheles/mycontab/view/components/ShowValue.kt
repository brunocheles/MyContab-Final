package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.model.entities.GroupsEntity
import br.com.brunocheles.mycontab.view.items.ShowValueItem

@Composable
fun ShowValue(
    layoutDirection: LayoutDirection,
    item: ShowValueItem?,
    groups: List<GroupsEntity?>
) {
    val group = remember(item?.groupId, groups) {
        groups.find { it!!.id == item?.groupId }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                group?.let {
                    Icon(
                        painter = painterResource(it.groupIcon),
                        contentDescription = it.groupName,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                }
                Spacer(modifier = Modifier.padding(horizontal = 1.dp))
                item?.name?.let {
                    Text(
                        text = it,
                        fontSize = 9.sp
                    )
                }
            }
            Text(
                text = "R$%.2f".format(item?.value),
                fontSize = 9.sp
            )
        }
    }
}

@Composable
@Preview
fun ShowValuePreview() {
    ShowValue(
        layoutDirection = LayoutDirection.Rtl,
        ShowValueItem(
            value = 0.00,
            name = "teste",
            day = 15,
            groupId = 1,
            isExpense = false
        ),
        groups = emptyList()
    )
}