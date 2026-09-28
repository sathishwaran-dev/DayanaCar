package com.example.myapplication.ui.screens.trips

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
import androidx.navigation.NavController
import com.example.myapplication.data.model.Car
import com.example.myapplication.data.model.Trip
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.AccentOrange
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val trips by mainViewModel.trips.collectAsState()
    val activeTrip by mainViewModel.activeTrip.collectAsState()
    val cars by mainViewModel.cars.collectAsState()

    var showStartDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trip Management & GPS", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (activeTrip != null) {
                        navController.navigate(Screen.TripTracking.route)
                    } else {
                        showStartDialog = true
                    }
                },
                containerColor = if (activeTrip != null) AccentOrange else GreenPrimary,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = if (activeTrip != null) Icons.Default.GpsFixed else Icons.Default.PlayArrow,
                    contentDescription = if (activeTrip != null) "View Live GPS" else "Start Trip"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Active Trip Banner Card
            if (activeTrip != null) {
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
                        Column {
                            Text(
                                text = "TRIP IN PROGRESS",
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "Car: ${activeTrip?.carNumber}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "From: ${activeTrip?.startLocation}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Button(
                            onClick = { navController.navigate(Screen.TripTracking.route) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
                        ) {
                            Text("Open GPS")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "Completed Trips History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            val completedTrips = remember(trips) { trips.filter { it.isCompleted } }

            if (completedTrips.isEmpty()) {
                EmptyStateView(
                    message = "No completed trips yet.",
                    icon = Icons.Default.Route,
                    actionText = "Start First Trip",
                    onActionClick = { showStartDialog = true }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(completedTrips) { trip ->
                        TripItemCard(trip = trip)
                    }
                }
            }
        }
    }

    if (showStartDialog) {
        StartTripDialog(
            cars = cars,
            onDismiss = { showStartDialog = false },
            onStart = { carId, carNumber, startLocation, customerName ->
                mainViewModel.startTrip(
                    carId = carId,
                    carNumber = carNumber,
                    startLocation = startLocation,
                    customerName = customerName
                )
                showStartDialog = false
                navController.navigate(Screen.TripTracking.route)
            }
        )
    }
}

@Composable
fun TripItemCard(trip: Trip) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GreenPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = trip.carNumber,
                        color = GreenPrimary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = Formatters.formatTimestamp(trip.startTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${trip.startLocation} ➔ ${trip.endLocation}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (trip.customerName.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Customer: ${trip.customerName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Distance", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(Formatters.formatKm(trip.distanceKm), fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Duration", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(Formatters.formatDuration(trip.durationMinutes), fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Avg Speed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(Formatters.formatSpeed(trip.avgSpeedKmh), fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Max Speed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    Text(Formatters.formatSpeed(trip.maxSpeedKmh), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartTripDialog(
    cars: List<Car>,
    onDismiss: () -> Unit,
    onStart: (carId: Int, carNumber: String, startLocation: String, customerName: String) -> Unit
) {
    var selectedCar by remember { mutableStateOf(cars.firstOrNull()) }
    var carDropdownExpanded by remember { mutableStateOf(false) }
    var startLocation by remember { mutableStateOf("Pickup Location") }
    var customerName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start New Trip & GPS", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = carDropdownExpanded,
                    onExpandedChange = { carDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCar?.let { "${it.carNumber} (${it.brand} ${it.model})" } ?: "Select Car",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Car") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = carDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = carDropdownExpanded,
                        onDismissRequest = { carDropdownExpanded = false }
                    ) {
                        cars.forEach { car ->
                            DropdownMenuItem(
                                text = { Text("${car.carNumber} - ${car.brand} ${car.model}") },
                                onClick = {
                                    selectedCar = car
                                    carDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = startLocation,
                    onValueChange = { startLocation = it },
                    label = { Text("Start Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name (Optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedCar?.let { car ->
                        onStart(car.id, car.carNumber, startLocation.trim(), customerName.trim())
                    }
                },
                enabled = selectedCar != null,
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start Tracking")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
