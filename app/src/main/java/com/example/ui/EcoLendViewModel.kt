package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.EcoLendDatabase
import com.example.data.model.AppNotification
import com.example.data.model.BorrowRequest
import com.example.data.model.ChatMessage
import com.example.data.model.BadgeCategory
import com.example.data.model.BadgeMilestone
import com.example.data.model.BadgeTier
import com.example.data.model.KarmaBadge
import com.example.data.model.KarmaBreakdown
import com.example.data.model.LendItem
import com.example.data.model.Review
import com.example.data.model.UserProfile
import com.example.data.model.WishlistEntry
import com.example.data.model.AvailabilityAlert
import com.example.data.repository.EcoLendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
    ADD_ITEM,
    TRANSACTION_HISTORY
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

    val isAuthenticated: StateFlow<Boolean> = userProfile
        .map { it.isAuthenticated }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val authProvider: StateFlow<String> = userProfile
        .map { it.authProvider }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "GUEST")

    val allMessages: StateFlow<List<ChatMessage>> = repository.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReviews: StateFlow<List<Review>> = repository.getAllReviews()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Community Karma & Progression State
    val karmaBreakdown: StateFlow<KarmaBreakdown> = combine(
        userProfile,
        allRequests,
        allReviews
    ) { profile, requests, reviews ->
        calculateKarmaBreakdown(profile, requests, reviews)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        calculateKarmaBreakdown(UserProfile(), emptyList(), emptyList())
    )

    val karmaBadges: StateFlow<List<KarmaBadge>> = combine(
        userProfile,
        allRequests,
        allReviews,
        allItems
    ) { profile, requests, reviews, items ->
        calculateKarmaBadges(profile, requests, reviews, items)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        calculateKarmaBadges(UserProfile(), emptyList(), emptyList(), emptyList())
    )

    private val _selectedBadgeForDetail = MutableStateFlow<KarmaBadge?>(null)
    val selectedBadgeForDetail: StateFlow<KarmaBadge?> = _selectedBadgeForDetail.asStateFlow()

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

    // --- Availability Alerts System ---
    val availabilityAlerts: StateFlow<List<AvailabilityAlert>> = repository.getAllAvailabilityAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showCreateAlertDialog = MutableStateFlow(false)
    val showCreateAlertDialog: StateFlow<Boolean> = _showCreateAlertDialog.asStateFlow()

    private val _createAlertInitialKeyword = MutableStateFlow("")
    val createAlertInitialKeyword: StateFlow<String> = _createAlertInitialKeyword.asStateFlow()

    private val _createAlertInitialCategory = MutableStateFlow("All")
    val createAlertInitialCategory: StateFlow<String> = _createAlertInitialCategory.asStateFlow()

    fun openCreateAlertDialog(initialKeyword: String = "", initialCategory: String = "All") {
        if (userProfile.value.isAuthenticated) {
            _createAlertInitialKeyword.value = initialKeyword
            _createAlertInitialCategory.value = initialCategory
            _showCreateAlertDialog.value = true
        } else {
            executeGatedAction("Sign in to save and manage neighborhood availability alerts") {
                _createAlertInitialKeyword.value = initialKeyword
                _createAlertInitialCategory.value = initialCategory
                _showCreateAlertDialog.value = true
            }
        }
    }

    fun closeCreateAlertDialog() {
        _showCreateAlertDialog.value = false
    }

    fun saveAvailabilityAlert(keywords: String, category: String, maxDistanceMiles: Double) {
        viewModelScope.launch {
            repository.saveAvailabilityAlert(keywords, category, maxDistanceMiles)
            _showCreateAlertDialog.value = false
        }
    }

    fun toggleAlertActive(alertId: Long, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleAlertActive(alertId, isActive)
        }
    }

    fun deleteAvailabilityAlert(alertId: Long) {
        viewModelScope.launch {
            repository.deleteAvailabilityAlert(alertId)
        }
    }

    fun testTriggerAlert(alert: AvailabilityAlert) {
        viewModelScope.launch {
            repository.testTriggerAlert(alert)
        }
    }

    // --- Auth Gate & Management State ---
    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    private val _authPromptMessage = MutableStateFlow("Sign in to access community borrowing & lending")
    val authPromptMessage: StateFlow<String> = _authPromptMessage.asStateFlow()

    private val _showAccountDetailsDialog = MutableStateFlow(false)
    val showAccountDetailsDialog: StateFlow<Boolean> = _showAccountDetailsDialog.asStateFlow()

    private var pendingGatedAction: (() -> Unit)? = null

    val pendingOtpCode: Flow<String?> = repository.pendingOtpCode

    fun openAuthDialog(prompt: String = "Sign in to access community borrowing & lending") {
        _authPromptMessage.value = prompt
        _showAuthDialog.value = true
    }

    fun closeAuthDialog() {
        _showAuthDialog.value = false
        pendingGatedAction = null
    }

    fun executeGatedAction(prompt: String, action: () -> Unit) {
        if (userProfile.value.isAuthenticated) {
            action()
        } else {
            pendingGatedAction = action
            _authPromptMessage.value = prompt
            _showAuthDialog.value = true
        }
    }

    fun openAddListing() {
        executeGatedAction("Sign in to list equipment in your neighborhood library") {
            navigateTo(Screen.ADD_ITEM)
        }
    }

    fun openAccountDetailsDialog() {
        _showAccountDetailsDialog.value = true
    }

    fun closeAccountDetailsDialog() {
        _showAccountDetailsDialog.value = false
    }

    fun sendMobileOtp(phoneNumber: String, onCodeSent: (String) -> Unit = {}) {
        viewModelScope.launch {
            val code = repository.sendMobileOtp(phoneNumber)
            onCodeSent(code)
        }
    }

    fun verifyMobileOtp(phoneNumber: String, code: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.verifyMobileOtp(phoneNumber, code)
            if (result.isSuccess) {
                _showAuthDialog.value = false
                val action = pendingGatedAction
                pendingGatedAction = null
                action?.invoke()
                onResult(true, "Phone verified successfully!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Verification failed")
            }
        }
    }

    fun signInWithGoogle(idToken: String, displayName: String?, email: String?, photoUrl: String?, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signInWithGoogleCredential(idToken, displayName, email, photoUrl)
            if (result.isSuccess) {
                _showAuthDialog.value = false
                val action = pendingGatedAction
                pendingGatedAction = null
                action?.invoke()
                onResult(true, "Signed in successfully!")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Sign-in failed")
            }
        }
    }

    fun signInWithDevGoogle(accountName: String, accountEmail: String) {
        viewModelScope.launch {
            val result = repository.signInWithSimulatedGoogle(accountName, accountEmail)
            if (result.isSuccess) {
                _showAuthDialog.value = false
                val action = pendingGatedAction
                pendingGatedAction = null
                action?.invoke()
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOutUser()
            _showAccountDetailsDialog.value = false
        }
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
        if (userProfile.value.isAuthenticated) {
            _itemForBorrowPayment.value = item
        } else {
            executeGatedAction("Sign in to complete your borrow request & authorize deposit hold") {
                _itemForBorrowPayment.value = item
            }
        }
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
        if (userProfile.value.isAuthenticated) {
            _activeChatConversationId.value = conversationId
            _currentScreen.value = Screen.MESSAGES
        } else {
            executeGatedAction("Sign in to message the tool owner") {
                _activeChatConversationId.value = conversationId
                _currentScreen.value = Screen.MESSAGES
            }
        }
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

    fun toggleLoanReminders(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleLoanReminders(enabled)
        }
    }

    fun simulate24HourLoanReminder() {
        viewModelScope.launch {
            repository.simulate24HourLoanReminder()
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

    // --- Karma Progression Actions ---

    fun selectBadge(badge: KarmaBadge) {
        _selectedBadgeForDetail.value = badge
    }

    fun closeBadgeDetail() {
        _selectedBadgeForDetail.value = null
    }

    fun simulateKarmaBonus(points: Int = 50) {
        viewModelScope.launch {
            val profile = userProfile.value
            val newScore = profile.karmaScore + points
            repository.updateUserProfile(profile.copy(karmaScore = newScore))
            _scanFeedbackMessage.value = "🎉 Earned +$points Karma points! Reputation increased to $newScore."
            // Update currently inspected badge if modal is open
            _selectedBadgeForDetail.value?.let { current ->
                val updatedBadges = calculateKarmaBadges(
                    profile.copy(karmaScore = newScore),
                    allRequests.value,
                    allReviews.value,
                    allItems.value
                )
                _selectedBadgeForDetail.value = updatedBadges.firstOrNull { it.id == current.id }
            }
        }
    }

    // --- Private Calculation Helpers ---

    private fun calculateKarmaBreakdown(
        profile: UserProfile,
        requests: List<BorrowRequest>,
        reviews: List<Review>
    ): KarmaBreakdown {
        val total = profile.karmaScore
        val completedReturns = requests.count { it.status == "RETURNED" }
        // Baseline 5 completed returns + dynamic returned requests
        val totalReturnsCount = 5 + completedReturns
        val returnPoints = totalReturnsCount * 50

        // Baseline 4 positive reviews + dynamic reviews with rating >= 4
        val positiveReviews = reviews.count { it.rating >= 4 }
        val totalReviewsCount = 4 + positiveReviews
        val feedbackPoints = totalReviewsCount * 25

        // Remainder represents community trust bonus (porch pickup safety, verification)
        val trustBonus = maxOf(20, total - returnPoints - feedbackPoints)

        // Tiers for total score
        val tier = when {
            total >= 600 -> BadgeTier.GOLD
            total >= 300 -> BadgeTier.SILVER
            total >= 100 -> BadgeTier.BRONZE
            else -> BadgeTier.NONE
        }

        val rankTitle = when {
            total >= 1000 -> "Platinum Steward"
            total >= 600 -> "Gold Guardian"
            total >= 300 -> "Silver Steward"
            total >= 100 -> "Bronze Neighbor"
            else -> "Community Starter"
        }

        val nextRankTitle = when {
            total >= 1000 -> "Diamond Ambassador"
            total >= 600 -> "Platinum Steward"
            total >= 300 -> "Gold Guardian"
            total >= 100 -> "Silver Steward"
            else -> "Bronze Neighbor"
        }

        val (floor, ceiling) = when {
            total >= 1000 -> Pair(1000, 1500)
            total >= 600 -> Pair(600, 1000)
            total >= 300 -> Pair(300, 600)
            total >= 100 -> Pair(100, 300)
            else -> Pair(0, 100)
        }

        val pointsToNext = maxOf(0, ceiling - total)
        val progress = if (ceiling > floor) {
            ((total - floor).toFloat() / (ceiling - floor).toFloat()).coerceIn(0f, 1f)
        } else 1f

        val verifiedFlair = when (tier) {
            BadgeTier.GOLD -> "Gold Guardian • Verified Community Pillar"
            BadgeTier.SILVER -> "Silver Steward • Verified Community Lender"
            BadgeTier.BRONZE -> "Bronze Neighbor • Trusted Member"
            BadgeTier.NONE -> "Community Neighbor"
        }

        return KarmaBreakdown(
            totalScore = total,
            returnPoints = returnPoints,
            feedbackPoints = feedbackPoints,
            trustBonusPoints = trustBonus,
            currentRankTitle = rankTitle,
            currentTier = tier,
            nextRankTitle = nextRankTitle,
            nextRankThreshold = ceiling,
            currentTierFloor = floor,
            pointsToNextRank = pointsToNext,
            progressToNextRank = progress,
            successfulReturnsCount = totalReturnsCount,
            positiveReviewsCount = totalReviewsCount,
            verifiedTrustFlair = verifiedFlair
        )
    }

    private fun calculateKarmaBadges(
        profile: UserProfile,
        requests: List<BorrowRequest>,
        reviews: List<Review>,
        items: List<LendItem>
    ): List<KarmaBadge> {
        val totalKarma = profile.karmaScore
        val completedReturns = 5 + requests.count { it.status == "RETURNED" }
        val userItemsCount = maxOf(items.count { it.ownerName == profile.name }, 5)
        val wasteKg = profile.wasteSavedKg.toInt()

        // 1. Community Hero (Reputation Track)
        val heroMilestones = listOf(
            BadgeMilestone(BadgeTier.BRONZE, 200, "Reach 200 Karma Points", "Community Welcomer flair on profile"),
            BadgeMilestone(BadgeTier.SILVER, 400, "Reach 400 Karma Points", "Verified Community Trust Shield"),
            BadgeMilestone(BadgeTier.GOLD, 750, "Reach 750 Karma Points", "Ambassador badge & priority equipment reserves")
        )
        val heroTier = when {
            totalKarma >= 750 -> BadgeTier.GOLD
            totalKarma >= 400 -> BadgeTier.SILVER
            totalKarma >= 200 -> BadgeTier.BRONZE
            else -> BadgeTier.NONE
        }
        val heroNextTier = when (heroTier) {
            BadgeTier.NONE -> BadgeTier.BRONZE
            BadgeTier.BRONZE -> BadgeTier.SILVER
            BadgeTier.SILVER -> BadgeTier.GOLD
            BadgeTier.GOLD -> null
        }
        val heroNextThreshold = when (heroTier) {
            BadgeTier.NONE -> 200
            BadgeTier.BRONZE -> 400
            BadgeTier.SILVER -> 750
            BadgeTier.GOLD -> 750
        }
        val heroProgress = if (heroNextTier != null) {
            (totalKarma.toFloat() / heroNextThreshold.toFloat()).coerceIn(0f, 1f)
        } else 1f

        val communityHeroBadge = KarmaBadge(
            id = "community_hero",
            title = "Community Hero",
            category = BadgeCategory.COMMUNITY,
            iconEmoji = "🏆",
            description = "Earned through exemplary neighborly conduct, verified on-time returns, and glowing community reviews.",
            currentTier = heroTier,
            currentValue = totalKarma,
            unit = "pts",
            nextTier = heroNextTier,
            currentTierThreshold = if (heroTier == BadgeTier.SILVER) 400 else if (heroTier == BadgeTier.BRONZE) 200 else 0,
            nextTierThreshold = heroNextThreshold,
            progressFraction = heroProgress,
            activePerk = when (heroTier) {
                BadgeTier.GOLD -> "Ambassador status & zero-deposit borrowing privileges"
                BadgeTier.SILVER -> "Verified Community Trust Shield displayed on listings"
                BadgeTier.BRONZE -> "Community Welcomer flair on profile"
                BadgeTier.NONE -> "Unlock at 200 Karma points"
            },
            nextPerk = when (heroTier) {
                BadgeTier.NONE -> "Unlock Community Welcomer flair at 200 pts"
                BadgeTier.BRONZE -> "Unlock Verified Trust Shield at 400 pts"
                BadgeTier.SILVER -> "Unlock Ambassador status at 750 pts"
                BadgeTier.GOLD -> null
            },
            milestones = heroMilestones,
            motivationalTip = "Keep returning borrowed equipment on time (+50 pts) and leaving reviews (+25 pts) to advance to Gold!"
        )

        // 2. Top Lender (Lending Track)
        val lenderMilestones = listOf(
            BadgeMilestone(BadgeTier.BRONZE, 2, "Share 2 equipment listings with neighbors", "Lender Spotlight in search results"),
            BadgeMilestone(BadgeTier.SILVER, 6, "Share 6 equipment listings with neighbors", "Verified Top Lender badge on item cards"),
            BadgeMilestone(BadgeTier.GOLD, 12, "Share 12 equipment listings with neighbors", "Zero-deposit guarantee coverage eligible")
        )
        val lenderTier = when {
            userItemsCount >= 12 -> BadgeTier.GOLD
            userItemsCount >= 6 -> BadgeTier.SILVER
            userItemsCount >= 2 -> BadgeTier.BRONZE
            else -> BadgeTier.NONE
        }
        val lenderNextTier = when (lenderTier) {
            BadgeTier.NONE -> BadgeTier.BRONZE
            BadgeTier.BRONZE -> BadgeTier.SILVER
            BadgeTier.SILVER -> BadgeTier.GOLD
            BadgeTier.GOLD -> null
        }
        val lenderNextThreshold = when (lenderTier) {
            BadgeTier.NONE -> 2
            BadgeTier.BRONZE -> 6
            BadgeTier.SILVER -> 12
            BadgeTier.GOLD -> 12
        }
        val lenderProgress = if (lenderNextTier != null) {
            (userItemsCount.toFloat() / lenderNextThreshold.toFloat()).coerceIn(0f, 1f)
        } else 1f

        val topLenderBadge = KarmaBadge(
            id = "top_lender",
            title = "Top Lender",
            category = BadgeCategory.LENDING,
            iconEmoji = "🛠️",
            description = "Lending high-demand tools and equipment to neighbors with pristine upkeep and zero disputes.",
            currentTier = lenderTier,
            currentValue = userItemsCount,
            unit = "tools shared",
            nextTier = lenderNextTier,
            currentTierThreshold = if (lenderTier == BadgeTier.BRONZE) 2 else 0,
            nextTierThreshold = lenderNextThreshold,
            progressFraction = lenderProgress,
            activePerk = when (lenderTier) {
                BadgeTier.GOLD -> "Zero-deposit guarantee & VIP lender badge"
                BadgeTier.SILVER -> "Verified Top Lender badge on all listings"
                BadgeTier.BRONZE -> "Lender Spotlight in search results"
                BadgeTier.NONE -> "Unlock at 2 tools shared"
            },
            nextPerk = when (lenderTier) {
                BadgeTier.NONE -> "Unlock Lender Spotlight at 2 tools shared"
                BadgeTier.BRONZE -> "Unlock Verified Top Lender badge at 6 tools shared"
                BadgeTier.SILVER -> "Unlock Zero-deposit guarantee at 12 tools shared"
                BadgeTier.GOLD -> null
            },
            milestones = lenderMilestones,
            motivationalTip = "Add 1 more tool or kitchen device to your community catalog to reach Silver Top Lender!"
        )

        // 3. Reliable Borrower (Borrowing Track)
        val borrowerMilestones = listOf(
            BadgeMilestone(BadgeTier.BRONZE, 2, "Complete 2 on-time returns", "Fast-track loan approval from neighbors"),
            BadgeMilestone(BadgeTier.SILVER, 6, "Complete 6 on-time returns", "Trusted Borrower flair on loan requests"),
            BadgeMilestone(BadgeTier.GOLD, 12, "Complete 12 on-time returns", "VIP Borrower status with 50% reduced deposit holds")
        )
        val borrowerTier = when {
            completedReturns >= 12 -> BadgeTier.GOLD
            completedReturns >= 6 -> BadgeTier.SILVER
            completedReturns >= 2 -> BadgeTier.BRONZE
            else -> BadgeTier.NONE
        }
        val borrowerNextTier = when (borrowerTier) {
            BadgeTier.NONE -> BadgeTier.BRONZE
            BadgeTier.BRONZE -> BadgeTier.SILVER
            BadgeTier.SILVER -> BadgeTier.GOLD
            BadgeTier.GOLD -> null
        }
        val borrowerNextThreshold = when (borrowerTier) {
            BadgeTier.NONE -> 2
            BadgeTier.BRONZE -> 6
            BadgeTier.SILVER -> 12
            BadgeTier.GOLD -> 12
        }
        val borrowerProgress = if (borrowerNextTier != null) {
            (completedReturns.toFloat() / borrowerNextThreshold.toFloat()).coerceIn(0f, 1f)
        } else 1f

        val reliableBorrowerBadge = KarmaBadge(
            id = "reliable_borrower",
            title = "Reliable Borrower",
            category = BadgeCategory.BORROWING,
            iconEmoji = "🤝",
            description = "Consistently returning borrowed gear in clean, working condition within the reserved loan window.",
            currentTier = borrowerTier,
            currentValue = completedReturns,
            unit = "verified returns",
            nextTier = borrowerNextTier,
            currentTierThreshold = if (borrowerTier == BadgeTier.SILVER) 6 else 2,
            nextTierThreshold = borrowerNextThreshold,
            progressFraction = borrowerProgress,
            activePerk = when (borrowerTier) {
                BadgeTier.GOLD -> "VIP Borrower status: 50% reduced deposit holds"
                BadgeTier.SILVER -> "Trusted Borrower flair shown on all loan requests"
                BadgeTier.BRONZE -> "Fast-track loan approval from neighbors"
                BadgeTier.NONE -> "Unlock at 2 on-time returns"
            },
            nextPerk = when (borrowerTier) {
                BadgeTier.NONE -> "Unlock Fast-track loan approval at 2 returns"
                BadgeTier.BRONZE -> "Unlock Trusted Borrower flair at 6 returns"
                BadgeTier.SILVER -> "Unlock VIP Borrower status at 12 returns"
                BadgeTier.GOLD -> null
            },
            milestones = borrowerMilestones,
            motivationalTip = "Always check in on time and snap a return photo to build trust with lenders!"
        )

        // 4. Zero Waste Champion (Eco Impact Track)
        val ecoMilestones = listOf(
            BadgeMilestone(BadgeTier.BRONZE, 15, "Divert 15 kg of manufacturing waste", "Eco Seedling profile badge"),
            BadgeMilestone(BadgeTier.SILVER, 40, "Divert 40 kg of manufacturing waste", "District Eco-Champion profile aura"),
            BadgeMilestone(BadgeTier.GOLD, 80, "Divert 80 kg of manufacturing waste", "Community Hall of Fame recognition")
        )
        val ecoTier = when {
            wasteKg >= 80 -> BadgeTier.GOLD
            wasteKg >= 40 -> BadgeTier.SILVER
            wasteKg >= 15 -> BadgeTier.BRONZE
            else -> BadgeTier.NONE
        }
        val ecoNextTier = when (ecoTier) {
            BadgeTier.NONE -> BadgeTier.BRONZE
            BadgeTier.BRONZE -> BadgeTier.SILVER
            BadgeTier.SILVER -> BadgeTier.GOLD
            BadgeTier.GOLD -> null
        }
        val ecoNextThreshold = when (ecoTier) {
            BadgeTier.NONE -> 15
            BadgeTier.BRONZE -> 40
            BadgeTier.SILVER -> 80
            BadgeTier.GOLD -> 80
        }
        val ecoProgress = if (ecoNextTier != null) {
            (wasteKg.toFloat() / ecoNextThreshold.toFloat()).coerceIn(0f, 1f)
        } else 1f

        val zeroWasteBadge = KarmaBadge(
            id = "zero_waste_champion",
            title = "Zero Waste Champion",
            category = BadgeCategory.ECO_IMPACT,
            iconEmoji = "🌱",
            description = "Preventing single-use tool purchases, carbon emissions, and landfill waste through collective neighborhood reuse.",
            currentTier = ecoTier,
            currentValue = wasteKg,
            unit = "kg waste saved",
            nextTier = ecoNextTier,
            currentTierThreshold = if (ecoTier == BadgeTier.SILVER) 40 else 15,
            nextTierThreshold = ecoNextThreshold,
            progressFraction = ecoProgress,
            activePerk = when (ecoTier) {
                BadgeTier.GOLD -> "Community Hall of Fame recognition & golden tree flair"
                BadgeTier.SILVER -> "District Eco-Champion profile aura"
                BadgeTier.BRONZE -> "Eco Seedling profile badge"
                BadgeTier.NONE -> "Unlock at 15 kg saved"
            },
            nextPerk = when (ecoTier) {
                BadgeTier.NONE -> "Unlock Eco Seedling at 15 kg saved"
                BadgeTier.BRONZE -> "Unlock District Eco-Champion aura at 40 kg saved"
                BadgeTier.SILVER -> "Unlock Hall of Fame recognition at 80 kg saved"
                BadgeTier.GOLD -> null
            },
            milestones = ecoMilestones,
            motivationalTip = "Sharing heavy tools like pressure washers and mowers diverts the most manufacturing waste!"
        )

        // 5. Verified Neighbor (Authentication & Trust Track)
        val isAuth = profile.isAuthenticated
        val authMilestones = listOf(
            BadgeMilestone(BadgeTier.BRONZE, 1, "Browse community equipment as guest", "Guest explorer pass"),
            BadgeMilestone(BadgeTier.SILVER, 2, "Link Google or verify mobile phone", "Verified Trust shield on profile"),
            BadgeMilestone(BadgeTier.GOLD, 3, "Complete 2-way authenticated exchange", "Gold Verified Neighbor badge")
        )
        val authTier = if (isAuth) BadgeTier.GOLD else BadgeTier.NONE
        val verifiedNeighborBadge = KarmaBadge(
            id = "verified_neighbor",
            title = "Verified Neighbor",
            category = BadgeCategory.COMMUNITY,
            iconEmoji = "🛡️",
            description = "Verified community identity linked via Google Sign-In or Mobile Phone OTP.",
            currentTier = authTier,
            currentValue = if (isAuth) 1 else 0,
            unit = "verified account",
            nextTier = if (isAuth) null else BadgeTier.GOLD,
            currentTierThreshold = if (isAuth) 1 else 0,
            nextTierThreshold = 1,
            progressFraction = if (isAuth) 1f else 0f,
            activePerk = if (isAuth) "Verified Identity flair on all equipment listings & instant borrow clearance" else "Sign in to unlock verified custody & lend tools",
            nextPerk = if (isAuth) null else "Sign in with Google or Mobile OTP for verified status",
            milestones = authMilestones,
            motivationalTip = "Verified accounts enjoy zero friction pickup approvals from community tool owners!"
        )

        return listOf(communityHeroBadge, topLenderBadge, reliableBorrowerBadge, zeroWasteBadge, verifiedNeighborBadge)
    }
}

