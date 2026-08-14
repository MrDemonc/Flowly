package com.demonlab.flowly.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.demonlab.flowly.data.local.dao.BatchDao
import com.demonlab.flowly.data.local.dao.BatchProductDao
import com.demonlab.flowly.data.local.dao.BatchSaleDao
import com.demonlab.flowly.data.local.dao.ProductDao
import com.demonlab.flowly.data.local.entity.BatchEntity
import com.demonlab.flowly.data.local.entity.BatchProductEntity
import com.demonlab.flowly.data.local.entity.BatchSaleEntity
import com.demonlab.flowly.data.local.entity.ProductEntity

@Database(
    entities = [
        ProductEntity::class,
        BatchEntity::class,
        BatchProductEntity::class,
        BatchSaleEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun batchDao(): BatchDao
    abstract fun batchProductDao(): BatchProductDao
    abstract fun batchSaleDao(): BatchSaleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flowly_v2.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
