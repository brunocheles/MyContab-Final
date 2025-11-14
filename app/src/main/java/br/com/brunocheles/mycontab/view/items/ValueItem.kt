package br.com.brunocheles.mycontab.view.items

import br.com.brunocheles.mycontab.view.components.ValueType

data class ValueItem (
    val type: ValueType? = null,
    val id: Int? = 0,
    val value: Double? = 0.0,
    val desc: String? = "",
    val groupId: Int? = 0,
    val groupIcon: Int? = 0,
    val month: Int? = 0,
    val year: Int? = 0,
    val day: Int? = 0
)