package com.example.appfirst.ui.examen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.repo.ExamenRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import com.example.appfirst.data.local.entity.Examen

// Estado del formulario de examen
data class ExamenFormState(
    val titulo: String = "",
    val fechaExamen: String = "",
    val fechaRecordatorio: String = "",
    val asignatura: String = "",
    val categoria: String = "escrito", // Categoría por defecto
    val nota: String = "",
    val errors: Map<String, String> = emptyMap()
)

class ExamenViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = ExamenRepository(AppDatabase.get(app).examenDao())

    // Para búsqueda y lista de exámenes
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // Exámenes del usuario actual
    private val _userId = MutableStateFlow<Long?>(null)
    val examenes = _userId.flatMapLatest { userId ->
        if (userId == null) emptyFlow() else repo.getExamenesByUser(userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario
    private val _form = MutableStateFlow(ExamenFormState())
    val form: StateFlow<ExamenFormState> = _form

    private var editingId: Long? = null

    // Estado para navegación/éxito
    private val _navigateToSuccess = MutableStateFlow<Long?>(null)
    val navigateToSuccess: StateFlow<Long?> = _navigateToSuccess

    // Estado para mensajes
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Categorías predefinidas
    val categoriasDisponibles = listOf("oral", "escrito", "práctico")

    // Establecer usuario actual
    fun setUserId(userId: Long) {
        _userId.value = userId
    }

    // Cambiar query de búsqueda
    fun setQuery(q: String) { _query.value = q }

    // Filtrar exámenes por query
    val filteredExamenes = combine(examenes, query) { examenesList, queryText ->
        if (queryText.isBlank()) {
            examenesList
        } else {
            examenesList.filter { examen ->
                examen.titulo.contains(queryText, ignoreCase = true) ||
                        examen.asignatura.contains(queryText, ignoreCase = true) ||
                        examen.nota?.contains(queryText, ignoreCase = true) ?: false
            }
        }
    }

    // Iniciar creación de nuevo examen
    fun startCreate() {
        editingId = null
        _form.value = ExamenFormState()
        _navigateToSuccess.value = null
        _message.value = null
    }

    // Cargar examen para editar
    fun loadForEdit(id: Long) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        val examen = repo.getExamenById(id, userId) ?: return@launch

        editingId = id
        _form.value = ExamenFormState(
            titulo = examen.titulo,
            fechaExamen = examen.fechaExamen.toString(),
            fechaRecordatorio = examen.fechaRecordatorio.toString(),
            asignatura = examen.asignatura,
            categoria = examen.categoria,
            nota = examen.nota ?: ""
        )
    }

    // Actualizar campos del formulario
    fun onFormChange(
        titulo: String? = null,
        fechaExamen: String? = null,
        fechaRecordatorio: String? = null,
        asignatura: String? = null,
        categoria: String? = null,
        nota: String? = null
    ) {
        _form.value = _form.value.copy(
            titulo = titulo ?: _form.value.titulo,
            fechaExamen = fechaExamen ?: _form.value.fechaExamen,
            fechaRecordatorio = fechaRecordatorio ?: _form.value.fechaRecordatorio,
            asignatura = asignatura ?: _form.value.asignatura,
            categoria = categoria ?: _form.value.categoria,
            nota = nota ?: _form.value.nota
        )
    }

    // Validar formulario
    private fun validate(): Boolean {
        val f = _form.value
        val errs = mutableMapOf<String, String>()

        if (f.titulo.isBlank()) errs["titulo"] = "Título obligatorio"
        if (f.asignatura.isBlank()) errs["asignatura"] = "Asignatura obligatoria"

        val fechaExamen = f.fechaExamen.toLongOrNull()
        if (fechaExamen == null) {
            errs["fechaExamen"] = "Fecha de examen inválida"
        } else if (fechaExamen <= System.currentTimeMillis()) {
            errs["fechaExamen"] = "La fecha debe ser futura"
        }

        val fechaRecordatorio = f.fechaRecordatorio.toLongOrNull()
        if (fechaRecordatorio == null) {
            errs["fechaRecordatorio"] = "Fecha de recordatorio inválida"
        } else if (fechaRecordatorio >= fechaExamen ?: Long.MAX_VALUE) {
            errs["fechaRecordatorio"] = "El recordatorio debe ser antes del examen"
        }

        if (f.categoria.isBlank() || !categoriasDisponibles.contains(f.categoria)) {
            errs["categoria"] = "Categoría inválida. Use: oral, escrito, práctico"
        }

        _form.value = f.copy(errors = errs)
        return errs.isEmpty()
    }

    // Guardar examen (crear o actualizar)
    fun save() = viewModelScope.launch {
        if (!validate()) return@launch

        val f = _form.value
        val userId = _userId.value ?: return@launch
        val fechaExamen = f.fechaExamen.toLong()
        val fechaRecordatorio = f.fechaRecordatorio.toLong()

        try {
            val id = editingId
            if (id == null) {
                // Crear nuevo examen
                val newId = repo.crearExamen(
                    titulo = f.titulo,
                    fechaExamen = fechaExamen,
                    fechaRecordatorio = fechaRecordatorio,
                    asignatura = f.asignatura,
                    categoria = f.categoria,
                    nota = if (f.nota.isBlank()) null else f.nota,
                    userId = userId
                )
                _navigateToSuccess.value = newId
                _message.value = "Examen creado exitosamente"
            } else {
                // Actualizar examen existente
                repo.actualizarExamen(
                    id = id,
                    titulo = f.titulo,
                    fechaExamen = fechaExamen,
                    fechaRecordatorio = fechaRecordatorio,
                    asignatura = f.asignatura,
                    categoria = f.categoria,
                    nota = if (f.nota.isBlank()) null else f.nota,
                    userId = userId
                )
                _navigateToSuccess.value = id
                _message.value = "Examen actualizado exitosamente"
            }

            startCreate()

        } catch (e: Exception) {
            _form.value = f.copy(errors = mapOf("general" to (e.message ?: "Error desconocido")))
        }
    }

    // Eliminar examen
    fun delete(id: Long) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        try {
            repo.eliminarExamen(id, userId)
            _message.value = "Examen eliminado"
        } catch (e: Exception) {
            _message.value = "Error al eliminar: ${e.message}"
        }
    }

    // Obtener exámenes próximos (próximos 7 días)
    fun getExamenesProximos(): Flow<List<Examen>> {
        val userId = _userId.value ?: return emptyFlow()
        return repo.getExamenesProximos(userId)
    }

    // Obtener exámenes de hoy
    fun getExamenesDeHoy(): Flow<List<Examen>> {
        val userId = _userId.value ?: return emptyFlow()
        return repo.getExamenesDeHoy(userId)
    }

    // Obtener exámenes por categoría
    fun getExamenesPorCategoria(categoria: String): Flow<List<Examen>> {
        val userId = _userId.value ?: return emptyFlow()
        return repo.getExamenesByCategoria(userId, categoria)
    }

    // Obtener exámenes por asignatura
    fun getExamenesPorAsignatura(asignatura: String): Flow<List<Examen>> {
        val userId = _userId.value ?: return emptyFlow()
        return repo.getExamenesByAsignatura(userId, asignatura)
    }

    // Obtener exámenes vencidos
    suspend fun getExamenesVencidos(): List<Examen> {
        val userId = _userId.value ?: return emptyList()
        return repo.getExamenesVencidos(userId)
    }

    // Obtener estadísticas
    suspend fun getEstadisticas(): Map<String, Int> {
        val userId = _userId.value ?: return emptyMap()
        return repo.getEstadisticasExamenes(userId)
    }

    // Buscar exámenes
    suspend fun buscarExamenes(query: String): List<Examen> {
        val userId = _userId.value ?: return emptyList()
        return repo.buscarExamenes(userId, query)
    }

    // Limpiar mensajes
    fun clearMessage() {
        _message.value = null
    }

    // Resetear navegación
    fun resetNavigation() {
        _navigateToSuccess.value = null
    }

    // Formatear fecha para mostrar
    fun formatFecha(timestamp: Long): String {
        return android.text.format.DateFormat.format("dd/MM/yyyy HH:mm", Date(timestamp)).toString()
    }

    // Obtener icono según categoría
    fun getIconoPorCategoria(categoria: String): String {
        return when (categoria.lowercase()) {
            "oral" -> "🎤"
            "escrito" -> "📝"
            "práctico" -> "🔬"
            else -> "📚"
        }
    }
}