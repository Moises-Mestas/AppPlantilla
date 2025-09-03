package com.example.appfirst.ui.asignatura

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Asignatura
import com.example.appfirst.data.repo.AsignaturaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


// Estado del formulario de asignatura
data class AsignaturaFormState(
    val nombre: String = "",
    val profesor: String = "",
    val aula: String = "",
    val errors: Map<String, String> = emptyMap()
)

class AsignaturaViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = AsignaturaRepository(AppDatabase.get(app).asignaturaDao())

    // Para búsqueda y lista de asignaturas
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // Asignaturas del usuario actual
    private val _userId = MutableStateFlow<Long?>(null)
    val asignaturas = _userId.flatMapLatest { userId ->
        if (userId == null) emptyFlow() else repo.getAsignaturasByUser(userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario
    private val _form = MutableStateFlow(AsignaturaFormState())
    val form: StateFlow<AsignaturaFormState> = _form

    private var editingId: Long? = null

    // Estado para navegación/éxito
    private val _navigateToSuccess = MutableStateFlow<Long?>(null)
    val navigateToSuccess: StateFlow<Long?> = _navigateToSuccess

    // Estado para mensajes
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Establecer usuario actual
    fun setUserId(userId: Long) {
        _userId.value = userId
    }

    // Cambiar query de búsqueda
    fun setQuery(q: String) { _query.value = q }

    // Filtrar asignaturas por query
    val filteredAsignaturas = combine(asignaturas, query) { asignaturasList, queryText ->
        if (queryText.isBlank()) {
            asignaturasList
        } else {
            asignaturasList.filter { asignatura ->
                asignatura.nombre.contains(queryText, ignoreCase = true) ||
                        asignatura.profesor.contains(queryText, ignoreCase = true) ||
                        asignatura.aula.contains(queryText, ignoreCase = true)
            }
        }
    }

    // Iniciar creación de nueva asignatura
    fun startCreate() {
        editingId = null
        _form.value = AsignaturaFormState()
        _navigateToSuccess.value = null
        _message.value = null
    }

    // Cargar asignatura para editar
    fun loadForEdit(id: Long) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        val asignatura = repo.getAsignaturaById(id, userId) ?: return@launch

        editingId = id
        _form.value = AsignaturaFormState(
            nombre = asignatura.nombre,
            profesor = asignatura.profesor,
            aula = asignatura.aula
        )
    }

    // Actualizar campos del formulario
    fun onFormChange(
        nombre: String? = null,
        profesor: String? = null,
        aula: String? = null
    ) {
        _form.value = _form.value.copy(
            nombre = nombre ?: _form.value.nombre,
            profesor = profesor ?: _form.value.profesor,
            aula = aula ?: _form.value.aula
        )
    }

    // Validar formulario
    private fun validate(): Boolean {
        val f = _form.value
        val errs = mutableMapOf<String, String>()

        if (f.nombre.isBlank()) errs["nombre"] = "Nombre obligatorio"
        if (f.profesor.isBlank()) errs["profesor"] = "Profesor obligatorio"
        if (f.aula.isBlank()) errs["aula"] = "Aula obligatoria"

        _form.value = f.copy(errors = errs)
        return errs.isEmpty()
    }


    fun save() = viewModelScope.launch {
        if (!validate()) return@launch

        val f = _form.value
        val userId = _userId.value ?: return@launch

        try {
            val id = editingId
            if (id == null) {
                // Crear nueva asignatura
                val result = repo.createAsignatura(
                    nombre = f.nombre,
                    profesor = f.profesor,
                    aula = f.aula,
                    userId = userId
                )

                if (result.isSuccess) {
                    _navigateToSuccess.value = result.getOrNull()
                    _message.value = "Asignatura creada exitosamente"
                    startCreate()
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _form.value = f.copy(errors = f.errors + mapOf("general" to errorMsg))
                }
            } else {
                // Actualizar asignatura existente
                val asignatura = repo.getAsignaturaById(id, userId) ?: run {
                    _form.value = f.copy(errors = f.errors + mapOf("general" to "Asignatura no encontrada"))
                    return@launch
                }

                val updatedAsignatura = asignatura.copy(
                    nombre = f.nombre,
                    profesor = f.profesor,
                    aula = f.aula
                )

                repo.updateAsignatura(updatedAsignatura)
                _navigateToSuccess.value = id
                _message.value = "Asignatura actualizada exitosamente"
                startCreate()
            }

        } catch (e: Exception) {
            _form.value = f.copy(errors = f.errors + mapOf("general" to (e.message ?: "Error desconocido")))
        }
    }



    // Eliminar asignatura
    fun delete(id: Long) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        try {
            val asignatura = repo.getAsignaturaById(id, userId) ?: run {
                _message.value = "Asignatura no encontrada"
                return@launch
            }

            repo.deleteAsignatura(asignatura)
            _message.value = "Asignatura eliminada"
        } catch (e: Exception) {
            _message.value = "Error al eliminar: ${e.message}"
        }
    }

    // Buscar asignaturas
    fun searchAsignaturas(query: String): Flow<List<Asignatura>> {
        val userId = _userId.value ?: return emptyFlow()
        return repo.searchAsignaturas(userId, query)
    }

    // Obtener asignatura por nombre
    suspend fun getAsignaturaByNombre(nombre: String): Asignatura? {
        val userId = _userId.value ?: return null
        return repo.getAsignaturaByNombre(userId, nombre)
    }

    // Verificar si existe asignatura con mismo nombre
    suspend fun existsAsignatura(nombre: String): Boolean {
        val userId = _userId.value ?: return false
        return repo.existsAsignatura(userId, nombre)
    }

    // Limpiar mensajes
    fun clearMessage() {
        _message.value = null
    }

    // Resetear navegación
    fun resetNavigation() {
        _navigateToSuccess.value = null
    }
}