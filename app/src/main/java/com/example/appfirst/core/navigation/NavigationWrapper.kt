package com.example.appfirst.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appfirst.CuentasScreen
import com.example.appfirst.GastoScreen
import com.example.appfirst.IngresoScreen
import com.example.appfirst.InicioScreen
import com.example.appfirst.LoginScreen
import com.example.appfirst.MonederoScreen
import com.example.appfirst.RegistroScreen


@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navigateToHome = { navController.navigate("inicio") },
                navigateToRegister = { navController.navigate("registro") }
            )
        }
        composable("inicio") {
            InicioScreen(navController)
        }
        composable("registro") {
            RegistroScreen()
        }
        composable("monedero") {
            MonederoScreen(navController)
        }
        composable("cuentas") {
            CuentasScreen(navController)
        }
        composable("gasto") {
            GastoScreen(navController)
        }
        composable("ingreso") {
            IngresoScreen(navController)
        }
    }
}
