package com.example.myapplication.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.data.dao.*
import com.example.myapplication.data.model.*
import com.example.myapplication.utils.PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Owner::class,
        Customer::class,
        Driver::class,
        Car::class,
        Booking::class,
        Trip::class,
        Expense::class,
        Offer::class,
        DriverBonus::class,
        Payslip::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ownerDao(): OwnerDao
    abstract fun customerDao(): CustomerDao
    abstract fun driverDao(): DriverDao
    abstract fun carDao(): CarDao
    abstract fun bookingDao(): BookingDao
    abstract fun tripDao(): TripDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun offerDao(): OfferDao
    abstract fun driverBonusDao(): DriverBonusDao
    abstract fun payslipDao(): PayslipDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dayanacar_manager_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDb(database)
                    }
                }
            }
        }

        private fun populateDb(database: AppDatabase) {
            CoroutineScope(Dispatchers.IO).launch {
                // Default Owner
                database.ownerDao().insertOwner(
                    Owner(
                        username = "admin",
                        passwordHash = PasswordHasher.hashPassword("admin123"),
                        name = "DayanaCar Owner",
                        mobile = "9876543210"
                    )
                )

                // Sample Customer
                val customerId = database.customerDao().insertCustomer(
                    Customer(
                        name = "Rajesh Kumar",
                        mobile = "9876543210",
                        email = "rajesh@example.com",
                        passwordHash = PasswordHasher.hashPassword("123456"),
                        pickupLocation = "Airport Terminal 1",
                        destination = "City Center Hotel",
                        status = "ACTIVE"
                    )
                ).toInt()

                // Sample Drivers
                val driver1Id = database.driverDao().insertDriver(
                    Driver(
                        name = "Manoj Singh",
                        mobile = "9123456789",
                        email = "manoj@dayanacar.com",
                        passwordHash = PasswordHasher.hashPassword("driver123"),
                        licenceNumber = "TN-01-2020-0012345",
                        licenceExpiry = "2030-12-31",
                        address = "12 Main St, City",
                        emergencyContact = "9123456780",
                        joiningDate = "2024-01-15",
                        baseSalary = 22000.0,
                        status = "AVAILABLE"
                    )
                ).toInt()

                val driver2Id = database.driverDao().insertDriver(
                    Driver(
                        name = "Suresh Verma",
                        mobile = "9876123456",
                        email = "suresh@dayanacar.com",
                        passwordHash = PasswordHasher.hashPassword("driver123"),
                        licenceNumber = "TN-05-2021-0098765",
                        licenceExpiry = "2031-06-20",
                        address = "45 Cross St, City",
                        emergencyContact = "9876123450",
                        joiningDate = "2024-03-01",
                        baseSalary = 20000.0,
                        status = "AVAILABLE"
                    )
                ).toInt()

                // Sample Cars
                val car1Id = database.carDao().insertCar(
                    Car(
                        carNumber = "TN-01-AB-1234",
                        brand = "Toyota",
                        model = "Innova Crysta",
                        year = 2023,
                        fuelType = "Diesel",
                        seatingCapacity = 7,
                        currentOdometer = 32500.0,
                        insuranceExpiry = "2026-12-31",
                        serviceDueDate = "2025-06-15",
                        status = "AVAILABLE",
                        assignedDriverId = driver1Id,
                        farePerKm = 18.0,
                        notes = "Executive AC 7-Seater"
                    )
                ).toInt()

                database.carDao().insertCar(
                    Car(
                        carNumber = "TN-05-MN-5678",
                        brand = "Maruti Suzuki",
                        model = "Ertiga",
                        year = 2023,
                        fuelType = "CNG",
                        seatingCapacity = 7,
                        currentOdometer = 24100.0,
                        insuranceExpiry = "2026-10-20",
                        serviceDueDate = "2025-08-10",
                        status = "AVAILABLE",
                        assignedDriverId = driver2Id,
                        farePerKm = 14.0,
                        notes = "Economical 7-Seater Taxi"
                    )
                )

                database.carDao().insertCar(
                    Car(
                        carNumber = "TN-09-XY-9012",
                        brand = "Hyundai",
                        model = "Aura",
                        year = 2024,
                        fuelType = "Petrol",
                        seatingCapacity = 5,
                        currentOdometer = 12000.0,
                        insuranceExpiry = "2027-01-15",
                        serviceDueDate = "2025-09-01",
                        status = "AVAILABLE",
                        farePerKm = 12.0,
                        notes = "Comfortable Sedan"
                    )
                )

                // Assign car to driver
                database.driverDao().assignCarToDriver(driver1Id, car1Id)

                // Sample Special Offers
                database.offerDao().insertOffer(
                    Offer(
                        offerName = "Welcome Discount",
                        description = "Get 10% discount on your first 5 bookings!",
                        discountPercentage = 10.0,
                        minBookingsRequired = 1,
                        startDate = "2025-01-01",
                        endDate = "2025-12-31",
                        isActive = true
                    )
                )

                database.offerDao().insertOffer(
                    Offer(
                        offerName = "Loyalty Super Discount",
                        description = "Frequent Traveller Special: 20% OFF after 5 completed trips!",
                        discountPercentage = 20.0,
                        minBookingsRequired = 5,
                        startDate = "2025-01-01",
                        endDate = "2025-12-31",
                        isActive = true
                    )
                )

                // Sample Booking
                database.bookingDao().insertBooking(
                    Booking(
                        customerId = customerId,
                        customerName = "Rajesh Kumar",
                        customerMobile = "9876543210",
                        carId = car1Id,
                        carNumber = "TN-01-AB-1234",
                        carModel = "Toyota Innova Crysta",
                        driverId = driver1Id,
                        driverName = "Manoj Singh",
                        pickupLocation = "Airport Terminal 1",
                        destination = "City Center Hotel",
                        bookingDate = "2025-02-25",
                        bookingTime = "10:30",
                        passengersCount = 4,
                        fare = 2500.0,
                        status = "CONFIRMED",
                        notes = "Flight pickup with luggage"
                    )
                )

                // Sample Driver Bonus
                database.driverBonusDao().insertBonus(
                    DriverBonus(
                        driverId = driver1Id,
                        driverName = "Manoj Singh",
                        bonusAmount = 2500.0,
                        reason = "Completed 50 Trips Milestone",
                        tripsTarget = 50,
                        month = "2025-02",
                        status = "APPROVED",
                        date = "2025-02-20"
                    )
                )

                // Sample Payslip
                database.payslipDao().insertPayslip(
                    Payslip(
                        driverId = driver1Id,
                        driverName = "Manoj Singh",
                        payPeriod = "January 2025",
                        basicSalary = 22000.0,
                        allowances = 1500.0,
                        bonusAmount = 2500.0,
                        deductions = 500.0,
                        netSalary = 25500.0,
                        payDate = "2025-02-01",
                        status = "PAID"
                    )
                )
            }
        }
    }
}
