package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.CustomerEntity
import com.example.data.CustomerWithSummary
import com.example.data.PreferencesManager
import com.example.data.TransactionEntity
import com.example.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

sealed class Screen {
    object Auth : Screen()
    object Home : Screen()
    object Credit : Screen()
    object Profile : Screen()
    data class Detail(val customerId: Long) : Screen()
}

data class TrendPoint(
    val dayLabel: String,
    val dateLabel: String,
    val fullDate: String = "",
    val amount: Double,
    val normalizedVal: Float
)

class MyDueViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = AppRepository(db.appDao())
    private val preferencesManager = PreferencesManager(application)

    val userProfile: StateFlow<UserProfile> = preferencesManager.userProfile

    private val _currentScreen = MutableStateFlow<Screen>(
        if (preferencesManager.userProfile.value.isLoggedIn && preferencesManager.userProfile.value.userId > 0L) Screen.Home else Screen.Auth
    )
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _ledgerFilter = MutableStateFlow("all") // all, due, today, settled
    val ledgerFilter: StateFlow<String> = _ledgerFilter.asStateFlow()

    private val _sortOrder = MutableStateFlow("highest_due") // highest_due, lowest_due, alphabetical, recent
    val sortOrder: StateFlow<String> = _sortOrder.asStateFlow()

    private val _selectedCustomerId = MutableStateFlow<Long?>(null)
    val selectedCustomerId: StateFlow<Long?> = _selectedCustomerId.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val allSummaries: StateFlow<List<CustomerWithSummary>> = repository.customersWithSummary
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val selectedCustomerTransactions: StateFlow<List<TransactionEntity>> =
        _selectedCustomerId.flatMapLatest { id ->
            if (id != null) {
                repository.getTransactionsForCustomer(id)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalOutstanding: StateFlow<Double> = allSummaries.combine(MutableStateFlow(Unit)) { list, _ ->
        list.filter { it.balance > 0 }.sumOf { it.balance }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalCollected: StateFlow<Double> = allSummaries.combine(MutableStateFlow(Unit)) { list, _ ->
        list.sumOf { it.totalPaid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val pendingCount: StateFlow<Int> = allSummaries.combine(MutableStateFlow(Unit)) { list, _ ->
        list.count { it.balance > 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val settledCount: StateFlow<Int> = allSummaries.combine(MutableStateFlow(Unit)) { list, _ ->
        list.count { it.balance <= 0 && it.transactionCount > 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weeklyTrendPoints: StateFlow<List<TrendPoint>> = repository.allTransactions
        .combine(totalOutstanding) { txs, currentTotal ->
            calculateWeeklyTrend(txs, currentTotal)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlyTrendPoints: StateFlow<List<TrendPoint>> = repository.allTransactions
        .combine(totalOutstanding) { txs, currentTotal ->
            calculateMonthlyTrend(txs, currentTotal)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyPercentageChange: StateFlow<Double> = weeklyTrendPoints.combine(MutableStateFlow(Unit)) { points, _ ->
        calculatePercentageChange(points)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8.2)

    val monthlyPercentageChange: StateFlow<Double> = monthlyTrendPoints.combine(MutableStateFlow(Unit)) { points, _ ->
        calculatePercentageChange(points)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 12.5)

    private fun calculateWeeklyTrend(txs: List<TransactionEntity>, currentTotal: Double): List<TrendPoint> {
        val sdfDay = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
        val sdfDate = java.text.SimpleDateFormat("d", java.util.Locale.getDefault())
        val sdfFull = java.text.SimpleDateFormat("EEE, d MMM", java.util.Locale.getDefault())

        // 7 days for a complete weekly overview: 6 days ago up to today (0 days ago)
        val rawPoints = (6 downTo 0).map { daysAgo ->
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -daysAgo)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val endOfDay = cal.timeInMillis
            val dayLabel = if (daysAgo == 0) "Today" else sdfDay.format(cal.time)
            val dateLabel = sdfDate.format(cal.time)
            val fullDate = sdfFull.format(cal.time)

            val txUpToDay = txs.filter { it.timestamp <= endOfDay }
            val credits = txUpToDay.filter { it.type == "credit" }.sumOf { it.amount }
            val payments = txUpToDay.filter { it.type == "payment" }.sumOf { it.amount }
            val balance = (credits - payments).coerceAtLeast(0.0)

            val amount = if (daysAgo == 0 && currentTotal > 0.0) currentTotal else balance
            Triple(dayLabel, dateLabel, amount) to fullDate
        }

        val amounts = rawPoints.map { it.first.third }
        val min = amounts.minOrNull() ?: 0.0
        val max = (amounts.maxOrNull() ?: currentTotal).coerceAtLeast(1.0)
        val range = (max - min).coerceAtLeast(1.0)

        return rawPoints.mapIndexed { _, (triple, fullDate) ->
            val amt = triple.third
            val norm = if (max == min) {
                0.50f
            } else {
                (0.18f + ((amt - min) / range).toFloat() * 0.64f).coerceIn(0.18f, 0.85f)
            }
            TrendPoint(
                dayLabel = triple.first,
                dateLabel = triple.second,
                fullDate = fullDate,
                amount = amt,
                normalizedVal = norm
            )
        }
    }

    private fun calculateMonthlyTrend(txs: List<TransactionEntity>, currentTotal: Double): List<TrendPoint> {
        val sdfDate = java.text.SimpleDateFormat("d MMM", java.util.Locale.getDefault())

        // 5 milestone points across the last 28 days: 28, 21, 14, 7, 0 days ago
        val intervals = listOf(28, 21, 14, 7, 0)
        val rawPoints = intervals.mapIndexed { idx, daysAgo ->
            val cal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -daysAgo)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }
            val endOfDay = cal.timeInMillis
            val dayLabel = when (idx) {
                0 -> "4W"
                1 -> "3W"
                2 -> "2W"
                3 -> "1W"
                else -> "Today"
            }
            val dateLabel = sdfDate.format(cal.time)
            val fullDate = "${cal.get(Calendar.DAY_OF_MONTH)} ${java.text.SimpleDateFormat("MMMM", java.util.Locale.getDefault()).format(cal.time)}"

            val txUpToDay = txs.filter { it.timestamp <= endOfDay }
            val credits = txUpToDay.filter { it.type == "credit" }.sumOf { it.amount }
            val payments = txUpToDay.filter { it.type == "payment" }.sumOf { it.amount }
            val balance = (credits - payments).coerceAtLeast(0.0)

            val amount = if (daysAgo == 0 && currentTotal > 0.0) currentTotal else balance
            Triple(dayLabel, dateLabel, amount) to fullDate
        }

        val amounts = rawPoints.map { it.first.third }
        val min = amounts.minOrNull() ?: 0.0
        val max = (amounts.maxOrNull() ?: currentTotal).coerceAtLeast(1.0)
        val range = (max - min).coerceAtLeast(1.0)

        return rawPoints.mapIndexed { _, (triple, fullDate) ->
            val amt = triple.third
            val norm = if (max == min) {
                0.50f
            } else {
                (0.18f + ((amt - min) / range).toFloat() * 0.64f).coerceIn(0.18f, 0.85f)
            }
            TrendPoint(
                dayLabel = triple.first,
                dateLabel = triple.second,
                fullDate = fullDate,
                amount = amt,
                normalizedVal = norm
            )
        }
    }

    private fun calculatePercentageChange(points: List<TrendPoint>): Double {
        if (points.size < 2) return 0.0
        val first = points.first().amount
        val last = points.last().amount
        return if (first > 0) {
            ((last - first) / first) * 100.0
        } else if (last > 0) {
            100.0
        } else {
            0.0
        }
    }

    val filteredHomeCustomers: StateFlow<List<CustomerWithSummary>> =
        combine(allSummaries, _searchQuery, _sortOrder) { list, query, sort ->
            var filtered = if (query.isBlank()) {
                list
            } else {
                list.filter {
                    it.customer.name.contains(query, ignoreCase = true) ||
                            it.customer.phone.contains(query, ignoreCase = true)
                }
            }
            when (sort) {
                "highest_due" -> filtered.sortedByDescending { it.balance }
                "lowest_due" -> filtered.sortedBy { it.balance }
                "alphabetical" -> filtered.sortedBy { it.customer.name.lowercase() }
                "recent" -> filtered.sortedByDescending { it.lastTransactionTime }
                else -> filtered
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredLedgerCustomers: StateFlow<List<CustomerWithSummary>> =
        combine(allSummaries, _ledgerFilter, _searchQuery) { list, filter, query ->
            var filtered = if (query.isBlank()) {
                list
            } else {
                list.filter {
                    it.customer.name.contains(query, ignoreCase = true) ||
                            it.customer.phone.contains(query, ignoreCase = true)
                }
            }

            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            when (filter) {
                "due" -> filtered.filter { it.balance > 0 }
                "settled" -> filtered.filter { it.balance <= 0 }
                "today" -> filtered.filter { it.lastTransactionTime >= todayStart }
                else -> filtered
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: Screen) {
        if (screen is Screen.Detail) {
            _selectedCustomerId.value = screen.customerId
        }
        _currentScreen.value = screen
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            is Screen.Detail -> _currentScreen.value = Screen.Home
            is Screen.Credit -> _currentScreen.value = Screen.Home
            is Screen.Profile -> _currentScreen.value = Screen.Home
            else -> {}
        }
    }

    fun selectCustomer(id: Long) {
        _selectedCustomerId.value = id
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLedgerFilter(filter: String) {
        _ledgerFilter.value = filter
    }

    fun setSortOrder(order: String) {
        _sortOrder.value = order
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
    }

    fun dismissToast() {
        _toastMessage.value = null
    }

    fun addDue(customerName: String, phone: String, amount: Double, note: String, onDone: () -> Unit) {
        if (customerName.isBlank() || amount <= 0) {
            showToast("Please enter a valid customer name and amount")
            return
        }
        viewModelScope.launch {
            repository.addDue(customerName, phone, amount, note)
            showToast("₹${amount.toInt()} credit recorded for $customerName")
            onDone()
        }
    }

    fun recordPayment(customerId: Long, amount: Double, note: String, onDone: () -> Unit) {
        if (amount <= 0) {
            showToast("Please enter a valid payment amount")
            return
        }
        viewModelScope.launch {
            repository.recordPayment(customerId, amount, note)
            showToast("Payment of ₹${amount.toInt()} recorded!")
            onDone()
        }
    }

    fun updateCustomer(customer: CustomerEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.updateCustomer(customer)
            showToast("Customer updated successfully")
            onDone()
        }
    }

    fun deleteCustomer(customerId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteCustomer(customerId)
            showToast("Customer and records deleted")
            if (_currentScreen.value is Screen.Detail) {
                _currentScreen.value = Screen.Home
            }
            onDone()
        }
    }

    fun updateTransaction(transaction: TransactionEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
            showToast("Transaction updated")
            onDone()
        }
    }

    fun deleteTransaction(transactionId: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionId)
            showToast("Transaction removed")
            onDone()
        }
    }

    fun resetData(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.resetToInitialData()
            showToast("Records reset to sample data")
            onDone()
        }
    }

    fun exportBackup(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val json = repository.exportToJson()
            onResult(json)
        }
    }

    fun importBackup(json: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.importFromJson(json)
            if (success) {
                showToast("Backup restored successfully!")
            } else {
                showToast("Failed to restore backup format")
            }
            onResult(success)
        }
    }

    fun toggleDarkMode() {
        val current = preferencesManager.userProfile.value.isDarkMode
        preferencesManager.setDarkMode(!current)
    }

    fun setLanguage(lang: String) {
        preferencesManager.setLanguage(lang)
        showToast("Language set to $lang")
    }

    fun updateProfile(name: String, title: String, email: String, phone: String, onDone: () -> Unit) {
        preferencesManager.updateProfile(name, title, email, phone)
        showToast("Profile updated")
        onDone()
    }

    fun loginUser(
        identifier: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (identifier.isBlank() || pass.isBlank()) {
            onError("Please enter your email/username and password")
            return
        }
        viewModelScope.launch {
            val result = repository.authenticateUser(identifier, pass)
            result.onSuccess { user ->
                preferencesManager.setSession(user)
                _currentScreen.value = Screen.Home
                showToast("Welcome back, ${user.name}!")
                onSuccess()
            }.onFailure { error ->
                onError(error.message ?: "Authentication failed. Check your credentials.")
            }
        }
    }

    fun registerUser(
        name: String,
        email: String,
        phone: String,
        username: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank()) {
            onError("Please enter your full name")
            return
        }
        val trimmedEmail = email.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            onError("Please enter a valid email address (e.g. name@domain.com)")
            return
        }
        val cleanUsername = if (username.isBlank()) {
            trimmedEmail.substringBefore("@")
        } else {
            username.trim()
        }
        if (cleanUsername.length < 3) {
            onError("Username must be at least 3 characters")
            return
        }
        if (pass.length < 4) {
            onError("Password must be at least 4 characters")
            return
        }
        viewModelScope.launch {
            val result = repository.registerUser(name, trimmedEmail, cleanUsername, phone, pass)
            result.onSuccess { user ->
                preferencesManager.setSession(user)
                _currentScreen.value = Screen.Home
                showToast("Account created successfully! Welcome, ${user.name}")
                onSuccess()
            }.onFailure { error ->
                onError(error.message ?: "Registration failed. Please try again.")
            }
        }
    }

    fun forgotPassword(
        identifier: String,
        onSuccess: (code: String, email: String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (identifier.isBlank()) {
            onError("Please enter your registered email or username")
            return
        }
        viewModelScope.launch {
            val result = repository.initiatePasswordReset(identifier)
            result.onSuccess { (user, code) ->
                onSuccess(code, user.email)
            }.onFailure { error ->
                onError(error.message ?: "No matching account found")
            }
        }
    }

    fun resetPassword(
        identifier: String,
        code: String,
        newPass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (code.trim().length != 6) {
            onError("Please enter the 6-digit verification code")
            return
        }
        if (newPass.length < 4) {
            onError("New password must be at least 4 characters")
            return
        }
        viewModelScope.launch {
            val result = repository.resetPasswordWithCode(identifier, code, newPass)
            result.onSuccess {
                showToast("Password updated successfully! Please login.")
                onSuccess()
            }.onFailure { error ->
                onError(error.message ?: "Failed to reset password")
            }
        }
    }

    fun logout() {
        preferencesManager.clearSession()
        _currentScreen.value = Screen.Auth
        showToast("Logged out successfully")
    }
}
