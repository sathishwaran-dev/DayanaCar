package com.example.myapplication.ui.screens.expenses

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Expense
import com.example.myapplication.ui.components.ConfirmDeleteDialog
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    mainViewModel: MainViewModel
) {
    val expenses by mainViewModel.expenses.collectAsState()
    val cars by mainViewModel.cars.collectAsState()
    val categoryFilter by mainViewModel.expenseCategoryFilter.collectAsState()

    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val categories = listOf("All", "Fuel", "Service", "Repair", "Insurance", "Toll", "Parking", "Other")

    val filteredExpenses = remember(expenses, categoryFilter) {
        if (categoryFilter == "All") expenses else expenses.filter { it.category == categoryFilter }
    }

    val totalAmount = remember(filteredExpenses) { filteredExpenses.sumOf { it.amount } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Management", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddExpenseDialog = true },
                containerColor = GreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Record Expense")
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

            // Total Expense Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Expense (${if (categoryFilter == "All") "All" else categoryFilter})", style = MaterialTheme.typography.bodyMedium)
                        Text(Formatters.formatCurrency(totalAmount), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = categoryFilter == category,
                        onClick = { mainViewModel.setExpenseCategoryFilter(category) },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GreenPrimary, selectedLabelColor = Color.White)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredExpenses.isEmpty()) {
                EmptyStateView(
                    message = "No expense records found.",
                    icon = Icons.Default.Receipt,
                    actionText = "Record Expense",
                    onActionClick = { showAddExpenseDialog = true }
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredExpenses) { expense ->
                        ExpenseItemCard(
                            expense = expense,
                            onDelete = { expenseToDelete = expense }
                        )
                    }
                }
            }
        }
    }

    if (showAddExpenseDialog) {
        AddEditExpenseDialog(
            cars = cars,
            onDismiss = { showAddExpenseDialog = false },
            onSave = { expense ->
                mainViewModel.addExpense(expense)
                showAddExpenseDialog = false
            }
        )
    }

    expenseToDelete?.let { expense ->
        ConfirmDeleteDialog(
            title = "Delete Expense Record",
            message = "Are you sure you want to delete expense of ${Formatters.formatCurrency(expense.amount)} for ${expense.carNumber}?",
            onConfirm = {
                mainViewModel.deleteExpense(expense)
                expenseToDelete = null
            },
            onDismiss = { expenseToDelete = null }
        )
    }
}

@Composable
fun ExpenseItemCard(
    expense: Expense,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = expense.category,
                            color = GreenPrimary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = expense.carNumber,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (expense.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(expense.description, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Date: ${expense.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    if (expense.odometerReading > 0) {
                        Text("Odo: ${Formatters.formatKm(expense.odometerReading)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = Formatters.formatCurrency(expense.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                }
            }
        }
    }
}
