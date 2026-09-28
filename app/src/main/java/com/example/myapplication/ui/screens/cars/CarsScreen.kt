package com.example.myapplication.ui.screens.cars

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
import com.example.myapplication.data.model.Car
import com.example.myapplication.ui.components.ConfirmDeleteDialog
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.components.SearchFilterBar
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarsScreen(
    mainViewModel: MainViewModel,
    onAddCarClick: () -> Unit,
    onCarClick: (Int) -> Unit
) {
    val cars by mainViewModel.cars.collectAsState()
    val searchQuery by mainViewModel.carSearchQuery.collectAsState()

    var carToEdit by remember { mutableStateOf<Car?>(null) }
    var carToDelete by remember { mutableStateOf<Car?>(null) }

    val filteredCars = remember(cars, searchQuery) {
        if (searchQuery.isBlank()) {
            cars
        } else {
            cars.filter {
                it.carNumber.contains(searchQuery, ignoreCase = true) ||
                        it.brand.contains(searchQuery, ignoreCase = true) ||
                        it.model.contains(searchQuery, ignoreCase = true) ||
                        it.fuelType.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Fleet Management", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCarClick,
                containerColor = GreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Car")
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
                onQueryChange = { mainViewModel.setCarSearchQuery(it) },
                placeholder = "Search car number, brand, fuel..."
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (filteredCars.isEmpty()) {
                EmptyStateView(
                    message = "No cars found in fleet.",
                    icon = Icons.Default.DirectionsCar,
                    actionText = "Add First Car",
                    onActionClick = onAddCarClick
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredCars) { car ->
                        CarItemCard(
                            car = car,
                            onClick = { onCarClick(car.id) },
                            onEditClick = { carToEdit = car },
                            onDeleteClick = { carToDelete = car }
                        )
                    }
                }
            }
        }
    }

    carToEdit?.let { car ->
        AddEditCarDialog(
            car = car,
            onDismiss = { carToEdit = null },
            onSave = { updated ->
                mainViewModel.updateCar(updated)
                carToEdit = null
            }
        )
    }

    carToDelete?.let { car ->
        ConfirmDeleteDialog(
            title = "Delete Car",
            message = "Are you sure you want to delete car ${car.carNumber} (${car.brand} ${car.model})?",
            onConfirm = {
                mainViewModel.deleteCar(car)
                carToDelete = null
            },
            onDismiss = { carToDelete = null }
        )
    }
}

@Composable
fun CarItemCard(
    car: Car,
    onClick: () -> Unit,
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GreenPrimary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = car.fuelType,
                            color = GreenPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = car.carNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Car")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${car.brand} ${car.model} (${car.year})",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Current Odometer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = Formatters.formatKm(car.currentOdometer),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Insurance Expiry",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = car.insuranceExpiry.ifBlank { "-" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Column {
                    Text(
                        text = "Service Due",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = car.serviceDueDate.ifBlank { "-" },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
