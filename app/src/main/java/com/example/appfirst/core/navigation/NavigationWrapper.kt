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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.appfirst.ui.screens.tareas.TareasScreen
import com.example.appfirst.ui.ingresos.IngresoScreen
import com.example.appfirst.ui.ingresos.IngresoScreen2
import com.example.appfirst.ui.screens.ingreso.CuentasScreen


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
            PrincipalScreen(
                navigateTotarea = { navController.navigate("tarea") },
                navigateToIngreso = { navController.navigate("ingreso") },
                navigateToCuentas = { navController.navigate("cuentas") } // Navegar a cuentas
            )
        }

        composable("tarea") {
            TareasScreen()
        }

        composable("ingreso") {
            IngresoScreen(
                navigateToCuentas = { navController.navigate("cuentas") }, // Navegar a cuentas
                navigateToFormIngreso2 = { navController.navigate("ingreso2") }, // Navegar a form de ingreso
                navigateToFormGasto = { navController.navigate("gastos") }, // Navegar a form de gasto
                navigateBack = { navController.popBackStack() }
            )
        }

        composable("ingreso2") {
            IngresoScreen2(
                navigateBack = { navController.popBackStack() },
                navigateToGastos = { navController.navigate("gastos") }, // Nave

                navigateToCuentas = { navController.navigate("cuentas") }
            )
        }

        composable("gastos") {
            GastoScreen(
                navigateToCuentas = { navController.navigate("cuentas") },
                navigateToIngreso2 = { navController.navigate("ingreso2") }, // Navegar a form de ingreso
                navigateBack = { navController.popBackStack() }
            )
        }



        composable("cuentas") {
            CuentasScreen(
                navigateToIngreso2 = { navController.navigate("ingreso2") }, // Navegar a form de ingreso
                navigateToGastos = { navController.navigate("gastos") }, // Nave
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
