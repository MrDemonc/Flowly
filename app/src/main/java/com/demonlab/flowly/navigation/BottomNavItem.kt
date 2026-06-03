package com.demonlab.flowly.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Dashboard : BottomNavItem(Screen.Dashboard.route, "Inicio", Icons.Default.Home)
    data object Inventory : BottomNavItem(Screen.Inventory.route, "Inventario", Icons.Default.Inventory2)
    data object Recipes : BottomNavItem(Screen.Recipes.route, "Recetas", Icons.AutoMirrored.Filled.MenuBook)
    data object Operations : BottomNavItem(Screen.Production.route, "Operaciones", Icons.Default.ShoppingCart)
    data object Reports : BottomNavItem(Screen.Reports.route, "Reportes", Icons.Default.Assessment)

    companion object {
        val items = listOf(Dashboard, Inventory, Recipes, Operations, Reports)
    }
}
