package br.com.brunocheles.mycontab.view.nav

import androidx.navigation3.runtime.NavKey
import br.com.brunocheles.mycontab.view.components.ValueType
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen: NavKey {
    @Serializable
    data object Login : Screen()
    @Serializable
    data object Splash : Screen()
    @Serializable
    data object Register : Screen()
    @Serializable
    data class Loading(val targetScreen: Screen) : Screen()
    @Serializable
    data class LoadingMin(val targetScreen: Screen) : Screen()
    @Serializable
    data class NewValue(val type: ValueType) : Screen()
    @Serializable
    data object EditValue : Screen()
    @Serializable
    data object NestedGraph: Screen()
}