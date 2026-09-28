package com.example.myapplication.ui.screens.distance

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
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistanceHistoryScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val cars by mainViewModel.cars.collectAsState()
    val trips by mainViewModel.trips.collectAsState()

    val todayStr = remember { Formatters.getTodayDateString() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily & Car-wise Distance", fontWeight = FontWeight.Bold) },
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
                Text("Fleet Distance Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(cars) { car ->
                val carTrips = trips.filter { it.carId == car.id && it.isCompleted }

                val todayKm = carTrips.filter { it.dateString == todayStr }.sumOf { it.distanceKm }
                val totalKm = carTrips.sumOf { it.distanceKm }

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
                            Text("${car.carNumber} - ${car.brand} ${car.model}", fontWeight = FontWeight.Bold)
                            Text("Odo: ${Formatters.formatKm(car.currentOdometer)}", style = MaterialTheme.typography.bodySmall, color = GreenPrimary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Today Travelled", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                Text(Formatters.formatKm(todayKm), fontWeight = FontWeight.Bold, color = GreenPrimary)
                            }

                            Column {
                                Text("Total Trip Distance", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                Text(Formatters.formatKm(totalKm), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
