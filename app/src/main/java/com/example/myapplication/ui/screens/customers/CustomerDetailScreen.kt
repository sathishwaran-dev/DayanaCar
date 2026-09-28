package com.example.myapplication.ui.screens.customers

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
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Int,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val customers by mainViewModel.customers.collectAsState()
    val customer = customers.find { it.id == customerId }

    val bookings by mainViewModel.bookings.collectAsState()
    val customerBookings = remember(bookings, customer) {
        if (customer == null) emptyList()
        else bookings.filter { it.customerId == customer.id || it.customerMobile == customer.mobile }
    }

    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Customer Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    customer?.let {
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = GreenPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        if (customer == null) {
            EmptyStateView(message = "Customer not found", icon = Icons.Default.People)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
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
                                Column {
                                    Text(
                                        text = customer.name,
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = customer.mobile,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    if (customer.email.isNotBlank()) {
                                        Text(
                                            text = customer.email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                Button(
                                    onClick = { mainViewModel.callNumber(context, customer.mobile) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call")
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Default Pickup: ${customer.pickupLocation.ifBlank { "N/A" }}", style = MaterialTheme.typography.bodyMedium)
                            Text("Default Destination: ${customer.destination.ifBlank { "N/A" }}", style = MaterialTheme.typography.bodyMedium)
                            if (customer.notes.isNotBlank()) {
                                Text("Notes: ${customer.notes}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }

                item {
                    Text("Booking History (${customerBookings.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                if (customerBookings.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text("No booking history for this customer.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.outline)
                        }
                    }
                } else {
                    items(customerBookings) { booking ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${booking.pickupLocation} ➔ ${booking.destination}", fontWeight = FontWeight.Bold)
                                    Text(Formatters.formatCurrency(booking.fare), fontWeight = FontWeight.Bold, color = GreenPrimary)
                                }
                                Text("Car: ${booking.carNumber} | Date: ${booking.bookingDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEditDialog && customer != null) {
        AddEditCustomerDialog(
            customer = customer,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                mainViewModel.updateCustomer(updated)
                showEditDialog = false
            }
        )
    }
}
