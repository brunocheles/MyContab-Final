package br.com.brunocheles.mycontab.view.nav

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import br.com.brunocheles.mycontab.view.screens.EditValueScreen
import br.com.brunocheles.mycontab.view.screens.LoadingMinScreen
import br.com.brunocheles.mycontab.view.screens.LoadingScreen
import br.com.brunocheles.mycontab.view.screens.LoginScreen
import br.com.brunocheles.mycontab.view.screens.ManageGroupsScreen
import br.com.brunocheles.mycontab.view.screens.NewValueScreen
import br.com.brunocheles.mycontab.view.screens.RegisterScreen
import br.com.brunocheles.mycontab.view.screens.SplashScreen
import br.com.brunocheles.mycontab.view.viewmodel.AuthUiEvent
import br.com.brunocheles.mycontab.view.viewmodel.AuthViewModel

@Composable
fun SetupNavDisplay(
    activity: Activity,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val authUiState by authViewModel.uiState.collectAsState()

    val isAuthReady = !authUiState.isLoading
    val isAuthChecked by authViewModel.isAuthChecked.collectAsState()
    val backStack = rememberNavBackStack(Screen.Splash)

    val userLogged = authUiState.userLogged

    val context = LocalContext.current
    LaunchedEffect(authViewModel) {
        authViewModel.uiEvent.collect { event ->
            when (event) {
                is AuthUiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                // 💡 Logout deve levar para Splash (que verificará e irá para Login)
                AuthUiEvent.NavigateToLoading -> {
                    backStack.clear()
                    backStack.add(Screen.Loading(targetScreen = Screen.Login))
                }
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Screen.Splash> {
                SplashScreen(
                    userLogged = userLogged,
                    isAuthChecked = isAuthReady,
                    onNavigateToHome = {
                        backStack.clear()
                        backStack.add(Screen.NestedGraph)
                    },
                    onNavigateToLogin = {
                        backStack.clear()
                        backStack.add(Screen.Login)
                    }
                )
            }
            entry<Screen.Login> {
                LoginScreen(
                    activity = activity,
                    authViewModel = authViewModel,
                    onRegisterClick = { backStack.add(Screen.LoadingMin(Screen.Register))},
                    onNavigateToHome = {
                        backStack.add(Screen.Loading(Screen.NestedGraph))
                    }
                )
            }
            entry<Screen.NestedGraph> {
                SetupNestedNavDisplay(
                    authViewModel = authViewModel,
                    onNavigateToFullscreen = { screen ->
                        backStack.add(screen)
                    },
                )
            }
            entry<Screen.Loading> {
                LoadingScreen(
                    navigateScreen = {
                        backStack.clear()
                        backStack.add(it.targetScreen)
                    }
                )
            }
            entry<Screen.Register> {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onLoginClick = {
                        backStack.clear()
                        backStack.add(Screen.LoadingMin(Screen.Login))
                    }
                )
            }
            entry<Screen.NewValue> {
                NewValueScreen(
                    type = it.type,
                    onClose = {
                        backStack.clear()
                        backStack.add(Screen.LoadingMin(Screen.NestedGraph))
                    },
                    authViewModel = authViewModel,
                    onManageGroupsClick = {
                        backStack.add(Screen.ManageGroups)
                    }
                )
            }
            entry<Screen.EditValue> {
                EditValueScreen(
                    typeIndex = it.index,
                    onBackPressed = {
                        backStack.clear()
                        backStack.add(Screen.LoadingMin(Screen.NestedGraph))
                    },
                    authViewModel = authViewModel,
                    onManageGroupsClick = {
                        backStack.add(Screen.ManageGroups)
                    }
                )
            }
            entry<Screen.LoadingMin> {
                LoadingMinScreen(
                    navigateScreen = {
                        backStack.clear()
                        backStack.add(it.targetScreen)
                    }
                )
            }
            entry<Screen.ManageGroups> {
                ManageGroupsScreen(
                    authViewModel = authViewModel,
                    onBackClick = {
                        backStack.removeLastOrNull()
                    }
                )
            }
        }
    )
}