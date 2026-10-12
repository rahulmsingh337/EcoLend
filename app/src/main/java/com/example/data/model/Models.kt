package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lend_items")
data class LendItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val ownerId: String,
    val ownerName: String,
    val ownerRating: Float,
    val ownerReviewsCount: Int,
    val ownerAvatarSeed: String,
    val distanceMiles: Double,
    val neighborhood: String,
    val condition: String,
    val depositAmount: Double,
    val dailyFee: Double,
    val estimatedWasteSavedKg: Double,
    val estimatedDollarSaved: Double,
    val estimatedCo2SavedKg: Double,
    val availability: String, // "AVAILABLE_NOW", "BORROWED", "RESERVED"
    val returnDateMillis: Long? = null,
    val borrowerName: String? = null,
    val isFavorite: Boolean = false,
    val iconCategory: String = "TOOLS", // "TOOLS", "GARDEN", "KITCHEN", "CAMPING", "CLEANING", "PARTY", "GAMES"
    val photoUri: String? = null,
    val maxBorrowDurationDays: Int = 7,
    val availableFromMillis: Long? = null,
    val availableUntilMillis: Long? = null,
    val pickupLocationName: String? = null,
    val pickupAddress: String? = null,
    val pickupNotes: String? = null,
    val isFuzzyPrivacy: Boolean = true
)

@Entity(tableName = "borrow_requests")
data class BorrowRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: Long,
    val itemTitle: String,
    val requesterName: String,
    val ownerName: String,
    val startDateMillis: Long,
    val endDateMillis: Long,
    val status: String, // "PENDING", "APPROVED", "ACTIVE", "RETURNED", "CANCELLED"
    val depositPaid: Double,
    val feePaid: Double,
    val transactionId: String? = null,
    val note: String,
    val pickupOption: String = "Porch Pickup Box",
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val conversationId: String,
    val itemId: Long,
    val itemTitle: String,
    val senderName: String,
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis(),
    val isFromCurrentUser: Boolean,
    val isRead: Boolean = true
)

@Entity(tableName = "reviews")
data class Review(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: Long,
    val itemTitle: String,
    val reviewerName: String,
    val targetUserName: String,
    val rating: Int,
    val comment: String,
    val dateStr: String,
    val tags: String
)

@Entity(tableName = "app_notifications")
data class AppNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // "REQUEST", "STATUS_UPDATE", "RETURN_REMINDER", "CHAT", "REWARD"
    val timestampMillis: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val isOfflineQueued: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Alex Green",
    val neighborhood: String = "Oakridge Woods, Sector 4",
    val karmaScore: Int = 420,
    val levelTitle: String = "Green Guardian (Tier 3)",
    val wasteSavedKg: Double = 42.6,
    val moneySavedDollars: Double = 385.0,
    val co2PreventedKg: Double = 91.2,
    val exchangesCount: Int = 14,
    val rating: Float = 4.95f,
    val exactAddressHidden: Boolean = true,
    val fuzzyRadiusKm: Double = 0.5,
    val showOnLeaderboard: Boolean = true,
    val phoneVisibleToConfirmedOnly: Boolean = true,
    val darkModeSetting: String = "SYSTEM", // "LIGHT", "DARK", "SYSTEM"
    val largeTextMode: Boolean = false,
    val hasCompletedOnboarding: Boolean = true
)

@Entity(tableName = "wishlist_entries")
data class WishlistEntry(
    @PrimaryKey
    val itemId: Long,
    val itemTitle: String,
    val category: String,
    val note: String = "",
    val targetDate: String = "",
    val notifyWhenAvailable: Boolean = true,
    val addedAtMillis: Long = System.currentTimeMillis()
)

