package com.example.myapplication.ui.screens.expenses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Car
import com.example.myapplication.data.model.Expense
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseDialog(
    cars: List<Car>,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var selectedCar by remember { mutableStateOf(cars.firstOrNull()) }
    var carDropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Fuel", "Service", "Repair", "Insurance", "Toll", "Parking", "Other")
    var category by remember { mutableStateOf(categories.first()) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    var amountStr by remember { mutableStateOf("") }
    var dateStr by remember { mutableStateOf(Formatters.getTodayDateString()) }
    var description by remember { mutableStateOf("") }
    var odometerStr by remember { mutableStateOf(selectedCar?.currentOdometer?.toString() ?: "0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Car Selector
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
                                text = { Text("${car.carNumber} - ${car.brand}") },
                                onClick = {
                                    selectedCar = car
                                    odometerStr = car.currentOdometer.toString()
                                    carDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Category Selector
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Expense Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { dateStr = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = odometerStr,
                    onValueChange = { odometerStr = it },
                    label = { Text("Current Odometer Reading (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description / Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    val odo = odometerStr.toDoubleOrNull() ?: 0.0
                    val car = selectedCar
                    if (car != null && amt > 0) {
                        val newExpense = Expense(
                            carId = car.id,
                            carNumber = car.carNumber,
                            amount = amt,
                            date = dateStr.trim(),
                            category = category,
                            description = description.trim(),
                            odometerReading = odo
                        )
                        onSave(newExpense)
                    }
                },
                enabled = selectedCar != null && amountStr.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Expense")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
