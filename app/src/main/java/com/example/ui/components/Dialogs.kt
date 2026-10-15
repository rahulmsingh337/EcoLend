package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppNotification
import com.example.data.model.UserProfile
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary

// --- 1. Onboarding Tutorial Dialog ---
data class OnboardingStep(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val stat: String
)

@Composable
fun OnboardingTutorialDialog(
    onDismiss: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val steps = listOf(
        OnboardingStep(
            title = "Borrow, Don't Buy 🛠️",
            description = "The average power drill is only used for 13 minutes in its entire lifespan! Sharing equipment among neighbors keeps tons of plastic and metal out of local landfills.",
            icon = Icons.Default.Eco,
            stat = "Over 80% of household tools sit unused in garages."
        ),
        OnboardingStep(
            title = "Track Your Waste Impact 🌍",
            description = "Every borrowed item logs real kilograms of waste diverted and carbon emissions prevented. Watch your personal and neighborhood eco footprint shrink!",
            icon = Icons.Default.AutoAwesome,
            stat = "1 pressure washer shared saves ~18.5 kg raw materials."
        ),
        OnboardingStep(
            title = "Trust & Porch Pickups 🤝",
            description = "Coordinate contactless front porch box pickups or friendly hand-offs. Verified ratings, reviews, and optional refundable deposit holds keep items safe.",
            icon = Icons.Default.Shield,
            stat = "99.8% on-time return rate across community shares."
        ),
        OnboardingStep(
            title = "Level Up & Earn Karma 🏆",
            description = "Earn Eco Karma points for every tool shared, on-time return, and neighbor review. Unlock badges like 'Zero Waste Hero' and climb the community leaderboard!",
            icon = Icons.Default.Star,
            stat = "Join 450+ neighbors active in your district."
        )
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("onboarding_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            val step = steps[currentStep]
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Step Dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    steps.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .size(width = if (index == currentStep) 24.dp else 8.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (index == currentStep) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                // Step Icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = step.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = step.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = EcoGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = step.stat,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        TextButton(onClick = { currentStep-- }) {
                            Text("Back")
                        }
                    } else {
                        TextButton(onClick = onDismiss) {
                            Text("Skip")
                        }
                    }

                    Button(
                        onClick = {
                            if (currentStep < steps.size - 1) {
                                currentStep++
                            } else {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("onboarding_next_btn")
                    ) {
                        Text(
                            text = if (currentStep < steps.size - 1) "Next" else "Start Sharing",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// --- 2. Comprehensive Support Chat Dialog ---
data class SupportMessage(
    val sender: String,
    val text: String,
    val isUser: Boolean,
    val time: String = "Now"
)

@Composable
fun SupportChatDialog(
    onDismiss: () -> Unit
) {
    var inputQuery by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            SupportMessage(
                sender = "EcoSupport",
                text = "Hello neighbor! 👋 I'm your EcoLend Community Assistant. How can I help you today? Choose a common topic below or type any question.",
                isUser = false
            )
        )
    }

    val faqTopics = listOf(
        "How do refundable deposits work?" to "Security deposits are held as a temporary authorization. When the item is returned in agreed condition, the full deposit is instantly released with 0% fees.",
        "What if an item gets damaged?" to "EcoLend recommends inspecting items at porch pickup. In case of unexpected damage, the deposit covers repairs, or neighbors coordinate resolution via secure chat mediation.",
        "Contactless Porch Pickup guidelines" to "Many neighbors place equipment in a weather-tight front porch bin with a lock code sent via app chat. No direct physical contact required!",
        "How do I list my own tools?" to "Tap 'Add Listing' on the Home screen! You set your deposit, availability, and description. You'll earn +30 Karma right away."
    )

    fun answerQuery(query: String) {
        messages.add(SupportMessage(sender = "You", text = query, isUser = true))
        val matchedFaq = faqTopics.find { it.first.contains(query, ignoreCase = true) || query.contains(it.first, ignoreCase = true) }
        val reply = matchedFaq?.second ?: when {
            query.contains("offline", ignoreCase = true) ->
                "EcoLend features full offline mode! You can browse cached equipment, draft requests, and access your borrow history without internet. Offline actions automatically sync upon reconnection."
            query.contains("karma", ignoreCase = true) ->
                "Eco Karma rewards your community impact! You earn +50 Karma for on-time returns, +30 for listing equipment, and +25 for leaving thoughtful neighbor reviews."
            query.contains("payment", ignoreCase = true) ->
                "We support Google Pay and encrypted cards for security deposits. Most items are 100% free community shares with zero daily rental fees."
            else ->
                "Thanks for reaching out! Our neighborhood coordinators review community inquiries regularly. We've logged your request: '$query' and you can also check our safety community pledge in Settings."
        }
        messages.add(SupportMessage(sender = "EcoSupport", text = reply, isUser = false))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .testTag("support_chat_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Help,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Community Support 💬",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Instant guidance & FAQs",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Topic Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    faqTopics.forEach { (question, _) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { answerQuery(question) }
                        ) {
                            Text(
                                text = question,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Chat Messages List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { msg ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (msg.isUser) Alignment.End else Alignment.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (msg.isUser) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = msg.sender,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (msg.isUser) MaterialTheme.colorScheme.primary else EcoGreenDark
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = msg.text,
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputQuery,
                        onValueChange = { inputQuery = it },
                        placeholder = { Text("Ask anything about lending...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("support_input_query"),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (inputQuery.isNotBlank()) {
                                answerQuery(inputQuery.trim())
                                inputQuery = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .testTag("support_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- 3. Notification Center Dialog ---
@Composable
fun NotificationCenterDialog(
    notifications: List<AppNotification>,
    isOffline: Boolean,
    onDismiss: () -> Unit,
    onMarkAllRead: () -> Unit,
    onMarkRead: (Long) -> Unit,
    onTriggerDueDatesCheck: (() -> Unit)? = null,
    onOpenHub: (() -> Unit)? = null,
    onOpenMessages: (() -> Unit)? = null
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .testTag("notification_center_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Community Alerts",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (notifications.any { !it.isRead }) {
                        TextButton(onClick = onMarkAllRead) {
                            Text("Mark All Read", fontSize = 12.sp)
                        }
                    }
                }

                if (isOffline) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFF3E0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = EcoAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Offline Mode: Notifications stored in local database.",
                                fontSize = 11.sp,
                                color = Color(0xFFE65100),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (notifications.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No pending notifications.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(notifications) { notif ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (notif.isRead) MaterialTheme.colorScheme.surface
                                else if (notif.type == "RETURN_REMINDER") MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onMarkRead(notif.id) }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (notif.isRead) Color.Transparent
                                                    else if (notif.type == "RETURN_REMINDER") EcoAmber
                                                    else MaterialTheme.colorScheme.primary
                                                )
                                                .padding(top = 4.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = notif.title,
                                                fontSize = 13.sp,
                                                fontWeight = if (notif.isRead) FontWeight.SemiBold else FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = notif.message,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }

                                    // Quick actions for return reminders
                                    if (notif.type == "RETURN_REMINDER") {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (onOpenHub != null) {
                                                Button(
                                                    onClick = {
                                                        onMarkRead(notif.id)
                                                        onDismiss()
                                                        onOpenHub()
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    modifier = Modifier
                                                        .height(28.dp)
                                                        .testTag("notif_direct_return_btn_${notif.id}")
                                                ) {
                                                    Text("Return Tool 🚀", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            if (onOpenMessages != null) {
                                                OutlinedButton(
                                                    onClick = {
                                                        onMarkRead(notif.id)
                                                        onDismiss()
                                                        onOpenMessages()
                                                    },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    modifier = Modifier
                                                        .height(28.dp)
                                                        .testTag("notif_message_owner_btn_${notif.id}")
                                                ) {
                                                    Text("Message Owner 💬", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onTriggerDueDatesCheck != null) {
                        OutlinedButton(
                            onClick = onTriggerDueDatesCheck,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("trigger_worker_check_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassBottom,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Check Deadlines", fontSize = 11.sp)
                        }
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(if (onTriggerDueDatesCheck != null) 1f else 2f)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

// --- 4. Social Media Sharing Dialog ---
@Composable
fun ShareImpactDialog(
    userProfile: UserProfile,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val shareText = "🌍 I've diverted ${userProfile.wasteSavedKg} kg of waste and saved $${userProfile.moneySavedDollars.toInt()} by sharing household tools on EcoLend! Join our neighborhood circular sharing club: https://ecolend.community #ZeroWaste #CommunityLend #ClimateAction"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("share_impact_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Share Your Eco Impact 📣",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Inspire your friends and neighbors to borrow instead of buying new equipment!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Impact Card Preview
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🌱 EcoLend Community Champion",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenDark
                            )
                            Text(
                                text = userProfile.levelTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${userProfile.wasteSavedKg} kg",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenDark
                                )
                                Text("Waste Diverted", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$${userProfile.moneySavedDollars.toInt()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoAmber
                                )
                                Text("Money Saved", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${userProfile.exchangesCount}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text("Exchanges", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "My Community Impact on EcoLend")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share via..."))
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("launch_system_share_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Impact", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- 5. Add Listing Dialog ---
@Composable
fun AddListingDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, description: String, category: String, deposit: Double, condition: String, wasteKg: Double, photoUri: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Power Tools") }
    var depositStr by remember { mutableStateOf("30") }
    var condition by remember { mutableStateOf("Like New") }
    var wasteKgStr by remember { mutableStateOf("8.5") }
    var capturedPhotoPath by remember { mutableStateOf<String?>(null) }
    var showCameraDialog by remember { mutableStateOf(false) }

    val categories = listOf("Power Tools", "Lawn & Garden", "Home Repair", "Kitchen & Cooking", "Camping & Outdoors", "Cleaning Gear", "Party & Events")

    val photoBitmap = remember(capturedPhotoPath) {
        if (!capturedPhotoPath.isNullOrEmpty()) {
            try {
                android.graphics.BitmapFactory.decodeFile(capturedPhotoPath)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_listing_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Lend an Item to Neighbors 📦",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Put your unused garage & closet items to work! Earn +30 Eco Karma.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Camera Photo Option inside dialog
                Text("Item Photo (Optional)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))

                if (photoBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black)
                    ) {
                        Image(
                            bitmap = photoBitmap.asImageBitmap(),
                            contentDescription = "Item Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { showCameraDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retake", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = { capturedPhotoPath = null },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Remove Photo", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    OutlinedButton(
                        onClick = { showCameraDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_open_camera_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Take Photo with Camera", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Item Title (e.g., Cordless Jigsaw)") },
                    modifier = Modifier.fillMaxWidth().testTag("new_item_title_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Accessories included") },
                    modifier = Modifier.fillMaxWidth().testTag("new_item_desc_input"),
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (category == cat) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { category = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                color = if (category == cat) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = depositStr,
                        onValueChange = { depositStr = it },
                        label = { Text("Deposit ($)") },
                        modifier = Modifier.weight(1f).testTag("new_item_deposit_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = wasteKgStr,
                        onValueChange = { wasteKgStr = it },
                        label = { Text("Waste Saved (kg)") },
                        modifier = Modifier.weight(1f).testTag("new_item_waste_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onSubmit(
                                    title.trim(),
                                    description.trim().ifEmpty { "Available for community lending." },
                                    category,
                                    depositStr.toDoubleOrNull() ?: 20.0,
                                    condition,
                                    wasteKgStr.toDoubleOrNull() ?: 6.0,
                                    capturedPhotoPath
                                )
                            }
                        },
                        enabled = title.isNotBlank(),
                        modifier = Modifier.weight(1.5f).testTag("publish_new_item_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Publish Item", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showCameraDialog) {
        CameraPhotoCaptureDialog(
            item = null,
            itemTitle = title.ifBlank { "Community Tool" },
            itemCondition = condition,
            onDismiss = { showCameraDialog = false },
            onPhotoCaptured = { savedPath ->
                capturedPhotoPath = savedPath
                showCameraDialog = false
            }
        )
    }
}
