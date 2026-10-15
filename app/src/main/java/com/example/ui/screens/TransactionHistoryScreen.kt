package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BorrowRequest
import com.example.data.model.LendItem
import com.example.data.model.Review
import com.example.ui.EcoLendViewModel
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoEmerald
import com.example.ui.theme.EcoGold
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoOrange
import com.example.ui.theme.EcoSage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TransactionTab(val title: String) {
    ALL("All Exchanges"),
    BORROWED("Borrowed by Me"),
    LENT("Lent to Others")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    viewModel: EcoLendViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val allRequests by viewModel.allRequests.collectAsState()
    val allReviews by viewModel.allReviews.collectAsState()
    val allItems by viewModel.allItems.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedReceiptRequest by remember { mutableStateOf<BorrowRequest?>(null) }

    // System Back Handler
    BackHandler {
        onBack()
    }

    // Filter requests based on selected tab
    val filteredRequests = remember(allRequests, selectedTabIndex, userProfile.name) {
        when (selectedTabIndex) {
            1 -> allRequests.filter { it.requesterName.equals(userProfile.name, ignoreCase = true) }
            2 -> allRequests.filter { !it.requesterName.equals(userProfile.name, ignoreCase = true) }
            else -> allRequests
        }.sortedByDescending { it.createdAtMillis }
    }

    val totalCompletedCount = remember(allRequests) {
        allRequests.count { it.status == "RETURNED" || it.status == "ACTIVE" }
    }

    val lentCount = remember(allRequests, userProfile.name) {
        allRequests.count { it.ownerName.equals(userProfile.name, ignoreCase = true) || !it.requesterName.equals(userProfile.name, ignoreCase = true) }
    }

    val borrowedCount = remember(allRequests, userProfile.name) {
        allRequests.count { it.requesterName.equals(userProfile.name, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Transaction History",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Past equipment loans, returns & mutual ratings",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("transaction_history_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Profile"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("transaction_history_screen")
        ) {
            // 1. Cumulative Activity & Karma Impact Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("transaction_summary_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SummaryMetric(
                        label = "Exchanges",
                        value = "${allRequests.size}",
                        icon = Icons.Default.SwapHoriz,
                        color = MaterialTheme.colorScheme.primary
                    )
                    SummaryMetric(
                        label = "Lent",
                        value = "$lentCount",
                        icon = Icons.Default.VolunteerActivism,
                        color = EcoGreenDark
                    )
                    SummaryMetric(
                        label = "Borrowed",
                        value = "$borrowedCount",
                        icon = Icons.Default.Paid,
                        color = EcoAmber
                    )
                    SummaryMetric(
                        label = "Rating",
                        value = String.format("%.1f ★", userProfile.rating),
                        icon = Icons.Default.Star,
                        color = EcoGold
                    )
                }
            }

            // 2. Filter Tabs (All, Borrowed, Lent)
            SecondaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                TransactionTab.values().forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            val count = when (index) {
                                1 -> allRequests.count { it.requesterName.equals(userProfile.name, ignoreCase = true) }
                                2 -> allRequests.count { !it.requesterName.equals(userProfile.name, ignoreCase = true) }
                                else -> allRequests.size
                            }
                            Text(
                                text = "${tab.title} ($count)",
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        modifier = Modifier.testTag("transaction_tab_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 3. Transactions List
            if (filteredRequests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No transactions found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "When you lend or borrow tools with neighbors, your transaction receipts and mutual ratings will appear here.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredRequests) { request ->
                        val matchingItem = allItems.find { it.id == request.itemId }
                        val matchingReview = allReviews.find { it.itemId == request.itemId }
                        val isUserBorrower = request.requesterName.equals(userProfile.name, ignoreCase = true)

                        TransactionRecordCard(
                            request = request,
                            item = matchingItem,
                            review = matchingReview,
                            isUserBorrower = isUserBorrower,
                            onViewReceipt = { selectedReceiptRequest = request }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Digital Exchange Receipt Modal
    selectedReceiptRequest?.let { req ->
        val matchingItem = allItems.find { it.id == req.itemId }
        val matchingReview = allReviews.find { it.itemId == req.itemId }
        val isUserBorrower = req.requesterName.equals(userProfile.name, ignoreCase = true)

        DigitalExchangeReceiptDialog(
            request = req,
            item = matchingItem,
            review = matchingReview,
            isUserBorrower = isUserBorrower,
            onDismiss = { selectedReceiptRequest = null }
        )
    }
}

// -------------------------------------------------------------
// Transaction Record Card Component
// -------------------------------------------------------------
@Composable
private fun TransactionRecordCard(
    request: BorrowRequest,
    item: LendItem?,
    review: Review?,
    isUserBorrower: Boolean,
    onViewReceipt: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }
    val startDateStr = dateFormatter.format(Date(request.startDateMillis))
    val endDateStr = dateFormatter.format(Date(request.endDateMillis))

    val statusColor = when (request.status) {
        "RETURNED" -> EcoGreenDark
        "ACTIVE" -> Color(0xFF1976D2)
        "APPROVED" -> EcoAmber
        "CANCELLED" -> MaterialTheme.colorScheme.error
        else -> Color.Gray
    }

    val statusLabel = when (request.status) {
        "RETURNED" -> "RETURNED & COMPLETED"
        "ACTIVE" -> "ACTIVE LOAN"
        "APPROVED" -> "PICKUP READY"
        "CANCELLED" -> "CANCELLED"
        else -> "PENDING APPROVAL"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("transaction_card_${request.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = borderStroke(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Transaction ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (item?.category) {
                                "Power Tools" -> Icons.Default.Build
                                "Lawn & Garden" -> Icons.Default.Park
                                "Home Repair" -> Icons.Default.HomeRepairService
                                "Kitchen & Cooking" -> Icons.Default.Restaurant
                                "Camping & Outdoors" -> Icons.Default.Terrain
                                else -> Icons.Default.CleaningServices
                            },
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = request.itemTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "TX-${request.transactionId ?: (100000 + request.id)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Parties & Direction Info
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isUserBorrower) "Borrowed from ${request.ownerName}" else "Lent to ${request.requesterName}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$startDateStr ➔ $endDateStr",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Deposit Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = if (request.status == "RETURNED") EcoGreenDark else EcoAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (request.status == "RETURNED") {
                            "✓ $${request.depositPaid.toInt()} Deposit Refunded"
                        } else {
                            "🔒 $${request.depositPaid.toInt()} Held in Escrow"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (request.status == "RETURNED") EcoGreenDark else EcoAmber
                    )
                }

                Text(
                    text = "Via ${request.pickupOption}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Star Rating & Review Section (if available)
            if (review != null || request.status == "RETURNED") {
                val ratingScore = review?.rating ?: 5
                val commentText = review?.comment ?: "Returned on schedule in excellent working condition. Great community exchange!"
                val tagsList = review?.tags?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: listOf("Punctual Return", "Handled with Care", "Cleaned Gear")

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(ratingScore) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = EcoGold,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$ratingScore.0",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "by ${review?.reviewerName ?: if (isUserBorrower) request.ownerName else request.requesterName}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"$commentText\"",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    // Review Feedback Tags
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(tagsList) { tag ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button: View Digital Receipt
            Button(
                onClick = onViewReceipt,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("view_receipt_btn_${request.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "View Digital Exchange Receipt",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Digital Exchange Receipt Dialog
// -------------------------------------------------------------
@Composable
private fun DigitalExchangeReceiptDialog(
    request: BorrowRequest,
    item: LendItem?,
    review: Review?,
    isUserBorrower: Boolean,
    onDismiss: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()) }
    val startDateStr = dateFormatter.format(Date(request.startDateMillis))
    val endDateStr = dateFormatter.format(Date(request.endDateMillis))
    val txId = request.transactionId ?: "TX-${100000 + request.id}"
    val wasteSavedKg = item?.estimatedWasteSavedKg ?: 8.5
    val co2SavedKg = item?.estimatedCo2SavedKg ?: (wasteSavedKg * 2.2)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("digital_exchange_receipt_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Receipt Header
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
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Exchange Receipt",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = txId,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Item & Exchange details
                Text(
                    text = request.itemTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item?.category ?: "Community Tool"} • ${item?.condition ?: "Good Condition"}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                ReceiptDetailRow("Lender", request.ownerName)
                ReceiptDetailRow("Borrower", request.requesterName)
                ReceiptDetailRow("Pickup Option", request.pickupOption)
                ReceiptDetailRow("Loan Period", "$startDateStr – $endDateStr")

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))

                // Financial Breakdown
                ReceiptDetailRow("Lending Fee", "$0.00 (Free Community Share)")
                ReceiptDetailRow("Refundable Security Deposit", "$${request.depositPaid.toInt()}.00")

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (request.status == "RETURNED") EcoGreenDark.copy(alpha = 0.12f) else EcoAmber.copy(alpha = 0.12f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (request.status == "RETURNED") "Deposit Escrow Status" else "Escrow Status",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (request.status == "RETURNED") EcoGreenDark else EcoAmber
                        )
                        Text(
                            text = if (request.status == "RETURNED") "100% Refunded ✓" else "Held Securely 🔒",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (request.status == "RETURNED") EcoGreenDark else EcoAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Eco Impact Earned
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = EcoGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Environmental Impact Earned 🌱",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "+30 Eco Karma • ${String.format("%.1f", wasteSavedKg)} kg waste prevented • ${String.format("%.1f", co2SavedKg)} kg CO2 saved",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Close Receipt", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReceiptDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
    }
}

private fun borderStroke(color: Color, width: androidx.compose.ui.unit.Dp): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(width, color)
}
