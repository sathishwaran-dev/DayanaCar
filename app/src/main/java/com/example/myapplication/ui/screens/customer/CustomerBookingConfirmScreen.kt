package com.example.myapplication.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Booking
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.GreenContainer
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerBookingConfirmScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val selectedCar by mainViewModel.pendingBookingCar.collectAsState()
    val userId = authViewModel.sessionManager.getUserId()
    val userName = authViewModel.sessionManager.getName()
    val userMobile = authViewModel.sessionManager.getMobile()

    var pickupLocation by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var dateStr by remember { mutableStateOf(Formatters.getTodayDateString()) }
    var timeStr by remember { mutableStateOf("10:00") }
    var passengersCount by remember { mutableStateOf("2") }
    var approxDistanceKm by remember { mutableStateOf("25") }
    var notes by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val car = selectedCar

    val estimatedFare = remember(approxDistanceKm, car) {
        val dist = approxDistanceKm.toDoubleOrNull() ?: 20.0
        val rate = car?.farePerKm ?: 15.0
        dist * rate
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm Booking Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        if (car == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No car selected for booking.", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate(Screen.CustomerHome.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Select a Car from Fleet")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Car Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SELECTED VEHICLE", style = MaterialTheme.typography.labelSmall, color = GreenPrimary, fontWeight = FontWeight.Bold)
                        Text("${car.brand} ${car.model}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("${car.carNumber} | ${car.fuelType} | ${car.seatingCapacity} Seats", style = MaterialTheme.typography.bodyMedium)
                        Text("Rate: ${Formatters.formatCurrency(car.farePerKm)} / km", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = GreenPrimary)
                    }
                }

                // Booking Inputs
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Trip Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = pickupLocation,
                            onValueChange = {
                                pickupLocation = it
                                errorMessage = null
                            },
                            label = { Text("Pickup Location *") },
                            placeholder = { Text("e.g., Central Station / Airport / Home Address") },
                            leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = destination,
                            onValueChange = {
                                destination = it
                                errorMessage = null
                            },
                            label = { Text("Destination *") },
                            placeholder = { Text("e.g., Hotel Boulevard / Tech Park") },
                            leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = dateStr,
                                onValueChange = { dateStr = it },
                                label = { Text("Date (YYYY-MM-DD)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = timeStr,
                                onValueChange = { timeStr = it },
                                label = { Text("Time (HH:mm)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = passengersCount,
                                onValueChange = { passengersCount = it },
                                label = { Text("Passengers") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = approxDistanceKm,
                                onValueChange = { approxDistanceKm = it },
                                label = { Text("Est. Distance (km)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Special Instructions / Notes") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        errorMessage?.let { error ->
                            Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Estimated Fare Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ESTIMATED FARE", style = MaterialTheme.typography.labelSmall, color = GreenPrimary)
                            Text(Formatters.formatCurrency(estimatedFare), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = GreenPrimary)
                        }
                        Text("Status: PENDING", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.outline)
                    }
                }

                Button(
                    onClick = {
                        if (pickupLocation.isBlank() || destination.isBlank()) {
                            errorMessage = "Pickup Location and Destination are required."
                            return@Button
                        }
                        val newBooking = Booking(
                            customerId = userId,
                            customerName = userName,
                            customerMobile = userMobile,
                            carId = car.id,
                            carNumber = car.carNumber,
                            carModel = "${car.brand} ${car.model}",
                            pickupLocation = pickupLocation.trim(),
                            destination = destination.trim(),
                            bookingDate = dateStr.trim(),
                            bookingTime = timeStr.trim(),
                            passengersCount = passengersCount.toIntOrNull() ?: 1,
                            fare = estimatedFare,
                            status = "PENDING",
                            notes = notes.trim()
                        )
                        mainViewModel.addBooking(newBooking, context)
                        mainViewModel.setPendingBookingCar(null)
                        navController.navigate(Screen.CustomerBookings.route) {
                            popUpTo(Screen.CustomerHome.route)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirm Booking Request", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
