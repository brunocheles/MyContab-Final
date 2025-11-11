package br.com.brunocheles.mycontab.model.components

import br.com.brunocheles.mycontab.model.items.User

interface AuthInterface {
    suspend fun loginWithGoogle(idToken: String): Result<User>
    suspend fun loginWithApple(idToken: String): Result<User>
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    suspend fun registerWithEmail(email: String, username: String, password: String): Result<User>
    suspend fun logout()
    fun getCurrentUser(): User?
}