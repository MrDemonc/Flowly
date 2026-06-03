package com.demonlab.flowly.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")

    data object Inventory : Screen("inventory")
    data object InventoryDetail : Screen("inventory/{itemId}") {
        fun createRoute(itemId: Long) = "inventory/$itemId"
    }

    data object Ingredients : Screen("ingredients")
    data object IngredientDetail : Screen("ingredients/{ingredientId}") {
        fun createRoute(ingredientId: Long) = "ingredients/$ingredientId"
    }

    data object Categories : Screen("categories")

    data object Recipes : Screen("recipes")
    data object RecipeDetail : Screen("recipes/{recipeId}") {
        fun createRoute(recipeId: Long) = "recipes/$recipeId"
    }
    data object RecipeCreate : Screen("recipes/new")

    data object Production : Screen("production")
    data object ProductionCreate : Screen("production/new")

    data object Sales : Screen("sales")
    data object SaleCreate : Screen("sales/new")

    data object Purchases : Screen("purchases")
    data object PurchaseCreate : Screen("purchases/new")

    data object Suppliers : Screen("suppliers")

    data object Expenses : Screen("expenses")
    data object ExpenseCreate : Screen("expenses/new")

    data object Profits : Screen("profits")
    data object Statistics : Screen("statistics")
    data object Reports : Screen("reports")
    data object Export : Screen("export")
    data object Backup : Screen("backup")
    data object Settings : Screen("settings")
}
