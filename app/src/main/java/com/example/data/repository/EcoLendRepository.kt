package com.example.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.data.local.EcoLendDao
import com.example.data.local.InitialData
import com.example.data.model.AppNotification
import com.example.data.model.BorrowRequest
import com.example.data.model.ChatMessage
import com.example.data.model.LendItem
import com.example.data.model.Review
import com.example.data.model.UserProfile
import com.example.data.model.WishlistEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class EcoLendRepository(
    private val dao: EcoLendDao,
    private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    // Offline mode simulation state
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: Flow<Boolean> = _isOfflineMode.asStateFlow()

    // Offline pending actions counter
    private val _pendingSyncCount = MutableStateFlow(0)
    val pendingSyncCount: Flow<Int> = _pendingSyncCount.asStateFlow()

    init {
        createNotificationChannel()
        seedInitialDataIfNeeded()
        try {
            com.example.data.worker.ReturnDueNotificationWorker.schedulePeriodicWork(context)
        } catch (_: Exception) {}
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "EcoLend Community Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for borrow requests, item status updates, and community messages"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    private fun seedInitialDataIfNeeded() {
        scope.launch {
            val profile = dao.getUserProfileSync()
            if (profile == null) {
                dao.insertUserProfile(UserProfile())
                dao.insertItems(InitialData.sampleItems)
                dao.insertReviews(InitialData.sampleReviews)
                dao.insertRequests(InitialData.sampleRequests)
                dao.insertMessages(InitialData.sampleChatMessages)
                dao.insertNotifications(InitialData.sampleNotifications)
            }
        }
    }

    fun setOfflineMode(offline: Boolean) {
        _isOfflineMode.value = offline
        if (!offline && _pendingSyncCount.value > 0) {
            // Reconnected: sync actions
            scope.launch {
                val notif = AppNotification(
                    title = "Connected: Synced with Community 🌐",
                    message = "${_pendingSyncCount.value} pending offline exchanges successfully synchronized.",
                    type = "STATUS_UPDATE",
                    isRead = false,
                    isOfflineQueued = false
                )
                dao.insertNotification(notif)
                _pendingSyncCount.value = 0
                showSystemNotification(notif.title, notif.message)
            }
        }
    }

    // --- Items ---
    fun getAllItems(): Flow<List<LendItem>> = dao.getAllItems()
    fun getItemById(id: Long): Flow<LendItem?> = dao.getItemById(id)
    fun getItemsByCategory(category: String): Flow<List<LendItem>> = dao.getItemsByCategory(category)
    fun getFavoriteItems(): Flow<List<LendItem>> = dao.getFavoriteItems()
    fun getAllWishlistEntries(): Flow<List<WishlistEntry>> = dao.getAllWishlistEntries()

    suspend fun toggleFavorite(itemId: Long, currentFav: Boolean) {
        val newFav = !currentFav
        dao.updateFavorite(itemId, newFav)
        if (newFav) {
            val item = dao.getItemByIdSync(itemId)
            if (item != null) {
                dao.insertWishlistEntry(
                    WishlistEntry(
                        itemId = item.id,
                        itemTitle = item.title,
                        category = item.category,
                        note = "",
                        targetDate = "",
                        notifyWhenAvailable = true,
                        addedAtMillis = System.currentTimeMillis()
                    )
                )
            }
        } else {
            dao.deleteWishlistEntry(itemId)
        }
    }

    suspend fun saveToWishlistWithNote(
        item: LendItem,
        note: String,
        targetDate: String,
        notifyWhenAvailable: Boolean
    ) {
        dao.updateFavorite(item.id, true)
        dao.insertWishlistEntry(
            WishlistEntry(
                itemId = item.id,
                itemTitle = item.title,
                category = item.category,
                note = note,
                targetDate = targetDate,
                notifyWhenAvailable = notifyWhenAvailable,
                addedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateWishlistDetails(itemId: Long, note: String, targetDate: String, notify: Boolean) {
        dao.updateWishlistDetails(itemId, note, targetDate, notify)
    }

    suspend fun removeFromWishlist(itemId: Long) {
        dao.updateFavorite(itemId, false)
        dao.deleteWishlistEntry(itemId)
    }

    suspend fun updateItemPhoto(itemId: Long, photoUri: String) {
        dao.updateItemPhoto(itemId, photoUri)
    }

    suspend fun addNewItem(item: LendItem) {
        dao.insertItem(item)
        val profile = dao.getUserProfileSync() ?: UserProfile()
        dao.updateUserProfile(profile.copy(
            karmaScore = profile.karmaScore + 30
        ))
    }

    // --- Borrow Request with Payment/Deposit Simulation ---
    suspend fun createBorrowRequest(
        item: LendItem,
        note: String,
        startDate: Long,
        endDate: Long,
        depositPaid: Double,
        feePaid: Double,
        pickupOption: String
    ): BorrowRequest {
        val txnId = "TXN-${UUID.randomUUID().toString().take(8).uppercase()}"
        val request = BorrowRequest(
            itemId = item.id,
            itemTitle = item.title,
            requesterName = "Alex Green",
            ownerName = item.ownerName,
            startDateMillis = startDate,
            endDateMillis = endDate,
            status = "PENDING",
            depositPaid = depositPaid,
            feePaid = feePaid,
            transactionId = txnId,
            note = note,
            pickupOption = pickupOption
        )
        val requestId = dao.insertRequest(request)

        val isOffline = _isOfflineMode.value
        if (isOffline) {
            _pendingSyncCount.value = _pendingSyncCount.value + 1
        }

        // Add corresponding notification
        val notif = AppNotification(
            title = if (isOffline) "Offline Request Queued ⏳" else "Borrow Request Sent! 📬",
            message = "Your request for '${item.title}' was sent to ${item.ownerName}. Deposit authorized: $$depositPaid.",
            type = "REQUEST",
            isOfflineQueued = isOffline
        )
        dao.insertNotification(notif)
        showSystemNotification(notif.title, notif.message)

        // Seed an initial chat message
        val convId = "conv_${item.id}"
        dao.insertMessage(
            ChatMessage(
                conversationId = convId,
                itemId = item.id,
                itemTitle = item.title,
                senderName = "Alex Green",
                text = "Hi ${item.ownerName}! I've submitted a borrow request ($pickupOption). $note",
                isFromCurrentUser = true
            )
        )

        return request.copy(id = requestId)
    }

    suspend fun approveRequest(request: BorrowRequest) {
        dao.updateRequestStatus(request.id, "ACTIVE")
        dao.updateAvailability(
            id = request.itemId,
            status = "BORROWED",
            returnDate = request.endDateMillis,
            borrower = request.requesterName
        )
        val notif = AppNotification(
            title = "Item Picked Up! 🤝",
            message = "Exchange active for '${request.itemTitle}'. Remember to return on time to keep high trust.",
            type = "STATUS_UPDATE"
        )
        dao.insertNotification(notif)
        showSystemNotification(notif.title, notif.message)
    }

    suspend fun returnItem(request: BorrowRequest) {
        dao.updateRequestStatus(request.id, "RETURNED")
        dao.updateAvailability(
            id = request.itemId,
            status = "AVAILABLE_NOW",
            returnDate = null,
            borrower = null
        )

        // Update user stats (Karma, waste saved, exchanges)
        val item = dao.getItemByIdSync(request.itemId)
        val profile = dao.getUserProfileSync() ?: UserProfile()
        val wasteSaved = item?.estimatedWasteSavedKg ?: 8.0
        val moneySaved = item?.estimatedDollarSaved ?: 60.0
        val co2Saved = item?.estimatedCo2SavedKg ?: 15.0

        dao.updateUserProfile(
            profile.copy(
                karmaScore = profile.karmaScore + 50,
                exchangesCount = profile.exchangesCount + 1,
                wasteSavedKg = profile.wasteSavedKg + wasteSaved,
                moneySavedDollars = profile.moneySavedDollars + moneySaved,
                co2PreventedKg = profile.co2PreventedKg + co2Saved
            )
        )

        val notif = AppNotification(
            title = "Item Returned! Deposit Released 💰",
            message = "Full deposit $$${request.depositPaid} returned for '${request.itemTitle}'. +50 Eco Karma added!",
            type = "RETURN_REMINDER"
        )
        dao.insertNotification(notif)
        showSystemNotification(notif.title, notif.message)
    }

    // --- Reviews ---
    fun getReviewsForItem(itemId: Long): Flow<List<Review>> = dao.getReviewsForItem(itemId)
    fun getAllReviews(): Flow<List<Review>> = dao.getAllReviews()

    suspend fun submitReview(
        itemId: Long,
        itemTitle: String,
        targetUser: String,
        rating: Int,
        comment: String,
        tags: String
    ) {
        val review = Review(
            itemId = itemId,
            itemTitle = itemTitle,
            reviewerName = "Alex Green",
            targetUserName = targetUser,
            rating = rating,
            comment = comment,
            dateStr = "Just now",
            tags = tags
        )
        dao.insertReview(review)

        val profile = dao.getUserProfileSync() ?: UserProfile()
        dao.updateUserProfile(profile.copy(karmaScore = profile.karmaScore + 25))

        val notif = AppNotification(
            title = "Review Published! ⭐",
            message = "Thank you for reviewing $targetUser for '$itemTitle'. Trust builds community!",
            type = "REWARD"
        )
        dao.insertNotification(notif)
    }

    // --- Messages ---
    fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>> =
        dao.getMessagesForConversation(convId)
    fun getAllMessages(): Flow<List<ChatMessage>> = dao.getAllMessages()

    suspend fun sendMessage(
        conversationId: String,
        itemId: Long,
        itemTitle: String,
        text: String
    ) {
        val msg = ChatMessage(
            conversationId = conversationId,
            itemId = itemId,
            itemTitle = itemTitle,
            senderName = "Alex Green",
            text = text,
            isFromCurrentUser = true
        )
        dao.insertMessage(msg)

        if (_isOfflineMode.value) {
            _pendingSyncCount.value = _pendingSyncCount.value + 1
        }
    }

    // --- Notifications ---
    fun getAllNotifications(): Flow<List<AppNotification>> = dao.getAllNotifications()

    suspend fun markNotificationRead(id: Long) = dao.markNotificationRead(id)
    suspend fun markAllNotificationsRead() = dao.markAllNotificationsRead()

    // --- User Profile ---
    fun getUserProfile(): Flow<UserProfile?> = dao.getUserProfile()
    suspend fun updateUserProfile(profile: UserProfile) = dao.updateUserProfile(profile)

    // --- Requests ---
    fun getAllRequests(): Flow<List<BorrowRequest>> = dao.getAllRequests()

    private fun showSystemNotification(title: String, message: String) {
        try {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
            manager?.notify((System.currentTimeMillis() % 10000).toInt(), notification)
        } catch (_: Exception) {
            // Graceful fallback if permission not granted
        }
    }

    companion object {
        const val CHANNEL_ID = "ecolend_channel"
    }
}
