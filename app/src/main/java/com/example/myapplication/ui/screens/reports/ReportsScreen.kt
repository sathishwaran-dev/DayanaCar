package com.example.myapplication.ui.screens.reports

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.ui.components.StatCard
import com.example.myapplication.ui.theme.AccentOrange
import com.example.myapplication.ui.theme.GreenContainer
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val cars by mainViewModel.cars.collectAsState()
    val bookings by mainViewModel.bookings.collectAsState()
    val trips by mainViewModel.trips.collectAsState()
    val expenses by mainViewModel.expenses.collectAsState()

    val totalRevenue = remember(bookings) {
        bookings.filter { it.status == "Completed" || it.status == "Confirmed" }.sumOf { it.fare }
    }

    val totalExpenses = remember(expenses) { expenses.sumOf { it.amount } }
    val fuelExpenses = remember(expenses) { expenses.filter { it.category == "Fuel" }.sumOf { it.amount } }
    val netProfit = remember(totalRevenue, totalExpenses) { totalRevenue - totalExpenses }
    val totalDistance = remember(trips) { trips.sumOf { it.distanceKm } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Reports & Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            item {
                Text("Financial Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Total Revenue",
                        value = Formatters.formatCurrency(totalRevenue),
                        icon = Icons.Default.AttachMoney,
                        containerColor = GreenContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Expenses",
                        value = Formatters.formatCurrency(totalExpenses),
                        icon = Icons.Default.Receipt,
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Estimated Profit",
                        value = Formatters.formatCurrency(netProfit),
                        icon = Icons.Default.AccountBalance,
                        containerColor = if (netProfit >= 0) GreenContainer else MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Fuel Expenses",
                        value = Formatters.formatCurrency(fuelExpenses),
                        icon = Icons.Default.LocalGasStation,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text("Operational Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        title = "Total Trips",
                        value = "${trips.size}",
                        icon = Icons.Default.Route,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Distance",
                        value = Formatters.formatKm(totalDistance),
                        icon = Icons.Default.Speed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Car-wise Performance Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(cars) { car ->
                val carTrips = trips.filter { it.carId == car.id }
                val carDist = carTrips.sumOf { it.distanceKm }
                val carExp = expenses.filter { it.carId == car.id }.sumOf { it.amount }
                val carRev = bookings.filter { it.carId == car.id && (it.status == "Completed" || it.status == "Confirmed") }.sumOf { it.fare }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${car.carNumber} (${car.brand} ${car.model})", fontWeight = FontWeight.Bold)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GreenPrimary.copy(alpha = 0.1f)
                            ) {
                                Text(car.fuelType, color = GreenPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Trips", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                Text("${carTrips.size}", fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Distance", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                Text(Formatters.formatKm(carDist), fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Revenue", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                Text(Formatters.formatCurrency(carRev), fontWeight = FontWeight.Bold, color = GreenPrimary)
                            }
                            Column {
                                Text("Expense", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                Text(Formatters.formatCurrency(carExp), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
