package br.com.brunocheles.mycontab.view.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.view.screens.IconOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupSelection(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    selectedGroupId: Int = 0,
    groups: List<IconOption>,
    onConfirm: (Int) -> Unit
) {
    var selectedGroupId by rememberSaveable { mutableIntStateOf(selectedGroupId) }


//    val selectedGroup: IconOption? = selectedGroupId.let { id ->
//        groupOptions.firstOrNull { it.groupId == id }
//    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = Light
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                modifier = Modifier.padding(bottom = 10.dp),
                text = "Selecione um Grupo",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(
                    items = groups,
                    key = { it.groupId }
                ) { item ->
                    ShowGroupSelection(
                        icon = item,
                        isSelected = item.groupId == selectedGroupId,
                        onClick = {
                            selectedGroupId = item.groupId
                        }
                    )
                    // Divisor opcional entre itens
                    HorizontalDivider(
                        color = NewGray.copy(alpha = 0.1f),
                        thickness = 0.5.dp
                    )
                }
                item {
                    ListItem(
                        modifier = Modifier.fillMaxWidth(),
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_add),
                                contentDescription = "AddGroup",
                                modifier = Modifier.size(24.dp),
                                tint = NewGray
                            )
                        },
                        headlineContent = {
                            Text(
                                text = "Add New Group",
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.SemiBold,
                                color = NewGray,
                                fontSize = 14.sp
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        )
                    )
                    HorizontalDivider(
                        color = NewGray.copy(alpha = 0.7f),
                        thickness = 0.5.dp
                    )
                }
                item {
                    ListItem(
                        modifier = Modifier.fillMaxWidth(),
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.rounded_settings),
                                contentDescription = "EditGroups",
                                modifier = Modifier
                                    .size(24.dp),
                                tint = NewGray
                            )
                        },
                        headlineContent = {
                            Text(
                                text = "Edit Groups",
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.SemiBold,
                                color = NewGray,
                                fontSize = 14.sp
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
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
                    onClick = {
                        onConfirm(selectedGroupId)
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

@Composable
fun ShowGroupSelection(
    icon: IconOption?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    //val iconResId = IconUtils.getIconIdByName(icon?.groupIcon)

    icon?.let {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            leadingContent = {
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    Icon(
                        painter = it.painter,
                        contentDescription = it.description,
                        modifier = Modifier.size(24.dp),
                        tint = NewGray
                    )
                }
            },
            headlineContent = {
                Text(
                    text = it.description,
                    textAlign = TextAlign.Start,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) Principal else NewGray,
                    fontSize = 14.sp
                )
            },
            trailingContent = {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Principal,
                        uncheckedColor = NewGray,
                        checkmarkColor = Light
                    )
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GroupSelectionPreview() {
    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded, // Set to Expanded for preview visibility
        confirmValueChange = { true }
    )
    GroupSelection(
        sheetState = sheetState,
        onDismiss = {},
        selectedGroupId = 0,
        groups = emptyList(),
        onConfirm = {}
    )
}