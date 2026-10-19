package com.example.data

import com.example.util.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class AppRepository(private val dao: AppDao) {

    val allCustomers: Flow<List<CustomerEntity>> = dao.getAllCustomers()
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

    val customersWithSummary: Flow<List<CustomerWithSummary>> =
        combine(allCustomers, allTransactions) { customers, transactions ->
            val txByCustomer = transactions.groupBy { it.customerId }
            customers.map { customer ->
                val list = txByCustomer[customer.id] ?: emptyList()
                val totalCredit = list.filter { it.type == "credit" }.sumOf { it.amount }
                val totalPaid = list.filter { it.type == "payment" }.sumOf { it.amount }
                val balance = totalCredit - totalPaid
                val lastTxTime = list.maxOfOrNull { it.timestamp } ?: customer.createdAt
                CustomerWithSummary(
                    customer = customer,
                    totalCredit = totalCredit,
                    totalPaid = totalPaid,
                    balance = balance,
                    lastTransactionTime = lastTxTime,
                    transactionCount = list.size
                )
            }
        }

    fun getTransactionsForCustomer(customerId: Long): Flow<List<TransactionEntity>> {
        return dao.getTransactionsForCustomer(customerId)
    }

    suspend fun addDue(
        customerName: String,
        phone: String,
        amount: Double,
        note: String
    ): Long = withContext(Dispatchers.IO) {
        val trimmedName = customerName.trim()
        val existingCustomer = dao.findCustomerByName(trimmedName)
        val customerId = if (existingCustomer != null) {
            if (phone.isNotBlank() && existingCustomer.phone.isBlank()) {
                dao.updateCustomer(existingCustomer.copy(phone = phone.trim()))
            }
            existingCustomer.id
        } else {
            dao.insertCustomer(
                CustomerEntity(
                    name = trimmedName,
                    phone = phone.trim(),
                    createdAt = System.currentTimeMillis()
                )
            )
        }

        val tx = TransactionEntity(
            customerId = customerId,
            type = "credit",
            amount = amount,
            note = note.trim(),
            timestamp = System.currentTimeMillis()
        )
        dao.insertTransaction(tx)
        customerId
    }

    suspend fun recordPayment(
        customerId: Long,
        amount: Double,
        note: String
    ): Long = withContext(Dispatchers.IO) {
        val tx = TransactionEntity(
            customerId = customerId,
            type = "payment",
            amount = amount,
            note = note.trim(),
            timestamp = System.currentTimeMillis()
        )
        dao.insertTransaction(tx)
    }

    suspend fun updateCustomer(customer: CustomerEntity) = withContext(Dispatchers.IO) {
        dao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customerId: Long) = withContext(Dispatchers.IO) {
        dao.deleteCustomerById(customerId)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        dao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transactionId: Long) = withContext(Dispatchers.IO) {
        dao.deleteTransactionById(transactionId)
    }

    suspend fun resetToInitialData() = withContext(Dispatchers.IO) {
        dao.deleteAllTransactions()
        dao.deleteAllCustomers()
        AppDatabase.populateInitialData(dao)
    }

    suspend fun exportToJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("version", "2.0")
        root.put("appName", "MyDue")
        root.put("exportTime", System.currentTimeMillis())

        val customers = dao.getCustomerListSync()
        val transactions = dao.getTransactionListSync()

        val custArr = JSONArray()
        for (c in customers) {
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("phone", c.phone)
            obj.put("createdAt", c.createdAt)
            custArr.put(obj)
        }
        root.put("customers", custArr)

        val txArr = JSONArray()
        for (t in transactions) {
            val obj = JSONObject()
            obj.put("id", t.id)
            obj.put("customerId", t.customerId)
            obj.put("type", t.type)
            obj.put("amount", t.amount)
            obj.put("note", t.note)
            obj.put("timestamp", t.timestamp)
            txArr.put(obj)
        }
        root.put("transactions", txArr)
        root.toString(2)
    }

    suspend fun importFromJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            val custArr = root.optJSONArray("customers") ?: return@withContext false
            val txArr = root.optJSONArray("transactions") ?: return@withContext false

            dao.deleteAllTransactions()
            dao.deleteAllCustomers()

            for (i in 0 until custArr.length()) {
                val cObj = custArr.getJSONObject(i)
                dao.insertCustomer(
                    CustomerEntity(
                        id = cObj.optLong("id", 0),
                        name = cObj.optString("name", "Unknown"),
                        phone = cObj.optString("phone", ""),
                        createdAt = cObj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            for (i in 0 until txArr.length()) {
                val tObj = txArr.getJSONObject(i)
                dao.insertTransaction(
                    TransactionEntity(
                        id = tObj.optLong("id", 0),
                        customerId = tObj.optLong("customerId", 0),
                        type = tObj.optString("type", "credit"),
                        amount = tObj.optDouble("amount", 0.0),
                        note = tObj.optString("note", ""),
                        timestamp = tObj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // User Authentication Operations
    suspend fun registerUser(
        name: String,
        email: String,
        username: String,
        phone: String,
        password: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val trimmedEmail = email.trim().lowercase()
        val trimmedUsername = username.trim().lowercase()
        if (dao.findUserByEmail(trimmedEmail) != null) {
            return@withContext Result.failure(Exception("An account with this email already exists"))
        }
        if (dao.findUserByUsername(trimmedUsername) != null) {
            return@withContext Result.failure(Exception("Username is already taken"))
        }
        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword(password, salt)
        val user = UserEntity(
            name = name.trim(),
            email = trimmedEmail,
            username = trimmedUsername,
            phone = phone.trim(),
            passwordHash = hash,
            passwordSalt = salt
        )
        val id = dao.insertUser(user)
        Result.success(user.copy(id = id))
    }

    suspend fun authenticateUser(
        identifier: String,
        password: String
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val trimmed = identifier.trim().lowercase()
        val user = dao.findUserByIdentifier(trimmed)
            ?: return@withContext Result.failure(Exception("No account found with this email or username"))

        val isValid = SecurityUtils.verifyPassword(password, user.passwordSalt, user.passwordHash)
        if (!isValid) {
            return@withContext Result.failure(Exception("Incorrect password. Please try again."))
        }
        Result.success(user)
    }

    suspend fun initiatePasswordReset(identifier: String): Result<Pair<UserEntity, String>> = withContext(Dispatchers.IO) {
        val trimmed = identifier.trim().lowercase()
        val user = dao.findUserByIdentifier(trimmed)
            ?: return@withContext Result.failure(Exception("No account found with this email or username"))

        // Generate a 6-digit verification code
        val code = (100000..999999).random().toString()
        val expiry = System.currentTimeMillis() + (15 * 60 * 1000L) // 15 mins
        dao.setResetCode(user.id, code, expiry)
        Result.success(Pair(user, code))
    }

    suspend fun resetPasswordWithCode(
        identifier: String,
        code: String,
        newPassword: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val trimmed = identifier.trim().lowercase()
        val user = dao.findUserByIdentifier(trimmed)
            ?: return@withContext Result.failure(Exception("User not found"))

        if (user.resetCode == null || user.resetCode != code.trim()) {
            return@withContext Result.failure(Exception("Invalid reset verification code"))
        }
        if (user.resetCodeExpiry != null && System.currentTimeMillis() > user.resetCodeExpiry) {
            return@withContext Result.failure(Exception("Reset code has expired. Please request a new code."))
        }

        val newSalt = SecurityUtils.generateSalt()
        val newHash = SecurityUtils.hashPassword(newPassword, newSalt)
        dao.updatePassword(user.id, newHash, newSalt)
        Result.success(Unit)
    }

    suspend fun getUserById(id: Long): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUserById(id)
    }
}
