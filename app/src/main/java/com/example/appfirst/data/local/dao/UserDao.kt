package com.example.appfirst.data.local.dao

import androidx.room.*
import com.example.appfirst.data.local.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<User>>

    @Query("""
        SELECT * FROM users 
        WHERE (name LIKE '%' || :query || '%' OR 
               email LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun search(query: String): Flow<List<User>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User): Int

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun checkEmailExists(email: String): Int

    @Delete
    suspend fun delete(user: User): Int

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT id FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserIdByEmail(email: String): Long?

    @Query("SELECT * FROM users WHERE name = :name AND password = :password LIMIT 1")
    suspend fun loginWithName(name: String, password: String): User?
}