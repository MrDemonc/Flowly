package com.demonlab.flowly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val quantity: Double,
    val unit: String,
    val unitPrice: Double = 0.0,
    val minStock: Double? = null,
    val category: String = "General",
    val notes: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
