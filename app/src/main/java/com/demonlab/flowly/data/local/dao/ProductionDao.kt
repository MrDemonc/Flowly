package com.demonlab.flowly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.demonlab.flowly.data.local.entity.ProductionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductionDao {
    @Query("SELECT * FROM productions ORDER BY productionDate DESC")
    fun getAllFlow(): Flow<List<ProductionEntity>>

    @Query("SELECT * FROM productions WHERE id = :id")
    suspend fun getById(id: Long): ProductionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(production: ProductionEntity): Long

    @Update
    suspend fun update(production: ProductionEntity)

    @Delete
    suspend fun delete(production: ProductionEntity)

    @Query("""
        SELECT p.*, r.name AS recipeName 
        FROM productions p 
        INNER JOIN recipes r ON p.recipeId = r.id 
        ORDER BY p.productionDate DESC
    """)
    fun getProductionsWithRecipeFlow(): Flow<List<ProductionWithRecipe>>

    @Query("""
        SELECT p.*, r.name AS recipeName 
        FROM productions p 
        INNER JOIN recipes r ON p.recipeId = r.id 
        WHERE p.productionDate BETWEEN :startDate AND :endDate 
        ORDER BY p.productionDate DESC
    """)
    fun getProductionsInRangeFlow(startDate: Long, endDate: Long): Flow<List<ProductionWithRecipe>>

    @Query("SELECT SUM(totalCost) FROM productions WHERE productionDate BETWEEN :startDate AND :endDate")
    fun getTotalCostInRangeFlow(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(quantity) FROM productions WHERE recipeId = :recipeId")
    suspend fun getTotalProducedQuantity(recipeId: Long): Int
}

data class ProductionWithRecipe(
    val id: Long,
    val recipeId: Long,
    val quantity: Int,
    val totalCost: Double,
    val productionDate: Long,
    val notes: String?,
    val recipeName: String
)
