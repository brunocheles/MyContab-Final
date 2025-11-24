package br.com.brunocheles.mycontab.view.components

import br.com.brunocheles.mycontab.R

object IconUtils {

    private val iconMap = mapOf(
        "Wallet" to R.drawable.core_group_rounded_wallet,
        "Card" to R.drawable.core_group_rounded_credit_card,
        "Home" to R.drawable.rounded_house,
        "Trip" to R.drawable.core_group_rounded_trip,
        "Wage" to R.drawable.core_group_rounded_money_bag,
        "Vehicle" to R.drawable.core_group_rounded_directions_car,
        "Shopping" to R.drawable.core_group_rounded_shopping_cart,
        // Adicione novos ícones aqui conforme necessário
        "Person" to R.drawable.rounded_profile,
        "Theater" to R.drawable.rounded_theater,
        "Mobile" to R.drawable.rounded_mobile,
        "Gas" to R.drawable.rounded_local_gas,
        "Health" to R.drawable.rounded_health,
        "Refit" to R.drawable.rounded_handyman,
        "Food" to R.drawable.rounded_food,
        "Exercise" to R.drawable.rounded_exercise,
        "Cloud" to R.drawable.rounded_cloud,
        "Apartment" to R.drawable.rounded_apartment,
        "Travel" to R.drawable.rounded_airplane_ticket
    )

    val availableIcons: List<Pair<String, Int>>
        get() = iconMap.toList() // Transforma o Map em List<Pair<String, Int>>

    fun getIconIdByName(name: String?): Int {
        return iconMap[name] ?: R.drawable.core_group_rounded_wallet
    }

    fun getIconNameById(resId: Int): String {
        return iconMap.entries.find { it.value == resId }?.key ?: "Wallet"
    }
}