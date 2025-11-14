package br.com.brunocheles.mycontab.view.items

import androidx.compose.ui.graphics.painter.Painter

data class NavigationItem(
    val title: String,
    val icon: Painter,
    val route: String
)