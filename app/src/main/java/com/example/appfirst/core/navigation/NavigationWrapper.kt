package com.example.appfirst.core.navigation

import android.app.Application
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.data.local.entity.Nota
import com.example.appfirst.ui.screens.home.InicioScreen
import com.example.appfirst.ui.screens.home.LoginScreen
import com.example.appfirst.ui.screens.home.PrincipalScreen
import com.example.appfirst.ui.screens.home.RegistroScreen
import com.example.appfirst.ui.screens.onboarding.OnboardingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//  Screens Agenda
import com.example.appfirst.ui.screens.Agenda.FormExamenScreen
import com.example.appfirst.ui.screens.Agenda.FormTareaScreen
import com.example.appfirst.ui.screens.Agenda.RecordatorioScreen
import com.example.appfirst.ui.screens.AsignaturaScreen
import com.example.appfirst.ui.screens.calendar.NotaViewModel

// creens Finanzas
import com.example.appfirst.ui.ingresos.GastoScreen
import com.example.appfirst.ui.ingresos.HistorialScreen
import com.example.appfirst.ui.ingresos.IngresoScreen2
import com.example.appfirst.ui.screens.Agenda.AgendaScreen

// Screens Calendario
import com.example.appfirst.ui.screens.calendar.AccionDiariaViewModel
import com.example.appfirst.ui.screens.calendar.CalendarioScreen
import com.example.appfirst.ui.screens.calendar.HorarioDiarioScreen
import com.example.appfirst.ui.screens.calendar.VistaDetallesDiaScreen
import com.example.appfirst.ui.screens.calendar.elementos.AccionDiariaViewModelFactory
import com.example.appfirst.ui.screens.calendar.elementos.FormularioAccionDiariaScreen
import com.example.appfirst.ui.screens.calendar.elementos.FormularioNotaScreen
import com.example.appfirst.ui.screens.calendar.elementos.NotaViewModelFactory
import com.example.appfirst.ui.screens.cuentas.CuentasScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val notaViewModel: NotaViewModel = viewModel()
    var startDestination by remember { mutableStateOf("onboarding") }
    val accionDiariaViewModel: AccionDiariaViewModel = viewModel(
        factory = AccionDiariaViewModelFactory(context.applicationContext as Application)
    )

    LaunchedEffect(Unit) {
        val (isOnboardDone, isLoggedIn) = withContext(Dispatchers.IO) {
            val onboardDone = UserPrefs.getOnboardDone(context)
            val loggedIn = UserPrefs.isLoggedIn(context)
            Pair(onboardDone, loggedIn)
        }

        startDestination = when {
            !isOnboardDone -> "onboarding"
            isLoggedIn -> "principal"
            else -> "login"
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Onboarding
        composable("onboarding") {
            OnboardingScreen(
                onFinish = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        // Login / Registro
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

        // Principal
        composable("principal") {
            PrincipalScreen(
                navigateTotarea = { navController.navigate("tarea") },
                navigateToCuentas = { navController.navigate("cuentas") },
                navigateToCalendario = { navController.navigate("CalendarioScreen") },
                navigateToHorarioDiario = { navController.navigate("HorarioDiario") }
            )
        }

        // =========================
        // 📌 Agenda
        // =========================
        composable("tarea") {
            AgendaScreen (
                navigatetoAsignatura = { navController.navigate("Asignatura") },
                navigateToFormTarea = { navController.navigate("FormTarea") },
                navigateToExamen = { navController.navigate("Examen") },
                navigateToRecordatorio = { navController.navigate("recordatorio") } ,
                navigateTotarea = { navController.navigate("tarea") },
                navigateToCuentas = { navController.navigate("cuentas") },
                navigateToCalendario = { navController.navigate("CalendarioScreen") },
                navigateToHorarioDiario = { navController.navigate("HorarioDiario") }
            )
        }
        composable("FormTarea") { FormTareaScreen() }
        composable("Asignatura") { AsignaturaScreen() }
        composable("Examen") { FormExamenScreen() }
        composable("recordatorio") { RecordatorioScreen() }

        // =========================
        // 📌 Finanzas
        // =========================
        composable("ingreso2") {
            IngresoScreen2(
                navController = navController,
                ingresoId = null,
                navigateBack = { navController.popBackStack() },
                navigateToGastos = { navController.navigate("gastos") },
                navigateToHistorial = { navController.navigate("historial") },
                navigateToCuentas = { navController.navigate("cuentas") }
            )
        }

        composable("gastos") {
            GastoScreen(
                navController = navController,
                gastoId = null,
                navigateToCuentas = { navController.navigate("cuentas") },
                navigateToIngreso2 = { navController.navigate("ingreso2") },
                navigateToHistorial = { navController.navigate("historial") },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable("cuentas") {
            CuentasScreen(
                navController = navController,
                navigateToCalendario = { navController.navigate("CalendarioScreen") },
                navigateToHorarioDiario = { navController.navigate("HorarioDiario") },
                navigateToCuentas = { navController.navigate("cuentas") },
                navigateTotarea = { navController.navigate("tarea") },
                navigateToHistorial = { navController.navigate("historial") },
                navigateToGastos = { navController.navigate("gastos") },
                navigateToIngreso2 = { navController.navigate("ingreso2") },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable("historial") {
            HistorialScreen(
                navigateToCuentas = { navController.navigate("cuentas") },
                navigateToFormIngreso2 = { navController.navigate("ingreso2") },
                navigateToFormGasto = { navController.navigate("gastos") },
                navigateBack = { navController.popBackStack() },
                navigateToHistorial = { navController.navigate("historial") },
                navigateToEditIngreso = { id -> navController.navigate("ingreso2/$id") },
                navigateToEditGasto = { id -> navController.navigate("gastos/$id") }
            )
        }

        composable(
            route = "ingreso2/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            IngresoScreen2(
                navController = navController,
                ingresoId = id,
                navigateBack = { navController.popBackStack() },
                navigateToGastos = { navController.navigate("gastos") },
                navigateToHistorial = { navController.navigate("historial") },
                navigateToCuentas = { navController.navigate("cuentas") }
            )
        }

        composable(
            route = "gastos/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            GastoScreen(
                navController = navController,
                gastoId = id,
                navigateToCuentas = { navController.navigate("cuentas") },
                navigateToIngreso2 = { navController.navigate("ingreso2") },
                navigateToHistorial = { navController.navigate("historial") },
                navigateBack = { navController.popBackStack() }
            )
        }

        // =========================
        // 📌 Calendario
        // =========================
        composable("CalendarioScreen") {
            CalendarioScreen(
                navController = navController,
                onNavigateToInicio = { navController.navigate("principal") }
            )
        }

        composable(
            "detalles-dia/{fecha}",
            arguments = listOf(navArgument("fecha") { type = NavType.StringType })
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            VistaDetallesDiaScreen(
                navController = navController,
                fecha = fecha,
                onBackToCalendario = { navController.popBackStack() }
            )
        }

        composable(
            "nueva-nota/{fecha}",
            arguments = listOf(navArgument("fecha") { type = NavType.StringType })
        ) { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val application = LocalContext.current.applicationContext as Application
            val notaViewModel: NotaViewModel = viewModel(
                factory = NotaViewModelFactory(application)
            )

            FormularioNotaScreen(
                navController = navController,
                fecha = fecha,
                notaExistente = null,
                onCancel = { navController.popBackStack() },
                onSave = { nuevaNota ->
                    notaViewModel.insertarNota(nuevaNota)
                    navController.popBackStack()
                },
                onDelete = null
            )
        }

        composable(
            "editar-nota/{notaId}",
            arguments = listOf(navArgument("notaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val notaId = backStackEntry.arguments?.getInt("notaId") ?: 0
            val application = LocalContext.current.applicationContext as Application
            val notaViewModel: NotaViewModel = viewModel(
                factory = NotaViewModelFactory(application)
            )

            var notaExistente by remember { mutableStateOf<Nota?>(null) }

            LaunchedEffect(notaId) {
                if (notaId > 0) {
                    notaExistente = notaViewModel.obtenerNotaPorId(notaId)
                }
            }

            if (notaExistente != null) {
                FormularioNotaScreen(
                    navController = navController,
                    fecha = notaExistente!!.fecha,
                    notaExistente = notaExistente,
                    onCancel = { navController.popBackStack() },
                    onSave = { notaActualizada ->
                        notaViewModel.actualizarNota(notaActualizada)
                        navController.popBackStack()
                    },
                    onDelete = {
                        notaViewModel.eliminarNota(notaId)
                        navController.popBackStack()
                    }
                )
            } else {
                CircularProgressIndicator()
            }
        }

        composable("HorarioDiario") {
            HorarioDiarioScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable("nueva-accion") {
            val application = LocalContext.current.applicationContext as Application
            val viewModel: AccionDiariaViewModel = viewModel(
                factory = AccionDiariaViewModelFactory(application)
            )

            FormularioAccionDiariaScreen(
                navController = navController,
                accionId = 0,
                viewModel = viewModel,
                onGuardar = { accionDiaria ->
                    viewModel.insertarAccion(accionDiaria)
                    navController.popBackStack()
                },
                onCancelar = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            "editar-accion/{accionId}",
            arguments = listOf(navArgument("accionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val accionId = backStackEntry.arguments?.getInt("accionId") ?: 0
            val application = LocalContext.current.applicationContext as Application
            val viewModel: AccionDiariaViewModel = viewModel(
                factory = AccionDiariaViewModelFactory(application)
            )

            FormularioAccionDiariaScreen(
                navController = navController,
                accionId = accionId,
                viewModel = viewModel,
                onGuardar = { accionActualizada ->
                    viewModel.editarAccion(accionActualizada)
                    navController.popBackStack()
                },
                onCancelar = {
                    navController.popBackStack()
                }
            )
        }
    }
}
