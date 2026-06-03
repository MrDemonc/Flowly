package com.demonlab.flowly.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import com.demonlab.flowly.presentation.screens.categories.CategoriesScreen
import com.demonlab.flowly.presentation.screens.profits.ProfitsScreen
import com.demonlab.flowly.presentation.screens.statistics.StatisticsScreen
import com.demonlab.flowly.presentation.screens.suppliers.SuppliersScreen
import com.demonlab.flowly.ui.components.FloatingNavBar
import com.demonlab.flowly.ui.screens.dashboard.DashboardScreen
import com.demonlab.flowly.ui.screens.expenses.ExpenseCreateScreen
import com.demonlab.flowly.ui.screens.expenses.ExpensesScreen
import com.demonlab.flowly.ui.screens.ingredients.IngredientDetailScreen
import com.demonlab.flowly.ui.screens.ingredients.IngredientsScreen
import com.demonlab.flowly.ui.screens.inventory.InventoryDetailScreen
import com.demonlab.flowly.ui.screens.inventory.InventoryScreen
import com.demonlab.flowly.ui.screens.production.ProductionCreateScreen
import com.demonlab.flowly.ui.screens.production.ProductionScreen
import com.demonlab.flowly.ui.screens.purchases.PurchaseCreateScreen
import com.demonlab.flowly.ui.screens.purchases.PurchasesScreen
import com.demonlab.flowly.ui.screens.recipes.RecipeCreateScreen
import com.demonlab.flowly.ui.screens.recipes.RecipeDetailScreen
import com.demonlab.flowly.ui.screens.recipes.RecipesScreen
import com.demonlab.flowly.ui.screens.reports.ReportsScreen
import com.demonlab.flowly.ui.screens.sales.SaleCreateScreen
import com.demonlab.flowly.ui.screens.sales.SalesScreen
import com.demonlab.flowly.ui.screens.settings.SettingsScreen

private val topLevelRoutes = setOf(
    Screen.Dashboard.route, Screen.Inventory.route, Screen.Recipes.route,
    Screen.Production.route, Screen.Reports.route
)

@Composable
fun FlowlyNavGraph(app: FlowlyApp) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route in topLevelRoutes

    Scaffold(
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
                            icon = { Icon(item.icon, contentDescription = item.title) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(app) }
            composable(Screen.Inventory.route) { InventoryScreen(app, navController) }
            composable(Screen.InventoryDetail.route, arguments = listOf(navArgument("itemId") { type = NavType.LongType })) { InventoryDetailScreen(app, navController) }
            composable(Screen.Ingredients.route) { IngredientsScreen(app, navController) }
            composable(Screen.IngredientDetail.route, arguments = listOf(navArgument("ingredientId") { type = NavType.LongType })) { IngredientDetailScreen(app, navController) }
            composable(Screen.Categories.route) { CategoriesScreen(app) }

            composable(Screen.Recipes.route) { RecipesScreen(app, navController) }
            composable(Screen.RecipeDetail.route, arguments = listOf(navArgument("recipeId") { type = NavType.LongType })) { RecipeDetailScreen(app, navController) }
            composable(Screen.RecipeCreate.route) { RecipeCreateScreen(app, navController) }
            composable(Screen.RecipeEdit.route, arguments = listOf(navArgument("recipeId") { type = NavType.LongType })) {
                RecipeCreateScreen(app, navController, recipeId = it.arguments?.getLong("recipeId"))
            }

            composable(Screen.Production.route) { ProductionScreen(app, navController) }
            composable(Screen.ProductionCreate.route) { ProductionCreateScreen(app, navController) }

            composable(Screen.Sales.route) { SalesScreen(app, navController) }
            composable(Screen.SaleCreate.route) { SaleCreateScreen(app, navController) }

            composable(Screen.Purchases.route) { PurchasesScreen(app, navController) }
            composable(Screen.PurchaseCreate.route) { PurchaseCreateScreen(app, navController) }
            composable(Screen.Suppliers.route) { SuppliersScreen(app) }

            composable(Screen.Expenses.route) { ExpensesScreen(app, navController) }
            composable(Screen.ExpenseCreate.route) { ExpenseCreateScreen(app, navController) }

            composable(Screen.Profits.route) { ProfitsScreen(app) }
            composable(Screen.Statistics.route) { StatisticsScreen(app) }
            composable(Screen.Reports.route) { ReportsScreen(app, navController) }
            composable(Screen.Settings.route) { SettingsScreen(app, navController) }
        }
    }
}
