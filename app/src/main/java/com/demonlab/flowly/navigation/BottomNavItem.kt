package com.demonlab.flowly.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Resumen : BottomNavItem(Screen.Resumen.route, "Resumen", Icons.Default.Analytics)
    object Productos : BottomNavItem(Screen.Productos.route, "Productos", Icons.Default.ShoppingBag)
    object Lotes : BottomNavItem(Screen.Lotes.route, "Lotes", Icons.Default.Layers)
    object Ajustes : BottomNavItem(Screen.Ajustes.route, "Ajustes", Icons.Default.Settings)

    companion object {
        val items = listOf(Resumen, Productos, Lotes, Ajustes)
    }
}
