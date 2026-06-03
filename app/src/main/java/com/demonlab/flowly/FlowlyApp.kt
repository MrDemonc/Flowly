package com.demonlab.flowly

import android.app.Application
import com.demonlab.flowly.data.local.AppDatabase
import com.demonlab.flowly.data.local.datastore.SettingsDataStore
import com.demonlab.flowly.data.repository.CategoryRepository
import com.demonlab.flowly.data.repository.ExpenseRepository
import com.demonlab.flowly.data.repository.IngredientRepository
import com.demonlab.flowly.data.repository.InventoryRepository
import com.demonlab.flowly.data.repository.ProductionRepository
import com.demonlab.flowly.data.repository.PurchaseRepository
import com.demonlab.flowly.data.repository.RecipeRepository
import com.demonlab.flowly.data.repository.SaleRepository
import com.demonlab.flowly.data.repository.SupplierRepository
import com.demonlab.flowly.domain.usecase.CalculateRecipeCostUseCase
import com.demonlab.flowly.domain.usecase.DeductInventoryUseCase
import com.demonlab.flowly.domain.usecase.GetProfitReportUseCase
import com.demonlab.flowly.domain.usecase.UpdateInventoryFromPurchaseUseCase

class FlowlyApp : Application() {

    private val database by lazy { AppDatabase.getInstance(this) }

    val settingsDataStore by lazy { SettingsDataStore(this) }

    val inventoryRepository by lazy { InventoryRepository(database.inventoryItemDao()) }
    val ingredientRepository by lazy { IngredientRepository(database.ingredientDao()) }
    val recipeRepository by lazy { RecipeRepository(database.recipeDao()) }
    val productionRepository by lazy { ProductionRepository(database.productionDao()) }
    val saleRepository by lazy { SaleRepository(database.saleDao()) }
    val purchaseRepository by lazy { PurchaseRepository(database.purchaseDao()) }
    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val supplierRepository by lazy { SupplierRepository(database.supplierDao()) }

    val calculateRecipeCostUseCase by lazy { CalculateRecipeCostUseCase(recipeRepository) }
    val deductInventoryUseCase by lazy { DeductInventoryUseCase(recipeRepository, inventoryRepository) }
    val updateInventoryFromPurchaseUseCase by lazy { UpdateInventoryFromPurchaseUseCase(inventoryRepository) }
    val getProfitReportUseCase by lazy { GetProfitReportUseCase(saleRepository, expenseRepository, purchaseRepository) }
}
