package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["customerId"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val type: String, // "credit" (due added) or "payment" (amount received)
    val amount: Double,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class CustomerWithSummary(
    val customer: CustomerEntity,
    val totalCredit: Double,
    val totalPaid: Double,
    val balance: Double, // totalCredit - totalPaid
    val lastTransactionTime: Long,
    val transactionCount: Int
)

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["username"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val username: String,
    val phone: String = "",
    val passwordHash: String,
    val passwordSalt: String,
    val resetCode: String? = null,
    val resetCodeExpiry: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
