package com.demonlab.flowly.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batches")
data class BatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val local1Name: String = "Local 1",
    val local2Name: String = "Local 2",
    val status: String = "ACTIVO",
    val createdAt: Long = System.currentTimeMillis()
)
