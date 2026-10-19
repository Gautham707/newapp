package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.CustomerEntity
import com.example.data.TransactionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: AppRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AppRepository(db.appDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MyDue", appName)
    }

    @Test
    fun `test add due and record payment balances`() = runBlocking {
        // Add customer with initial due
        val customerId = repository.addDue("SuperMart", "+91 99999 88888", 1000.0, "Goods purchase")
        assertTrue(customerId > 0)

        // Check summaries
        var summaries = repository.customersWithSummary.first()
        assertEquals(1, summaries.size)
        assertEquals("SuperMart", summaries[0].customer.name)
        assertEquals(1000.0, summaries[0].balance, 0.01)

        // Record a payment of 400
        repository.recordPayment(customerId, 400.0, "Cash")

        summaries = repository.customersWithSummary.first()
        assertEquals(600.0, summaries[0].balance, 0.01)
        assertEquals(1000.0, summaries[0].totalCredit, 0.01)
        assertEquals(400.0, summaries[0].totalPaid, 0.01)
    }

    @Test
    fun `test launch MainActivity and verify no crash`() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java)
        controller.create().start().resume().visible()
        val activity = controller.get()
        org.junit.Assert.assertNotNull(activity)
    }
}
