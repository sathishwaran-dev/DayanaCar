package com.example.myapplication.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.components.QuickActionButton
import com.example.myapplication.ui.components.StatCard
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.AccentOrange
import com.example.myapplication.ui.theme.GreenContainer
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    onNavigateToCarAdd: () -> Unit,
    onNavigateToBookingAdd: () -> Unit,
    onNavigateToExpenseAdd: () -> Unit,
    onNavigateToTripStart: () -> Unit
) {
    val ownerName = authViewModel.sessionManager.getName()
    val cars by mainViewModel.cars.collectAsState()
    val customers by mainViewModel.customers.collectAsState()
    val drivers by mainViewModel.drivers.collectAsState()
    val bookings by mainViewModel.bookings.collectAsState()
    val trips by mainViewModel.trips.collectAsState()
    val expenses by mainViewModel.expenses.collectAsState()
    val activeTrip by mainViewModel.activeTrip.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    val todayDate = remember { Formatters.getTodayDateString() }

    // Calculations for Dashboard Stats
    val totalCarsCount = cars.size
    val availableCarsCount = cars.count { it.status == "AVAILABLE" }
    val totalDriversCount = drivers.size
    val availableDriversCount = drivers.count { it.status == "AVAILABLE" }
    val totalCustomersCount = customers.size

    val todayTrips = trips.filter { it.dateString == todayDate }
    val todayDistanceKm = todayTrips.sumOf { it.distanceKm }

    val todayExpenses = expenses.filter { it.date == todayDate }.sumOf { it.amount }
    val currentMonthPrefix = remember { todayDate.take(7) } // YYYY-MM
    val monthlyExpenses = expenses.filter { it.date.startsWith(currentMonthPrefix) }.sumOf { it.amount }

    val pendingBookingsCount = bookings.count { it.status == "PENDING" }
    val totalRevenue = bookings.filter { it.status == "COMPLETED" || it.status == "CONFIRMED" }.sumOf { it.fare }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DayanaCar Owner Portal",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Welcome, $ownerName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Reports.route) }) {
                        Icon(Icons.Default.Assessment, contentDescription = "Reports", tint = GreenPrimary)
                    }
                    IconButton(onClick = { navController.navigate(Screen.Customers.route) }) {
                        Icon(Icons.Default.People, contentDescription = "Customers", tint = GreenPrimary)
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Active Trip Banner
            if (activeTrip != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AccentOrange.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TRIP IN PROGRESS",
                                    fontWeight = FontWeight.Bold,
                                    color = AccentOrange,
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = "Car: ${activeTrip?.carNumber}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Started at: ${activeTrip?.startLocation}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Button(
                                onClick = { navController.navigate(Screen.TripTracking.route) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Live GPS")
                            }
                        }
                    }
                }
            }

            // Quick Actions Header
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        title = "Add Car",
                        icon = Icons.Default.DirectionsCar,
                        onClick = onNavigateToCarAdd,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        title = "New Booking",
                        icon = Icons.Default.AddTask,
                        onClick = onNavigateToBookingAdd,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        title = "Record Expense",
                        icon = Icons.Default.ReceiptLong,
                        onClick = onNavigateToExpenseAdd,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        title = "Start Trip",
                        icon = Icons.Default.PlayArrow,
                        onClick = onNavigateToTripStart,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Performance Overview
            item {
                Text(
                    text = "Owner Dashboard Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Total Fleet Cars",
                        value = "$totalCarsCount ($availableCarsCount Avail)",
                        icon = Icons.Default.DirectionsCar,
                        onClick = { navController.navigate(Screen.Cars.route) },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Drivers",
                        value = "$totalDriversCount ($availableDriversCount Avail)",
                        icon = Icons.Default.Badge,
                        onClick = { navController.navigate(Screen.Drivers.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Pending Requests",
                        value = "$pendingBookingsCount",
                        icon = Icons.Default.Bookmark,
                        containerColor = AccentOrange.copy(alpha = 0.15f),
                        onClick = { navController.navigate(Screen.Bookings.route) },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Today's Distance",
                        value = Formatters.formatKm(todayDistanceKm),
                        icon = Icons.Default.Speed,
                        onClick = { navController.navigate(Screen.DistanceHistory.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Total Customers",
                        value = "$totalCustomersCount",
                        icon = Icons.Default.People,
                        onClick = { navController.navigate(Screen.Customers.route) },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Revenue",
                        value = Formatters.formatCurrency(totalRevenue),
                        icon = Icons.Default.AttachMoney,
                        containerColor = GreenContainer,
                        onClick = { navController.navigate(Screen.Reports.route) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout Confirmation", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to logout from Owner Portal?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        navController.navigate(Screen.OwnerAuth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
