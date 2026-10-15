package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import com.example.ui.EcoLendViewModel
import com.example.ui.Screen
import com.example.ui.components.AvailabilityAlertCard
import com.example.ui.components.BadgeDetailDialog
import com.example.ui.components.KarmaBadgeShowcase
import com.example.ui.components.KarmaScorecardView
import com.example.ui.components.VerifiedTrustBadge
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary

@Composable
fun ProfilePrivacyScreen(
    viewModel: EcoLendViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()
    val karmaBreakdown by viewModel.karmaBreakdown.collectAsState()
    val karmaBadges by viewModel.karmaBadges.collectAsState()
    val selectedBadgeForDetail by viewModel.selectedBadgeForDetail.collectAsState()
    val availabilityAlerts by viewModel.availabilityAlerts.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_privacy_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. User Header & Rating Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "AG",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userProfile.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = userProfile.neighborhood,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = EcoAmber,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${userProfile.rating} Rating",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = " • ${userProfile.exchangesCount} Exchanges",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        VerifiedTrustBadge(
                            tier = karmaBreakdown.currentTier,
                            label = karmaBreakdown.currentRankTitle
                        )
                    }
                }
            }
        }

        // Authentication & Identity Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_status_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (userProfile.isAuthenticated) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    } else {
                        Color(0xFFFFF8E1)
                    }
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (userProfile.isAuthenticated) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color(0xFFFFD54F)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (userProfile.isAuthenticated) MaterialTheme.colorScheme.primary else Color(0xFFE65100),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (userProfile.isAuthenticated) Icons.Default.Security else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (userProfile.isAuthenticated) "Verified Neighborhood Identity" else "Guest Neighbor Mode",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (userProfile.isAuthenticated) MaterialTheme.colorScheme.onSurface else Color(0xFF5D4037)
                            )
                            Text(
                                text = if (userProfile.isAuthenticated) {
                                    if (userProfile.authProvider == "GOOGLE") "Connected with Google (${if (userProfile.email.isNotBlank()) userProfile.email else "alex.green.oakridge@gmail.com"})"
                                    else "Verified via Mobile SMS (${if (userProfile.phoneNumber.isNotBlank()) userProfile.phoneNumber else "+1 (555) 382-9104"})"
                                } else {
                                    "Sign in to borrow, list items & build verified trust"
                                },
                                fontSize = 12.sp,
                                color = if (userProfile.isAuthenticated) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF795548)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (userProfile.isAuthenticated) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🛡️ 256-bit encrypted · Custody approved",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                            OutlinedButton(
                                onClick = { viewModel.signOut() },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("profile_sign_out_btn")
                            ) {
                                Text("Sign Out", fontSize = 12.sp)
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.openAuthDialog("Sign in with Google or Mobile OTP to unlock tool borrowing and lending") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("profile_sign_in_btn"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign In with Google or Mobile OTP", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Community Karma Scorecard Module
        item {
            KarmaScorecardView(
                breakdown = karmaBreakdown,
                onInspectBadges = {
                    karmaBadges.firstOrNull()?.let { viewModel.selectBadge(it) }
                }
            )
        }

        // Multi-tiered Badge Progression Showcase (Bronze, Silver, Gold)
        item {
            KarmaBadgeShowcase(
                badges = karmaBadges,
                onBadgeClick = { badge -> viewModel.selectBadge(badge) }
            )
        }

        // Transaction History Navigation Entry
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(Screen.TRANSACTION_HISTORY) }
                    .testTag("open_transaction_history_btn"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "Transaction History",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Transaction & Loan History 🧾",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "View digital exchange receipts, return statuses & borrower ratings",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Neighborhood Availability Alerts Manager Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("availability_alerts_manager_section"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Availability Alerts 🔔",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${availabilityAlerts.count { it.isActive }} active tool watch triggers",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.openCreateAlertDialog() },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("create_alert_header_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (availabilityAlerts.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No availability alerts set yet.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Create an alert for hard-to-find tools (e.g. pressure washer, rototiller) to get notified immediately when a neighbor lists one!",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            availabilityAlerts.forEach { alert ->
                                AvailabilityAlertCard(
                                    alert = alert,
                                    onToggleActive = { active -> viewModel.toggleAlertActive(alert.id, active) },
                                    onDelete = { viewModel.deleteAvailabilityAlert(alert.id) },
                                    onTestTrigger = { viewModel.testTriggerAlert(alert) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Automated 24-Hour Loan Return Reminders Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("loan_reminders_settings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF3E0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassBottom,
                                    contentDescription = null,
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "24-Hour Return Reminders ⏳",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Push alert 24h before loan period ends",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = userProfile.loanRemindersEnabled,
                            onCheckedChange = { viewModel.toggleLoanReminders(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFFE65100)
                            ),
                            modifier = Modifier.testTag("loan_reminders_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Automated reminder sends a notification with direct return and message owner actions 24 hours prior to deadline to release your security deposit and earn +50 Eco Karma.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.simulate24HourLoanReminder() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("simulate_24h_reminder_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test 24h Reminder 🔔", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.triggerDueDatesCheck() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("run_worker_now_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassBottom,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Deadlines", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 3. Network / Offline Mode Simulator Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.Wifi,
                            contentDescription = null,
                            tint = if (isOffline) Color(0xFFE65100) else EcoGreenPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isOffline) "Offline Mode (Active)" else "Network Status (Online)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isOffline) "Testing offline storage & local sync" else "Live updates & real-time notifications",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isOffline,
                        onCheckedChange = { viewModel.toggleOfflineMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE65100)),
                        modifier = Modifier.testTag("offline_mode_toggle")
                    )
                }
            }
        }

        // 3. Privacy Settings Section
        item {
            Text(
                text = "Detailed Community Privacy Settings 🛡️",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Setting 1: Fuzzy Address
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Fuzzy Neighborhood Radius (~500m)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Hides exact house number until a borrow is mutually accepted.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = userProfile.exactAddressHidden,
                            onCheckedChange = {
                                viewModel.updatePrivacySettings(
                                    exactAddressHidden = it,
                                    fuzzyRadiusKm = userProfile.fuzzyRadiusKm,
                                    showOnLeaderboard = userProfile.showOnLeaderboard,
                                    phoneVisibleToConfirmedOnly = userProfile.phoneVisibleToConfirmedOnly
                                )
                            },
                            modifier = Modifier.testTag("privacy_fuzzy_address_toggle")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Setting 2: Phone Visibility
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Phone Number Visibility",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Shared only with confirmed borrowers with active session.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = userProfile.phoneVisibleToConfirmedOnly,
                            onCheckedChange = {
                                viewModel.updatePrivacySettings(
                                    exactAddressHidden = userProfile.exactAddressHidden,
                                    fuzzyRadiusKm = userProfile.fuzzyRadiusKm,
                                    showOnLeaderboard = userProfile.showOnLeaderboard,
                                    phoneVisibleToConfirmedOnly = it
                                )
                            },
                            modifier = Modifier.testTag("privacy_phone_toggle")
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                    // Setting 3: Leaderboard Visibility
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "District Leaderboard Visibility",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Show your eco waste diverted on the neighborhood leaderboard.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = userProfile.showOnLeaderboard,
                            onCheckedChange = {
                                viewModel.updatePrivacySettings(
                                    exactAddressHidden = userProfile.exactAddressHidden,
                                    fuzzyRadiusKm = userProfile.fuzzyRadiusKm,
                                    showOnLeaderboard = it,
                                    phoneVisibleToConfirmedOnly = userProfile.phoneVisibleToConfirmedOnly
                                )
                            },
                            modifier = Modifier.testTag("privacy_leaderboard_toggle")
                        )
                    }
                }
            }
        }

        // 4. Accessibility & Dark Mode Theme
        item {
            Text(
                text = "Accessibility & Appearance 🎨",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Theme Mode", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("SYSTEM" to "System Auto", "DARK" to "Dark Mode", "LIGHT" to "Light Mode").forEach { (mode, label) ->
                            val isSelected = userProfile.darkModeSetting == mode
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.updateThemeSetting(mode) }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Help & Tutorials
        item {
            Text(
                text = "Help & Community Guide 📖",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.toggleOnboarding(true) }
                            .padding(12.dp)
                            .testTag("replay_onboarding_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Replay Onboarding Guide", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Learn community borrowing benefits & rules", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.toggleSupportChat(true) }
                            .padding(12.dp)
                            .testTag("open_support_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Community Support & FAQs", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Instant answers regarding deposits & safety", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    // Modal dialog for inspecting badge roadmap and perks
    selectedBadgeForDetail?.let { badge ->
        BadgeDetailDialog(
            badge = badge,
            onDismiss = { viewModel.closeBadgeDetail() },
            onSimulateBonus = { viewModel.simulateKarmaBonus(50) }
        )
    }
}
