package com.demonlab.flowly.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "batch_sales",
    foreignKeys = [
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BatchProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchProductId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("batchId"), Index("batchProductId")]
)
data class BatchSaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val batchId: Long,
    val batchProductId: Long,
    val local: String, // "LOCAL_1" or "LOCAL_2"
    val quantity: Int,
    val amountPaid: Double, // Recaudado
    val amountPending: Double, // Cuentas por cobrar
    val timestamp: Long = System.currentTimeMillis()
)
