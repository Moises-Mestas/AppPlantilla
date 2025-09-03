package com.example.appfirst.ui.user

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.User
import com.example.appfirst.data.repo.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.appfirst.data.local.dao.UserDao

// Estado del formulario de usuario
data class UserFormState(
    val name: String = "",
    val lastname: String = "",
    val email: String = "",
    val age: String = "", // String para manejar fácilmente en UI
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errors: Map<String, String> = emptyMap()
)

class UserViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = UserRepository(AppDatabase.getDatabase(app).userDao())

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
            lastname = user.lastname,
            email = user.email,
            age = user.age.toString(),
            phone = user.phone,
            password = user.password, // En realidad no deberíamos cargar la password
            confirmPassword = user.password
        )
    }

    // Actualizar campos del formulario
    fun onFormChange(
        name: String? = null,
        lastname: String? = null,
        email: String? = null,
        age: String? = null,
        phone: String? = null,
        password: String? = null,
        confirmPassword: String? = null
    ) {
        _form.value = _form.value.copy(
            name = name ?: _form.value.name,
            lastname = lastname ?: _form.value.lastname,
            email = email ?: _form.value.email,
            age = age ?: _form.value.age,
            phone = phone ?: _form.value.phone,
            password = password ?: _form.value.password,
            confirmPassword = confirmPassword ?: _form.value.confirmPassword
        )
    }

    // Validar formulario
    private fun validate(): Boolean {
        val f = _form.value
        val errs = mutableMapOf<String, String>()

        // Validaciones básicas
        if (f.name.isBlank()) errs["name"] = "Nombre obligatorio"
        if (f.lastname.isBlank()) errs["lastname"] = "Apellido obligatorio"

        if (f.email.isBlank() || !f.email.contains("@")) {
            errs["email"] = "Email inválido"
        }

        val age = f.age.toIntOrNull()
        if (age == null || age < 0) {
            errs["age"] = "Edad inválida"
        }

        if (f.phone.isBlank()) errs["phone"] = "Teléfono obligatorio"

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
        val age = f.age.toInt()

        try {
            val id = editingId
            if (id == null) {
                val newId = repo.registerUser(
                    name = f.name,
                    lastname = f.lastname,
                    email = f.email,
                    age = age,
                    phone = f.phone,
                    password = f.password
                )
                _navigateToSuccess.value = newId
            } else {
                repo.updateUser(
                    id = id,
                    name = f.name,
                    lastname = f.lastname,
                    email = f.email,
                    age = age,
                    phone = f.phone
                )
                _navigateToSuccess.value = id
            }

            startCreate()

        } catch (e: Exception) {
            _form.value = f.copy(errors = mapOf("email" to (e.message ?: "Error desconocido")))
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

    fun resetNavigation() {
        _navigateToSuccess.value = null
    }
}