package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.IngredientWithQuantity
import com.demonlab.flowly.data.local.dao.RecipeDao
import com.demonlab.flowly.data.local.entity.RecipeEntity
import com.demonlab.flowly.data.local.entity.RecipeIngredientEntity
import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val dao: RecipeDao) {

    fun getAllFlow(): Flow<List<RecipeEntity>> = dao.getAllFlow()

    suspend fun getById(id: Long): RecipeEntity? = dao.getById(id)

    suspend fun insert(recipe: RecipeEntity): Long = dao.insert(recipe)

    suspend fun update(recipe: RecipeEntity) = dao.update(recipe)

    suspend fun delete(recipe: RecipeEntity) = dao.delete(recipe)

    fun searchFlow(query: String): Flow<List<RecipeEntity>> = dao.searchFlow(query)

    fun getIngredientsForRecipeFlow(recipeId: Long): Flow<List<RecipeIngredientEntity>> =
        dao.getIngredientsForRecipeFlow(recipeId)

    fun getRecipeIngredientsWithDetailsFlow(recipeId: Long): Flow<List<IngredientWithQuantity>> =
        dao.getRecipeIngredientsWithDetailsFlow(recipeId)

    suspend fun getRecipeIngredientsSync(recipeId: Long): List<RecipeIngredientEntity> =
        dao.getRecipeIngredientsSync(recipeId)

    suspend fun getRecipeIngredientsWithDetails(recipeId: Long): List<IngredientWithQuantity> =
        dao.getRecipeIngredientsWithDetails(recipeId)

    suspend fun addIngredientToRecipe(relation: RecipeIngredientEntity): Long =
        dao.addIngredientToRecipe(relation)

    suspend fun removeIngredientFromRecipe(relationId: Long) =
        dao.removeIngredientFromRecipe(relationId)

    suspend fun updateRecipeIngredients(recipeId: Long, ingredients: List<RecipeIngredientEntity>) =
        dao.updateRecipeIngredients(recipeId, ingredients)

    suspend fun calculateRecipeCost(recipeId: Long): Double {
        return dao.getRecipeIngredientsWithDetails(recipeId).sumOf { it.quantity * it.costPerUnit }
    }
}
