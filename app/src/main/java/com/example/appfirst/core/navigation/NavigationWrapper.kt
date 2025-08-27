package com.example.appfirst.core.navigation

import android.net.Uri
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
import com.example.appfirst.ui.screens.calendar.CalendarioScreen
import com.example.appfirst.ui.screens.calendar.FormularioNotaScreen
import com.example.appfirst.ui.screens.calendar.VistaDetallesDiaScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
                    navController.navigate("detalles/${Uri.encode(fecha)}")},
                onNavigateToNota = { navController.navigate("VistaNotas") }
            )
        }

        composable("detalles/{fecha}") { backStackEntry ->
            val fecha = backStackEntry.arguments?.getString("fecha") ?: "Sin fecha"
            VistaDetallesDiaScreen(
                fecha = fecha,
                onBackToCalendario = { navController.popBackStack() }
            )
        }

        composable("VistaNotas") {
            FormularioNotaScreen(
                onCancel = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
    }
}