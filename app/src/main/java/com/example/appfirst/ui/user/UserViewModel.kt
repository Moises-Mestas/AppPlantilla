package com.example.appfirst.ui.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.User
import com.example.appfirst.data.repo.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Estado del formulario de usuario (simplificado)
data class UserFormState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errors: Map<String, String> = emptyMap()
)

class UserViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = UserRepository(AppDatabase.get(app).userDao())

    // Para búsqueda y lista de usuarios
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    val users = query
        .debounce(250) // Espera 250ms después de teclear
        .flatMapLatest { q ->
            if (q.isBlank()) repo.getAllUsers() else repo.searchUsers(q)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario
    private val _form = MutableStateFlow(UserFormState())
    val form: StateFlow<UserFormState> = _form

    private var editingId: Long? = null

    // Estado para navegación/éxito
    private val _navigateToSuccess = MutableStateFlow<Long?>(null)
    val navigateToSuccess: StateFlow<Long?> = _navigateToSuccess

    // Cambiar query de búsqueda
    fun setQuery(q: String) { _query.value = q }

    // Iniciar creación de nuevo usuario
    fun startCreate() {
        editingId = null
        _form.value = UserFormState()
        _navigateToSuccess.value = null
    }

    // Cargar usuario para editar
    fun loadForEdit(id: Long) = viewModelScope.launch {
        val user = repo.getUserById(id) ?: return@launch
        editingId = id
        _form.value = UserFormState(
            name = user.name,
            email = user.email,
            password = user.password,
            confirmPassword = user.password
        )
    }

    // Actualizar campos del formulario
    fun onFormChange(
        name: String? = null,
        email: String? = null,
        password: String? = null,
        confirmPassword: String? = null
    ) {
        _form.value = _form.value.copy(
            name = name ?: _form.value.name,
            email = email ?: _form.value.email,
            password = password ?: _form.value.password,
            confirmPassword = confirmPassword ?: _form.value.confirmPassword
        )
    }

    // Validar formulario (simplificado)
    private fun validate(): Boolean {
        val f = _form.value
        val errs = mutableMapOf<String, String>()

        // Validaciones básicas
        if (f.name.isBlank()) errs["name"] = "Nombre obligatorio"

        if (f.email.isBlank() || !f.email.contains("@")) {
            errs["email"] = "Email inválido"
        }

        if (f.password.length < 4) {
            errs["password"] = "Mínimo 4 caracteres"
        }

        if (f.password != f.confirmPassword) {
            errs["confirmPassword"] = "Las contraseñas no coinciden"
        }

        _form.value = f.copy(errors = errs)
        return errs.isEmpty()
    }

    fun save() = viewModelScope.launch {
        if (!validate()) return@launch

        val f = _form.value

        try {
            val id = editingId
            if (id == null) {
                // Crear nuevo usuario
                val newId = repo.registerUser(
                    name = f.name,
                    email = f.email,
                    password = f.password
                )
                _navigateToSuccess.value = newId
            } else {
                // Actualizar usuario existente
                repo.updateUser(
                    id = id,
                    name = f.name,
                    email = f.email
                )
                _navigateToSuccess.value = id
            }

            startCreate()

        } catch (e: Exception) {
            _form.value = f.copy(errors = mapOf("general" to (e.message ?: "Error desconocido")))
        }
    }

    fun delete(id: Long) = viewModelScope.launch {
        repo.deleteUser(id)
    }

    fun login(email: String, password: String, onSuccess: (User) -> Unit, onError: (String) -> Unit) = viewModelScope.launch {
        try {
            val user = repo.login(email, password)
            onSuccess(user)
        } catch (e: Exception) {
            onError(e.message ?: "Error en login")
        }
    }

    // Nuevo método para login con nombre y contraseña
    fun loginWithName(name: String, password: String, onSuccess: (User) -> Unit, onError: (String) -> Unit) = viewModelScope.launch {
        try {
            val user = repo.loginWithName(name, password)
            onSuccess(user)
        } catch (e: Exception) {
            onError(e.message ?: "Error en login")
        }
    }

    fun resetNavigation() {
        _navigateToSuccess.value = null
    }
}