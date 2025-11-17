package br.com.brunocheles.mycontab.view.components

import br.com.brunocheles.mycontab.R

object IconUtils {

    // Mapa centralizado. Se adicionar um ícone novo, adicione aqui.
    // A chave (String) é o que vai para o banco de dados.
    // O valor (Int) é o ID atual gerado pelo Android.
    private val iconMap = mapOf(
        "Wallet" to R.drawable.core_group_rounded_wallet,
        "Card" to R.drawable.core_group_rounded_credit_card,
        "Home" to R.drawable.core_group_rounded_family_home,
        "Trip" to R.drawable.core_group_rounded_trip,
        "Wage" to R.drawable.core_group_rounded_money_bag,
        "Vehicle" to R.drawable.core_group_rounded_directions_car,
        "Shopping" to R.drawable.core_group_rounded_shopping_cart
        // Adicione novos ícones aqui conforme necessário
    )

    /**
     * Recupera o ID do drawable a partir do nome salvo.
     * Retorna um ícone padrão (ex: Wallet) se o nome não for encontrado.
     */
    fun getIconIdByName(name: String?): Int {
        return iconMap[name] ?: R.drawable.core_group_rounded_wallet
    }

    /**
     * (Opcional) Recupera o nome para salvar no banco, caso você tenha apenas o ID em mãos.
     * Útil se você estiver selecionando ícones em uma UI de grid.
     */
    fun getIconNameById(resId: Int): String {
        return iconMap.entries.find { it.value == resId }?.key ?: "Wallet"
    }
}