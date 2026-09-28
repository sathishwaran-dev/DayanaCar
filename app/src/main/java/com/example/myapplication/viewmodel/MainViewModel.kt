package com.example.myapplication.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.model.*
import com.example.myapplication.data.repository.SmartCarRepository
import com.example.myapplication.utils.Formatters
import com.example.myapplication.utils.LocationTracker
import com.example.myapplication.utils.NotificationHelper
import com.example.myapplication.utils.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository: SmartCarRepository
    val sessionManager: SessionManager = SessionManager(application)
    val locationTracker: LocationTracker = LocationTracker(application)

    // App Settings States
    private val _isDarkMode = MutableStateFlow(sessionManager.isDarkMode())
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _isNotificationsEnabled = MutableStateFlow(sessionManager.isNotificationsEnabled())
    val isNotificationsEnabled = _isNotificationsEnabled.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(sessionManager.getLanguage())
    val selectedLanguage = _selectedLanguage.asStateFlow()

    // Data Flows from Room
    val cars: StateFlow<List<Car>>
    val availableCars: StateFlow<List<Car>>
    val customers: StateFlow<List<Customer>>
    val drivers: StateFlow<List<Driver>>
    val availableDrivers: StateFlow<List<Driver>>
    val bookings: StateFlow<List<Booking>>
    val trips: StateFlow<List<Trip>>
    val expenses: StateFlow<List<Expense>>
    val offers: StateFlow<List<Offer>>
    val bonuses: StateFlow<List<DriverBonus>>
    val payslips: StateFlow<List<Payslip>>
    val activeTrip: StateFlow<Trip?>

    // Selected car state for pending customer booking flow
    private val _pendingBookingCar = MutableStateFlow<Car?>(null)
    val pendingBookingCar = _pendingBookingCar.asStateFlow()

    // Search Query States
    private val _carSearchQuery = MutableStateFlow("")
    val carSearchQuery = _carSearchQuery.asStateFlow()

    private val _customerSearchQuery = MutableStateFlow("")
    val customerSearchQuery = _customerSearchQuery.asStateFlow()

    private val _bookingSearchQuery = MutableStateFlow("")
    val bookingSearchQuery = _bookingSearchQuery.asStateFlow()

    private val _bookingStatusFilter = MutableStateFlow("All")
    val bookingStatusFilter = _bookingStatusFilter.asStateFlow()

    private val _expenseCategoryFilter = MutableStateFlow("All")
    val expenseCategoryFilter = _expenseCategoryFilter.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SmartCarRepository(
            ownerDao = database.ownerDao(),
            customerDao = database.customerDao(),
            driverDao = database.driverDao(),
            carDao = database.carDao(),
            bookingDao = database.bookingDao(),
            tripDao = database.tripDao(),
            expenseDao = database.expenseDao(),
            offerDao = database.offerDao(),
            driverBonusDao = database.driverBonusDao(),
            payslipDao = database.payslipDao()
        )

        cars = repository.getAllCars().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        availableCars = repository.getAvailableCars().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        customers = repository.getAllCustomers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        drivers = repository.getAllDrivers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        availableDrivers = repository.getAvailableDrivers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        bookings = repository.getAllBookings().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        trips = repository.getAllTrips().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        expenses = repository.getAllExpenses().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        offers = repository.getAllOffers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        bonuses = repository.getAllBonuses().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        payslips = repository.getAllPayslips().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        activeTrip = repository.getActiveTrip().stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    // App Settings Toggles
    fun setDarkMode(enabled: Boolean) {
        sessionManager.setDarkMode(enabled)
        _isDarkMode.value = enabled
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        sessionManager.setNotificationsEnabled(enabled)
        _isNotificationsEnabled.value = enabled
    }

    fun setLanguage(lang: String) {
        sessionManager.setLanguage(lang)
        _selectedLanguage.value = lang
    }

    // Profile Edit Action
    fun updateCustomerProfile(customerId: Int, newName: String, newMobile: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val existing = repository.getCustomerByIdDirect(customerId)
            if (existing != null) {
                val updated = existing.copy(name = newName.trim(), mobile = newMobile.trim())
                repository.updateCustomer(updated)
                sessionManager.updateUserInfo(newName.trim(), newMobile.trim())
            }
            onComplete()
        }
    }

    // Pending booking selection
    fun setPendingBookingCar(car: Car?) {
        _pendingBookingCar.value = car
    }

    // Car Operations
    fun addCar(car: Car) { viewModelScope.launch { repository.insertCar(car) } }
    fun updateCar(car: Car) { viewModelScope.launch { repository.updateCar(car) } }
    fun deleteCar(car: Car) { viewModelScope.launch { repository.deleteCar(car) } }
    fun updateCarStatus(id: Int, status: String) { viewModelScope.launch { repository.updateCarStatus(id, status) } }

    // Customer Operations
    fun addCustomer(customer: Customer) { viewModelScope.launch { repository.insertCustomer(customer) } }
    fun updateCustomer(customer: Customer) { viewModelScope.launch { repository.updateCustomer(customer) } }
    fun deleteCustomer(customer: Customer) { viewModelScope.launch { repository.deleteCustomer(customer) } }
    fun updateCustomerStatus(id: Int, status: String) { viewModelScope.launch { repository.updateCustomerStatus(id, status) } }

    // Driver Operations
    fun addDriver(driver: Driver) { viewModelScope.launch { repository.insertDriver(driver) } }
    fun updateDriver(driver: Driver) { viewModelScope.launch { repository.updateDriver(driver) } }
    fun deleteDriver(driver: Driver) { viewModelScope.launch { repository.deleteDriver(driver) } }
    fun updateDriverStatus(id: Int, status: String) { viewModelScope.launch { repository.updateDriverStatus(id, status) } }
    fun assignCarToDriver(driverId: Int, carId: Int?) {
        viewModelScope.launch {
            repository.assignCarToDriver(driverId, carId)
            if (carId != null) {
                repository.assignDriverToCar(carId, driverId)
            }
        }
    }

    // Booking Operations
    fun addBooking(booking: Booking, context: Context) {
        viewModelScope.launch {
            val id = repository.insertBooking(booking)
            if (id > 0 && sessionManager.isNotificationsEnabled()) {
                NotificationHelper.showBookingNotification(
                    context,
                    "New DayanaCar Booking",
                    "Booking requested by ${booking.customerName} (${booking.carNumber})"
                )
            }
        }
    }

    fun updateBooking(booking: Booking) { viewModelScope.launch { repository.updateBooking(booking) } }
    fun updateBookingStatus(id: Int, status: String, context: Context? = null) {
        viewModelScope.launch {
            repository.updateBookingStatus(id, status)
            if (context != null && sessionManager.isNotificationsEnabled()) {
                NotificationHelper.showBookingNotification(context, "DayanaCar Status Update", "Booking #$id updated to $status")
            }
        }
    }
    fun assignDriverToBooking(bookingId: Int, driverId: Int, driverName: String, context: Context? = null) {
        viewModelScope.launch {
            repository.assignDriverToBooking(bookingId, driverId, driverName)
            if (context != null && sessionManager.isNotificationsEnabled()) {
                NotificationHelper.showBookingNotification(context, "Driver Assigned", "$driverName assigned to booking #$bookingId")
            }
        }
    }
    fun deleteBooking(booking: Booking) { viewModelScope.launch { repository.deleteBooking(booking) } }

    // Trip & GPS Operations
    fun startTrip(
        carId: Int,
        carNumber: String,
        driverId: Int? = null,
        driverName: String = "",
        bookingId: Int? = null,
        customerId: Int? = null,
        customerName: String = "",
        startLocation: String = "GPS Current Location"
    ) {
        viewModelScope.launch {
            val currentActive = repository.getActiveTripDirect()
            if (currentActive == null) {
                val newTrip = Trip(
                    carId = carId,
                    carNumber = carNumber,
                    driverId = driverId,
                    driverName = driverName,
                    bookingId = bookingId,
                    customerId = customerId,
                    customerName = customerName,
                    startLocation = startLocation,
                    startTime = System.currentTimeMillis(),
                    dateString = Formatters.getTodayDateString(),
                    isCompleted = false
                )
                repository.insertTrip(newTrip)
                if (bookingId != null) {
                    repository.updateBookingStatus(bookingId, "STARTED")
                }
                repository.updateCarStatus(carId, "ON_TRIP")
                if (driverId != null) {
                    repository.updateDriverStatus(driverId, "ON_TRIP")
                }
                locationTracker.startTracking()
            }
        }
    }

    fun stopTrip(endLocation: String = "Destination") {
        viewModelScope.launch {
            val currentActive = repository.getActiveTripDirect()
            if (currentActive != null) {
                val trackingData = locationTracker.stopTracking()
                val endTimeMs = System.currentTimeMillis()
                val durationMinutes = ((endTimeMs - currentActive.startTime) / (1000 * 60)).coerceAtLeast(1)

                val completedTrip = currentActive.copy(
                    endLocation = endLocation.ifBlank { "Destination" },
                    endLat = trackingData.currentLat,
                    endLng = trackingData.currentLng,
                    distanceKm = trackingData.totalDistanceKm,
                    durationMinutes = durationMinutes,
                    avgSpeedKmh = trackingData.avgSpeedKmh,
                    maxSpeedKmh = trackingData.maxSpeedKmh,
                    endTime = endTimeMs,
                    isCompleted = true
                )

                repository.updateTrip(completedTrip)

                // Update car status & odometer
                val car = repository.getCarByIdDirect(currentActive.carId)
                if (car != null) {
                    val updatedOdo = car.currentOdometer + trackingData.totalDistanceKm
                    repository.updateOdometer(car.id, updatedOdo)
                    repository.updateCarStatus(car.id, "AVAILABLE")
                }

                // Update driver status
                currentActive.driverId?.let { driverId ->
                    repository.updateDriverStatus(driverId, "AVAILABLE")
                }

                // Update booking status
                if (currentActive.bookingId != null) {
                    repository.updateBookingStatus(currentActive.bookingId, "COMPLETED")
                }
            }
        }
    }

    // Expenses
    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.insertExpense(expense)
            if (expense.odometerReading > 0) {
                val car = repository.getCarByIdDirect(expense.carId)
                if (car != null && expense.odometerReading > car.currentOdometer) {
                    repository.updateOdometer(expense.carId, expense.odometerReading)
                }
            }
        }
    }
    fun deleteExpense(expense: Expense) { viewModelScope.launch { repository.deleteExpense(expense) } }

    // Offers
    fun addOffer(offer: Offer) { viewModelScope.launch { repository.insertOffer(offer) } }
    fun updateOffer(offer: Offer) { viewModelScope.launch { repository.updateOffer(offer) } }
    fun deleteOffer(offer: Offer) { viewModelScope.launch { repository.deleteOffer(offer) } }

    // Driver Bonus & Payslips
    fun addDriverBonus(bonus: DriverBonus) { viewModelScope.launch { repository.insertBonus(bonus) } }
    fun updateBonusStatus(id: Int, status: String) { viewModelScope.launch { repository.updateBonusStatus(id, status) } }
    fun generatePayslip(payslip: Payslip) { viewModelScope.launch { repository.insertPayslip(payslip) } }

    // Search filters
    fun setCarSearchQuery(query: String) { _carSearchQuery.value = query }
    fun setCustomerSearchQuery(query: String) { _customerSearchQuery.value = query }
    fun setBookingSearchQuery(query: String) { _bookingSearchQuery.value = query }
    fun setBookingStatusFilter(status: String) { _bookingStatusFilter.value = status }
    fun setExpenseCategoryFilter(category: String) { _expenseCategoryFilter.value = category }

    // Call Customer/Driver via Phone Dialer
    fun callNumber(context: Context, mobileNumber: String) {
        val cleanNumber = mobileNumber.replace("[^0-9+]".toRegex(), "")
        if (cleanNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanNumber")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    // Support Email Intent
    fun sendSupportEmail(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@dayanacar.com")
            putExtra(Intent.EXTRA_SUBJECT, "DayanaCar Customer Support Request")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
