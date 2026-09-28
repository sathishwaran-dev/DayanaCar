package com.example.myapplication.ui.screens.customers

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Customer
import com.example.myapplication.ui.components.ConfirmDeleteDialog
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.components.SearchFilterBar
import com.example.myapplication.ui.navigation.Screen
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val customers by mainViewModel.customers.collectAsState()
    val searchQuery by mainViewModel.customerSearchQuery.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var customerToEdit by remember { mutableStateOf<Customer?>(null) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    val filteredCustomers = remember(customers, searchQuery) {
        if (searchQuery.isBlank()) customers
        else customers.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.mobile.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer Directory", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
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
                onQueryChange = { mainViewModel.setCustomerSearchQuery(it) },
                placeholder = "Search customer name, mobile..."
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (filteredCustomers.isEmpty()) {
                EmptyStateView(
                    message = "No customers found.",
                    icon = Icons.Default.People,
                    actionText = "Add Customer",
                    onActionClick = { showAddDialog = true }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredCustomers) { customer ->
                        CustomerItemCard(
                            customer = customer,
                            onClick = { navController.navigate(Screen.CustomerDetail.createRoute(customer.id)) },
                            onCallClick = { mainViewModel.callNumber(context, customer.mobile) },
                            onEditClick = { customerToEdit = customer },
                            onDeleteClick = { customerToDelete = customer }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditCustomerDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newCust ->
                mainViewModel.addCustomer(newCust)
                showAddDialog = false
            }
        )
    }

    customerToEdit?.let { customer ->
        AddEditCustomerDialog(
            customer = customer,
            onDismiss = { customerToEdit = null },
            onSave = { updated ->
                mainViewModel.updateCustomer(updated)
                customerToEdit = null
            }
        )
    }

    customerToDelete?.let { cust ->
        ConfirmDeleteDialog(
            title = "Delete Customer",
            message = "Are you sure you want to delete ${cust.name}?",
            onConfirm = {
                mainViewModel.deleteCustomer(cust)
                customerToDelete = null
            },
            onDismiss = { customerToDelete = null }
        )
    }
}

@Composable
fun CustomerItemCard(
    customer: Customer,
    onClick: () -> Unit,
    onCallClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = customer.mobile,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                if (customer.pickupLocation.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pickup: ${customer.pickupLocation}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCallClick) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call Customer",
                        tint = GreenPrimary
                    )
                }
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Customer",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
