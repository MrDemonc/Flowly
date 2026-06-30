package com.demonlab.flowly.domain.usecase

import com.demonlab.flowly.data.local.dao.RecipeDao
import com.demonlab.flowly.data.local.dao.IngredientWithQuantity
import com.demonlab.flowly.data.local.entity.InventoryItemEntity
import com.demonlab.flowly.data.repository.InventoryRepository
import com.demonlab.flowly.data.repository.RecipeRepository

class DeductInventoryUseCase(
    private val recipeRepository: RecipeRepository,
    private val inventoryRepository: InventoryRepository
) {
    suspend fun execute(recipeId: Long, quantity: Int): Result<Unit> {
        return try {
            val ingredients = recipeRepository.getRecipeIngredientsWithDetails(recipeId)
            for (ingredient in ingredients) {
                val inventoryItem = inventoryRepository.getByName(ingredient.ingredientName)
                if (inventoryItem != null) {
                    val unitSize = if (inventoryItem.unitSize > 0) inventoryItem.unitSize else 1.0
                    val totalSubUnitsUsed = ingredient.quantity * quantity
                    val inventoryUnitsUsed = totalSubUnitsUsed / unitSize
                    val newQuantity = (inventoryItem.quantity - inventoryUnitsUsed).coerceAtLeast(0.0)
                    inventoryRepository.update(
                        inventoryItem.copy(
                            quantity = newQuantity,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
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
