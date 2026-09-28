package com.example.myapplication.ui.screens.customer

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
fun CustomerBookingsScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val currentUserId = authViewModel.sessionManager.getUserId()
    val currentUserMobile = authViewModel.sessionManager.getMobile()
    val allBookings by mainViewModel.bookings.collectAsState()

    // STRICT PRIVACY: Filter ONLY bookings belonging to this customer!
    val customerBookings = remember(allBookings, currentUserId, currentUserMobile) {
        allBookings.filter { it.customerId == currentUserId || it.customerMobile == currentUserMobile }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings", fontWeight = FontWeight.Bold) },
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

            if (customerBookings.isEmpty()) {
                EmptyStateView(
                    message = "You haven't made any bookings yet.",
                    icon = Icons.Default.Bookmark,
                    actionText = "Browse Cars",
                    onActionClick = { navController.navigate(Screen.CustomerHome.route) }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(customerBookings) { booking ->
                        CustomerBookingCard(
                            booking = booking,
                            onTrackGpsClick = {
                                navController.navigate(Screen.TripTracking.route)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerBookingCard(
    booking: Booking,
    onTrackGpsClick: () -> Unit
) {
    val statusColor = when (booking.status) {
        "CONFIRMED" -> GreenPrimary
        "STARTED" -> AccentOrange
        "COMPLETED" -> MaterialTheme.colorScheme.primary
        "CANCELLED", "REJECTED" -> MaterialTheme.colorScheme.error
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
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = Formatters.formatCurrency(booking.fare),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${booking.carModel} (${booking.carNumber})",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            if (booking.driverName.isNotBlank()) {
                Text(
                    text = "Assigned Driver: ${booking.driverName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${booking.pickupLocation} ➔ ${booking.destination}", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Date & Time: ${booking.bookingDate} at ${booking.bookingTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)

            if (booking.status == "STARTED") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onTrackGpsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.GpsFixed, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Track Live Trip GPS")
                }
            }
        }
    }
}
