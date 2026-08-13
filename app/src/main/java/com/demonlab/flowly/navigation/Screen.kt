package com.demonlab.flowly.navigation

sealed class Screen(val route: String) {
    object Resumen : Screen("resumen")
    object Productos : Screen("productos")
    object Lotes : Screen("lotes")
    object LoteCreate : Screen("lotes/create")
    object LoteDetail : Screen("lotes/{batchId}") {
        fun createRoute(batchId: Long) = "lotes/$batchId"
    }
    object Ajustes : Screen("ajustes")
    object AcercaDe : Screen("acerca_de")
}
