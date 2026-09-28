package com.example.myapplication.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Car
import com.example.myapplication.data.model.Driver
import com.example.myapplication.data.model.DriverBonus
import com.example.myapplication.data.model.Payslip
import com.example.myapplication.ui.components.ConfirmDeleteDialog
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriversScreen(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val drivers by mainViewModel.drivers.collectAsState()
    val cars by mainViewModel.cars.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var driverToEdit by remember { mutableStateOf<Driver?>(null) }
    var driverToBonus by remember { mutableStateOf<Driver?>(null) }
    var driverToPayslip by remember { mutableStateOf<Driver?>(null) }
    var driverToDelete by remember { mutableStateOf<Driver?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Management", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Driver")
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

            if (drivers.isEmpty()) {
                EmptyStateView(
                    message = "No drivers added yet.",
                    icon = Icons.Default.Badge,
                    actionText = "Add Driver",
                    onActionClick = { showAddDialog = true }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(drivers) { driver ->
                        val assignedCar = cars.find { it.id == driver.assignedCarId }
                        AdminDriverCard(
                            driver = driver,
                            assignedCarNumber = assignedCar?.carNumber ?: "None",
                            onCallClick = { mainViewModel.callNumber(context, driver.mobile) },
                            onEditClick = { driverToEdit = driver },
                            onAddBonusClick = { driverToBonus = driver },
                            onGeneratePayslipClick = { driverToPayslip = driver },
                            onDeleteClick = { driverToDelete = driver }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEditDriverDialog(
            cars = cars,
            onDismiss = { showAddDialog = false },
            onSave = { newDriver ->
                mainViewModel.addDriver(newDriver)
                showAddDialog = false
            }
        )
    }

    driverToEdit?.let { driver ->
        AddEditDriverDialog(
            driver = driver,
            cars = cars,
            onDismiss = { driverToEdit = null },
            onSave = { updated ->
                mainViewModel.updateDriver(updated)
                driverToEdit = null
            }
        )
    }

    driverToBonus?.let { driver ->
        AddBonusDialog(
            driver = driver,
            onDismiss = { driverToBonus = null },
            onSave = { bonus ->
                mainViewModel.addDriverBonus(bonus)
                driverToBonus = null
            }
        )
    }

    driverToPayslip?.let { driver ->
        GeneratePayslipDialog(
            driver = driver,
            onDismiss = { driverToPayslip = null },
            onSave = { payslip ->
                mainViewModel.generatePayslip(payslip)
                driverToPayslip = null
            }
        )
    }

    driverToDelete?.let { driver ->
        ConfirmDeleteDialog(
            title = "Delete Driver",
            message = "Are you sure you want to delete driver ${driver.name}?",
            onConfirm = {
                mainViewModel.deleteDriver(driver)
                driverToDelete = null
            },
            onDismiss = { driverToDelete = null }
        )
    }
}

@Composable
fun AdminDriverCard(
    driver: Driver,
    assignedCarNumber: String,
    onCallClick: () -> Unit,
    onEditClick: () -> Unit,
    onAddBonusClick: () -> Unit,
    onGeneratePayslipClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
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
                Text(driver.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GreenPrimary.copy(alpha = 0.15f)
                ) {
                    Text(driver.status, color = GreenPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Mobile: ${driver.mobile} | Licence: ${driver.licenceNumber}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            Text("Assigned Car: $assignedCarNumber | Base Salary: ${Formatters.formatCurrency(driver.baseSalary)}", style = MaterialTheme.typography.bodyMedium)

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
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Driver")
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onAddBonusClick, shape = RoundedCornerShape(8.dp)) {
                        Text("+ Bonus", style = MaterialTheme.typography.labelSmall)
                    }
                    Button(onClick = onGeneratePayslipClick, colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary), shape = RoundedCornerShape(8.dp)) {
                        Text("Payslip", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDriverDialog(
    driver: Driver? = null,
    cars: List<Car>,
    onDismiss: () -> Unit,
    onSave: (Driver) -> Unit
) {
    var name by remember { mutableStateOf(driver?.name ?: "") }
    var mobile by remember { mutableStateOf(driver?.mobile ?: "") }
    var password by remember { mutableStateOf(driver?.passwordHash ?: "driver123") }
    var licenceNumber by remember { mutableStateOf(driver?.licenceNumber ?: "") }
    var licenceExpiry by remember { mutableStateOf(driver?.licenceExpiry ?: "2030-12-31") }
    var salaryStr by remember { mutableStateOf(driver?.baseSalary?.toString() ?: "22000") }
    var selectedCar by remember { mutableStateOf(cars.find { it.id == driver?.assignedCarId }) }
    var carDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (driver == null) "Add New Driver" else "Edit Driver Details", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = mobile, onValueChange = { mobile = it }, label = { Text("Mobile Number *") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = licenceNumber, onValueChange = { licenceNumber = it }, label = { Text("Licence Number") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = salaryStr, onValueChange = { salaryStr = it }, label = { Text("Base Salary (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(
                    expanded = carDropdownExpanded,
                    onExpandedChange = { carDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCar?.let { "${it.carNumber} (${it.brand})" } ?: "Assign Car (Optional)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Assign Car") },
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && mobile.isNotBlank()) {
                        val updated = (driver ?: Driver(
                            name = name.trim(),
                            mobile = mobile.trim(),
                            passwordHash = password.ifBlank { "driver123" },
                            licenceNumber = licenceNumber.trim(),
                            licenceExpiry = licenceExpiry.trim(),
                            baseSalary = salaryStr.toDoubleOrNull() ?: 20000.0,
                            assignedCarId = selectedCar?.id
                        )).copy(
                            name = name.trim(),
                            mobile = mobile.trim(),
                            passwordHash = password.ifBlank { "driver123" },
                            licenceNumber = licenceNumber.trim(),
                            licenceExpiry = licenceExpiry.trim(),
                            baseSalary = salaryStr.toDoubleOrNull() ?: 20000.0,
                            assignedCarId = selectedCar?.id
                        )
                        onSave(updated)
                    }
                },
                enabled = name.isNotBlank() && mobile.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Save Driver")
            }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun AddBonusDialog(
    driver: Driver,
    onDismiss: () -> Unit,
    onSave: (DriverBonus) -> Unit
) {
    var bonusAmount by remember { mutableStateOf("2500") }
    var reason by remember { mutableStateOf("Monthly Performance Bonus") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bonus for ${driver.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = bonusAmount, onValueChange = { bonusAmount = it }, label = { Text("Bonus Amount (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Reason / Milestone") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = bonusAmount.toDoubleOrNull() ?: 0.0
                    if (amt > 0) {
                        onSave(
                            DriverBonus(
                                driverId = driver.id,
                                driverName = driver.name,
                                bonusAmount = amt,
                                reason = reason.trim(),
                                month = Formatters.getTodayDateString().take(7),
                                status = "APPROVED",
                                date = Formatters.getTodayDateString()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) { Text("Approve Bonus") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun GeneratePayslipDialog(
    driver: Driver,
    onDismiss: () -> Unit,
    onSave: (Payslip) -> Unit
) {
    var basicSalary by remember { mutableStateOf(driver.baseSalary.toString()) }
    var allowances by remember { mutableStateOf("1500") }
    var bonusAmount by remember { mutableStateOf("2500") }
    var deductions by remember { mutableStateOf("500") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Payslip for ${driver.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = basicSalary, onValueChange = { basicSalary = it }, label = { Text("Basic Salary (₹)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = allowances, onValueChange = { allowances = it }, label = { Text("Allowances (₹)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = bonusAmount, onValueChange = { bonusAmount = it }, label = { Text("Approved Bonus (₹)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = deductions, onValueChange = { deductions = it }, label = { Text("Deductions (₹)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val basic = basicSalary.toDoubleOrNull() ?: 20000.0
                    val allow = allowances.toDoubleOrNull() ?: 0.0
                    val bon = bonusAmount.toDoubleOrNull() ?: 0.0
                    val ded = deductions.toDoubleOrNull() ?: 0.0
                    val net = basic + allow + bon - ded

                    onSave(
                        Payslip(
                            driverId = driver.id,
                            driverName = driver.name,
                            payPeriod = Formatters.getTodayDateString().take(7),
                            basicSalary = basic,
                            allowances = allow,
                            bonusAmount = bon,
                            deductions = ded,
                            netSalary = net,
                            payDate = Formatters.getTodayDateString(),
                            status = "GENERATED"
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) { Text("Generate Payslip") }
        },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
