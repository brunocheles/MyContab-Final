package br.com.brunocheles.mycontab.view.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.brunocheles.mycontab.model.items.User
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.Principal
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    userLogged: User?,
    isAuthChecked: Boolean,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    val animationDuration = 1500L

    // 🔹 Controla opacidade e escala da logo
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
    )

    // 🔹 Dispara animação e navegação
    LaunchedEffect(true) {
        startAnimation = true
    }

    LaunchedEffect(isAuthChecked) {
        if (isAuthChecked) {
            // Pequeno delay para garantir que o usuário veja a tela de splash
            delay(animationDuration)

            if (userLogged != null) {
                // Usuário logado: navega para a Home
                onNavigateToHome()
            } else {
                // Usuário não logado: navega para a tela de Login
                onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Light),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MyContab",
                color = NewGray,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .graphicsLayer(
                        alpha = alphaAnim.value,
                        scaleX = scaleAnim.value,
                        scaleY = scaleAnim.value
                    )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Simplifying your finances",
                color = NewGray.copy(alpha = 0.7f),
                fontSize = 16.sp,
                modifier = Modifier.alpha(alphaAnim.value)
            )

            if (!isAuthChecked) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(
                    color = Principal,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    val fakeUser = User(
        userId = "testeUserId",
        username = "brunoTeste",
        email = "teste@teste.com",
        userConfig = null
    )

    SplashScreen(
        isAuthChecked = false,
        userLogged = fakeUser,
        onNavigateToHome = {},
        onNavigateToLogin = {}
    )
}