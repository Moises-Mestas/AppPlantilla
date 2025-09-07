package com.example.appfirst.ui.ingreso

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.AppDatabase
import com.example.appfirst.data.local.entity.Ingreso
import com.example.appfirst.data.local.entity.MedioPago
import com.example.appfirst.data.repo.IngresoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Estado del formulario de ingreso
data class IngresoFormState(
    val monto: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val depositadoEn: com.example.appfirst.data.local.entity.MedioPago = com.example.appfirst.data.local.entity.MedioPago.TARJETA,
    val notas: String = "",
    val errors: Map<String, String> = emptyMap()
)

class IngresoViewModel(app: Application) : AndroidViewModel(app) {
    // Agregar variable para total de ingresos
    private val _montoTotal = MutableStateFlow(0.0)
    val montoTotal: StateFlow<Double> = _montoTotal



    private val _montoTotalTarjeta = MutableStateFlow(0.0)
    val montoTotalTarjeta: StateFlow<Double> = _montoTotalTarjeta

    private val _montoTotalEfectivo = MutableStateFlow(0.0)
    val montoTotalEfectivo: StateFlow<Double> = _montoTotalEfectivo

    private val _montoTotalYape = MutableStateFlow(0.0)
    val montoTotalYape: StateFlow<Double> = _montoTotalYape
    // Guardar el monto total cuando se crea un ingreso

    suspend fun updateMontoTotal() {
        val userId = _userId.value ?: return // Asegúrate de que userId no sea nulo
        val total = repo.getAllIngresosSum(userId) // Llama a la función con el userId

        // Obtén la suma de cada tipo de pago desde el repositorio
        val totalTarjeta = repo.getIngresosByDeposito(userId, MedioPago.TARJETA).first().sumOf { it.monto }
        val totalEfectivo = repo.getIngresosByDeposito(userId, MedioPago.EFECTIVO).first().sumOf { it.monto }
        val totalYape = repo.getIngresosByDeposito(userId, MedioPago.YAPE).first().sumOf { it.monto }

        _montoTotal.value = total
        _montoTotalTarjeta.value = totalTarjeta
        _montoTotalEfectivo.value = totalEfectivo
        _montoTotalYape.value = totalYape


    }









    private val repo = IngresoRepository(AppDatabase.get(app).ingresoDao())

    // Para búsqueda y lista de ingresos
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    // Ingresos del usuario actual
    private val _userId = MutableStateFlow<Long?>(null)
    val ingresos = _userId.flatMapLatest { userId ->
        if (userId == null) emptyFlow() else repo.getIngresosByUser(userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Estado del formulario
    private val _form = MutableStateFlow(IngresoFormState())
    val form: StateFlow<IngresoFormState> = _form

    // Cambia el tipo a nullable
    private var editingId: Int? = null

    // Navegación con Int (id de ingreso)
    private val _navigateToSuccess = MutableStateFlow<Int?>(null)
    val navigateToSuccess: StateFlow<Int?> = _navigateToSuccess


    // Estado para mensajes
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Establecer usuario actual
    fun setUserId(userId: Long) {
        _userId.value = userId
    }

    // Cambiar query de búsqueda
    fun setQuery(q: String) {
        _query.value = q
    }

    // Filtrar ingresos por query
    val filteredIngresos = combine(ingresos, query) { ingresosList, queryText ->
        if (queryText.isBlank()) {
            ingresosList
        } else {
            val q = queryText.trim()
            ingresosList.filter { ingreso ->
                ingreso.descripcion.contains(q, ignoreCase = true) ||
                        ingreso.notas.contains(q, ignoreCase = true) ||
                        // Si creaste "label" o "display()", usa eso; si no, usa name:
                        ingreso.depositadoEn.name.contains(q, ignoreCase = true)
                // o: ingreso.depositadoEn.label.contains(q, ignoreCase = true)
            }
        }
    }

    // Cargar el userId al iniciar el ViewModel
    init {
        loadUserId()
    }

    private fun loadUserId() {
        viewModelScope.launch {
            try {
                val userDao = AppDatabase.get(getApplication()).userDao()
                val userEmail = UserPrefs.getLoggedUserEmail(getApplication())
                val users = userDao.getAllUsers().first()
                val userId = users.firstOrNull { it.email == userEmail }?.id
                if (userId != null) {
                    _userId.value = userId
                    updateMontoTotal()
                }
            } catch (e: Exception) {
                _message.value = "Error al cargar el usuario: ${e.message}"
            }
        }
    }

    // Iniciar creación de nuevo ingreso
    fun startCreate() {
        editingId = null
        _form.value = IngresoFormState(
            fecha = System.currentTimeMillis().toString() // 👈 default
        )
        _navigateToSuccess.value = null
        _message.value = null
    }

    // Cargar ingreso para editar
    fun loadForEdit(id: Int) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        val ingreso = repo.getIngresoById(id, userId) ?: return@launch

        editingId = id
        _form.value = IngresoFormState(
            monto = ingreso.monto.toString(),
            descripcion = ingreso.descripcion,
            fecha = ingreso.fecha.toString(),
            depositadoEn = ingreso.depositadoEn,
            notas = ingreso.notas
        )
    }

    // Actualizar campos del formulario
    fun onFormChange(
        monto: String? = null,
        descripcion: String? = null,
        fecha: String? = null,
        depositadoEn: com.example.appfirst.data.local.entity.MedioPago? = null,
        notas: String? = null
    ) {
        _form.value = _form.value.copy(
            monto = monto ?: _form.value.monto,
            descripcion = descripcion ?: _form.value.descripcion,
            fecha = fecha ?: _form.value.fecha,
            depositadoEn = depositadoEn ?: _form.value.depositadoEn,
            notas = notas ?: _form.value.notas
        )
    }

    // Validar formulario
    private fun validate(): Boolean {
        val f = _form.value
        val errs = mutableMapOf<String, String>()

        if (f.descripcion.isBlank()) errs["descripcion"] = "Descripción obligatoria"

        val monto = f.monto.replace(',', '.').toDoubleOrNull()
        if (monto == null || monto <= 0.0) {
            errs["monto"] = "Monto debe ser mayor a 0"
        }

        // fecha: si viene algo y no es número -> error
        val fechaNum = f.fecha.takeIf { it.isNotBlank() }?.toLongOrNull()
        if (f.fecha.isNotBlank() && fechaNum == null) {
            errs["fecha"] = "Fecha inválida"
        }

        _form.value = f.copy(errors = errs)
        return errs.isEmpty()
    }


    // Guardar ingreso (crear o actualizar)
    fun save(isGasto: Boolean = false) {
        viewModelScope.launch {
            if (!validate()) return@launch

            val f = _form.value
            val userId = _userId.value ?: run {
                _message.value = "No se ha establecido el usuario. Vuelve a iniciar sesión."
                return@launch
            }

            val fecha = f.fecha.toLongOrNull() ?: System.currentTimeMillis()
            val monto = f.monto.replace(',', '.').toDouble() // ya validado

            try {
                val id = editingId
                if (id == null) {
                    val newId = repo.crearIngreso(
                        monto = if (isGasto) -monto else monto,  // Si es gasto, lo hacemos negativo
                        descripcion = f.descripcion.trim(),
                        fecha = fecha,
                        depositadoEn = f.depositadoEn,   // ✅ enum directo
                        notas = f.notas.trim(),
                        userId = userId
                    )
                    _navigateToSuccess.value = newId.toInt()
                    _message.value = "Ingreso creado exitosamente"
                } else {
                    repo.actualizarIngreso(
                        id = id,
                        monto = if (isGasto) -monto else monto,
                        descripcion = f.descripcion.trim(),
                        fecha = fecha,
                        depositadoEn = f.depositadoEn,   // ✅ enum directo
                        notas = f.notas.trim(),
                        userId = userId
                    )
                    _navigateToSuccess.value = id
                    _message.value = "Ingreso actualizado exitosamente"
                }

                // Llamar a esta función después de guardar o actualizar
                updateMontoTotal()  // Esto actualizará el monto total

                startCreate() // limpia formulario

            } catch (e: Exception) {
                _form.value = f.copy(errors = mapOf("general" to (e.message ?: "Error desconocido")))

            }
        }
    }

    // Eliminar ingreso
    fun delete(id: Int) = viewModelScope.launch {
        val userId = _userId.value ?: return@launch
        try {
            repo.eliminarIngreso(id, userId)
            _message.value = "Ingreso eliminado"
            updateMontoTotal()           // <--- recalcula totales después de eliminar

        } catch (e: Exception) {
            _message.value = "Error al eliminar: ${e.message}"
        }
    }
    fun reloadData() {
        // Esta función recarga los datos
        viewModelScope.launch {
            updateMontoTotal()  // Vuelve a calcular los totales
        }
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
