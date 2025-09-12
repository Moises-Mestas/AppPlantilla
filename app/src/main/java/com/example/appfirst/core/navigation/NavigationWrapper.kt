package com.example.appfirst.core.navigation

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            PrincipalScreen()
        }

        composable ("tarea" ){
            AgendaScreen(
                navigatetoAsignatura = {navController.navigate("Asignatura")},
                navigateToFormTarea = {navController.navigate("FormTarea") } ,
                navigateToExamen= {navController.navigate("Examen")},
                        navigateToRecordatorio={navController.navigate("recordatorio")}
            )
        }
        composable ("FormTarea"){
            FormTareaScreen()
        }
        composable ("Asignatura" ){
            AsignaturaScreen()
        }
        composable ("Examen") {
            FormExamenScreen() }
        composable ("recordatorio") {
            RecordatorioScreen() }
        }
    }
