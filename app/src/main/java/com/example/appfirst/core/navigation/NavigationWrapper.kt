package com.example.appfirst.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.appfirst.ui.ingresos.GastoScreen
import com.example.appfirst.ui.ingresos.HistorialScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.appfirst.ui.screens.tareas.TareasScreen
import com.example.appfirst.ui.ingresos.IngresoScreen2
import com.example.appfirst.ui.screens.ingreso.CuentasScreen

import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    val context = LocalContext.current
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
        navController = navController, startDestination = startDestination
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
            PrincipalScreen(
                navigateTotarea = { navController.navigate("tarea") },
                navigateToCuentas = { navController.navigate("cuentas") } // Navegar a cuentas
            )
        }

        composable("tarea") {
            TareasScreen()
        }




        composable("ingreso2") {
            IngresoScreen2(
                navController = navController,
                ingresoId = null,  // creación
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
                navigateToFormIngreso2 = { navController.navigate("ingreso2") },  // crear
                navigateToFormGasto = { navController.navigate("gastos") },      // crear
                navigateBack = { navController.popBackStack() },
                navigateToHistorial = { navController.navigate("historial") },
                navigateToEditIngreso = { id -> navController.navigate("ingreso2/$id") }, // EDITAR ingreso
                navigateToEditGasto = { id -> navController.navigate("gastos/$id") }      // EDITAR gasto
            )
        }


// Ingreso (crear/editar)
        // Editar ingreso
        composable(
            route = "ingreso2/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            IngresoScreen2(
                navController = navController,
                ingresoId = id,  // edición
                navigateBack = { navController.popBackStack() },
                navigateToGastos = { navController.navigate("gastos") },
                navigateToHistorial = { navController.navigate("historial") },
                navigateToCuentas = { navController.navigate("cuentas") }
            )
        }

        // Editar gasto
        composable(
            route = "gastos/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            GastoScreen(
                navController = navController,
                gastoId = id, // edición
                navigateToCuentas = { navController.navigate("cuentas") },
                navigateToIngreso2 = { navController.navigate("ingreso2") },
                navigateToHistorial = { navController.navigate("historial") },
                navigateBack = { navController.popBackStack() }
            )
        }


    }
}
