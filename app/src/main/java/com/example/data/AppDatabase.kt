package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.example.util.SecurityUtils

@Database(
    entities = [CustomerEntity::class, TransactionEntity::class, UserEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mydue_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.appDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        seedDefaultUser(database.appDao())
                    }
                }
            }
        }

        suspend fun seedDefaultUser(dao: AppDao) {
            if (dao.getUserCount() == 0) {
                val salt = SecurityUtils.generateSalt()
                val hash = SecurityUtils.hashPassword("1234", salt)
                dao.insertUser(
                    UserEntity(
                        name = "Lino Joy",
                        email = "linojoy428@gmail.com",
                        username = "linojoy",
                        phone = "+91 98470 11223",
                        passwordHash = hash,
                        passwordSalt = salt
                    )
                )
            }
        }

        suspend fun populateInitialData(dao: AppDao) {
            seedDefaultUser(dao)
            val now = System.currentTimeMillis()
            val oneDay = 86_400_000L

            // Customer 1: Rose Hotel
            val c1Id = dao.insertCustomer(
                CustomerEntity(
                    name = "Rose Hotel",
                    phone = "+91 98471 23456",
                    createdAt = now - (5 * oneDay)
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    customerId = c1Id,
                    type = "credit",
                    amount = 850.0,
                    note = "Weekly groceries & dairy supplies",
                    timestamp = now - (4 * oneDay)
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    customerId = c1Id,
                    type = "credit",
                    amount = 420.0,
                    note = "Vegetables & cooking oils",
                    timestamp = now - (2 * oneDay)
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    customerId = c1Id,
                    type = "payment",
                    amount = 500.0,
                    note = "UPI Payment - Ref #928371",
                    timestamp = now - oneDay
                )
            )

            // Customer 2: AnnaStore
            val c2Id = dao.insertCustomer(
                CustomerEntity(
                    name = "AnnaStore",
                    phone = "+91 98472 87654",
                    createdAt = now - (7 * oneDay)
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    customerId = c2Id,
                    type = "credit",
                    amount = 600.0,
                    note = "Rice sacks & whole wheat flour",
                    timestamp = now - (3 * oneDay)
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    customerId = c2Id,
                    type = "payment",
                    amount = 175.0,
                    note = "Cash payment on delivery",
                    timestamp = now - (12 * 3600 * 1000L)
                )
            )
        }
    }
}
