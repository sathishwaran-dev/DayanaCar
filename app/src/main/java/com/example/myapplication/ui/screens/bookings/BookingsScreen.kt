package com.example.myapplication.ui.screens.bookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Booking
import com.example.myapplication.ui.components.ConfirmDeleteDialog
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.components.SearchFilterBar
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.AccentOrange
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val bookings by mainViewModel.bookings.collectAsState()
    val cars by mainViewModel.cars.collectAsState()
    val searchQuery by mainViewModel.bookingSearchQuery.collectAsState()
    val statusFilter by mainViewModel.bookingStatusFilter.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var bookingToEdit by remember { mutableStateOf<Booking?>(null) }
    var bookingToDelete by remember { mutableStateOf<Booking?>(null) }

    val statusList = listOf("All", "PENDING", "CONFIRMED", "DRIVER_ASSIGNED", "DRIVER_ACCEPTED", "STARTED", "COMPLETED", "CANCELLED")

    val filteredBookings = remember(bookings, searchQuery, statusFilter) {
        bookings.filter { booking ->
            val matchesStatus = if (statusFilter == "All") true else booking.status == statusFilter
            val matchesSearch = if (searchQuery.isBlank()) true else {
                booking.customerName.contains(searchQuery, ignoreCase = true) ||
                        booking.customerMobile.contains(searchQuery, ignoreCase = true) ||
                        booking.pickupLocation.contains(searchQuery, ignoreCase = true) ||
                        booking.destination.contains(searchQuery, ignoreCase = true) ||
                        booking.carNumber.contains(searchQuery, ignoreCase = true)
            }
            matchesStatus && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Management", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Booking")
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
            SearchFilterBar(
                query = searchQuery,
                onQueryChange = { mainViewModel.setBookingSearchQuery(it) },
                placeholder = "Search customer, location, car..."
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(statusList) { status ->
                    FilterChip(
                        selected = statusFilter == status,
                        onClick = { mainViewModel.setBookingStatusFilter(status) },
                        label = { Text(status) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredBookings.isEmpty()) {
                EmptyStateView(
                    message = "No bookings found.",
                    icon = Icons.Default.Bookmark,
                    actionText = "Create New Booking",
                    onActionClick = { showAddDialog = true }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredBookings) { booking ->
                        BookingItemCard(
                            booking = booking,
                            onCallClick = { mainViewModel.callNumber(context, booking.customerMobile) },
                            onStartTripClick = {
                                mainViewModel.startTrip(
                                    carId = booking.carId,
                                    carNumber = booking.carNumber,
                                    driverId = booking.driverId,
                                    driverName = booking.driverName,
                                    bookingId = booking.id,
                                    customerId = booking.customerId,
                                    customerName = booking.customerName,
                                    startLocation = booking.pickupLocation
                                )
                                navController.navigate(Screen.TripTracking.route)
                            },
                            onStatusChange = { newStatus -> mainViewModel.updateBookingStatus(booking.id, newStatus, context) },
                            onEditClick = { bookingToEdit = booking },
                            onDeleteClick = { bookingToDelete = booking }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditBookingDialog(
            cars = cars,
            onDismiss = { showAddDialog = false },
            onSave = { booking ->
                mainViewModel.addBooking(booking, context)
                showAddDialog = false
            }
        )
    }

    bookingToEdit?.let { booking ->
        AddEditBookingDialog(
            booking = booking,
            cars = cars,
            onDismiss = { bookingToEdit = null },
            onSave = { updated ->
                mainViewModel.updateBooking(updated)
                bookingToEdit = null
            }
        )
    }

    bookingToDelete?.let { booking ->
        ConfirmDeleteDialog(
            title = "Delete Booking",
            message = "Are you sure you want to delete booking for ${booking.customerName} (${booking.carNumber})?",
            onConfirm = {
                mainViewModel.deleteBooking(booking)
                bookingToDelete = null
            },
            onDismiss = { bookingToDelete = null }
        )
    }
}

@Composable
fun BookingItemCard(
    booking: Booking,
    onCallClick: () -> Unit,
    onStartTripClick: () -> Unit,
    onStatusChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = booking.carNumber,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
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
                text = "Customer: ${booking.customerName} (${booking.customerMobile})",
                fontWeight = FontWeight.SemiBold
            )
            if (booking.driverName.isNotBlank()) {
                Text(
                    text = "Driver: ${booking.driverName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = GreenPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("From: ${booking.pickupLocation}", style = MaterialTheme.typography.bodyMedium)
            Text("To: ${booking.destination}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Date & Time: ${booking.bookingDate} at ${booking.bookingTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    IconButton(onClick = onCallClick) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = GreenPrimary)
                    }
                    if (booking.status != "COMPLETED" && booking.status != "CANCELLED") {
                        IconButton(onClick = onStartTripClick) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start GPS Trip", tint = AccentOrange)
                        }
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }

                Box {
                    OutlinedButton(
                        onClick = { menuExpanded = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Status ▾", style = MaterialTheme.typography.labelMedium)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        listOf("PENDING", "CONFIRMED", "DRIVER_ASSIGNED", "DRIVER_ACCEPTED", "STARTED", "COMPLETED", "CANCELLED", "REJECTED").forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status) },
                                onClick = {
                                    onStatusChange(status)
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
