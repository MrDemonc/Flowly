package com.demonlab.flowly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.demonlab.flowly.data.local.entity.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY saleDate DESC")
    fun getAllFlow(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getById(id: Long): SaleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sale: SaleEntity): Long

    @Update
    suspend fun update(sale: SaleEntity)

    @Delete
    suspend fun delete(sale: SaleEntity)

    @Query("""
        SELECT s.*, r.name AS recipeName 
        FROM sales s 
        INNER JOIN recipes r ON s.recipeId = r.id 
        ORDER BY s.saleDate DESC
    """)
    fun getSalesWithRecipeFlow(): Flow<List<SaleWithRecipe>>

    @Query("""
        SELECT s.*, r.name AS recipeName 
        FROM sales s 
        INNER JOIN recipes r ON s.recipeId = r.id 
        WHERE s.saleDate BETWEEN :startDate AND :endDate 
        ORDER BY s.saleDate DESC
    """)
    fun getSalesInRangeFlow(startDate: Long, endDate: Long): Flow<List<SaleWithRecipe>>

    @Query("SELECT SUM(totalAmount) FROM sales WHERE saleDate BETWEEN :startDate AND :endDate")
    fun getTotalRevenueInRangeFlow(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(costAtSale) FROM sales WHERE saleDate BETWEEN :startDate AND :endDate")
    fun getTotalCostInRangeFlow(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(totalAmount - costAtSale) FROM sales WHERE saleDate BETWEEN :startDate AND :endDate")
    fun getTotalProfitInRangeFlow(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT COUNT(*) AS `count`, SUM(totalAmount) AS totalAmount FROM sales WHERE saleDate BETWEEN :startDate AND :endDate")
    fun getSalesSummaryFlow(startDate: Long, endDate: Long): Flow<SaleSummary>

    @Query("SELECT paymentMethod, SUM(totalAmount) AS totalAmount FROM sales WHERE saleDate BETWEEN :startDate AND :endDate GROUP BY paymentMethod")
    fun getSalesByPaymentMethodFlow(startDate: Long, endDate: Long): Flow<List<PaymentMethodSummary>>
}

data class SaleWithRecipe(
    val id: Long,
    val recipeId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val totalAmount: Double,
    val costAtSale: Double,
    val saleDate: Long,
    val paymentMethod: String,
    val notes: String?,
    val recipeName: String
) {
    val profit: Double get() = totalAmount - costAtSale
}

data class SaleSummary(
    val count: Int,
    val totalAmount: Double
)

data class PaymentMethodSummary(
    val paymentMethod: String,
    val totalAmount: Double
)
