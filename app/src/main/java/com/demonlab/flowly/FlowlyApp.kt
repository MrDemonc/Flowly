package com.demonlab.flowly

import android.app.Application
import com.demonlab.flowly.data.local.AppDatabase
import com.demonlab.flowly.data.local.datastore.SettingsDataStore
import com.demonlab.flowly.data.repository.BatchRepository
import com.demonlab.flowly.data.repository.ProductRepository

class FlowlyApp : Application() {

    private val database by lazy { AppDatabase.getInstance(this) }

    val settingsDataStore by lazy { SettingsDataStore(this) }

    val productRepository by lazy { ProductRepository(database.productDao()) }
    val batchRepository by lazy {
        BatchRepository(
            batchDao = database.batchDao(),
            batchProductDao = database.batchProductDao(),
            batchSaleDao = database.batchSaleDao()
        )
    }
}
