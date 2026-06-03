package com.demonlab.flowly.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.demonlab.flowly.data.local.dao.CategoryDao
import com.demonlab.flowly.data.local.dao.ExpenseDao
import com.demonlab.flowly.data.local.dao.IngredientDao
import com.demonlab.flowly.data.local.dao.InventoryItemDao
import com.demonlab.flowly.data.local.dao.ProductionDao
import com.demonlab.flowly.data.local.dao.PurchaseDao
import com.demonlab.flowly.data.local.dao.RecipeDao
import com.demonlab.flowly.data.local.dao.SaleDao
import com.demonlab.flowly.data.local.dao.SupplierDao
import com.demonlab.flowly.data.local.entity.CategoryEntity
import com.demonlab.flowly.data.local.entity.ExpenseEntity
import com.demonlab.flowly.data.local.entity.IngredientEntity
import com.demonlab.flowly.data.local.entity.InventoryItemEntity
import com.demonlab.flowly.data.local.entity.ProductionEntity
import com.demonlab.flowly.data.local.entity.PurchaseEntity
import com.demonlab.flowly.data.local.entity.RecipeEntity
import com.demonlab.flowly.data.local.entity.RecipeIngredientEntity
import com.demonlab.flowly.data.local.entity.SaleEntity
import com.demonlab.flowly.data.local.entity.SupplierEntity

@Database(
    entities = [
        InventoryItemEntity::class,
        IngredientEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class,
        ProductionEntity::class,
        SaleEntity::class,
        PurchaseEntity::class,
        ExpenseEntity::class,
        CategoryEntity::class,
        SupplierEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inventoryItemDao(): InventoryItemDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao
    abstract fun productionDao(): ProductionDao
    abstract fun saleDao(): SaleDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun supplierDao(): SupplierDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flowly.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
