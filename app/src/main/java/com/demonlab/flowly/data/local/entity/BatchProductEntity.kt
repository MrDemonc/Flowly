package com.demonlab.flowly.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "batch_products",
    foreignKeys = [
        ForeignKey(
            entity = BatchEntity::class,
            parentColumns = ["id"],
            childColumns = ["batchId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("batchId")]
)
data class BatchProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val batchId: Long,
    val productId: Long,
    val productName: String,
    val productPrice: Double,
    val local1InitialQty: Int,
    val local1CurrentQty: Int,
    val local2InitialQty: Int,
    val local2CurrentQty: Int
)
