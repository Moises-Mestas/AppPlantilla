package com.example.appfirst.core.navigation

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appfirst.ui.screens.home.InicioScreen
import com.example.appfirst.ui.screens.home.LoginScreen
import com.example.appfirst.ui.screens.home.PrincipalScreen
import com.example.appfirst.ui.screens.home.RegistroScreen
import com.example.appfirst.ui.screens.onboarding.OnboardingScreen
import com.example.appfirst.data.datastore.UserPrefs
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appfirst.data.local.entity.AccionDiaria
import com.example.appfirst.data.local.entity.Nota
import com.example.appfirst.ui.screens.calendar.AccionDiariaViewModel
import com.example.appfirst.ui.screens.calendar.elementos.AccionDiariaViewModelFactory
import com.example.appfirst.ui.screens.calendar.CalendarioScreen
import com.example.appfirst.ui.screens.calendar.FormularioAccionDiariaScreen
import com.example.appfirst.ui.screens.calendar.FormularioNotaScreen
import com.example.appfirst.ui.screens.calendar.HorarioDiarioScreen
import com.example.appfirst.ui.screens.calendar.NotaViewModel
import com.example.appfirst.ui.screens.calendar.VistaDetallesDiaScreen
import com.example.appfirst.ui.screens.calendar.elementos.NotaViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val notaViewModel: NotaViewModel = viewModel ()
    var startDestination by remember { mutableStateOf("onboarding") }

    LaunchedEffect(Unit) {
        val (isOnboardDone, isLoggedIn) = withContext(Dispatchers.IO) {
            val onboardDone = UserPrefs.getOnboardDone(context)
            val loggedIn = UserPrefs.isLoggedIn(context)
            Pair(onboardDone, loggedIn)
        }

        startDestination = when {
            !isOnboardDone -> "onboarding"
            isLoggedIn -> "principal" // ← Ir DIRECTAMENTE a principal si ya está logueado
            else -> "login"
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                navigateToHome = { navController.navigate("inicio") },
                navigateToRegister = { navController.navigate("registro") }
            )
        }

        composable("inicio") {
            InicioScreen(
                navigateToPrincipal = { navController.navigate("principal") }
            )
        }

        composable("registro") {
            RegistroScreen(
                navigateToHome = {
                    navController.navigate("inicio") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("principal") {
            PrincipalScreen(navController = navController)
        }

        ////// seccion de calendario
        composable("VistaCalendario") {
            CalendarioScreen(
                onNavigateToInicio = { navController.navigate("principal") },
                onNavigateToDetalles = { fecha ->
                    val fechaCodificada = URLEncoder.encode(fecha, "UTF-8")
                    navController.navigate("detalles/$fechaCodificada")
                },
                onNavigateToNota = { fecha ->
                    val fechaCodificada = URLEncoder.encode(fecha, "UTF-8")
                    navController.navigate("formularioNota/$fechaCodificada")
                }
            )
        }

        composable("detalles/{fechaCodificada}") { backStackEntry ->
            val fechaCodificada = backStackEntry.arguments?.getString("fechaCodificada") ?: ""
            val fecha = URLDecoder.decode(fechaCodificada, "UTF-8")

            VistaDetallesDiaScreen(
                fecha = fecha,
                onBackToCalendario = { navController.popBackStack() },
                onAddNota = { fechaSeleccionada ->
                    val fechaCod = URLEncoder.encode(fechaSeleccionada, "UTF-8")
                    navController.navigate("formularioNota/$fechaCod")
                },
                onEditarNota = { notaId ->
                    navController.navigate("editarNota/$notaId")
                }
            )
        }

        composable("formularioNota/{fechaCodificada}") { backStackEntry ->
            val fechaCodificada = backStackEntry.arguments?.getString("fechaCodificada") ?: ""
            val fecha = URLDecoder.decode(fechaCodificada, "UTF-8")

            FormularioNotaScreen(
                fecha = fecha,
                notaExistente = null, // Para crear nueva
                onCancel = { navController.popBackStack() },
                onSave = { nota ->
                    notaViewModel.crearNota(nota)
                    navController.popBackStack()
                }
            )
        }

        composable("editarNota/{notaId}") { backStackEntry ->
            val notaId = backStackEntry.arguments?.getString("notaId")?.toIntOrNull() ?: 0
            val viewModel: NotaViewModel = viewModel(
                factory = NotaViewModelFactory(LocalContext.current.applicationContext as Application)
            )

            var notaExistente by remember { mutableStateOf<Nota?>(null) }
            var isLoading by remember { mutableStateOf(notaId > 0) }

            // Cargar la nota
            LaunchedEffect(notaId) {
                if (notaId > 0) {
                    try {
                        notaExistente = viewModel.obtenerNotaPorId(notaId)
                    } catch (e: Exception) {
                        // Manejar error
                    } finally {
                        isLoading = false
                    }
                } else {
                    isLoading = false
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (notaExistente != null) {
                FormularioNotaScreen(
                    fecha = notaExistente!!.fecha,
                    notaExistente = notaExistente,
                    onCancel = { navController.popBackStack() },
                    onSave = { nota ->
                        viewModel.actualizarNota(nota) // ✅ Usar ViewModel
                        navController.popBackStack()
                    },
                    onDelete = {
                        notaExistente?.id?.let { id ->
                            viewModel.eliminarNota(id) // ✅ Usar ViewModel
                            navController.popBackStack()
                        }
                    }
                )
            } else {
                // Manejar caso de error
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No se pudo cargar la nota")
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }
        }

        composable("VistaNotas") {
            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().time)

            FormularioNotaScreen(
                fecha = fechaActual,
                onCancel = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable("horario-diario") {
            HorarioDiarioScreen(
                onBack = { navController.popBackStack() },
                onEditarAccion = { accion ->
                    val accionId = accion?.id ?: 0
                    navController.navigate("editar-accion/$accionId")
                },
                //navController = navController
            )
        }

        composable("editar-accion/{accionId}") { backStackEntry ->
            val accionId = backStackEntry.arguments?.getString("accionId")?.toIntOrNull() ?: 0
            val viewModel: AccionDiariaViewModel = viewModel(
                factory = AccionDiariaViewModelFactory(
                    LocalContext.current.applicationContext as Application
                )
            )

            var accionExistente by remember { mutableStateOf<AccionDiaria?>(null) }
            var isLoading by remember { mutableStateOf(accionId > 0) }

            LaunchedEffect(accionId) {
                if (accionId > 0) {
                    try {
                        accionExistente = viewModel.obtenerAccionPorId(accionId)
                        Log.d("EdicionAccion", "Acción cargada: ${accionExistente?.titulo}")
                    } catch (e: Exception) {
                        Log.e("EdicionAccion", "Error cargando acción: ${e.message}")
                    } finally {
                        isLoading = false
                    }
                } else {
                    isLoading = false
                }
            }

            if (isLoading) {
                // Mostrar loading
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                FormularioAccionDiariaScreen(
                    accionExistente = accionExistente,
                    onGuardar = { accion ->
                        if (accionExistente == null) {
                            // Es una nueva acción
                            viewModel.insertarAccion(accion)
                            Log.d("EdicionAccion", "Nueva acción insertada: ${accion.titulo}")
                        } else {
                            // Es una edición - asegurarnos de mantener el ID
                            val accionActualizada = accion.copy(id = accionExistente!!.id)
                            viewModel.actualizarAccion(accionActualizada)
                            Log.d("EdicionAccion", "Acción actualizada: ${accionActualizada.titulo}")
                        }
                        navController.popBackStack()
                    },
                    onCancelar = { navController.popBackStack() }
                )
            }
        }
    }
}