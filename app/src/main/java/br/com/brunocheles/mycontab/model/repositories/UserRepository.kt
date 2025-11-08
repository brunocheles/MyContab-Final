package br.com.brunocheles.mycontab.model.repositories

import br.com.brunocheles.mycontab.model.components.AuthInterface
import br.com.brunocheles.mycontab.model.items.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthInterface {

    // 🔹 Google Sign-In
    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.failure(Exception("Usuário não encontrado"))
            Result.success(user.toUser("google"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 Apple Sign-In (placeholder)
    override suspend fun loginWithApple(idToken: String): Result<User> {
        return Result.failure(Exception("Apple Sign-In ainda não implementado"))
    }

    // 🔹 Login com e-mail/senha
    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Usuário não encontrado"))
            Result.success(user.toUser("email"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 Registro com e-mail/senha
    override suspend fun registerWithEmail(
        email: String,
        password: String,
        username: String
    ): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Erro ao criar usuário"))

            // Atualiza o displayName no Firebase
            val profileUpdates = userProfileChangeRequest {
                displayName = username
            }
            user.updateProfile(profileUpdates).await()

            Result.success(user.toUser("email"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 Logout
    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    // 🔹 Usuário atual (já autenticado)
    override fun getCurrentUser(): User? {
        val user = firebaseAuth.currentUser ?: return null
        return user.toUser(user.providerData.firstOrNull()?.providerId ?: "unknown")
    }

    // 🔸 Extensão auxiliar para converter FirebaseUser → User
    private fun FirebaseUser.toUser(provider: String): User {
        return User(
            userId = uid,
            username = displayName ?: "",
            email = email ?: "",
            userConfig = null,
            photoUrl = photoUrl?.toString(),
            provider = provider
        )
    }
}