package br.com.brunocheles.mycontab.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.Principal
import kotlinx.coroutines.launch

@Composable
fun ComposeYearPicker(
    years: List<Int>,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit
) {
    val density = LocalDensity.current
    val itemHeightDp = 40.dp
    val itemHeightPx = remember { with(density) { itemHeightDp.toPx() } }

    // Calcula o índice inicial para centralizar o ano selecionado
    val initialIndex = years.indexOf(selectedYear)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val scope = rememberCoroutineScope()

    // 💡 Lógica de Snapping: Monitora quando a rolagem para
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val firstItem = visibleItems.first()
                val offset = firstItem.offset
                val targetIndex = if (offset < -itemHeightPx / 2) {
                    // Se rolou mais da metade, vai para o próximo item
                    firstItem.index + 1
                } else {
                    // Caso contrário, volta para o item atual
                    firstItem.index
                }

                // 2. Executa a rolagem suave para o item calculado
                scope.launch {
                    listState.animateScrollToItem(targetIndex)
                    onYearSelected(years[targetIndex])
                }
            }
        }
    }

    // 3. UI com LazyColumn
    LazyColumn(
        state = listState,
        // Restringe a altura visível para mostrar 3 itens (o do meio e os de cima/baixo)
        modifier = Modifier.height(itemHeightDp * 3),
        horizontalAlignment = Alignment.CenterHorizontally,
        // Adiciona padding para o item visível iniciar no meio do viewport
        contentPadding = PaddingValues(vertical = itemHeightDp),
    ) {
        items(years) { year ->
            Box(
                modifier = Modifier
                    .height(itemHeightDp)
                    .fillMaxWidth()
                    .clickable {
                        // Permite clique para rolagem manual (melhor UX)
                        scope.launch {
                            listState.animateScrollToItem(years.indexOf(year))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = year.toString(),
                    fontSize = 24.sp,
                    color = if (year == selectedYear) Principal else NewGray
                )
            }
        }
    }
}

@Composable
@Preview
fun ComposeYearPickerPewview() {
    ComposeYearPicker(
        years = (1900..2100).toList(),
        selectedYear = 2025,
        onYearSelected = {}
    )
}