package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.model.Customer
import com.example.myapplication.data.model.Driver
import com.example.myapplication.data.model.Owner
import com.example.myapplication.data.repository.SmartCarRepository
import com.example.myapplication.utils.PasswordHasher
import com.example.myapplication.utils.SessionManager
import com.example.myapplication.utils.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class LoggedIn(val role: UserRole, val userId: Int, val name: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SmartCarRepository
    val sessionManager: SessionManager = SessionManager(application)

    private val _authState = MutableStateFlow<AuthState>(
        if (sessionManager.isLoggedIn()) {
            AuthState.LoggedIn(sessionManager.getRole(), sessionManager.getUserId(), sessionManager.getName())
        } else {
            AuthState.Idle
        }
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentRole = MutableStateFlow(sessionManager.getRole())
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

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
    }

    fun loginOwner(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Username and password are required.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val owner = repository.getOwnerByUsername(username.trim())
            val hashed = PasswordHasher.hashPassword(password)
            if (owner != null && (owner.passwordHash == password || owner.passwordHash == hashed)) {
                sessionManager.saveSession(UserRole.OWNER, owner.id, owner.username, owner.name, owner.mobile)
                _currentRole.value = UserRole.OWNER
                _authState.value = AuthState.LoggedIn(UserRole.OWNER, owner.id, owner.name)
            } else {
                _authState.value = AuthState.Error("Invalid owner credentials.")
            }
        }
    }

    fun loginCustomer(mobile: String, password: String) {
        if (mobile.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Mobile number and password are required.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val customer = repository.getCustomerByMobile(mobile.trim())
            val hashed = PasswordHasher.hashPassword(password)
            if (customer != null && (customer.passwordHash == password || customer.passwordHash == hashed)) {
                if (customer.status == "INACTIVE") {
                    _authState.value = AuthState.Error("Account deactivated. Contact DayanaCar support.")
                    return@launch
                }
                sessionManager.saveSession(UserRole.CUSTOMER, customer.id, "", customer.name, customer.mobile)
                _currentRole.value = UserRole.CUSTOMER
                _authState.value = AuthState.LoggedIn(UserRole.CUSTOMER, customer.id, customer.name)
            } else {
                _authState.value = AuthState.Error("Invalid mobile or password.")
            }
        }
    }

    fun registerCustomer(name: String, mobile: String, email: String, password: String, confirmPass: String) {
        if (name.isBlank() || mobile.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Name, Mobile, and Password are required.")
            return
        }
        if (mobile.length < 10) {
            _authState.value = AuthState.Error("Please enter a valid 10-digit mobile number.")
            return
        }
        if (password != confirmPass) {
            _authState.value = AuthState.Error("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val existing = repository.getCustomerByMobile(mobile.trim())
            if (existing != null) {
                _authState.value = AuthState.Error("Mobile number already registered. Please login.")
            } else {
                val newCustomer = Customer(
                    name = name.trim(),
                    mobile = mobile.trim(),
                    email = email.trim(),
                    passwordHash = PasswordHasher.hashPassword(password)
                )
                val id = repository.insertCustomer(newCustomer).toInt()
                sessionManager.saveSession(UserRole.CUSTOMER, id, "", newCustomer.name, newCustomer.mobile)
                _currentRole.value = UserRole.CUSTOMER
                _authState.value = AuthState.LoggedIn(UserRole.CUSTOMER, id, newCustomer.name)
            }
        }
    }

    fun loginDriver(mobile: String, password: String) {
        if (mobile.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Mobile number and password are required.")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val driver = repository.getDriverByMobile(mobile.trim())
            val hashed = PasswordHasher.hashPassword(password)
            if (driver != null && (driver.passwordHash == password || driver.passwordHash == hashed)) {
                if (driver.status == "INACTIVE") {
                    _authState.value = AuthState.Error("Driver account is inactive.")
                    return@launch
                }
                sessionManager.saveSession(UserRole.DRIVER, driver.id, "", driver.name, driver.mobile)
                _currentRole.value = UserRole.DRIVER
                _authState.value = AuthState.LoggedIn(UserRole.DRIVER, driver.id, driver.name)
            } else {
                _authState.value = AuthState.Error("Invalid driver credentials.")
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _currentRole.value = UserRole.GUEST
        _authState.value = AuthState.Idle
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }
}
