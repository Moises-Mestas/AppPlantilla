package com.example.appfirst.core.navigation

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.appfirst.data.local.entity.AccionDiaria
import com.example.appfirst.ui.screens.calendar.AccionDiariaViewModel
import com.example.appfirst.ui.screens.calendar.AccionDiariaViewModelFactory
import com.example.appfirst.ui.screens.calendar.CalendarioScreen
import com.example.appfirst.ui.screens.calendar.FormularioAccionDiariaScreen
import com.example.appfirst.ui.screens.calendar.FormularioNotaScreen
import com.example.appfirst.ui.screens.calendar.HorarioDiarioScreen
import com.example.appfirst.ui.screens.calendar.NotaViewModel
import com.example.appfirst.ui.screens.calendar.NotaViewModelFactory
import com.example.appfirst.ui.screens.calendar.VistaDetallesDiaScreen
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
                onNavigateToNota = {
                    navController.navigate("VistaNotas")
                }
            )
        }

        composable("detalles/{fechaCodificada}") { backStackEntry ->
            val fechaCodificada = backStackEntry.arguments?.getString("fechaCodificada") ?: ""
            val fecha = URLDecoder.decode(fechaCodificada, "UTF-8")

            VistaDetallesDiaScreen(
                fecha = fecha,
                onBackToCalendario = { navController.popBackStack() }
            )
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