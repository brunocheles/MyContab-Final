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
import br.com.brunocheles.mycontab.view.components.LoadingShimmer
import br.com.brunocheles.mycontab.view.components.UserSettingsContent
import br.com.brunocheles.mycontab.view.states.AuthUiState
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    authUiState: AuthUiState,
    onLogoutClick: () -> Unit
) {
    val user = authUiState.userLogged
    val isLoading = authUiState.isLoading
    var showLoggedOutView by remember { mutableStateOf(false) }

    LaunchedEffect(user, isLoading) {
        if (user == null && !isLoading) {
            delay(500) // Ajuste o tempo conforme necessário (ex: 500ms)
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


@Preview(showBackground = true, apiLevel = 35)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        authUiState = AuthUiState(),
        onLogoutClick = {}
    )
}