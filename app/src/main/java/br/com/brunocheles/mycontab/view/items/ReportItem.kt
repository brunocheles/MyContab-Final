package br.com.brunocheles.mycontab.view.items

import androidx.compose.runtime.Composable

data class ReportItem(
    val title: String,
    val report: @Composable () -> Unit
)
