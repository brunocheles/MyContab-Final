package br.com.brunocheles.mycontab.view.screens

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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.brunocheles.mycontab.R
import br.com.brunocheles.mycontab.ui.theme.NewGray
import br.com.brunocheles.mycontab.ui.theme.Light
import br.com.brunocheles.mycontab.ui.theme.MyContabShapes
import br.com.brunocheles.mycontab.ui.theme.Principal
import br.com.brunocheles.mycontab.ui.theme.PrincipalLight
import br.com.brunocheles.mycontab.ui.theme.Red
import br.com.brunocheles.mycontab.view.components.Logo
import br.com.brunocheles.mycontab.view.states.AuthUiState

@Composable
fun RegisterScreen(
    uiState: AuthUiState,
    resetRegister: () -> Unit,
    onRegisterClick: (String, String, String) -> Unit,
    onLoginClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        resetRegister()
    }

    val allFieldsFilled = username.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            cPassword.isNotBlank()

    val passwordsMatch = password == cPassword
    val isFormValid = allFieldsFilled && passwordsMatch

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Light)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 60.dp)
        )
        {
            Logo()

            Spacer(modifier = Modifier.height(40.dp))

            // Email
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.rounded_alternate_email),
                        contentDescription = "email icon"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = MyContabShapes.extraLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Principal,
                    unfocusedBorderColor = PrincipalLight,
                    focusedLabelColor = Principal,
                    unfocusedLabelColor = NewGray,
                    unfocusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    focusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    unfocusedTextColor = NewGray.copy(alpha = 0.7f),
                    focusedTextColor = NewGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Username
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username,
                onValueChange = { username = it },
                singleLine = true,
                label = { Text("Name") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.rounded_profile),
                        contentDescription = "user icon"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = MyContabShapes.extraLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Principal,
                    unfocusedBorderColor = PrincipalLight,
                    focusedLabelColor = Principal,
                    unfocusedLabelColor = NewGray,
                    unfocusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    focusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    unfocusedTextColor = NewGray.copy(alpha = 0.7f),
                    focusedTextColor = NewGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Password
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                singleLine = true,
                label = { Text("Password") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.rounded_lock),
                        contentDescription = "password icon"
                    )
                },
                trailingIcon = {
                    val icon = if (isPasswordVisible) R.drawable.rounded_visibility_off else R.drawable.rounded_visibility
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                shape = MyContabShapes.extraLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Principal,
                    unfocusedBorderColor = PrincipalLight,
                    focusedLabelColor = Principal,
                    unfocusedLabelColor = NewGray,
                    unfocusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    focusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    unfocusedTextColor = NewGray.copy(alpha = 0.7f),
                    focusedTextColor = NewGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))


            // Confirm password
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = cPassword,
                onValueChange = { cPassword = it },
                singleLine = true,
                label = { Text("Confirm Password") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.rounded_lock),
                        contentDescription = "confirm password icon"
                    )
                },
                trailingIcon = {
                    val icon = if (isConfirmPasswordVisible) R.drawable.rounded_visibility_off else R.drawable.rounded_visibility
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "toggle confirm password visibility"
                        )
                    }
                },
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    if (isFormValid) {
                        onRegisterClick(email, username, password)
                    }
                }),
                shape = MyContabShapes.extraLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Principal,
                    unfocusedBorderColor = PrincipalLight,
                    focusedLabelColor = Principal,
                    unfocusedLabelColor = NewGray,
                    unfocusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    focusedContainerColor = PrincipalLight.copy(alpha = 0.1f),
                    unfocusedTextColor = NewGray.copy(alpha = 0.7f),
                    focusedTextColor = NewGray
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Senhas não coincidem
            if (cPassword.isNotBlank() && !passwordsMatch) {
                Text(
                    text = "As senhas não coincidem",
                    color = Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Mensagem de erro do UserUiState
            uiState.errorMessage?.let { error ->
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = error,
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
                onClick = { onRegisterClick(email, username, password) },
                enabled = isFormValid,
                shape = MyContabShapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Principal,
                    disabledContainerColor = PrincipalLight
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Light,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "REGISTER",
                        fontWeight = FontWeight.Bold,
                        color = Light
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = { onLoginClick() }) {
                Text(
                    text = "Already have an account? Login",
                    fontWeight = FontWeight.Bold,
                    color = Principal
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun RegisterScreenPreview() {
    RegisterScreen(
        uiState = AuthUiState(),
        resetRegister = {},
        onRegisterClick = {_, _, _ -> },
        onLoginClick = {}
    )
}