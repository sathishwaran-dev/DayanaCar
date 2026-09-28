package com.example.myapplication.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
fun DriverDashboardScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val driverId = authViewModel.sessionManager.getUserId()
    val driverName = authViewModel.sessionManager.getName()

    val drivers by mainViewModel.drivers.collectAsState()
    val driver = drivers.find { it.id == driverId }

    val cars by mainViewModel.cars.collectAsState()
    val assignedCar = remember(cars, driver) { cars.find { it.id == driver?.assignedCarId } }

    val bookings by mainViewModel.bookings.collectAsState()
    val myBookings = remember(bookings, driverId) { bookings.filter { it.driverId == driverId } }

    val trips by mainViewModel.trips.collectAsState()
    val myTrips = remember(trips, driverId) { trips.filter { it.driverId == driverId } }

    val bonuses by mainViewModel.bonuses.collectAsState()
    val totalBonus = remember(bonuses, driverId) { bonuses.filter { it.driverId == driverId && it.status == "APPROVED" }.sumOf { it.bonusAmount } }

    val activeTrip by mainViewModel.activeTrip.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Driver Dashboard", fontWeight = FontWeight.Bold)
                        Text("Welcome, $driverName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                },
                actions = {
                    IconButton(onClick = { authViewModel.logout() }) {
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Active GPS Banner
            if (activeTrip != null && activeTrip?.driverId == driverId) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AccentOrange.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("ACTIVE TRIP IN PROGRESS", fontWeight = FontWeight.Bold, color = AccentOrange)
                                Text("Car: ${activeTrip?.carNumber}", fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { navController.navigate(Screen.TripTracking.route) },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                            ) {
                                Text("Live GPS")
                            }
                        }
                    }
                }
            }

            // Assigned Car Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ASSIGNED VEHICLE", style = MaterialTheme.typography.labelSmall, color = GreenPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        if (assignedCar != null) {
                            Text("${assignedCar.brand} ${assignedCar.model}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Car No: ${assignedCar.carNumber} | ${assignedCar.fuelType}", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Text("No car assigned yet. Contact Owner.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            // Stats Grid
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Assigned Trips",
                        value = "${myBookings.size}",
                        icon = Icons.Default.Route,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Completed Trips",
                        value = "${myTrips.size}",
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Total Distance",
                        value = Formatters.formatKm(myTrips.sumOf { it.distanceKm }),
                        icon = Icons.Default.Speed,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Bonus Earned",
                        value = Formatters.formatCurrency(totalBonus),
                        icon = Icons.Default.Payments,
                        containerColor = GreenContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
