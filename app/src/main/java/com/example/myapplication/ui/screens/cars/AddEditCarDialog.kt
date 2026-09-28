package com.example.myapplication.ui.screens.cars

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Car
import com.example.myapplication.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCarDialog(
    car: Car? = null,
    onDismiss: () -> Unit,
    onSave: (Car) -> Unit
) {
    var carNumber by remember { mutableStateOf(car?.carNumber ?: "") }
    var brand by remember { mutableStateOf(car?.brand ?: "") }
    var model by remember { mutableStateOf(car?.model ?: "") }
    var yearStr by remember { mutableStateOf(car?.year?.toString() ?: "2023") }
    var fuelType by remember { mutableStateOf(car?.fuelType ?: "Diesel") }
    var odometerStr by remember { mutableStateOf(car?.currentOdometer?.toString() ?: "0") }
    var insuranceExpiry by remember { mutableStateOf(car?.insuranceExpiry ?: "") }
    var serviceDueDate by remember { mutableStateOf(car?.serviceDueDate ?: "") }
    var notes by remember { mutableStateOf(car?.notes ?: "") }

    val fuelOptions = listOf("Diesel", "Petrol", "CNG", "Electric", "Hybrid")
    var fuelDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (car == null) "Add New Car" else "Edit Car Details",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = carNumber,
                    onValueChange = { carNumber = it.uppercase() },
                    label = { Text("Car Number (e.g. KA-01-AB-1234)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand (e.g. Toyota, Maruti)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model (e.g. Innova, Ertiga)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = yearStr,
                        onValueChange = { yearStr = it },
                        label = { Text("Year") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    ExposedDropdownMenuBox(
                        expanded = fuelDropdownExpanded,
                        onExpandedChange = { fuelDropdownExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = fuelType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fuel Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelDropdownExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = fuelDropdownExpanded,
                            onDismissRequest = { fuelDropdownExpanded = false }
                        ) {
                            fuelOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        fuelType = option
                                        fuelDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = odometerStr,
                    onValueChange = { odometerStr = it },
                    label = { Text("Current Odometer (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = insuranceExpiry,
                    onValueChange = { insuranceExpiry = it },
                    label = { Text("Insurance Expiry (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = serviceDueDate,
                    onValueChange = { serviceDueDate = it },
                    label = { Text("Service Due Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (carNumber.isNotBlank() && brand.isNotBlank()) {
                        val year = yearStr.toIntOrNull() ?: 2023
                        val odometer = odometerStr.toDoubleOrNull() ?: 0.0
                        val updatedCar = (car ?: Car(
                            carNumber = carNumber.trim(),
                            brand = brand.trim(),
                            model = model.trim(),
                            year = year,
                            fuelType = fuelType,
                            currentOdometer = odometer,
                            insuranceExpiry = insuranceExpiry.trim(),
                            serviceDueDate = serviceDueDate.trim(),
                            notes = notes.trim()
                        )).copy(
                            carNumber = carNumber.trim(),
                            brand = brand.trim(),
                            model = model.trim(),
                            year = year,
                            fuelType = fuelType,
                            currentOdometer = odometer,
                            insuranceExpiry = insuranceExpiry.trim(),
                            serviceDueDate = serviceDueDate.trim(),
                            notes = notes.trim()
                        )
                        onSave(updatedCar)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
