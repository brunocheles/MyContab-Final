package br.com.brunocheles.mycontab.model.data.repositories

import android.util.Log
import br.com.brunocheles.mycontab.model.components.AuthInterface
import br.com.brunocheles.mycontab.model.dao.UserDao
import br.com.brunocheles.mycontab.model.data.entities.UserEntity
import br.com.brunocheles.mycontab.model.items.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth
): AuthInterface {

    private suspend fun saveUserToRoom(firebaseUser: FirebaseUser, provider: String): Result<User> {
        return withContext(Dispatchers.IO) {
            // 1. Converte FirebaseUser para sua classe User de domínio
            val user = firebaseUser.toUser(provider)
            // 2. CRUCIAL: Salva/Atualiza o usuário no banco de dados Room (cache local)
            try {
                userDao.upsertUser(user.toUserEntity())
                val savedUser = userDao.getUserWithFirebaseId(user.userId!!)
                Log.d("ROOM_CHECK", "Usuário salvo no Room: $savedUser")
                Result.success(user)
            } catch (e: Exception) {
                Log.e("ROOM_CHECK", "Erro ao salvar no Room", e)
                Result.success(user)
            }
        }
    }

    // 🔹 Google Sign-In
    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.failure(Exception("Usuário não encontrado"))
            saveUserToRoom(user, "google")
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
            saveUserToRoom(user, "email")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔹 Registro com e-mail/senha
    override suspend fun registerWithEmail(
        email: String,
        username: String,
        password: String
    ): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Erro ao criar usuário"))

            // Atualiza o displayName no Firebase
            val profileUpdates = userProfileChangeRequest {
                displayName = username
            }
            user.updateProfile(profileUpdates).await()
            saveUserToRoom(user, "email")
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

    private fun User.toUserEntity(): UserEntity {
        return UserEntity(
            userId = 0,
            firebaseId = userId!!,
            userName = username!!,
            userConfig = userConfig,
            userPhoto = photoUrl
        )
    }
}