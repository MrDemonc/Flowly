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
    val productName: String = "",
    val local: String, // "LOCAL_1" or "LOCAL_2"
    val quantity: Int,
    val amountPaid: Double = 0.0, // Recaudado (0 si es fiado)
    val amountPending: Double = 0.0, // Por cobrar (monto fiado si no está pagado)
    val customerName: String? = null, // Nombre del cliente cuando se fía
    val isFiado: Boolean = false, // Indica si es una venta a crédito/fiado
    val isPaid: Boolean = false, // Indica si ya fue pagado / tachado
    val timestamp: Long = System.currentTimeMillis()
)
