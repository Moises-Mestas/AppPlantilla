package com.example.appfirst.ui.tarea

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Tarea
import com.example.appfirst.data.repo.TareaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Estado del formulario de tarea
data class TareaFormState(
    val titulo: String = "",
    val fechaEntrega: String = "",
    val fechaRecordatorio: String = "",
    val asignaturaId: Long? = null,
    val nota: String = "",
    val completada: Boolean = false,
    val archivos: List<String> = emptyList(),
    val errors: Map<String, String> = emptyMap()

)

class TareaViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = TareaRepository(AppDatabase.get(app).tareaDao())

    // Para búsqueda y lista de tareas
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // Tareas del usuario actual
    private val _userId = MutableStateFlow<Long?>(null)
    val tareas = _userId.flatMapLatest { userId ->
        if (userId == null) emptyFlow() else repo.getTareasByUser(userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario
    private val _form = MutableStateFlow(TareaFormState())
    val form: StateFlow<TareaFormState> = _form

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

    // Filtrar tareas por query
    val filteredTareas = combine(tareas, query) { tareasList, queryText ->
        if (queryText.isBlank()) {
            tareasList
        } else {
            tareasList.filter { tarea ->
                tarea.titulo.contains(queryText, ignoreCase = true) ||
                        (tarea.nota?.contains(queryText, ignoreCase = true) ?: false)
            }
        }
    }

    // Iniciar creación de nueva tarea
    fun startCreate() {
        editingId = null
        _form.value = TareaFormState()
        _navigateToSuccess.value = null
        _message.value = null
    }

    // Cargar tarea para editar
    fun loadForEdit(id: Long) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        val tarea = repo.getTareaById(id, userId) ?: return@launch

        editingId = id
        _form.value = TareaFormState(
            titulo = tarea.titulo,
            fechaEntrega = tarea.fechaEntrega.toString(),
            fechaRecordatorio = tarea.fechaRecordatorio.toString(),
            asignaturaId = tarea.asignaturaId,
            nota = tarea.nota ?: "",
            completada = tarea.completada,
            archivos = tarea.archivos
        )
    }

    // Actualizar campos del formulario
    fun onFormChange(
        titulo: String? = null,
        fechaEntrega: String? = null,
        fechaRecordatorio: String? = null,
        asignaturaId: Long? = null,
        nota: String? = null,
        completada: Boolean? = null,
        archivos: List<String>? = null
    ) {
        _form.value = _form.value.copy(
            titulo = titulo ?: _form.value.titulo,
            fechaEntrega = fechaEntrega ?: _form.value.fechaEntrega,
            fechaRecordatorio = fechaRecordatorio ?: _form.value.fechaRecordatorio,
            asignaturaId = asignaturaId ?: _form.value.asignaturaId,
            nota = nota ?: _form.value.nota,
            completada = completada ?: _form.value.completada,
            archivos = archivos ?: _form.value.archivos
        )
    }

    // Validar formulario
    private fun validate(): Boolean {
        val f = _form.value
        val errs = mutableMapOf<String, String>()

        if (f.titulo.isBlank()) errs["titulo"] = "Título obligatorio"
        if (f.asignaturaId == null) errs["asignaturaId"] = "Debe seleccionar una asignatura"

        val fechaEntrega = f.fechaEntrega.toLongOrNull()
        if (fechaEntrega == null) {
            errs["fechaEntrega"] = "Fecha de entrega inválida"
        } else if (fechaEntrega <= System.currentTimeMillis()) {
            errs["fechaEntrega"] = "La fecha debe ser futura"
        }

        val fechaRecordatorio = f.fechaRecordatorio.toLongOrNull()
        if (fechaRecordatorio == null) {
            errs["fechaRecordatorio"] = "Fecha de recordatorio inválida"
        } else if (fechaEntrega != null && fechaRecordatorio >= fechaEntrega) {
            errs["fechaRecordatorio"] = "El recordatorio debe ser antes de la entrega"
        }

        _form.value = f.copy(errors = errs)
        return errs.isEmpty()
    }

    // Guardar tarea (crear o actualizar)
    fun save() = viewModelScope.launch {
        if (!validate()) return@launch

        val f = _form.value
        val userId = _userId.value ?: return@launch
        val fechaEntrega = f.fechaEntrega.toLong()
        val fechaRecordatorio = f.fechaRecordatorio.toLong()

        try {
            val id = editingId
            if (id == null) {
                // Crear nueva tarea
                val newId = repo.crearTarea(
                    titulo = f.titulo,
                    fechaEntrega = fechaEntrega,
                    fechaRecordatorio = fechaRecordatorio,
                    asignaturaId = f.asignaturaId!!,
                    nota = if (f.nota.isBlank()) null else f.nota,
                    archivos = f.archivos,
                    completada = f.completada,
                    userId = userId
                )
                _navigateToSuccess.value = newId
                _message.value = "Tarea creada exitosamente"
            } else {
                // Actualizar tarea existente
                repo.actualizarTarea(
                    id = id,
                    titulo = f.titulo,
                    fechaEntrega = fechaEntrega,
                    fechaRecordatorio = fechaRecordatorio,
                    asignaturaId = f.asignaturaId!!,
                    nota = if (f.nota.isBlank()) null else f.nota,
                    archivos = f.archivos,
                    completada = f.completada,
                    userId = userId
                )
                _navigateToSuccess.value = id
                _message.value = "Tarea actualizada exitosamente"
            }

            startCreate()

        } catch (e: Exception) {
            _form.value = f.copy(errors = mapOf("general" to (e.message ?: "Error desconocido")))
        }
    }

    // Marcar tarea como completada
    fun marcarCompletada(id: Long) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        try {
            repo.marcarCompletada(id, userId)
            _message.value = "Tarea marcada como completada"
        } catch (e: Exception) {
            _message.value = "Error: ${e.message}"
        }
    }

    // Marcar tarea como pendiente
    fun marcarPendiente(id: Long) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        try {
            repo.marcarPendiente(id, userId)
            _message.value = "Tarea marcada como pendiente"
        } catch (e: Exception) {
            _message.value = "Error: ${e.message}"
        }
    }

    // Eliminar tarea
    fun delete(id: Long) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        try {
            repo.eliminarTarea(id, userId)
            _message.value = "Tarea eliminada"
        } catch (e: Exception) {
            _message.value = "Error al eliminar: ${e.message}"
        }
    }

    // Obtener tareas próximas
    fun getTareasProximas(): Flow<List<Tarea>> {
        val userId = _userId.value ?: return emptyFlow()
        return repo.getTareasProximas(userId)
    }

    // Obtener tareas vencidas
    suspend fun getTareasVencidas(): List<Tarea> {
        val userId = _userId.value ?: return emptyList()
        return repo.getTareasVencidas(userId)
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
