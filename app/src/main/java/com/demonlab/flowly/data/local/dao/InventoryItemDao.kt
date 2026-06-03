package com.demonlab.flowly.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.demonlab.flowly.data.local.entity.InventoryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryItemDao {
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllFlow(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE unit = :unit ORDER BY name ASC")
    fun getByUnitFlow(unit: String): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE quantity <= minStock")
    fun getLowStockFlow(): Flow<List<InventoryItemEntity>>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    suspend fun getById(id: Long): InventoryItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryItemEntity): Long

    @Update
    suspend fun update(item: InventoryItemEntity)

    @Delete
    suspend fun delete(item: InventoryItemEntity)

    @Query("SELECT * FROM inventory_items WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFlow(query: String): Flow<List<InventoryItemEntity>>

    @Query("SELECT DISTINCT unit FROM inventory_items ORDER BY unit ASC")
    fun getDistinctUnitsFlow(): Flow<List<String>>
}
