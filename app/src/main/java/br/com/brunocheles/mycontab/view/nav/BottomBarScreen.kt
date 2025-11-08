package br.com.brunocheles.mycontab.view.nav

import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavKey
import br.com.brunocheles.mycontab.R
import kotlinx.serialization.Serializable

@Serializable
sealed class BottomBarScreen(
    val icon: Int,
    val title: String
): NavKey {
    @Serializable
    data object Home : BottomBarScreen(
        icon = R.drawable.rounded_home,
        title = "Home"
    )
    @Serializable
    data object Stats : BottomBarScreen(
        icon = R.drawable.rounded_bar_chart,
        title = "Stats"
    )
    @Serializable
    data object Contab : BottomBarScreen(
        icon = R.drawable.mycontab_logo,
        title = "Contab"
    )
    @Serializable
    data object Plans : BottomBarScreen(
        icon = R.drawable.rounded_edit_plans,
        title = "Plans"
    )
    @Serializable
    data object Profile : BottomBarScreen(
        icon = R.drawable.rounded_profile,
        title = "Profile"
    )
}

val BottomBarScreenSaver = Saver<BottomBarScreen, String>(
    save = { it::class.simpleName ?: "Unknown"},
    restore = {
        when(it) {
            BottomBarScreen.Home::class.simpleName -> BottomBarScreen.Home
            BottomBarScreen.Stats::class.simpleName -> BottomBarScreen.Stats
            BottomBarScreen.Contab::class.simpleName -> BottomBarScreen.Contab
            BottomBarScreen.Plans::class.simpleName -> BottomBarScreen.Plans
            BottomBarScreen.Profile::class.simpleName -> BottomBarScreen.Profile
            else -> BottomBarScreen.Home
        }
    }
)