package com.demonlab.flowly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.demonlab.flowly.data.local.entity.PurchaseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases ORDER BY purchaseDate DESC")
    fun getAllFlow(): Flow<List<PurchaseEntity>>

    @Query("SELECT * FROM purchases WHERE id = :id")
    suspend fun getById(id: Long): PurchaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: PurchaseEntity): Long

    @Update
    suspend fun update(purchase: PurchaseEntity)

    @Delete
    suspend fun delete(purchase: PurchaseEntity)

    @Query("""
        SELECT p.*, i.name AS ingredientName 
        FROM purchases p 
        INNER JOIN ingredients i ON p.ingredientId = i.id 
        ORDER BY p.purchaseDate DESC
    """)
    fun getPurchasesWithIngredientFlow(): Flow<List<PurchaseWithIngredient>>

    @Query("""
        SELECT p.*, i.name AS ingredientName 
        FROM purchases p 
        INNER JOIN ingredients i ON p.ingredientId = i.id 
        WHERE p.purchaseDate BETWEEN :startDate AND :endDate 
        ORDER BY p.purchaseDate DESC
    """)
    fun getPurchasesInRangeFlow(startDate: Long, endDate: Long): Flow<List<PurchaseWithIngredient>>

    @Query("SELECT SUM(totalCost) FROM purchases WHERE purchaseDate BETWEEN :startDate AND :endDate")
    fun getTotalSpentInRangeFlow(startDate: Long, endDate: Long): Flow<Double?>
}

data class PurchaseWithIngredient(
    val id: Long,
    val ingredientId: Long,
    val quantity: Double,
    val unitCost: Double,
    val totalCost: Double,
    val purchaseDate: Long,
    val supplier: String?,
    val notes: String?,
    val ingredientName: String
)
