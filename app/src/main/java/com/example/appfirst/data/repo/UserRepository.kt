package com.example.appfirst.data.repo

import com.example.appfirst.data.local.dao.UserDao
import com.example.appfirst.data.local.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val dao: UserDao) {

    fun getAllUsers(): Flow<List<User>> = dao.observeAll()

    fun searchUsers(query: String): Flow<List<User>> = dao.search(query)

    suspend fun getUserById(id: Long): User? = dao.findById(id)

    suspend fun registerUser(
        name: String,
        lastname: String,
        email: String,
        age: Int,
        phone: String,
        password: String
    ): Long {
        if (name.isBlank() || lastname.isBlank()) {
            throw IllegalArgumentException("Nombre y apellido son obligatorios")
        }

        if (email.isBlank() || !email.contains("@")) {
            throw IllegalArgumentException("Email inválido")
        }

        if (password.length < 4) {
            throw IllegalArgumentException("La contraseña debe tener al menos 4 caracteres")
        }

        // Verificar si el email ya existe
        if (dao.checkEmailExists(email) > 0) {
            throw IllegalArgumentException("El email ya está registrado")
        }

        val user = User(
            name = name.trim(),
            lastname = lastname.trim(),
            email = email.trim().lowercase(),
            age = age,
            phone = phone.trim(),
            password = password
        )

        return dao.insert(user)
    }

    suspend fun updateUser(
        id: Long,
        name: String,
        lastname: String,
        email: String,
        age: Int,
        phone: String
    ) {
        val currentUser = dao.findById(id) ?: throw Exception("Usuario no encontrado")

        if (email != currentUser.email && dao.checkEmailExists(email) > 0) {
            throw IllegalArgumentException("El email ya está en uso por otro usuario")
        }

        val updatedUser = currentUser.copy(
            name = name.trim(),
            lastname = lastname.trim(),
            email = email.trim().lowercase(),
            age = age,
            phone = phone.trim()
        )

        dao.update(updatedUser)
    }

    suspend fun deleteUser(id: Long): Int = dao.deleteById(id)

    suspend fun login(email: String, password: String): User {
        val user = dao.login(email.trim().lowercase(), password)
            ?: throw Exception("Email o contraseña incorrectos")

        return user
    }

    suspend fun checkEmailExists(email: String): Boolean =
        dao.checkEmailExists(email.trim().lowercase()) > 0

    suspend fun getUserByEmail(email: String): User? {

        return try {
            dao.login(email, "temp")
        } catch (e: Exception) {
            null
        }
    }
}