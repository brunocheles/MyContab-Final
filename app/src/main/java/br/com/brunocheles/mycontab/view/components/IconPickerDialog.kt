package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import br.com.brunocheles.mycontab.ui.theme.LessBlack
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.PrincipalLight

@Composable
fun IconPickerDialog(
    onDismiss: () -> Unit,
    onIconSelected: (String) -> Unit
) {
    // Lista de ícones disponíveis (Adicione os nomes que você tem no seu IconUtils)
    val iconsList = remember { IconUtils.availableIcons }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MyContabShapes.small,
            colors = CardDefaults.cardColors(containerColor = Light),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Icon",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LessBlack,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4), // 4 ícones por linha
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.heightIn(max = 300.dp) // Limita altura
                ) {
                    items(items = iconsList) { (iconName, iconResId) ->
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(PrincipalLight.copy(alpha = 0.1f))
                                .clickable { onIconSelected(iconName) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(iconResId),
                                contentDescription = iconName,
                                tint = Principal,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = NewGray)
                }
            }
        }
    }
}

@Composable
@Preview
fun IconPickerDialogPreview() {
    IconPickerDialog(
        onDismiss = {},
        onIconSelected = {}
    )
}