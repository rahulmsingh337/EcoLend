package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.EcoLendViewModel
import com.example.ui.Screen
import com.example.ui.components.AddListingDialog
import com.example.ui.components.CameraPhotoCaptureDialog
import com.example.ui.components.NotificationCenterDialog
import com.example.ui.components.OnboardingTutorialDialog
import com.example.ui.components.PaymentSheetDialog
import com.example.ui.components.QrCodeScannerDialog
import com.example.ui.components.ReviewDialog
import com.example.ui.components.ShareImpactDialog
import com.example.ui.components.SupportChatDialog
import com.example.ui.components.ToolQrTagDialog
import com.example.ui.components.WishlistNoteDialog
import com.example.ui.screens.AddItemFormScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExploreMapScreen
import com.example.ui.screens.GamificationScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ItemDetailScreen
import com.example.ui.screens.MessagesScreen
import com.example.ui.screens.ProfilePrivacyScreen
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoLendTheme

class MainActivity : ComponentActivity() {

    private val viewModel: EcoLendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userProfile by viewModel.userProfile.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDarkTheme = when (userProfile.darkModeSetting) {
                "DARK" -> true
                "LIGHT" -> false
                else -> systemDark
            }

            EcoLendTheme(darkTheme = isDarkTheme) {
                EcoLendApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoLendApp(viewModel: EcoLendViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val notifications by viewModel.allNotifications.collectAsState()
    val unreadNotifs = notifications.count { !it.isRead }

    // Dialog states
    val selectedItemForDetail by viewModel.selectedItemForDetail.collectAsState()
    val itemForBorrowPayment by viewModel.itemForBorrowPayment.collectAsState()
    val itemForReview by viewModel.itemForReview.collectAsState()
    val showSupportChat by viewModel.showSupportChat.collectAsState()
    val showNotificationTray by viewModel.showNotificationTray.collectAsState()
    val showOnboarding by viewModel.showOnboarding.collectAsState()
    val showShareImpact by viewModel.showShareImpactDialog.collectAsState()
    val showAddListing by viewModel.showAddListingDialog.collectAsState()
    val showQrScanner by viewModel.showQrScanner.collectAsState()
    val showItemQrTag by viewModel.showItemQrTag.collectAsState()
    val itemForPhotoCapture by viewModel.itemForPhotoCapture.collectAsState()
    val itemForWishlistNote by viewModel.itemForWishlistNote.collectAsState()
    val wishlistEntries by viewModel.wishlistEntries.collectAsState()
    val scanFeedbackMessage by viewModel.scanFeedbackMessage.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    // Handle back button when on sub-screens or in detail view
    BackHandler(enabled = selectedItemForDetail != null || currentScreen != Screen.HOME) {
        if (selectedItemForDetail != null) {
            viewModel.closeItemDetail()
        } else if (currentScreen != Screen.HOME) {
            viewModel.navigateTo(Screen.HOME)
        }
    }

    if (selectedItemForDetail != null) {
        ItemDetailScreen(
            item = selectedItemForDetail!!,
            viewModel = viewModel,
            onBack = { viewModel.closeItemDetail() }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EcoLend",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        // Offline Mode Indicator / Switcher
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isOffline) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .testTag("top_bar_offline_badge")
                        ) {
                            IconButton(
                                onClick = { viewModel.toggleOfflineMode() },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.Wifi,
                                    contentDescription = "Toggle Network Status",
                                    tint = if (isOffline) Color(0xFFE65100) else EcoGreenPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // QR Scanner Button
                        IconButton(
                            onClick = { viewModel.toggleQrScanner(true) },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("top_bar_qr_scanner_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan Tool QR Tag",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Help / Support
                        IconButton(
                            onClick = { viewModel.toggleSupportChat(true) },
                            modifier = Modifier.size(36.dp).testTag("top_bar_support_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Support Chat",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Notifications with Badge
                        IconButton(
                            onClick = { viewModel.toggleNotificationTray(true) },
                            modifier = Modifier.size(36.dp).testTag("top_bar_notifications_btn")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotifs > 0) {
                                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                            Text("$unreadNotifs", fontSize = 9.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    val navItems = listOf(
                        Triple(Screen.HOME, "Home", Icons.Default.Home),
                        Triple(Screen.MAP, "Radar Map", Icons.Default.Map),
                        Triple(Screen.DASHBOARD, "Hub", Icons.Default.Dashboard),
                        Triple(Screen.MESSAGES, "Chat", Icons.Default.Chat),
                        Triple(Screen.GAMIFICATION, "Karma", Icons.Default.EmojiEvents),
                        Triple(Screen.PROFILE, "Profile", Icons.Default.Person)
                    )

                    navItems.forEach { (screen, label, icon) ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(screen) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_item_${label.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    val feedback = scanFeedbackMessage
                    if (feedback != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .testTag("scan_feedback_banner"),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Eco,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = feedback,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.dismissScanFeedback() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (currentScreen) {
                            Screen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onItemClick = { viewModel.openItemDetail(it) }
                            )
                            Screen.MAP -> ExploreMapScreen(
                                viewModel = viewModel,
                                onItemClick = { viewModel.openItemDetail(it) }
                            )
                            Screen.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onItemClick = { viewModel.openItemDetail(it) }
                            )
                            Screen.MESSAGES -> MessagesScreen(viewModel = viewModel)
                            Screen.GAMIFICATION -> GamificationScreen(viewModel = viewModel)
                            Screen.PROFILE -> ProfilePrivacyScreen(viewModel = viewModel)
                            Screen.ADD_ITEM -> AddItemFormScreen(
                                viewModel = viewModel,
                                onBack = { viewModel.navigateTo(Screen.HOME) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    val itemToBorrow = itemForBorrowPayment
    if (itemToBorrow != null) {
        PaymentSheetDialog(
            item = itemToBorrow,
            onDismiss = { viewModel.closeBorrowFlow() },
            onConfirm = { note, deposit, fee, pickup ->
                viewModel.confirmBorrowRequest(itemToBorrow, note, deposit, fee, pickup)
            }
        )
    }

    val reqToReview = itemForReview
    if (reqToReview != null) {
        ReviewDialog(
            request = reqToReview,
            onDismiss = { viewModel.closeReviewDialog() },
            onSubmit = { itemId, itemTitle, targetUser, rating, comment, tags ->
                viewModel.submitReview(itemId, itemTitle, targetUser, rating, comment, tags)
            }
        )
    }

    if (showSupportChat) {
        SupportChatDialog(onDismiss = { viewModel.toggleSupportChat(false) })
    }

    if (showNotificationTray) {
        NotificationCenterDialog(
            notifications = notifications,
            isOffline = isOffline,
            onDismiss = { viewModel.toggleNotificationTray(false) },
            onMarkAllRead = { viewModel.markAllNotificationsRead() },
            onMarkRead = { viewModel.markNotificationRead(it) },
            onTriggerDueDatesCheck = { viewModel.triggerDueDatesCheck() }
        )
    }

    if (showOnboarding) {
        OnboardingTutorialDialog(onDismiss = { viewModel.toggleOnboarding(false) })
    }

    if (showShareImpact) {
        ShareImpactDialog(
            userProfile = userProfile,
            onDismiss = { viewModel.toggleShareImpactDialog(false) }
        )
    }

    if (showAddListing) {
        AddListingDialog(
            onDismiss = { viewModel.toggleAddListingDialog(false) },
            onSubmit = { title, desc, cat, deposit, cond, waste, photo ->
                viewModel.addNewCommunityItem(title, desc, cat, deposit, cond, waste, photo)
            }
        )
    }

    if (showQrScanner) {
        QrCodeScannerDialog(
            availableItems = allItems,
            onDismiss = { viewModel.toggleQrScanner(false) },
            onCodeScanned = { code ->
                viewModel.onQrCodeScanned(code)
            }
        )
    }

    val itemForTag = showItemQrTag
    if (itemForTag != null) {
        ToolQrTagDialog(
            item = itemForTag,
            onDismiss = { viewModel.toggleItemQrTag(null) }
        )
    }

    val itemToPhotograph = itemForPhotoCapture
    if (itemToPhotograph != null) {
        CameraPhotoCaptureDialog(
            item = itemToPhotograph,
            onDismiss = { viewModel.closeCameraForPhotoCapture() },
            onPhotoCaptured = { photoPath ->
                viewModel.saveCapturedPhoto(itemToPhotograph, photoPath)
            }
        )
    }

    val itemToWishlist = itemForWishlistNote
    if (itemToWishlist != null) {
        val existingEntry = wishlistEntries.find { it.itemId == itemToWishlist.id }
        WishlistNoteDialog(
            item = itemToWishlist,
            existingEntry = existingEntry,
            onDismiss = { viewModel.closeWishlistNoteDialog() },
            onSave = { note, targetDate, notify ->
                viewModel.saveToWishlistWithCustomNote(itemToWishlist, note, targetDate, notify)
            },
            onRemove = {
                viewModel.removeFromWishlist(itemToWishlist)
                viewModel.closeWishlistNoteDialog()
            }
        )
    }
}
