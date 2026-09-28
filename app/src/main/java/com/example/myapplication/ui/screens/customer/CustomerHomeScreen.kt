package com.example.myapplication.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Car
import com.example.myapplication.ui.components.QuickActionButton
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.AccentOrange
import com.example.myapplication.ui.theme.GreenContainer
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.utils.UserRole
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    onNavigateToBookingAdd: () -> Unit = {},
    onNavigateToTripStart: () -> Unit = {}
) {
    val availableCars by mainViewModel.availableCars.collectAsState()
    val activeTrip by mainViewModel.activeTrip.collectAsState()
    val currentRole = authViewModel.sessionManager.getRole()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenPrimary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DayanaCar", fontWeight = FontWeight.Bold, color = GreenPrimary)
                    }
                },
                actions = {
                    if (currentRole == UserRole.GUEST) {
                        TextButton(onClick = { navController.navigate(Screen.OwnerAuth.route) }) {
                            Text("Owner Login", color = GreenPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        TextButton(onClick = { navController.navigate(Screen.DriverAuth.route) }) {
                            Text("Driver Login", color = GreenPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        IconButton(onClick = { authViewModel.logout() }) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.error)
                        }
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
            item { Spacer(modifier = Modifier.height(4.dp)) }

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

            // Hero Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Premium Taxi & Outstation Booking",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Safe, Reliable & Clean AC Cabs with Professional Drivers",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Quick Actions Section (New Booking & Start Trip)
            item {
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        title = "New Booking",
                        icon = Icons.Default.AddTask,
                        onClick = {
                            if (currentRole == UserRole.CUSTOMER || currentRole == UserRole.OWNER || currentRole == UserRole.DRIVER) {
                                onNavigateToBookingAdd()
                            } else {
                                navController.navigate(Screen.CustomerAuth.route)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionButton(
                        title = "Start Trip",
                        icon = Icons.Default.PlayArrow,
                        onClick = {
                            if (currentRole == UserRole.CUSTOMER || currentRole == UserRole.OWNER || currentRole == UserRole.DRIVER) {
                                onNavigateToTripStart()
                            } else {
                                navController.navigate(Screen.CustomerAuth.route)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "Available Fleet for Booking",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (availableCars.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "All cars are currently on trip or booked. Please check back shortly!",
                            modifier = Modifier.padding(24.dp),
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                items(availableCars) { car ->
                    PublicCarCard(
                        car = car,
                        onBookNowClick = {
                            mainViewModel.setPendingBookingCar(car)
                            if (currentRole == UserRole.CUSTOMER) {
                                navController.navigate(Screen.CustomerBookingConfirm.route)
                            } else {
                                navController.navigate(Screen.CustomerAuth.route)
                            }
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun PublicCarCard(
    car: Car,
    onBookNowClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GreenPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(28.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${car.brand} ${car.model}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = car.carNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GreenPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "AVAILABLE",
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EventSeat, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${car.seatingCapacity} Seats", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(car.fuelType, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Rate / km", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(Formatters.formatCurrency(car.farePerKm), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GreenPrimary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBookNowClick,
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.BookmarkAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Book Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
