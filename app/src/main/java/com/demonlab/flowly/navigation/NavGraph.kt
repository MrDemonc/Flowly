package com.demonlab.flowly.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.demonlab.flowly.FlowlyApp
import com.demonlab.flowly.ui.screens.ajustes.AcercaDeScreen
import com.demonlab.flowly.ui.screens.ajustes.AjustesScreen
import com.demonlab.flowly.ui.screens.lotes.LoteCreateScreen
import com.demonlab.flowly.ui.screens.lotes.LoteDetailScreen
import com.demonlab.flowly.ui.screens.lotes.LotesScreen
import com.demonlab.flowly.ui.screens.productos.ProductosScreen
import com.demonlab.flowly.ui.screens.resumen.ResumenScreen

private val topLevelRoutes = setOf(
    Screen.Resumen.route,
    Screen.Productos.route,
    Screen.Lotes.route,
    Screen.Ajustes.route
)

@Composable
fun FlowlyNavGraph(app: FlowlyApp) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route in topLevelRoutes

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Resumen.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(120)) },
            exitTransition = { fadeOut(animationSpec = tween(120)) },
            popEnterTransition = { fadeIn(animationSpec = tween(120)) },
            popExitTransition = { fadeOut(animationSpec = tween(120)) }
        ) {
            composable(Screen.Resumen.route) {
                ResumenScreen(app = app, navController = navController)
            }
            composable(Screen.Productos.route) {
                ProductosScreen(app = app)
            }
            composable(Screen.Lotes.route) {
                LotesScreen(app = app, navController = navController)
            }
            composable(
                Screen.LoteCreate.route,
                enterTransition = { slideInHorizontally(tween(200)) { it } + fadeIn(tween(150)) },
                exitTransition = { slideOutHorizontally(tween(200)) { -it } + fadeOut(tween(150)) },
                popEnterTransition = { slideInHorizontally(tween(200)) { -it } + fadeIn(tween(150)) },
                popExitTransition = { slideOutHorizontally(tween(200)) { it } + fadeOut(tween(150)) }
            ) {
                LoteCreateScreen(app = app, navController = navController)
            }
            composable(
                Screen.LoteDetail.route,
                arguments = listOf(navArgument("batchId") { type = NavType.LongType }),
                enterTransition = { slideInHorizontally(tween(200)) { it } + fadeIn(tween(150)) },
                exitTransition = { slideOutHorizontally(tween(200)) { -it } + fadeOut(tween(150)) },
                popEnterTransition = { slideInHorizontally(tween(200)) { -it } + fadeIn(tween(150)) },
                popExitTransition = { slideOutHorizontally(tween(200)) { it } + fadeOut(tween(150)) }
            ) { backStackEntry ->
                val batchId = backStackEntry.arguments?.getLong("batchId") ?: 0L
                LoteDetailScreen(batchId = batchId, app = app, navController = navController)
            }
            composable(Screen.Ajustes.route) {
                AjustesScreen(app = app, navController = navController)
            }
            composable(
                Screen.AcercaDe.route,
                enterTransition = { slideInHorizontally(tween(200)) { it } + fadeIn(tween(150)) },
                exitTransition = { slideOutHorizontally(tween(200)) { -it } + fadeOut(tween(150)) },
                popEnterTransition = { slideInHorizontally(tween(200)) { -it } + fadeIn(tween(150)) },
                popExitTransition = { slideOutHorizontally(tween(200)) { it } + fadeOut(tween(150)) }
            ) {
                AcercaDeScreen(navController = navController)
            }
        }
    }
}
