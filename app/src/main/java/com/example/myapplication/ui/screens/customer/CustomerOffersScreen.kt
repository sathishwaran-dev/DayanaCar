package com.example.myapplication.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Offer
import com.example.myapplication.ui.components.EmptyStateView
import com.example.myapplication.ui.theme.GreenContainer
import com.example.myapplication.ui.theme.GreenPrimary
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOffersScreen(
    mainViewModel: MainViewModel,
    authViewModel: AuthViewModel
) {
    val allOffers by mainViewModel.offers.collectAsState()
    val allBookings by mainViewModel.bookings.collectAsState()
    val currentUserId = authViewModel.sessionManager.getUserId()
    val currentUserMobile = authViewModel.sessionManager.getMobile()

    val myBookingsCount = remember(allBookings, currentUserId, currentUserMobile) {
        allBookings.count {
            (it.customerId == currentUserId || it.customerMobile == currentUserMobile) && it.status == "COMPLETED"
        }
    }

    // Show applicable offers for this customer
    val applicableOffers = remember(allOffers, myBookingsCount) {
        allOffers.filter { it.isActive && myBookingsCount >= (it.minBookingsRequired - 1) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Special Loyalty Offers", fontWeight = FontWeight.Bold) },
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
            Spacer(modifier = Modifier.height(8.dp))

            // Loyalty Progress Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("YOUR LOYALTY LEVEL", style = MaterialTheme.typography.labelSmall, color = GreenPrimary, fontWeight = FontWeight.Bold)
                    Text("Completed Trips: $myBookingsCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Unlock higher discount tiers as you travel more with DayanaCar!", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (applicableOffers.isEmpty()) {
                EmptyStateView(
                    message = "No special offers unlocked yet. Complete your first trip to unlock rewards!",
                    icon = Icons.Default.LocalOffer
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(applicableOffers) { offer ->
                        OfferCard(offer = offer, customerTrips = myBookingsCount)
                    }
                }
            }
        }
    }
}

@Composable
fun OfferCard(offer: Offer, customerTrips: Int) {
    val isUnlocked = customerTrips >= offer.minBookingsRequired

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
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isUnlocked) GreenPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isUnlocked) "UNLOCKED: ${offer.discountPercentage.toInt()}% OFF" else "LOCK: Requires ${offer.minBookingsRequired} Trips",
                        color = if (isUnlocked) GreenPrimary else MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(offer.offerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(offer.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
