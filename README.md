#  DayanaCar – Taxi & Fleet Management App

**DayanaCar** is a modern Taxi & Fleet Management application built with **Kotlin, Jetpack Compose, Material 3, MVVM, Room Database, and GPS Tracking**.

It supports **Customers, Drivers, and Owners/Admins** with booking, fleet management, driver management, trip tracking, expenses, revenue, and reports.

##  Features

###  Customer

* Browse available cars
* Book a taxi
* Fare calculation
* Booking history & status
* Offers & discounts
* Profile & settings
* Dark / Light mode
* Multi-language support
* Help & Support

###  Driver

* Assigned vehicle details
* View & manage bookings
* Start / complete trips
* Real-time GPS tracking
* Speed & distance tracking
* Odometer update
* Bonus & payslip history

###  Admin / Owner

* Dashboard & analytics
* Fleet management
* Driver management
* Booking & dispatch management
* Expense management
* Revenue & profit tracking
* Reports
* Driver salary & bonus management

##  Production Upgrades

Planned support for:

* Google Maps & Live Location
* Online Payments
* Push Notifications
* ASP.NET Core Web API
* SQL Server
* JWT Authentication
* SignalR Real-Time Updates
* Cloud Deployment
* Docker & CI/CD
* Advanced Analytics

## 🛠️ Tech Stack

**Android**

* Kotlin
* Jetpack Compose
* Material 3
* MVVM
* Coroutines & Flow
* Room Database
* KSP
* Google Location Services

**Backend – Planned**

* ASP.NET Core Web API
* C#
* SQL Server
* Entity Framework Core
* SignalR

##  Architecture

```text
UI
 ↓
ViewModel
 ↓
Repository
 ↓
DAO
 ↓
Room Database
```

Production architecture:

```text
Android App
 ↓
ASP.NET Core API
 ↓
SQL Server
```

##  Demo Login

| Role     | Username     | Password    |
| -------- | ------------ | ----------- |
| Admin    | `admin`      | `admin123`  |
| Driver   | `9123456789` | `driver123` |
| Customer | `9876543210` | `123456`    |

> Demo credentials are for development/testing only.

##  Build

```bash
./gradlew assembleDebug
```

##  Status

**Core taxi, driver, fleet, booking, GPS, and admin features are implemented.**

Additional backend, payment, cloud, and real-time features are planned for production deployment.

---

© 2026 DayanaCar
