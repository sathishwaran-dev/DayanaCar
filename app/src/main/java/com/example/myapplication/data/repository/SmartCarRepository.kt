package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.*
import com.example.myapplication.data.model.*
import kotlinx.coroutines.flow.Flow

class SmartCarRepository(
    private val ownerDao: OwnerDao,
    private val customerDao: CustomerDao,
    private val driverDao: DriverDao,
    private val carDao: CarDao,
    private val bookingDao: BookingDao,
    private val tripDao: TripDao,
    private val expenseDao: ExpenseDao,
    private val offerDao: OfferDao,
    private val driverBonusDao: DriverBonusDao,
    private val payslipDao: PayslipDao
) {
    // Owner
    suspend fun getOwnerByUsername(username: String): Owner? = ownerDao.getOwnerByUsername(username)
    suspend fun insertOwner(owner: Owner) = ownerDao.insertOwner(owner)

    // Customer
    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()
    fun getCustomerById(id: Int): Flow<Customer?> = customerDao.getCustomerById(id)
    suspend fun getCustomerByIdDirect(id: Int): Customer? = customerDao.getCustomerByIdDirect(id)
    suspend fun getCustomerByMobile(mobile: String): Customer? = customerDao.getCustomerByMobile(mobile)
    suspend fun insertCustomer(customer: Customer): Long = customerDao.insertCustomer(customer)
    suspend fun updateCustomer(customer: Customer) = customerDao.updateCustomer(customer)
    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)
    suspend fun updateCustomerStatus(id: Int, status: String) = customerDao.updateCustomerStatus(id, status)

    // Driver
    fun getAllDrivers(): Flow<List<Driver>> = driverDao.getAllDrivers()
    fun getAvailableDrivers(): Flow<List<Driver>> = driverDao.getAvailableDrivers()
    fun getDriverById(id: Int): Flow<Driver?> = driverDao.getDriverById(id)
    suspend fun getDriverByMobile(mobile: String): Driver? = driverDao.getDriverByMobile(mobile)
    suspend fun insertDriver(driver: Driver): Long = driverDao.insertDriver(driver)
    suspend fun updateDriver(driver: Driver) = driverDao.updateDriver(driver)
    suspend fun deleteDriver(driver: Driver) = driverDao.deleteDriver(driver)
    suspend fun updateDriverStatus(id: Int, status: String) = driverDao.updateDriverStatus(id, status)
    suspend fun assignCarToDriver(driverId: Int, carId: Int?) = driverDao.assignCarToDriver(driverId, carId)

    // Cars
    fun getAllCars(): Flow<List<Car>> = carDao.getAllCars()
    fun getAvailableCars(): Flow<List<Car>> = carDao.getAvailableCars()
    fun getCarById(id: Int): Flow<Car?> = carDao.getCarById(id)
    suspend fun getCarByIdDirect(id: Int): Car? = carDao.getCarByIdDirect(id)
    suspend fun insertCar(car: Car): Long = carDao.insertCar(car)
    suspend fun updateCar(car: Car) = carDao.updateCar(car)
    suspend fun deleteCar(car: Car) = carDao.deleteCar(car)
    suspend fun updateCarStatus(id: Int, status: String) = carDao.updateCarStatus(id, status)
    suspend fun assignDriverToCar(carId: Int, driverId: Int?) = carDao.assignDriverToCar(carId, driverId)
    suspend fun updateOdometer(carId: Int, newOdometer: Double) = carDao.updateOdometer(carId, newOdometer)

    // Bookings
    fun getAllBookings(): Flow<List<Booking>> = bookingDao.getAllBookings()
    fun getBookingById(id: Int): Flow<Booking?> = bookingDao.getBookingById(id)
    fun getBookingsByCustomerId(customerId: Int): Flow<List<Booking>> = bookingDao.getBookingsByCustomerId(customerId)
    fun getBookingsByDriverId(driverId: Int): Flow<List<Booking>> = bookingDao.getBookingsByDriverId(driverId)
    fun getBookingsByCustomerMobile(mobile: String): Flow<List<Booking>> = bookingDao.getBookingsByCustomerMobile(mobile)
    suspend fun insertBooking(booking: Booking): Long = bookingDao.insertBooking(booking)
    suspend fun updateBooking(booking: Booking) = bookingDao.updateBooking(booking)
    suspend fun deleteBooking(booking: Booking) = bookingDao.deleteBooking(booking)
    suspend fun updateBookingStatus(id: Int, status: String) = bookingDao.updateBookingStatus(id, status)
    suspend fun assignDriverToBooking(bookingId: Int, driverId: Int, driverName: String) = bookingDao.assignDriverToBooking(bookingId, driverId, driverName)

    // Trips
    fun getAllTrips(): Flow<List<Trip>> = tripDao.getAllTrips()
    fun getTripById(id: Int): Flow<Trip?> = tripDao.getTripById(id)
    fun getActiveTrip(): Flow<Trip?> = tripDao.getActiveTrip()
    suspend fun getActiveTripDirect(): Trip? = tripDao.getActiveTripDirect()
    fun getTripsByCarId(carId: Int): Flow<List<Trip>> = tripDao.getTripsByCarId(carId)
    fun getTripsByDate(dateString: String): Flow<List<Trip>> = tripDao.getTripsByDate(dateString)
    suspend fun insertTrip(trip: Trip): Long = tripDao.insertTrip(trip)
    suspend fun updateTrip(trip: Trip) = tripDao.updateTrip(trip)

    // Expenses
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()
    fun getExpensesByCarId(carId: Int): Flow<List<Expense>> = expenseDao.getExpensesByCarId(carId)
    suspend fun insertExpense(expense: Expense): Long = expenseDao.insertExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    // Offers
    fun getAllOffers(): Flow<List<Offer>> = offerDao.getAllOffers()
    fun getOffersForCustomer(bookingCount: Int): Flow<List<Offer>> = offerDao.getOffersForCustomer(bookingCount)
    suspend fun insertOffer(offer: Offer): Long = offerDao.insertOffer(offer)
    suspend fun updateOffer(offer: Offer) = offerDao.updateOffer(offer)
    suspend fun deleteOffer(offer: Offer) = offerDao.deleteOffer(offer)

    // Driver Bonuses
    fun getAllBonuses(): Flow<List<DriverBonus>> = driverBonusDao.getAllBonuses()
    fun getBonusesByDriverId(driverId: Int): Flow<List<DriverBonus>> = driverBonusDao.getBonusesByDriverId(driverId)
    suspend fun insertBonus(bonus: DriverBonus): Long = driverBonusDao.insertBonus(bonus)
    suspend fun updateBonus(bonus: DriverBonus) = driverBonusDao.updateBonus(bonus)
    suspend fun updateBonusStatus(id: Int, status: String) = driverBonusDao.updateBonusStatus(id, status)

    // Payslips
    fun getAllPayslips(): Flow<List<Payslip>> = payslipDao.getAllPayslips()
    fun getPayslipsByDriverId(driverId: Int): Flow<List<Payslip>> = payslipDao.getPayslipsByDriverId(driverId)
    suspend fun insertPayslip(payslip: Payslip): Long = payslipDao.insertPayslip(payslip)
}
