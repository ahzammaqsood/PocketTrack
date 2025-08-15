package com.pockettrack.data

import android.content.Context
import androidx.lifecycle.LiveData
import com.pockettrack.data.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(context: Context) {
    private val dao = AppDatabase.get(context).transactionDao()

    fun observeAll(): LiveData<List<TransactionEntity>> = dao.observeAll()
    fun observeBetween(start: Long, end: Long): LiveData<List<TransactionEntity>> = dao.observeBetween(start, end)
    fun observeFiltered(category: String?, start: Long, end: Long): LiveData<List<TransactionEntity>> = dao.observeFiltered(category, start, end)

    suspend fun upsert(tx: TransactionEntity) = withContext(Dispatchers.IO) { dao.upsert(tx) }
    suspend fun delete(tx: TransactionEntity) = withContext(Dispatchers.IO) { dao.delete(tx) }
}