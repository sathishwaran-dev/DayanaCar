package com.example.myapplication.ui.screens.driver

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.theme.GreenContainer
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.utils.Formatters
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverBonusPayslipScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel
) {
    val driverId = authViewModel.sessionManager.getUserId()

    val bonuses by mainViewModel.bonuses.collectAsState()
    val myBonuses = remember(bonuses, driverId) { bonuses.filter { it.driverId == driverId } }

    val payslips by mainViewModel.payslips.collectAsState()
    val myPayslips = remember(payslips, driverId) { payslips.filter { it.driverId == driverId } }

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Earnings & Payslips", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("My Bonuses") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("My Payslips") })
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                if (myBonuses.isEmpty()) {
                    EmptyStateView(message = "No bonuses recorded yet.", icon = Icons.Default.Payments)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(myBonuses) { bonus ->
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
                                    Column {
                                        Text(bonus.reason, fontWeight = FontWeight.Bold)
                                        Text("Month: ${bonus.month}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Text(Formatters.formatCurrency(bonus.bonusAmount), fontWeight = FontWeight.Bold, color = GreenPrimary)
                                }
                            }
                        }
                    }
                }
            } else {
                if (myPayslips.isEmpty()) {
                    EmptyStateView(message = "No payslips generated yet.", icon = Icons.Default.ReceiptLong)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(myPayslips) { payslip ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = GreenContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Period: ${payslip.payPeriod}", fontWeight = FontWeight.Bold, color = GreenPrimary)
                                        Text(Formatters.formatCurrency(payslip.netSalary), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = GreenPrimary)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Basic: ${Formatters.formatCurrency(payslip.basicSalary)} | Allowances: ${Formatters.formatCurrency(payslip.allowances)}")
                                    Text("Bonus: ${Formatters.formatCurrency(payslip.bonusAmount)} | Deductions: ${Formatters.formatCurrency(payslip.deductions)}")
                                    Text("Pay Date: ${payslip.payDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
