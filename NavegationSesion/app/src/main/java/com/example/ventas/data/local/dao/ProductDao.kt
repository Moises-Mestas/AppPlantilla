package com.example.ventas.data.local.dao

import androidx.room.*
import com.example.ventas.data.local.entity.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<Product>>

    @Query("""
        SELECT * FROM products 
        WHERE isActive = 1 AND (name LIKE '%' || :q || '%' OR sku LIKE '%' || :q || '%')
        ORDER BY updatedAt DESC
    """)
    fun search(q: String): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Product?

    @Insert
    suspend fun insert(p: Product): Long

    @Update
    suspend fun update(p: Product)

    @Query("UPDATE products SET isActive = 0, updatedAt = :ts WHERE id = :id")
    suspend fun softDelete(id: Long, ts: Long = System.currentTimeMillis())
}
