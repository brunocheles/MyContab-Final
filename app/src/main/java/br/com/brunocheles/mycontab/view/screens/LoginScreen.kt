package br.com.brunocheles.mycontab.view.screens

import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.ui.theme.Gray
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.LightBlue
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.PrincipalLight
import br.com.brunocheles.mycontab.ui.theme.Red
import br.com.brunocheles.mycontab.view.components.Logo
import br.com.brunocheles.mycontab.view.components.TrailingIconButton
import br.com.brunocheles.mycontab.view.states.AuthUiState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    activity: Activity,
    uiState: AuthUiState,
    resetLogin: () -> Unit,
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleLogin: (String) -> Unit,
    onNavigateToHome: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginAttempted by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val allFieldsFilled = email.isNotBlank() && password.isNotBlank()
    val focusManager = LocalFocusManager.current

    val loginState = when (uiState.success) {
        true -> LoginState.Success
        false -> LoginState.Error
        null -> LoginState.Idle
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success == true) {
            // Se o login foi um sucesso (via Google ou Email/Senha), navega para a Home
            onNavigateToHome()
            // Reseta o estado para limpar o flag 'success', evitando navegação dupla
            resetLogin()
        } else if (uiState.success == false) {
            // Se falhou, marca que houve tentativa para mostrar a mensagem de erro
            loginAttempted = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Light)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Logo()

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        tint = Gray,
                        painter = painterResource(R.drawable.rounded_alternate_email),
                        contentDescription = "email icon"
                    )
                },
                shape = MyContabShapes.extraLarge,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Principal,
                    focusedLabelColor = Principal,
                    unfocusedLabelColor = Gray,
                    unfocusedBorderColor = PrincipalLight,
                    unfocusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    focusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    unfocusedTextColor = Gray.copy(alpha = 0.7f),
                    focusedTextColor = Gray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        tint = Gray,
                        painter = painterResource(R.drawable.rounded_lock),
                        contentDescription = "lock icon"
                    )
                },
                trailingIcon = {
                    val icon =
                        if (isPasswordVisible) R.drawable.rounded_visibility_off else R.drawable.rounded_visibility
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            tint = Gray,
                            painter = painterResource(icon),
                            contentDescription = "toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = MyContabShapes.extraLarge,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (allFieldsFilled) {
                        loginAttempted = true
                        onLoginClick(email, password)
                    }
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Principal,
                    unfocusedBorderColor = PrincipalLight,
                    focusedLabelColor = Principal,
                    unfocusedLabelColor = Gray,
                    unfocusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    focusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    unfocusedTextColor = Gray.copy(alpha = 0.7f),
                    focusedTextColor = Gray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Error message
            AnimatedVisibility(
                visible = loginAttempted && loginState == LoginState.Error,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "Invalid email or password",
                    color = Red,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    loginAttempted = true
                    onLoginClick(email, password)
                },
                enabled = allFieldsFilled && loginState != LoginState.Success,
                shape = MyContabShapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Principal,
                    disabledContainerColor = PrincipalLight
                )
            ) {
                when (uiState.isLoading) {
                    true -> CircularProgressIndicator(
                        color = Light,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )

                    false -> Text(
                        text = "LOGIN",
                        fontWeight = FontWeight.Bold,
                        color = Light
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LoginWithGoogleButton(
                activity = activity,
                onIdTokenReady = { token ->
                    onGoogleLogin(token)
                },
                onError = { msg ->
                    Log.e("LoginScreen", "Erro no Google Sign-In: $msg")
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Register button
            TextButton(onClick = { onRegisterClick() }) {
                Text(
                    text = "Create new account with Email",
                    fontWeight = FontWeight.Bold,
                    color = Principal
                )
            }
        }
        if (loginState == LoginState.Success) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Principal,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

enum class LoginState {
    Idle, Success, Error
}

@Composable
fun LoginWithGoogleButton(
    activity: Activity,
    onIdTokenReady: (String) -> Unit,
    onError: (String) -> Unit
) {
    val webClientId = stringResource(R.string.default_web_client_id)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    TrailingIconButton(
        onClick = {
            scope.launch {
                try {
                    // 1️⃣ Cria a opção do Google ID (use o web client ID)
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setServerClientId(webClientId)
                        .setFilterByAuthorizedAccounts(false)
                        .build()

                    // 2️⃣ Cria a requisição
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    // 3️⃣ Pede o credential
                    val credentialManager = CredentialManager.create(context)
                    val result = credentialManager.getCredential(activity, request)

                    // 4️⃣ Recupera o credential dentro do result
                    val credential = result.credential

                    // 5️⃣ Verifica se o tipo é o GoogleIdTokenCredential
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken

                        if (idToken.isNotBlank()) {
                            onIdTokenReady(idToken)
                        } else {
                            onError("Token Google vazio")
                        }
                    } else {
                        onError("Tipo de credencial inesperado: ${credential.type}")
                    }

                } catch (e: Exception) {
                    onError(e.localizedMessage ?: "Erro ao solicitar credencial")
                }
            }
        },
        text = "Login with Google".uppercase(),
        icon = painterResource(R.drawable.google),
        iconDesc = "Google",
        colors = ButtonDefaults.buttonColors(
            containerColor = LightBlue
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = MyContabShapes.extraLarge,
        iconTint = Color.Unspecified
    )
}

@Composable
@Preview(apiLevel = 35)
fun LoginScreenPreview() {
    LoginScreen(
        activity = Activity(),
        uiState = AuthUiState(),
        onLoginClick = { _, _ -> },
        onRegisterClick = {},
        resetLogin = {},
        onGoogleLogin = { _ -> },
        onNavigateToHome = {}
    )
}