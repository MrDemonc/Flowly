package com.demonlab.flowly.domain.usecase

import com.demonlab.flowly.data.local.dao.IngredientWithQuantity
import com.demonlab.flowly.data.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class RecipeCostBreakdown(
    val totalCost: Double,
    val ingredients: List<IngredientWithQuantity>,
    val profitPerServing: Double,
    val marginPercent: Double
)

class CalculateRecipeCostUseCase(
    private val recipeRepository: RecipeRepository
) {
    fun execute(recipeId: Long, salePrice: Double): Flow<RecipeCostBreakdown> {
        return recipeRepository.getRecipeIngredientsWithDetailsFlow(recipeId).map { ingredients ->
            val totalCost = ingredients.sumOf { it.quantity * it.costPerUnit }
            val profit = salePrice - totalCost
            val margin = if (salePrice > 0) (profit / salePrice) * 100 else 0.0
            RecipeCostBreakdown(
                totalCost = totalCost,
                ingredients = ingredients,
                profitPerServing = profit,
                marginPercent = margin
            )
        }
    }

    fun calculateUnitCost(ingredients: List<IngredientWithQuantity>): Double {
        return ingredients.sumOf { it.quantity * it.costPerUnit }
    }
}
