package com.demonlab.flowly.data.repository

import com.demonlab.flowly.data.local.dao.ProductDao
import com.demonlab.flowly.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {
    val productsFlow: Flow<List<ProductEntity>> = productDao.getAllFlow()

    suspend fun getById(id: Long): ProductEntity? = productDao.getById(id)

    suspend fun insert(name: String, price: Double): Long {
        return productDao.insert(ProductEntity(name = name, price = price))
    }

    suspend fun update(product: ProductEntity) {
        productDao.update(product)
    }

    suspend fun delete(product: ProductEntity) {
        productDao.delete(product)
    }
}
