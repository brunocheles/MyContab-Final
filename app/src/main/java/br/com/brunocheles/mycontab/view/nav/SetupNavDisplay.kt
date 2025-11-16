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
import br.com.brunocheles.mycontab.view.items.toExpense
import br.com.brunocheles.mycontab.view.items.toIncome
import br.com.brunocheles.mycontab.view.screens.EditValueScreen
import br.com.brunocheles.mycontab.view.screens.LoadingMinScreen
import br.com.brunocheles.mycontab.view.screens.LoadingScreen
import br.com.brunocheles.mycontab.view.screens.LoginScreen
import br.com.brunocheles.mycontab.view.screens.NewValueScreen
import br.com.brunocheles.mycontab.view.screens.RegisterScreen
import br.com.brunocheles.mycontab.view.screens.SplashScreen
import br.com.brunocheles.mycontab.view.viewmodel.AuthUiEvent
import br.com.brunocheles.mycontab.view.viewmodel.AuthViewModel
import br.com.brunocheles.mycontab.view.viewmodel.ExpenseViewModel
import br.com.brunocheles.mycontab.view.viewmodel.GroupViewModel
import br.com.brunocheles.mycontab.view.viewmodel.IncomeViewModel

@Composable
fun SetupNavDisplay(
    activity: Activity,
    authViewModel: AuthViewModel = hiltViewModel(),
    groupViewModel: GroupViewModel = hiltViewModel(),
    incomeViewModel: IncomeViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val groupUiState by groupViewModel.uiState.collectAsState()
    val isAuthChecked by authViewModel.isAuthChecked.collectAsState()
    val incomeUiState by incomeViewModel.uiState.collectAsState()
    val expenseUiState by expenseViewModel.uiState.collectAsState()
    val backStack = rememberNavBackStack(Screen.Splash)

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
                    // Se for logout, o destino final é o Splash (ou Login)
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
                    onRegisterClick = { backStack.add(Screen.LoadingMin(Screen.Register))},
                    onGoogleLogin = { idToken ->
                        authViewModel.loginWithGoogle(idToken)
                    },
                    onNavigateToHome = {
                        backStack.add(Screen.Loading(Screen.NestedGraph))
                    }
                )
            }
            entry<Screen.NestedGraph> {
                SetupNestedNavDisplay(
                    authViewModel = authViewModel,
                    groupViewModel = groupViewModel,
                    incomeViewModel = incomeViewModel,
                    expenseViewModel = expenseViewModel,
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
                LaunchedEffect(authUiState.success) {
                    if (authUiState.success == true) {
                        backStack.clear()
                        // ✅ Registro bem-sucedido -> Loading -> Home
                        backStack.add(Screen.Loading(targetScreen = Screen.NestedGraph))
                    }
                }
                RegisterScreen(
                    uiState = authUiState,
                    resetRegister = { authViewModel.resetState() },
                    onRegisterClick = { email, username, password ->
                        authViewModel.registerWithEmail(
                            username = username,
                            email = email,
                            password = password
                        )
                    },
                    onLoginClick = {
                        backStack.clear()
                        backStack.add(Screen.LoadingMin(Screen.Login))
                    }
                )
            }
            entry<Screen.NewValue> { it ->
                NewValueScreen(
                    type = it.type,
                    onClose = {
                        backStack.clear()
                        backStack.add(Screen.LoadingMin(Screen.NestedGraph))
                    },
                    authUiState = authUiState,
                    groupsUiState = groupUiState,
                    onInsertIncome = { income ->
                        authUiState.userLogged?.userId?.let {
                            incomeViewModel.insertIncome(
                                userId = it,
                                income = income
                            )
                        }
                        backStack.clear()
                        backStack.add(Screen.LoadingMin(Screen.NestedGraph))
                    },
                    onInsertExpense = { expense ->
                        authUiState.userLogged?.userId?.let {
                            expenseViewModel.insertExpense(
                                userId = it,
                                expense = expense
                            )
                        }
                        backStack.clear()
                        backStack.add(Screen.LoadingMin(Screen.NestedGraph))
                    }
                )
            }
            entry<Screen.EditValue> {
                EditValueScreen(
                    incomeUiState = incomeUiState,
                    expenseUiState = expenseUiState,
                    groups = groupUiState.groups,
                    onEdit = { value ->
                        when (value.isExpense) {
                            true -> {
                                authUiState.userLogged?.userId?.let {
                                    expenseViewModel.updateExpense(
                                        userId = it,
                                        expense = value.toExpense()
                                    )
                                }
                            }

                            false -> {
                                authUiState.userLogged?.userId?.let {
                                    incomeViewModel.updateIncome(
                                        userId = it,
                                        income = value.toIncome()
                                    )
                                }
                            }
                        }
                    },
                    onBackPressed = {
                        backStack.clear()
                        backStack.add(Screen.LoadingMin(Screen.NestedGraph))
                    },
                    onDelete = { value ->
                        when(value.isExpense) {
                            true -> {
                                authUiState.userLogged?.userId?.let {
                                    expenseViewModel.deleteExpense(
                                        userId = it,
                                        expense = value.toExpense()
                                    )
                                }
                            }
                            false -> {
                                authUiState.userLogged?.userId?.let {
                                    incomeViewModel.deleteIncome(
                                        userId = it,
                                        income = value.toIncome()
                                    )
                                }
                            }
                        }

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
        }
    )
}