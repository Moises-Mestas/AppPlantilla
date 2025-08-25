package com.example.ventas.navegation

sealed class AppRoute(val route: String) {
    data object Onboarding : AppRoute("onboarding")
    data object Home : AppRoute("home")

    // CRUD Productos
    data object ProductList : AppRoute("product_list")
    data object ProductNew : AppRoute("product_new")
    data class ProductEdit(val id: Long) : AppRoute("product_edit/{id}") {
        companion object { fun path(id: Long) = "product_edit/$id" }
    }
}
