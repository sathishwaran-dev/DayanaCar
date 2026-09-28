package com.example.myapplication.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Initial Role Selection Route
    object RoleSelection : Screen("role_selection", "Select Role")

    // Auth Routes
    object CustomerAuth : Screen("customer_auth", "Customer Login")
    object DriverAuth : Screen("driver_auth", "Driver Login")
    object OwnerAuth : Screen("owner_auth", "Owner Login")

    // Customer Routes
    object CustomerHome : Screen("customer_home", "Cars", Icons.Default.DirectionsCar)
    object CustomerBookingConfirm : Screen("customer_booking_confirm", "Confirm Booking")
    object CustomerBookings : Screen("customer_bookings", "My Bookings", Icons.Default.Bookmark)
    object CustomerOffers : Screen("customer_offers", "Offers", Icons.Default.LocalOffer)
    object CustomerProfile : Screen("customer_profile", "Profile", Icons.Default.Person)

    // Settings & Account Detailed Screens
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object EditProfile : Screen("edit_profile", "Edit Profile")
    object PrivacySecurity : Screen("privacy_security", "Privacy & Security")
    object AboutApp : Screen("about_app", "About App")
    object HelpSupport : Screen("help_support", "Help & Support")
    object TermsConditions : Screen("terms_conditions", "Terms & Conditions")
    object PrivacyPolicy : Screen("privacy_policy", "Privacy Policy")

    // Driver Routes
    object DriverDashboard : Screen("driver_dashboard", "Dashboard", Icons.Default.Dashboard)
    object DriverBookings : Screen("driver_bookings", "My Bookings", Icons.Default.Route)
    object DriverBonusPayslip : Screen("driver_bonus_payslip", "Bonus & Payslips", Icons.Default.Payments)
    object DriverProfile : Screen("driver_profile", "Profile", Icons.Default.Person)

    // Admin / Owner Routes
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Cars : Screen("cars", "Cars", Icons.Default.DirectionsCar)
    object CarDetail : Screen("car_detail/{carId}", "Car Details") {
        fun createRoute(carId: Int) = "car_detail/$carId"
    }
    object Drivers : Screen("drivers", "Drivers", Icons.Default.Badge)
    object Trips : Screen("trips", "Trips", Icons.Default.Route)
    object TripTracking : Screen("trip_tracking", "Live Tracking", Icons.Default.GpsFixed)
    object Expenses : Screen("expenses", "Expenses", Icons.Default.Receipt)
    object Bookings : Screen("bookings", "Bookings", Icons.Default.Bookmark)
    object Customers : Screen("customers", "Customers", Icons.Default.People)
    object CustomerDetail : Screen("customer_detail/{customerId}", "Customer Details") {
        fun createRoute(customerId: Int) = "customer_detail/$customerId"
    }
    object Reports : Screen("reports", "Reports", Icons.Default.Assessment)
    object DistanceHistory : Screen("distance_history", "Distance History", Icons.Default.Speed)
}

val customerBottomNavItems = listOf(
    Screen.CustomerHome,
    Screen.CustomerBookings,
    Screen.CustomerOffers,
    Screen.CustomerProfile
)

val driverBottomNavItems = listOf(
    Screen.DriverDashboard,
    Screen.DriverBookings,
    Screen.DriverBonusPayslip,
    Screen.DriverProfile
)

val ownerBottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Cars,
    Screen.Drivers,
    Screen.Customers,
    Screen.Bookings
)
