package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.EcoLendDatabase
import com.example.data.model.AppNotification
import com.example.data.model.BorrowRequest
import com.example.data.model.ChatMessage
import com.example.data.model.LendItem
import com.example.data.model.Review
import com.example.data.model.UserProfile
import com.example.data.model.WishlistEntry
import com.example.data.repository.EcoLendRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    HOME,
    MAP,
    DASHBOARD,
    MESSAGES,
    GAMIFICATION,
    PROFILE,
    ADD_ITEM
}

class EcoLendViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EcoLendRepository = EcoLendRepository(
        dao = EcoLendDatabase.getDatabase(application).dao(),
        context = application
    )

    // Current navigation tab
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Search & Filter State
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val selectedAvailability = MutableStateFlow("All") // "All", "Available", "Borrowed"
    val maxDistanceMiles = MutableStateFlow(3.0)

    // Repository flows
    val isOfflineMode: StateFlow<Boolean> = repository.isOfflineMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val pendingSyncCount: StateFlow<Int> = repository.pendingSyncCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allItems: StateFlow<List<LendItem>> = repository.getAllItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteItems: StateFlow<List<LendItem>> = repository.getFavoriteItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistEntries: StateFlow<List<WishlistEntry>> = repository.getAllWishlistEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequests: StateFlow<List<BorrowRequest>> = repository.getAllRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<AppNotification>> = repository.getAllNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile> = repository.getUserProfile()
        .combine(MutableStateFlow(UserProfile())) { profile, default ->
            profile ?: default
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    val allMessages: StateFlow<List<ChatMessage>> = repository.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReviews: StateFlow<List<Review>> = repository.getAllReviews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered items based on search, category, distance, availability
    val filteredItems: StateFlow<List<LendItem>> = combine(
        allItems,
        searchQuery,
        selectedCategory,
        selectedAvailability,
        maxDistanceMiles
    ) { items, query, category, availability, maxDist ->
        val trimmed = query.trim()
        items.filter { item ->
            val matchesQuery = trimmed.isBlank() ||
                    item.title.contains(trimmed, ignoreCase = true) ||
                    item.description.contains(trimmed, ignoreCase = true) ||
                    item.category.contains(trimmed, ignoreCase = true) ||
                    item.neighborhood.contains(trimmed, ignoreCase = true) ||
                    item.condition.contains(trimmed, ignoreCase = true)

            val matchesCategory = category == "All" || item.category.equals(category, ignoreCase = true)

            val matchesAvailability = when (availability) {
                "Available" -> item.availability == "AVAILABLE_NOW" || item.availability == "AVAILABLE"
                "Reserved" -> item.availability == "RESERVED"
                "Borrowed" -> item.availability == "BORROWED"
                else -> true
            }

            val matchesDistance = item.distanceMiles <= maxDist

            matchesQuery && matchesCategory && matchesAvailability && matchesDistance
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Personalized Recommendations: Curated based on user activity (e.g. Lawn/Garden, Power Tools)
    val recommendedItems: StateFlow<List<LendItem>> = allItems.combine(userProfile) { items, _ ->
        // Select top items that save the most waste and are closest
        items.filter { it.availability == "AVAILABLE_NOW" }
            .sortedByDescending { it.estimatedWasteSavedKg }
            .take(4)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Item Detail Screen Dialog / View
    private val _selectedItemForDetail = MutableStateFlow<LendItem?>(null)
    val selectedItemForDetail: StateFlow<LendItem?> = _selectedItemForDetail.asStateFlow()

    // Payment / Borrow Sheet Dialog State
    private val _itemForBorrowPayment = MutableStateFlow<LendItem?>(null)
    val itemForBorrowPayment: StateFlow<LendItem?> = _itemForBorrowPayment.asStateFlow()

    // Review Dialog State
    private val _itemForReview = MutableStateFlow<BorrowRequest?>(null)
    val itemForReview: StateFlow<BorrowRequest?> = _itemForReview.asStateFlow()

    // Active Chat Conversation Id
    private val _activeChatConversationId = MutableStateFlow<String?>("conv_1")
    val activeChatConversationId: StateFlow<String?> = _activeChatConversationId.asStateFlow()

    // Support Chat Assistant Dialog
    private val _showSupportChat = MutableStateFlow(false)
    val showSupportChat: StateFlow<Boolean> = _showSupportChat.asStateFlow()

    // Notifications Dialog
    private val _showNotificationTray = MutableStateFlow(false)
    val showNotificationTray: StateFlow<Boolean> = _showNotificationTray.asStateFlow()

    // Onboarding Dialog
    private val _showOnboarding = MutableStateFlow(false)
    val showOnboarding: StateFlow<Boolean> = _showOnboarding.asStateFlow()

    // Share Impact Card Dialog
    private val _showShareImpactDialog = MutableStateFlow(false)
    val showShareImpactDialog: StateFlow<Boolean> = _showShareImpactDialog.asStateFlow()

    // New Listing Dialog
    private val _showAddListingDialog = MutableStateFlow(false)
    val showAddListingDialog: StateFlow<Boolean> = _showAddListingDialog.asStateFlow()

    // QR Code Scanner State
    private val _showQrScanner = MutableStateFlow(false)
    val showQrScanner: StateFlow<Boolean> = _showQrScanner.asStateFlow()

    // Printable Tool QR Tag Dialog
    private val _showItemQrTag = MutableStateFlow<LendItem?>(null)
    val showItemQrTag: StateFlow<LendItem?> = _showItemQrTag.asStateFlow()

    // Camera Photo Capture for Items
    private val _itemForPhotoCapture = MutableStateFlow<LendItem?>(null)
    val itemForPhotoCapture: StateFlow<LendItem?> = _itemForPhotoCapture.asStateFlow()

    // Scanned Feedback Alert
    private val _scanFeedbackMessage = MutableStateFlow<String?>(null)
    val scanFeedbackMessage: StateFlow<String?> = _scanFeedbackMessage.asStateFlow()

    // Wishlist Note & Goal Dialog
    private val _itemForWishlistNote = MutableStateFlow<LendItem?>(null)
    val itemForWishlistNote: StateFlow<LendItem?> = _itemForWishlistNote.asStateFlow()

    fun openWishlistNoteDialog(item: LendItem) {
        _itemForWishlistNote.value = item
    }

    fun closeWishlistNoteDialog() {
        _itemForWishlistNote.value = null
    }

    // Navigation methods
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun openItemDetail(item: LendItem) {
        _selectedItemForDetail.value = item
    }

    fun closeItemDetail() {
        _selectedItemForDetail.value = null
    }

    fun startBorrowFlow(item: LendItem) {
        _itemForBorrowPayment.value = item
    }

    fun closeBorrowFlow() {
        _itemForBorrowPayment.value = null
    }

    fun openReviewDialog(request: BorrowRequest) {
        _itemForReview.value = request
    }

    fun openReviewDialogForItem(item: LendItem) {
        _itemForReview.value = BorrowRequest(
            id = 0,
            itemId = item.id,
            itemTitle = item.title,
            requesterName = userProfile.value.name,
            ownerName = item.ownerName,
            startDateMillis = System.currentTimeMillis() - 86400000L,
            endDateMillis = System.currentTimeMillis(),
            status = "RETURNED",
            depositPaid = item.depositAmount,
            feePaid = item.dailyFee,
            note = "Community review for ${item.title}"
        )
    }

    fun closeReviewDialog() {
        _itemForReview.value = null
    }

    fun openChatConversation(conversationId: String) {
        _activeChatConversationId.value = conversationId
        _currentScreen.value = Screen.MESSAGES
    }

    fun toggleSupportChat(show: Boolean) {
        _showSupportChat.value = show
    }

    fun toggleNotificationTray(show: Boolean) {
        _showNotificationTray.value = show
    }

    fun toggleOnboarding(show: Boolean) {
        _showOnboarding.value = show
    }

    fun toggleShareImpactDialog(show: Boolean) {
        _showShareImpactDialog.value = show
    }

    fun toggleAddListingDialog(show: Boolean) {
        _showAddListingDialog.value = show
    }

    fun toggleQrScanner(show: Boolean) {
        _showQrScanner.value = show
    }

    fun toggleItemQrTag(item: LendItem?) {
        _showItemQrTag.value = item
    }

    fun openCameraForPhotoCapture(item: LendItem) {
        _itemForPhotoCapture.value = item
    }

    fun closeCameraForPhotoCapture() {
        _itemForPhotoCapture.value = null
    }

    fun saveCapturedPhoto(item: LendItem, photoPath: String) {
        viewModelScope.launch {
            repository.updateItemPhoto(item.id, photoPath)
            _scanFeedbackMessage.value = "Photo captured for '${item.title}'! Saved to local item metadata."
            _itemForPhotoCapture.value = null
        }
    }

    fun updateItemMaxBorrowDuration(item: LendItem, newDurationDays: Int) {
        if (newDurationDays <= 0) return
        viewModelScope.launch {
            repository.updateMaxBorrowDuration(item.id, newDurationDays)
            _scanFeedbackMessage.value = "Max loan duration updated to $newDurationDays days for '${item.title}'."
        }
    }

    fun dismissScanFeedback() {
        _scanFeedbackMessage.value = null
    }

    fun onQrCodeScanned(rawCode: String) {
        _showQrScanner.value = false
        val extractedId = when {
            rawCode.contains("ITEM:") -> {
                rawCode.substringAfter("ITEM:").takeWhile { it.isDigit() }.toLongOrNull()
            }
            rawCode.all { it.isDigit() } -> {
                rawCode.toLongOrNull()
            }
            else -> null
        }

        val foundItem = allItems.value.find { it.id == extractedId }
        if (foundItem != null) {
            val activeReq = allRequests.value.find {
                it.itemId == foundItem.id && (it.status == "ACTIVE" || it.status == "APPROVED")
            }
            if (activeReq != null) {
                // Already borrowed: offer quick return and review
                _itemForReview.value = activeReq
                _scanFeedbackMessage.value = "Tool recognized: '${foundItem.title}' — Check-in return ready!"
            } else {
                // Not yet borrowed: launch instant borrow / checkout sheet
                _itemForBorrowPayment.value = foundItem
                _scanFeedbackMessage.value = "Tag scanned: '${foundItem.title}' — Proceeding to checkout!"
            }
        } else {
            _scanFeedbackMessage.value = "Scanned tag: '$rawCode' (No matching tool found in community ledger)"
        }
    }

    fun toggleOfflineMode() {
        val current = isOfflineMode.value
        repository.setOfflineMode(!current)
    }

    fun triggerDueDatesCheck() {
        try {
            com.example.data.worker.ReturnDueNotificationWorker.runOneTimeCheck(getApplication())
            _scanFeedbackMessage.value = "Background worker check initiated: Return dates analyzed!"
        } catch (e: Exception) {
            _scanFeedbackMessage.value = "Worker execution failed: ${e.message}"
        }
    }

    fun toggleFavorite(item: LendItem) {
        viewModelScope.launch {
            val willBeSaved = !item.isFavorite
            repository.toggleFavorite(item.id, item.isFavorite)
            if (_selectedItemForDetail.value?.id == item.id) {
                _selectedItemForDetail.value = item.copy(isFavorite = willBeSaved)
            }
            if (willBeSaved) {
                _scanFeedbackMessage.value = "❤️ Saved '${item.title}' to your Wishlist!"
            } else {
                _scanFeedbackMessage.value = "Removed '${item.title}' from your Wishlist."
            }
        }
    }

    fun saveToWishlistWithCustomNote(
        item: LendItem,
        note: String,
        targetDate: String,
        notifyWhenAvailable: Boolean
    ) {
        viewModelScope.launch {
            repository.saveToWishlistWithNote(item, note, targetDate, notifyWhenAvailable)
            if (_selectedItemForDetail.value?.id == item.id) {
                _selectedItemForDetail.value = item.copy(isFavorite = true)
            }
            _scanFeedbackMessage.value = "❤️ Added '${item.title}' to Wishlist with personal note!"
            _itemForWishlistNote.value = null
        }
    }

    fun updateWishlistDetails(itemId: Long, note: String, targetDate: String, notify: Boolean) {
        viewModelScope.launch {
            repository.updateWishlistDetails(itemId, note, targetDate, notify)
            _scanFeedbackMessage.value = "Wishlist note updated in local storage."
        }
    }

    fun removeFromWishlist(item: LendItem) {
        viewModelScope.launch {
            repository.removeFromWishlist(item.id)
            if (_selectedItemForDetail.value?.id == item.id) {
                _selectedItemForDetail.value = item.copy(isFavorite = false)
            }
            _scanFeedbackMessage.value = "Removed '${item.title}' from Wishlist."
        }
    }

    fun confirmBorrowRequest(
        item: LendItem,
        note: String,
        depositAmount: Double,
        dailyFee: Double,
        pickupOption: String
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val returnTime = now + (86400000L * 2) // 2 days standard
            repository.createBorrowRequest(
                item = item,
                note = note,
                startDate = now,
                endDate = returnTime,
                depositPaid = depositAmount,
                feePaid = dailyFee,
                pickupOption = pickupOption
            )
            _itemForBorrowPayment.value = null
            _selectedItemForDetail.value = null
        }
    }

    fun markItemReturned(request: BorrowRequest) {
        viewModelScope.launch {
            repository.returnItem(request)
            _itemForReview.value = request // Prompt review right after returning!
        }
    }

    fun approveRequest(request: BorrowRequest) {
        viewModelScope.launch {
            repository.approveRequest(request)
        }
    }

    fun submitReview(
        itemId: Long,
        itemTitle: String,
        targetUser: String,
        rating: Int,
        comment: String,
        tags: String
    ) {
        viewModelScope.launch {
            repository.submitReview(itemId, itemTitle, targetUser, rating, comment, tags)
            _itemForReview.value = null
        }
    }

    fun sendMessage(conversationId: String, itemId: Long, itemTitle: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(conversationId, itemId, itemTitle, text)
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun updatePrivacySettings(
        exactAddressHidden: Boolean,
        fuzzyRadiusKm: Double,
        showOnLeaderboard: Boolean,
        phoneVisibleToConfirmedOnly: Boolean
    ) {
        viewModelScope.launch {
            val current = userProfile.value
            repository.updateUserProfile(
                current.copy(
                    exactAddressHidden = exactAddressHidden,
                    fuzzyRadiusKm = fuzzyRadiusKm,
                    showOnLeaderboard = showOnLeaderboard,
                    phoneVisibleToConfirmedOnly = phoneVisibleToConfirmedOnly
                )
            )
        }
    }

    fun updateThemeSetting(themeSetting: String) {
        viewModelScope.launch {
            val current = userProfile.value
            repository.updateUserProfile(current.copy(darkModeSetting = themeSetting))
        }
    }

    fun addNewCommunityItem(
        title: String,
        description: String,
        category: String,
        deposit: Double,
        condition: String,
        wasteKg: Double,
        photoUri: String? = null,
        maxBorrowDurationDays: Int = 7,
        availableFromMillis: Long? = null,
        availableUntilMillis: Long? = null,
        pickupLocationName: String? = null,
        pickupAddress: String? = null,
        pickupDistanceMiles: Double = 0.2,
        isFuzzyPrivacy: Boolean = true,
        pickupNotes: String? = null
    ) {
        viewModelScope.launch {
            val locationLabel = pickupLocationName?.ifBlank { null } ?: userProfile.value.neighborhood
            val newItem = LendItem(
                title = title,
                description = description,
                category = category,
                ownerId = "user_alex",
                ownerName = userProfile.value.name,
                ownerRating = 5.0f,
                ownerReviewsCount = 0,
                ownerAvatarSeed = "AG",
                distanceMiles = pickupDistanceMiles,
                neighborhood = locationLabel,
                condition = condition,
                depositAmount = deposit,
                dailyFee = 0.0,
                estimatedWasteSavedKg = wasteKg,
                estimatedDollarSaved = wasteKg * 10.0,
                estimatedCo2SavedKg = wasteKg * 2.2,
                availability = "AVAILABLE_NOW",
                iconCategory = when (category) {
                    "Lawn & Garden" -> "GARDEN"
                    "Kitchen & Cooking" -> "KITCHEN"
                    "Camping & Outdoors" -> "CAMPING"
                    "Cleaning Gear" -> "CLEANING"
                    "Party & Events" -> "PARTY"
                    else -> "TOOLS"
                },
                photoUri = photoUri,
                maxBorrowDurationDays = maxBorrowDurationDays,
                availableFromMillis = availableFromMillis,
                availableUntilMillis = availableUntilMillis,
                pickupLocationName = locationLabel,
                pickupAddress = pickupAddress,
                pickupNotes = pickupNotes,
                isFuzzyPrivacy = isFuzzyPrivacy
            )
            repository.addNewItem(newItem)
            _showAddListingDialog.value = false
            _scanFeedbackMessage.value = "🎉 '${title}' was successfully added to the neighborhood sharing pool!"
            if (_currentScreen.value == Screen.ADD_ITEM) {
                _currentScreen.value = Screen.HOME
            }
        }
    }
}
