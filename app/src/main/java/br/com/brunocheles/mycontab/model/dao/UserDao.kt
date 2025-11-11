package br.com.brunocheles.mycontab.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import br.com.brunocheles.mycontab.model.data.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user_tb WHERE userId = :userId")
    fun getUserWithId(userId: Int): UserEntity

    @Query("SELECT * FROM user_tb WHERE firebase_Id = :firebaseId")
    fun getUserWithFirebaseId(firebaseId: String): UserEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Upsert
    fun upsertUser(user: UserEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateUser(user: UserEntity)

    @Delete
    fun deleteUser(user: UserEntity)
}