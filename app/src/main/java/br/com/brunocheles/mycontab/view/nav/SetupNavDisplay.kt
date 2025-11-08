package br.com.brunocheles.mycontab.view.nav

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import br.com.brunocheles.mycontab.view.screens.LoginScreen
import br.com.brunocheles.mycontab.view.screens.SplashScreen
import br.com.brunocheles.mycontab.view.viewmodel.AuthViewModel

@Composable
fun SetupNavDisplay(
    activity: Activity,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val isAuthChecked by authViewModel.isAuthChecked.collectAsState()
    val backStack = rememberNavBackStack(Screen.Splash)

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
                    userLogged = authUiState.userLogged,
                    isAuthChecked = isAuthChecked,
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
                    uiState = authUiState,
                    resetLogin = {},
                    onLoginClick = {email, password ->
                        authViewModel.loginWithEmail(email, password)
                    },
                    onRegisterClick = { backStack.add(Screen.Register) },
                    onGoogleLogin = { idToken ->
                        authViewModel.loginWithGoogle(idToken)
                    }
                )
            }
            entry<Screen.NestedGraph> {
                SetupNestedNavDisplay(
                    onNavigateToFullscreen = { screen ->
                        backStack.add(screen)
                    }
                )
            }
            entry<Screen.Loading> {

            }
            entry<Screen.Register> {

            }
            entry<Screen.NewValue> {

            }
            entry<Screen.EditValue> {
                val type = it.type
            }
            entry<Screen.LoadingMin> {
                val target = it.target
            }
        }
    )
}