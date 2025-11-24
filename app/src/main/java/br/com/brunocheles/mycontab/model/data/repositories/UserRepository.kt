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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth
) : AuthInterface {

    fun getUserStream(firebaseUid: String): Flow<User?> {
        return userDao.getUserFlow(firebaseUid)
            .map { entity -> entity?.toDomain() } // Mapper Entity -> Domain
            .flowOn(Dispatchers.IO)
    }

    private suspend fun saveUserToRoom(firebaseUser: FirebaseUser, provider: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Converte FirebaseUser -> User (Domínio)
                val newUserDomain = firebaseUser.toDomain(provider)

                // 2. Verifica se JÁ EXISTE no banco local
                val existingEntity = userDao.getUserWithFirebaseId(newUserDomain.userId!!)

                if (existingEntity != null) {
                    // === ATUALIZAR ===
                    // O usuário existe. Devemos MANTER o ID local (Int) dele.
                    Log.d("REPO", "Usuário já existe (ID Local: ${existingEntity.userId}). Atualizando...")

                    val entityToUpdate = newUserDomain.toEntity().copy(
                        userId = existingEntity.userId, // 🔥 MANTÉM O ID LOCAL
                        userConfig = existingEntity.userConfig // Opcional: Mantém configs antigas
                    )
                    userDao.updateUser(entityToUpdate)

                } else {
                    // === INSERIR ===
                    // Usuário novo neste dispositivo.
                    Log.d("REPO", "Usuário novo. Inserindo...")

                    val entityToInsert = newUserDomain.toEntity().copy(
                        userId = 0 // 0 = Room gera novo ID
                    )
                    userDao.insertUser(entityToInsert)
                }

                Result.success(newUserDomain)
            } catch (e: Exception) {
                Log.e("REPO", "Erro ao salvar no Room", e)
                // Retorna sucesso com os dados do Firebase para não travar o login,
                // mas idealmente você trataria o erro de cache aqui.
                Result.success(firebaseUser.toDomain(provider))
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

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Usuário não encontrado"))
            saveUserToRoom(user, "email")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerWithEmail(email: String, username: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Erro ao criar usuário"))

            val profileUpdates = userProfileChangeRequest { displayName = username }
            user.updateProfile(profileUpdates).await()

            saveUserToRoom(user, "email")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    // Mappers Auxiliares (Coloque onde preferir no seu projeto)
    private fun FirebaseUser.toDomain(provider: String) = User(
        userId = uid,
        username = displayName ?: "",
        email = email ?: "",
        photoUrl = photoUrl?.toString(),
        provider = provider
    )

    private fun UserEntity.toDomain() = User(
        userId = firebaseId,
        username = userName,
        userConfig = userConfig,
        photoUrl = userPhoto,
        provider = "local"
    )

    private fun User.toEntity() = UserEntity(
        userId = 0, // Será sobrescrito pela lógica do saveUserToRoom
        firebaseId = userId ?: "",
        userName = username ?: "",
        userConfig = userConfig,
        userPhoto = photoUrl
    )

    // Método getCurrentUser se necessário
    override fun getCurrentUser(): User? {
        val user = firebaseAuth.currentUser ?: return null
        return user.toDomain(user.providerData.firstOrNull()?.providerId ?: "unknown")
    }
}