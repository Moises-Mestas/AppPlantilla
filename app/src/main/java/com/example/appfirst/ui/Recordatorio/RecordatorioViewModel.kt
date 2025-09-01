package com.example.appfirst.ui.recordatorio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Recordatorio
import com.example.appfirst.data.repo.RecordatorioRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date

// Estado del formulario de recordatorio
data class RecordatorioFormState(
    val titulo: String = "",
    val fechaRecordatorio: String = "",
    val color: String = "#6200EE", // Color por defecto
    val nota: String = "",
    val errors: Map<String, String> = emptyMap()
)

class RecordatorioViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = RecordatorioRepository(AppDatabase.get(app).recordatorioDao())

    // Para búsqueda y lista de recordatorios
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // Recordatorios del usuario actual
    private val _userId = MutableStateFlow<Long?>(null)
    val recordatorios = _userId.flatMapLatest { userId ->
        if (userId == null) emptyFlow() else repo.getRecordatoriosByUser(userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario
    private val _form = MutableStateFlow(RecordatorioFormState())
    val form: StateFlow<RecordatorioFormState> = _form

    private var editingId: Long? = null

    // Estado para navegación/éxito
    private val _navigateToSuccess = MutableStateFlow<Long?>(null)
    val navigateToSuccess: StateFlow<Long?> = _navigateToSuccess

    // Estado para mensajes
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Colores predefinidos para elegir
    val coloresDisponibles = listOf(
        "#FF5252", // Rojo
        "#FF4081", // Rosa
        "#E040FB", // Púrpura
        "#7C4DFF", // Violeta
        "#536DFE", // Azul
        "#448AFF", // Azul claro
        "#40C4FF", // Celeste
        "#18FFFF", // Cian
        "#64FFDA", // Turquesa
        "#69F0AE", // Verde claro
        "#B2FF59", // Lima
        "#EEFF41", // Amarillo
        "#FFFF00", // Amarillo fuerte
        "#FFD740", // Ámbar
        "#FFAB40", // Naranja
        "#FF6E40"  // Naranja fuerte
    )

    // Establecer usuario actual
    fun setUserId(userId: Long) {
        _userId.value = userId
    }

    // Cambiar query de búsqueda
    fun setQuery(q: String) { _query.value = q }

    // Filtrar recordatorios por query
    val filteredRecordatorios = combine(recordatorios, query) { recordatoriosList, queryText ->
        if (queryText.isBlank()) {
            recordatoriosList
        } else {
            recordatoriosList.filter { recordatorio ->
                recordatorio.titulo.contains(queryText, ignoreCase = true) ||
                        recordatorio.nota?.contains(queryText, ignoreCase = true) ?: false
            }
        }
    }

    // Iniciar creación de nuevo recordatorio
    fun startCreate() {
        editingId = null
        _form.value = RecordatorioFormState()
        _navigateToSuccess.value = null
        _message.value = null
    }

    // Cargar recordatorio para editar
    fun loadForEdit(id: Long) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        val recordatorio = repo.getRecordatorioById(id, userId) ?: return@launch

        editingId = id
        _form.value = RecordatorioFormState(
            titulo = recordatorio.titulo,
            fechaRecordatorio = recordatorio.fechaRecordatorio.toString(),
            color = recordatorio.color,
            nota = recordatorio.nota ?: ""
        )
    }

    // Actualizar campos del formulario
    fun onFormChange(
        titulo: String? = null,
        fechaRecordatorio: String? = null,
        color: String? = null,
        nota: String? = null
    ) {
        _form.value = _form.value.copy(
            titulo = titulo ?: _form.value.titulo,
            fechaRecordatorio = fechaRecordatorio ?: _form.value.fechaRecordatorio,
            color = color ?: _form.value.color,
            nota = nota ?: _form.value.nota
        )
    }

    // Validar formulario
    private fun validate(): Boolean {
        val f = _form.value
        val errs = mutableMapOf<String, String>()

        if (f.titulo.isBlank()) errs["titulo"] = "Título obligatorio"

        val fechaRecordatorio = f.fechaRecordatorio.toLongOrNull()
        if (fechaRecordatorio == null) {
            errs["fechaRecordatorio"] = "Fecha inválida"
        } else if (fechaRecordatorio <= System.currentTimeMillis()) {
            errs["fechaRecordatorio"] = "La fecha debe ser futura"
        }

        if (f.color.isBlank() || !f.color.startsWith("#")) {
            errs["color"] = "Color inválido"
        }

        _form.value = f.copy(errors = errs)
        return errs.isEmpty()
    }

    // Guardar recordatorio (crear o actualizar)
    fun save() = viewModelScope.launch {
        if (!validate()) return@launch

        val f = _form.value
        val userId = _userId.value ?: return@launch
        val fechaRecordatorio = f.fechaRecordatorio.toLong()

        try {
            val id = editingId
            if (id == null) {
                // Crear nuevo recordatorio
                val newId = repo.crearRecordatorio(
                    titulo = f.titulo,
                    fechaRecordatorio = fechaRecordatorio,
                    color = f.color,
                    nota = if (f.nota.isBlank()) null else f.nota,
                    userId = userId
                )
                _navigateToSuccess.value = newId
                _message.value = "Recordatorio creado exitosamente"
            } else {
                // Actualizar recordatorio existente
                repo.actualizarRecordatorio(
                    id = id,
                    titulo = f.titulo,
                    fechaRecordatorio = fechaRecordatorio,
                    color = f.color,
                    nota = if (f.nota.isBlank()) null else f.nota,
                    userId = userId
                )
                _navigateToSuccess.value = id
                _message.value = "Recordatorio actualizado exitosamente"
            }

            startCreate()

        } catch (e: Exception) {
            _form.value = f.copy(errors = mapOf("general" to (e.message ?: "Error desconocido")))
        }
    }

    // Eliminar recordatorio
    fun delete(id: Long) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        try {
            repo.eliminarRecordatorio(id, userId)
            _message.value = "Recordatorio eliminado"
        } catch (e: Exception) {
            _message.value = "Error al eliminar: ${e.message}"
        }
    }

    // Obtener recordatorios próximos (próximos 7 días)
    fun getRecordatoriosProximos(): Flow<List<Recordatorio>> {
        val userId = _userId.value ?: return emptyFlow()
        return repo.getRecordatoriosProximos(userId)
    }

    // Obtener recordatorios de hoy
    fun getRecordatoriosDeHoy(): Flow<List<Recordatorio>> {
        val userId = _userId.value ?: return emptyFlow()
        return repo.getRecordatoriosDeHoy(userId)
    }

    // Obtener recordatorios por color
    suspend fun getRecordatoriosPorColor(color: String): List<Recordatorio> {
        val userId = _userId.value ?: return emptyList()
        return repo.getRecordatoriosByColor(userId, color)
    }

    // Buscar recordatorios
    suspend fun buscarRecordatorios(query: String): List<Recordatorio> {
        val userId = _userId.value ?: return emptyList()
        return repo.buscarRecordatorios(userId, query)
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
}