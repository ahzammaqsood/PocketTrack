package com.pockettrack.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pockettrack.data.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun observeAll(): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeBetween(start: Long, end: Long): LiveData<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE (:category IS NULL OR category = :category) AND date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeFiltered(category: String?, start: Long, end: Long): LiveData<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(tx: TransactionEntity)

    @Delete
    suspend fun delete(tx: TransactionEntity)
}