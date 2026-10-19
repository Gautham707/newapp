package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CustomerWithSummary
import com.example.ui.theme.TealSuccess
import com.example.ui.components.AddDueDialog
import com.example.ui.components.CustomerCard
import com.example.ui.components.DueActionCards
import com.example.ui.components.HeroOutstandingCard
import com.example.ui.components.InfoDialog
import com.example.ui.components.RecordPaymentDialog
import com.example.ui.components.SortFilterDialog
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.BluePrimaryLight
import com.example.ui.theme.RoseDue
import com.example.viewmodel.MyDueViewModel
import com.example.viewmodel.Screen

@Composable
fun HomeScreen(
    viewModel: MyDueViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val totalOutstanding by viewModel.totalOutstanding.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingCount.collectAsStateWithLifecycle()
    val settledCount by viewModel.settledCount.collectAsStateWithLifecycle()
    val filteredCustomers by viewModel.filteredHomeCustomers.collectAsStateWithLifecycle()
    val allSummaries by viewModel.allSummaries.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val currentSort by viewModel.sortOrder.collectAsStateWithLifecycle()
    val weeklyTrendPoints by viewModel.weeklyTrendPoints.collectAsStateWithLifecycle()
    val monthlyTrendPoints by viewModel.monthlyTrendPoints.collectAsStateWithLifecycle()
    val weeklyPercentage by viewModel.weeklyPercentageChange.collectAsStateWithLifecycle()
    val monthlyPercentage by viewModel.monthlyPercentageChange.collectAsStateWithLifecycle()

    var showAddDueDialog by remember { mutableStateOf(false) }
    var showRecordPaymentDialog by remember { mutableStateOf(false) }
    var selectedForCredit by remember { mutableStateOf<String?>(null) }
    var selectedForPayment by remember { mutableStateOf<CustomerWithSummary?>(null) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // USER PROFILE HEADER - FROSTED GLASS BADGE
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_user_header"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { viewModel.navigateTo(Screen.Profile) }
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // User Avatar Glass Squircle
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                                .border(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.name.take(2).uppercase(),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = userProfile.name,
                                    fontSize = 17.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.testTag("home_owner_name")
                                )
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(TealSuccess)
                                )
                            }
                            Text(
                                text = userProfile.title,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Notification Bell Button - Frosted Glass Box
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            .clickable { showNotificationsDialog = true }
                            .testTag("notification_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(22.dp)
                        )
                        // Radiant rose badge dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .padding(end = 9.dp, top = 9.dp)
                                .clip(CircleShape)
                                .background(RoseDue)
                        )
                    }
                }
            }

            // HERO OUTSTANDING CREDIT CARD
            item {
                HeroOutstandingCard(
                    totalAmount = totalOutstanding,
                    weeklyPoints = weeklyTrendPoints,
                    monthlyPoints = monthlyTrendPoints,
                    weeklyPercentage = weeklyPercentage,
                    monthlyPercentage = monthlyPercentage
                )
            }

            // SEARCH BAR & SORT FILTER - FROSTED GLASS CAPSULE
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = {
                            Text(
                                text = "Search accounts or phone...",
                                fontSize = 13.5.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                }
                            }
                        },
                        shape = RoundedCornerShape(22.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_search_input")
                    )

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                            .clickable { showSortDialog = true }
                            .testTag("filter_tune_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Sort Filter",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // SECTION ROW: RECENT CUSTOMERS & VIEW ALL
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Accounts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { viewModel.navigateTo(Screen.Credit) },
                            modifier = Modifier.testTag("view_all_customers_btn")
                        ) {
                            Text(
                                text = "View All (${allSummaries.size})",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
            }

            // CUSTOMER CARDS LIST
            if (filteredCustomers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = com.example.ui.components.glassBorderStroke(1.2.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Matching Accounts",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Use '+ Add Due' to log a new customer ledger entry",
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = filteredCustomers,
                    key = { it.customer.id },
                    contentType = { "customer_card" }
                ) { item ->
                    CustomerCard(
                        summary = item,
                        onClick = { viewModel.navigateTo(Screen.Detail(item.customer.id)) },
                        onAddCredit = {
                            selectedForCredit = item.customer.name
                            showAddDueDialog = true
                        },
                        onPay = {
                            selectedForPayment = item
                            showRecordPaymentDialog = true
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // FROSTED GLASS ACCENT FAB
        FloatingActionButton(
            onClick = {
                selectedForCredit = ""
                showAddDueDialog = true
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 20.dp)
                .border(com.example.ui.components.glassBorderStroke(1.2.dp), RoundedCornerShape(20.dp))
                .testTag("home_fab_add_due")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Due",
                modifier = Modifier.size(26.dp)
            )
        }
    }

    if (showAddDueDialog) {
        AddDueDialog(
            existingCustomers = allSummaries,
            initialCustomerName = selectedForCredit ?: "",
            onDismiss = { showAddDueDialog = false },
            onSave = { name, phone, amount, note ->
                viewModel.addDue(name, phone, amount, note) {
                    showAddDueDialog = false
                }
            }
        )
    }

    if (showRecordPaymentDialog) {
        RecordPaymentDialog(
            customerName = selectedForPayment?.customer?.name ?: "",
            currentBalance = selectedForPayment?.balance ?: 0.0,
            existingCustomers = allSummaries,
            onDismiss = {
                showRecordPaymentDialog = false
                selectedForPayment = null
            },
            onSaveCustomerPayment = { customerId, amount, note ->
                viewModel.recordPayment(customerId, amount, note) {
                    showRecordPaymentDialog = false
                    selectedForPayment = null
                }
            },
            onSave = { amount, note ->
                selectedForPayment?.let { cust ->
                    viewModel.recordPayment(cust.customer.id, amount, note) {
                        showRecordPaymentDialog = false
                        selectedForPayment = null
                    }
                }
            }
        )
    }

    if (showSortDialog) {
        SortFilterDialog(
            currentSort = currentSort,
            onDismiss = { showSortDialog = false },
            onSelectSort = {
                viewModel.setSortOrder(it)
                showSortDialog = false
            }
        )
    }

    if (showNotificationsDialog) {
        InfoDialog(
            title = "Notifications",
            content = "All customer dues are up to date! Rose Hotel has ₹770 pending and AnnaStore has ₹425 pending. Automatic payment reminders can be shared via SMS or WhatsApp from customer detail.",
            onDismiss = { showNotificationsDialog = false }
        )
    }
}
