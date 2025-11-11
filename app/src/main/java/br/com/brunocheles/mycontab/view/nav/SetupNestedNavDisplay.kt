package br.com.brunocheles.mycontab.view.nav

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.Typography
import br.com.brunocheles.mycontab.view.screens.ProfileScreen
import br.com.brunocheles.mycontab.view.viewmodel.AuthUiEvent
import br.com.brunocheles.mycontab.view.viewmodel.AuthViewModel

@Composable
fun SetupNestedNavDisplay(
    onNavigateToFullscreen: (Screen) -> Unit,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(BottomBarScreen.Home)

    val authUiState by authViewModel.uiState.collectAsState()

    var currentBottomBarScreen: BottomBarScreen by rememberSaveable(
        stateSaver = BottomBarScreenSaver
    ) { mutableStateOf(BottomBarScreen.Home) }

    BackHandler {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        } else {
            // Se está na primeira tela (Home), fecha o app
            (context as? Activity)?.finish()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Light,
        bottomBar = {
            NavigationBar(
                containerColor = Light
            ) {
                screensBottomBar.forEach { destination ->
                    NavigationBarItem(
                        selected = currentBottomBarScreen == destination,
                        icon = {
                            Icon(
                                painter = painterResource(destination.icon),
                                contentDescription = "${destination.title} icon",
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(text = destination.title)
                        },
                        onClick = {
                            if (backStack.lastOrNull() != destination) {
                                if (backStack.lastOrNull() in screensBottomBar) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                                backStack.add(destination)
                                currentBottomBarScreen = destination
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Principal,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray,
                            indicatorColor = Principal
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        NavDisplay(
            modifier = Modifier.padding(paddingValues),
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<BottomBarScreen.Home> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Home",
                            style = Typography.titleLarge
                        )
                    }
                }
                entry<BottomBarScreen.Stats> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Stats",
                            style = Typography.titleLarge
                        )
                    }
                }
                entry<BottomBarScreen.Contab> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Contab",
                            style = Typography.titleLarge
                        )
                    }
                }
                entry<BottomBarScreen.Plans> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Plans",
                            style = Typography.titleLarge
                        )
                    }
                }
                entry<BottomBarScreen.Profile> {
                    ProfileScreen(
                        authUiState = authUiState,
                        onLogoutClick = {
                            authViewModel.logout()
                        }
                    )
                }
            }
        )
    }
}

private val screensBottomBar = listOf(
    BottomBarScreen.Home,
    BottomBarScreen.Plans,
    BottomBarScreen.Contab,
    BottomBarScreen.Stats,
    BottomBarScreen.Profile
)