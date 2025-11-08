package br.com.brunocheles.mycontab.view.states

import br.com.brunocheles.mycontab.model.items.User
import java.time.LocalDate

data class AuthUiState(
    val isLoading: Boolean = false,
    val success: Boolean? = null,
    val errorMessage: String? = null,
    val userLogged: User? = null,
    val year: Int = LocalDate.now().year,
    val month: Int = LocalDate.now().monthValue,
    val actualScreen: Int = 0
)