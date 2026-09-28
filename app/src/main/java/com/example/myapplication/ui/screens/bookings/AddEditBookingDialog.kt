package com.example.myapplication.ui.screens.bookings

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
import com.example.myapplication.data.model.Booking
import com.example.myapplication.data.model.Car
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBookingDialog(
    booking: Booking? = null,
    cars: List<Car>,
    onDismiss: () -> Unit,
    onSave: (Booking) -> Unit
) {
    var selectedCar by remember { mutableStateOf(cars.find { it.id == booking?.carId } ?: cars.firstOrNull()) }
    var carDropdownExpanded by remember { mutableStateOf(false) }

    var customerName by remember { mutableStateOf(booking?.customerName ?: "") }
    var customerMobile by remember { mutableStateOf(booking?.customerMobile ?: "") }
    var pickup by remember { mutableStateOf(booking?.pickupLocation ?: "") }
    var destination by remember { mutableStateOf(booking?.destination ?: "") }
    var dateStr by remember { mutableStateOf(booking?.bookingDate ?: Formatters.getTodayDateString()) }
    var timeStr by remember { mutableStateOf(booking?.bookingTime ?: "10:00") }
    var fareStr by remember { mutableStateOf(booking?.fare?.toString() ?: "1500") }
    var status by remember { mutableStateOf(booking?.status ?: "Confirmed") }
    var notes by remember { mutableStateOf(booking?.notes ?: "") }

    val statusOptions = listOf("Pending", "Confirmed", "Started", "Completed", "Cancelled")
    var statusDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (booking == null) "New Booking" else "Edit Booking", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
                                    carDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = customerMobile,
                    onValueChange = { customerMobile = it },
                    label = { Text("Customer Mobile") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pickup,
                    onValueChange = { pickup = it },
                    label = { Text("Pickup Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = timeStr,
                        onValueChange = { timeStr = it },
                        label = { Text("Time (HH:mm)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = fareStr,
                    onValueChange = { fareStr = it },
                    label = { Text("Fare (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = statusDropdownExpanded,
                    onExpandedChange = { statusDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = statusDropdownExpanded,
                        onDismissRequest = { statusDropdownExpanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    status = option
                                    statusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Special Instructions") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val car = selectedCar
                    val fare = fareStr.toDoubleOrNull() ?: 0.0
                    if (car != null && customerName.isNotBlank() && customerMobile.isNotBlank()) {
                        val newBooking = (booking ?: Booking(
                            customerName = customerName.trim(),
                            customerMobile = customerMobile.trim(),
                            carId = car.id,
                            carNumber = car.carNumber,
                            pickupLocation = pickup.trim(),
                            destination = destination.trim(),
                            bookingDate = dateStr.trim(),
                            bookingTime = timeStr.trim(),
                            fare = fare,
                            status = status,
                            notes = notes.trim()
                        )).copy(
                            customerName = customerName.trim(),
                            customerMobile = customerMobile.trim(),
                            carId = car.id,
                            carNumber = car.carNumber,
                            pickupLocation = pickup.trim(),
                            destination = destination.trim(),
                            bookingDate = dateStr.trim(),
                            bookingTime = timeStr.trim(),
                            fare = fare,
                            status = status,
                            notes = notes.trim()
                        )
                        onSave(newBooking)
                    }
                },
                enabled = selectedCar != null && customerName.isNotBlank() && customerMobile.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Booking")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
