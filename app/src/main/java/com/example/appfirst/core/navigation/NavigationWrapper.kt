package com.example.appfirst.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appfirst.InicioScreen
import com.example.appfirst.LoginScreen
import com.example.appfirst.PrincipalScreen
import com.example.appfirst.RegistroScreen


@Composable
fun NavigationWrapper (){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Login ){
        composable<Login> {
            LoginScreen(
                navigateToHome = { navController.navigate(Inicio) },
                navigateToRegister = { navController.navigate(Registro) }
            )
        }
        composable<Inicio> {
            InicioScreen(
                navigateToPrincipal = { navController.navigate(Principal)}
            )
        }
        composable<Registro> {
            RegistroScreen()
        }
        composable<Principal>{
            PrincipalScreen()
        }
    }
}