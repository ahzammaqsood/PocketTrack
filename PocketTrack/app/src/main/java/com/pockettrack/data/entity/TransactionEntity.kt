package com.pockettrack.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val type: String, // "income" or "expense"
    val amount: Double,
    val category: String,
    val note: String?,
    val date: Long // epoch millis
) : Parcelable