package com.example.myapplication.ui.screens.driver

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Booking
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.AccentOrange
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverBookingsScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val driverId = authViewModel.sessionManager.getUserId()
    val driverName = authViewModel.sessionManager.getName()

    val bookings by mainViewModel.bookings.collectAsState()
    val myBookings = remember(bookings, driverId) { bookings.filter { it.driverId == driverId } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Assigned Bookings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (myBookings.isEmpty()) {
                EmptyStateView(
                    message = "No assigned bookings yet.",
                    icon = Icons.Default.Route
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(myBookings) { booking ->
                        DriverBookingCard(
                            booking = booking,
                            onCallCustomerClick = { mainViewModel.callNumber(context, booking.customerMobile) },
                            onStartTripClick = {
                                mainViewModel.startTrip(
                                    carId = booking.carId,
                                    carNumber = booking.carNumber,
                                    driverId = driverId,
                                    driverName = driverName,
                                    bookingId = booking.id,
                                    customerId = booking.customerId,
                                    customerName = booking.customerName,
                                    startLocation = booking.pickupLocation
                                )
                                navController.navigate(Screen.TripTracking.route)
                            },
                            onAcceptClick = {
                                mainViewModel.updateBookingStatus(booking.id, "DRIVER_ACCEPTED", context)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DriverBookingCard(
    booking: Booking,
    onCallCustomerClick: () -> Unit,
    onStartTripClick: () -> Unit,
    onAcceptClick: () -> Unit
) {
    val statusColor = when (booking.status) {
        "DRIVER_ACCEPTED", "CONFIRMED" -> GreenPrimary
        "STARTED" -> AccentOrange
        "COMPLETED" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }

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
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = booking.status,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(Formatters.formatCurrency(booking.fare), fontWeight = FontWeight.Bold, color = GreenPrimary)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Customer: ${booking.customerName}", fontWeight = FontWeight.Bold)
            Text("Pickup: ${booking.pickupLocation}", style = MaterialTheme.typography.bodyMedium)
            Text("Destination: ${booking.destination}", style = MaterialTheme.typography.bodyMedium)
            Text("Date & Time: ${booking.bookingDate} at ${booking.bookingTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onCallCustomerClick) {
                    Icon(Icons.Default.Call, contentDescription = "Call Customer", tint = GreenPrimary)
                }

                if (booking.status == "DRIVER_ASSIGNED") {
                    Button(
                        onClick = onAcceptClick,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Accept Booking")
                    }
                } else if (booking.status == "DRIVER_ACCEPTED" || booking.status == "CONFIRMED") {
                    Button(
                        onClick = onStartTripClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Start GPS Trip")
                    }
                }
            }
        }
    }
}
