package com.pockettrack.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String, // "income" or "expense"
    val amount: Double,
    val category: String,
    val note: String?,
    val date: Long // epoch millis
)