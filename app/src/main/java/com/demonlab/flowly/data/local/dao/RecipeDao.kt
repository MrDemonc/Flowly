package com.demonlab.flowly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.demonlab.flowly.data.local.entity.RecipeEntity
import com.demonlab.flowly.data.local.entity.RecipeIngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllFlow(): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getById(id: Long): RecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: RecipeEntity): Long

    @Update
    suspend fun update(recipe: RecipeEntity)

    @Delete
    suspend fun delete(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFlow(query: String): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    fun getIngredientsForRecipeFlow(recipeId: Long): Flow<List<RecipeIngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addIngredientToRecipe(relation: RecipeIngredientEntity): Long

    @Query("DELETE FROM recipe_ingredients WHERE id = :relationId")
    suspend fun removeIngredientFromRecipe(relationId: Long)

    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun clearIngredientsForRecipe(recipeId: Long)

    @Transaction
    suspend fun updateRecipeIngredients(recipeId: Long, ingredients: List<RecipeIngredientEntity>) {
        clearIngredientsForRecipe(recipeId)
        ingredients.forEach { addIngredientToRecipe(it.copy(recipeId = recipeId)) }
    }

    @Query("""
        SELECT ri.*, i.name AS ingredientName, i.unit, i.costPerUnit 
        FROM recipe_ingredients ri 
        INNER JOIN ingredients i ON ri.ingredientId = i.id 
        WHERE ri.recipeId = :recipeId
    """)
    fun getRecipeIngredientsWithDetailsFlow(recipeId: Long): Flow<List<IngredientWithQuantity>>

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun getRecipeIngredientsSync(recipeId: Long): List<RecipeIngredientEntity>
}

data class IngredientWithQuantity(
    val id: Long,
    val recipeId: Long,
    val ingredientId: Long,
    val quantity: Double,
    val ingredientName: String,
    val unit: String,
    val costPerUnit: Double
) {
    val totalCost: Double get() = quantity * costPerUnit
}
