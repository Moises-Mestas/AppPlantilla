package com.example.ventas.navegation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.ventas.ui.screens.home.HomeScreen
import com.example.ventas.ui.screens.onboarding.OnboardingScreen
import com.example.ventas.ui.screens.product.ProductFormScreen
import com.example.ventas.ui.screens.product.ProductListScreen

// El graph recibe onOpenDrawer para que pantallas abran el Drawer
fun NavGraphBuilder.appGraph(nav: NavHostController, onOpenDrawer: () -> Unit) {

    composable(AppRoute.Onboarding.route) {
        OnboardingScreen(onFinish = {
            nav.navigate(AppRoute.Home.route) {
                popUpTo(AppRoute.Onboarding.route) { inclusive = true }
            }
        })
    }

    composable(AppRoute.Home.route) {
        HomeScreen(
            onOpenDrawer = onOpenDrawer,
            onNewProduct = { nav.navigate(AppRoute.ProductNew.route) },
            onEditProduct = { id -> nav.navigate(AppRoute.ProductEdit.path(id)) }
        )
    }

    composable(AppRoute.ProductList.route) {
        ProductListScreen(
            onNew  = { nav.navigate(AppRoute.ProductNew.route) },
            onEdit = { id -> nav.navigate(AppRoute.ProductEdit.path(id)) },
            onOpenDrawer = onOpenDrawer
        )
    }

    composable(AppRoute.ProductNew.route) {
        ProductFormScreen(
            productId = null,
            onSaved = { nav.popBackStack() },
            onCancel = { nav.popBackStack() }   // 👈 aquí
        )
    }

    composable(AppRoute.ProductEdit(0).route) { backStack ->
        val id = backStack.arguments?.getString("id")?.toLongOrNull()
        ProductFormScreen(
            productId = id,
            onSaved = { nav.popBackStack() },
            onCancel = { nav.popBackStack() }   // 👈 aquí
        )
    }
}
