package com.example.myapplication.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.auth.OwnerAuthScreen
import com.example.myapplication.ui.screens.auth.RoleSelectionScreen
import com.example.myapplication.ui.screens.bookings.AddEditBookingDialog
import com.example.myapplication.ui.screens.bookings.BookingsScreen
import com.example.myapplication.ui.screens.cars.AddEditCarDialog
import com.example.myapplication.ui.screens.cars.CarDetailScreen
import com.example.myapplication.ui.screens.cars.CarsScreen
import com.example.myapplication.ui.screens.customer.AboutAppScreen
import com.example.myapplication.ui.screens.customer.CustomerAuthScreen
import com.example.myapplication.ui.screens.customer.CustomerBookingConfirmScreen
import com.example.myapplication.ui.screens.customer.CustomerBookingsScreen
import com.example.myapplication.ui.screens.customer.CustomerHomeScreen
import com.example.myapplication.ui.screens.customer.CustomerOffersScreen
import com.example.myapplication.ui.screens.customer.CustomerProfileScreen
import com.example.myapplication.ui.screens.customer.EditProfileScreen
import com.example.myapplication.ui.screens.customer.HelpSupportScreen
import com.example.myapplication.ui.screens.customer.PrivacyPolicyScreen
import com.example.myapplication.ui.screens.customer.PrivacySecurityScreen
import com.example.myapplication.ui.screens.customer.SettingsScreen
import com.example.myapplication.ui.screens.customer.TermsConditionsScreen
import com.example.myapplication.ui.screens.customers.CustomerDetailScreen
import com.example.myapplication.ui.screens.customers.CustomersScreen
import com.example.myapplication.ui.screens.dashboard.DashboardScreen
import com.example.myapplication.ui.screens.distance.DistanceHistoryScreen
import com.example.myapplication.ui.screens.driver.DriverAuthScreen
import com.example.myapplication.ui.screens.driver.DriverBonusPayslipScreen
import com.example.myapplication.ui.screens.driver.DriverBookingsScreen
import com.example.myapplication.ui.screens.driver.DriverDashboardScreen
import com.example.myapplication.ui.screens.driver.DriverProfileScreen
import com.example.myapplication.ui.screens.driver.DriversScreen
import com.example.myapplication.ui.screens.expenses.AddEditExpenseDialog
import com.example.myapplication.ui.screens.expenses.ExpensesScreen
import com.example.myapplication.ui.screens.reports.ReportsScreen
import com.example.myapplication.ui.screens.trips.StartTripDialog
import com.example.myapplication.ui.screens.trips.TripTrackingScreen
import com.example.myapplication.ui.screens.trips.TripsScreen
import com.example.myapplication.utils.UserRole
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.MainViewModel

@Composable
fun DayanaCarNavGraph(
    authViewModel: AuthViewModel,
    mainViewModel: MainViewModel,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val currentRole by authViewModel.currentRole.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showAddCarDialog by remember { mutableStateOf(false) }
    var showAddBookingDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showStartTripDialog by remember { mutableStateOf(false) }

    val cars by mainViewModel.cars.collectAsState()

    val showBottomBar = currentRoute in listOf(
        Screen.CustomerHome.route,
        Screen.CustomerBookings.route,
        Screen.CustomerOffers.route,
        Screen.CustomerProfile.route,
        Screen.DriverDashboard.route,
        Screen.DriverBookings.route,
        Screen.DriverBonusPayslip.route,
        Screen.DriverProfile.route,
        Screen.Dashboard.route,
        Screen.Cars.route,
        Screen.Drivers.route,
        Screen.Customers.route,
        Screen.Bookings.route
    )

    val startDestination = when (currentRole) {
        UserRole.OWNER -> Screen.Dashboard.route
        UserRole.DRIVER -> Screen.DriverDashboard.route
        UserRole.CUSTOMER -> Screen.CustomerHome.route
        UserRole.GUEST -> Screen.RoleSelection.route
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                DayanaCarBottomBar(navController = navController, userRole = currentRole)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Role Selection Initial Screen
            composable(Screen.RoleSelection.route) {
                RoleSelectionScreen(navController = navController)
            }

            // Public / Customer Routes
            composable(Screen.CustomerHome.route) {
                CustomerHomeScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    navController = navController,
                    onNavigateToBookingAdd = {
                        val role = authViewModel.sessionManager.getRole()
                        if (role == UserRole.CUSTOMER || role == UserRole.OWNER || role == UserRole.DRIVER) {
                            showAddBookingDialog = true
                        } else {
                            navController.navigate(Screen.CustomerAuth.route)
                        }
                    },
                    onNavigateToTripStart = {
                        val role = authViewModel.sessionManager.getRole()
                        if (role == UserRole.CUSTOMER || role == UserRole.OWNER || role == UserRole.DRIVER) {
                            showStartTripDialog = true
                        } else {
                            navController.navigate(Screen.CustomerAuth.route)
                        }
                    }
                )
            }

            composable(Screen.CustomerAuth.route) {
                CustomerAuthScreen(
                    authViewModel = authViewModel,
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(Screen.CustomerBookingConfirm.route) {
                CustomerBookingConfirmScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            composable(Screen.CustomerBookings.route) {
                CustomerBookingsScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            composable(Screen.CustomerOffers.route) {
                CustomerOffersScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel
                )
            }

            composable(Screen.CustomerProfile.route) {
                CustomerProfileScreen(
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            // Detailed Settings & Account Screens (Customer Only)
            composable(Screen.Settings.route) {
                SettingsScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            composable(Screen.PrivacySecurity.route) {
                PrivacySecurityScreen(navController = navController)
            }

            composable(Screen.AboutApp.route) {
                AboutAppScreen(navController = navController)
            }

            composable(Screen.HelpSupport.route) {
                HelpSupportScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(Screen.TermsConditions.route) {
                TermsConditionsScreen(navController = navController)
            }

            composable(Screen.PrivacyPolicy.route) {
                PrivacyPolicyScreen(navController = navController)
            }

            // Driver Routes
            composable(Screen.DriverAuth.route) {
                DriverAuthScreen(
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            composable(Screen.DriverDashboard.route) {
                DriverDashboardScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            composable(Screen.DriverBookings.route) {
                DriverBookingsScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            composable(Screen.DriverBonusPayslip.route) {
                DriverBonusPayslipScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel
                )
            }

            composable(Screen.DriverProfile.route) {
                DriverProfileScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            // Owner / Admin Routes
            composable(Screen.OwnerAuth.route) {
                OwnerAuthScreen(
                    authViewModel = authViewModel,
                    navController = navController
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    mainViewModel = mainViewModel,
                    authViewModel = authViewModel,
                    navController = navController,
                    onNavigateToCarAdd = { showAddCarDialog = true },
                    onNavigateToBookingAdd = { showAddBookingDialog = true },
                    onNavigateToExpenseAdd = { showAddExpenseDialog = true },
                    onNavigateToTripStart = { showStartTripDialog = true }
                )
            }

            composable(Screen.Cars.route) {
                CarsScreen(
                    mainViewModel = mainViewModel,
                    onAddCarClick = { showAddCarDialog = true },
                    onCarClick = { carId ->
                        navController.navigate(Screen.CarDetail.createRoute(carId))
                    }
                )
            }

            composable(
                route = Screen.CarDetail.route,
                arguments = listOf(navArgument("carId") { type = NavType.IntType })
            ) { backStackEntry ->
                val carId = backStackEntry.arguments?.getInt("carId") ?: 0
                CarDetailScreen(
                    carId = carId,
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(Screen.Drivers.route) {
                DriversScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(Screen.Trips.route) {
                TripsScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(Screen.TripTracking.route) {
                TripTrackingScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(Screen.Expenses.route) {
                ExpensesScreen(
                    mainViewModel = mainViewModel
                )
            }

            composable(Screen.Bookings.route) {
                BookingsScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(Screen.Customers.route) {
                CustomersScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(
                route = Screen.CustomerDetail.route,
                arguments = listOf(navArgument("customerId") { type = NavType.IntType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getInt("customerId") ?: 0
                CustomerDetailScreen(
                    customerId = customerId,
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(Screen.Reports.route) {
                ReportsScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }

            composable(Screen.DistanceHistory.route) {
                DistanceHistoryScreen(
                    mainViewModel = mainViewModel,
                    navController = navController
                )
            }
        }
    }

    // Quick Dialog Overlays
    if (showAddCarDialog) {
        AddEditCarDialog(
            onDismiss = { showAddCarDialog = false },
            onSave = { car ->
                mainViewModel.addCar(car)
                showAddCarDialog = false
            }
        )
    }

    if (showAddBookingDialog) {
        AddEditBookingDialog(
            cars = cars,
            onDismiss = { showAddBookingDialog = false },
            onSave = { booking ->
                mainViewModel.addBooking(booking, context)
                showAddBookingDialog = false
            }
        )
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

    if (showStartTripDialog) {
        StartTripDialog(
            cars = cars,
            onDismiss = { showStartTripDialog = false },
            onStart = { carId, carNumber, startLocation, customerName ->
                mainViewModel.startTrip(
                    carId = carId,
                    carNumber = carNumber,
                    startLocation = startLocation,
                    customerName = customerName
                )
                showStartTripDialog = false
                navController.navigate(Screen.TripTracking.route)
            }
        )
    }
}
