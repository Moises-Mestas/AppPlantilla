package com.example.appfirst.core.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.appfirst.data.datastore.UserPrefs
import com.example.appfirst.ui.screens.home.InicioScreen
import com.example.appfirst.ui.screens.home.LoginScreen
import com.example.appfirst.ui.screens.home.PrincipalScreen
import com.example.appfirst.ui.screens.home.RegistroScreen
import com.example.appfirst.ui.screens.onboarding.OnboardingScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ✅ Screens HEAD
import com.example.appfirst.ui.screens.Agenda.AgendaScreen
import com.example.appfirst.ui.screens.Agenda.FormExamenScreen
import com.example.appfirst.ui.screens.Agenda.FormTareaScreen
import com.example.appfirst.ui.screens.Agenda.RecordatorioScreen
import com.example.appfirst.ui.screens.AsignaturaScreen
import com.example.appfirst.ui.screens.calendar.NotaViewModel

// ✅ Screens Moises
import com.example.appfirst.ui.ingresos.GastoScreen
import com.example.appfirst.ui.ingresos.HistorialScreen
import com.example.appfirst.ui.ingresos.IngresoScreen2
import com.example.appfirst.ui.screens.tareas.TareasScreen
import com.example.appfirst.ui.screens.ingreso.CuentasScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val notaViewModel: NotaViewModel = viewModel()
    var startDestination by remember { mutableStateOf("onboarding") }

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
                navigateToCuentas = { navController.navigate("cuentas") }
            )
        }

        // =========================
        // 📌 HEAD: Agenda / Calendario
        // =========================
        composable("tarea") {
            AgendaScreen(
                navigatetoAsignatura = { navController.navigate("Asignatura") },
                navigateToFormTarea = { navController.navigate("FormTarea") },
                navigateToExamen = { navController.navigate("Examen") },
                navigateToRecordatorio = { navController.navigate("recordatorio") }
            )
        }
        composable("FormTarea") { FormTareaScreen() }
        composable("Asignatura") { AsignaturaScreen() }
        composable("Examen") { FormExamenScreen() }
        composable("recordatorio") { RecordatorioScreen() }

        // =========================
        // 📌 Moises: Finanzas / Ingresos / Gastos / Cuentas
        // =========================
        composable("tareas") {
            TareasScreen()
        }

        composable("ingreso2") {
            IngresoScreen2(
                navController = navController,
                ingresoId = null, // creación
                navigateBack = { navController.popBackStack() },
                navigateToGastos = { navController.navigate("gastos") },
                navigateToHistorial = { navController.navigate("historial") },
                navigateToCuentas = { navController.navigate("cuentas") }
            )
        }

        composable("gastos") {
            GastoScreen(
                navController = navController,
                gastoId = null, // creación
                navigateToCuentas = { navController.navigate("cuentas") },
                navigateToIngreso2 = { navController.navigate("ingreso2") },
                navigateToHistorial = { navController.navigate("historial") },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable("cuentas") {
            CuentasScreen(
                navController = navController,
                navigateToHistorial = { navController.navigate("historial") },
                navigateToIngreso2 = { navController.navigate("ingreso2") },
                navigateToGastos = { navController.navigate("gastos") },
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

        // Ingreso (editar)
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

        // Gasto (editar)
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
    }
}
