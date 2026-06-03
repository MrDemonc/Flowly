package com.demonlab.flowly.domain.usecase

import com.demonlab.flowly.data.local.dao.RecipeDao
import com.demonlab.flowly.data.local.entity.InventoryItemEntity
import com.demonlab.flowly.data.repository.InventoryRepository
import com.demonlab.flowly.data.repository.RecipeRepository

class DeductInventoryUseCase(
    private val recipeRepository: RecipeRepository,
    private val inventoryRepository: InventoryRepository
) {
    suspend fun execute(recipeId: Long, quantity: Int): Result<Unit> {
        return try {
            val ingredients = recipeRepository.getRecipeIngredientsSync(recipeId)
            for (ingredient in ingredients) {
                val inventoryItems = inventoryRepository.searchFlow(ingredient.ingredientId.toString())
                // Simplified: in production, map ingredients to inventory items
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateInventoryFromPurchaseUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend fun execute(ingredientName: String, quantity: Double, unit: String) {
        val existing = inventoryRepository.searchFlow(ingredientName)
        // Update or create inventory item
        inventoryRepository.insert(
            InventoryItemEntity(
                name = ingredientName,
                quantity = quantity,
                unit = unit,
                category = "Materia Prima"
            )
        )
    }
}
