package br.com.brunocheles.mycontab.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.model.items.User
import br.com.brunocheles.mycontab.view.components.LoadingShimmer
import br.com.brunocheles.mycontab.view.components.UserSettingsContent
import br.com.brunocheles.mycontab.view.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel
) {
    val authUiState by authViewModel.uiState.collectAsState()

    val onLocoutClick: () -> Unit = {
        authViewModel.logout()
    }
    // Wrapper chama o Content passando apenas dados primitivos/objetos simples
    ProfileContent(
        user = authUiState.userLogged,
        isLoading = authUiState.isLoading,
        onLogoutClick = onLocoutClick
    )
}

@Composable
fun ProfileContent(
    user: User?,
    isLoading: Boolean,
    onLogoutClick: () -> Unit
) {
    var showLoggedOutView by remember { mutableStateOf(false) }

    // Lógica visual: Só mostra "Logged Out" se passar 500ms sem usuário e sem loading
    LaunchedEffect(user, isLoading) {
        if (user == null && !isLoading) {
            delay(500)
            showLoggedOutView = true
        } else {
            showLoggedOutView = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 50.dp)
    ) {
        when {
            isLoading -> {
                // 🔹 Shimmer enquanto carrega o usuário
                LoadingShimmer()
            }

            user != null -> {
                // 🔹 Conteúdo principal
                UserSettingsContent(user = user, onLogoutClick = onLogoutClick)
            }
            showLoggedOutView -> {
                LoggedOutView()
            }
            else -> {
                LoadingShimmer()
            }
        }
    }
}

@Composable
private fun LoggedOutView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.rounded_logout),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "User logged out",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val mockUser = User(

    )

    ProfileContent(
        user = mockUser,
        isLoading = false,
        onLogoutClick = {}
    )
}