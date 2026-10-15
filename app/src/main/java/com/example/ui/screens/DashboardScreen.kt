package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BorrowRequest
import com.example.data.model.LendItem
import com.example.data.model.WishlistEntry
import com.example.ui.EcoLendViewModel
import com.example.ui.Screen
import com.example.ui.components.AvailabilityChip
import com.example.ui.components.ItemCard
import com.example.ui.components.QuickAlertSearchBanner
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary

@Composable
fun DashboardScreen(
    viewModel: EcoLendViewModel,
    onItemClick: (LendItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val allRequests by viewModel.allRequests.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val wishlistEntries by viewModel.wishlistEntries.collectAsState()
    val isOffline by viewModel.isOfflineMode.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val categories = listOf(
        "All",
        "Power Tools",
        "Lawn & Garden",
        "Home Repair",
        "Kitchen & Cooking",
        "Camping & Outdoors",
        "Cleaning Gear",
        "Party & Events"
    )

    // Filtered All Equipment in community ledger
    val filteredAllItems = allItems.filter { item ->
        val matchesQuery = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true) ||
                item.neighborhood.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
        matchesQuery && matchesCategory
    }

    // Active & Pending Requests
    val activeOrPending = allRequests.filter { it.status == "PENDING" || it.status == "APPROVED" || it.status == "ACTIVE" }
    val filteredActiveOrPending = activeOrPending.filter { req ->
        val item = allItems.find { it.id == req.itemId }
        val matchesQuery = searchQuery.isBlank() ||
                req.itemTitle.contains(searchQuery, ignoreCase = true) ||
                req.ownerName.contains(searchQuery, ignoreCase = true) ||
                req.note.contains(searchQuery, ignoreCase = true) ||
                (item?.category?.contains(searchQuery, ignoreCase = true) == true)
        val matchesCategory = selectedCategory == "All" || (item?.category == selectedCategory)
        matchesQuery && matchesCategory
    }

    // Returned History
    val returnedHistory = allRequests.filter { it.status == "RETURNED" }
    val filteredReturnedHistory = returnedHistory.filter { req ->
        val item = allItems.find { it.id == req.itemId }
        val matchesQuery = searchQuery.isBlank() ||
                req.itemTitle.contains(searchQuery, ignoreCase = true) ||
                req.ownerName.contains(searchQuery, ignoreCase = true) ||
                (item?.category?.contains(searchQuery, ignoreCase = true) == true)
        val matchesCategory = selectedCategory == "All" || (item?.category == selectedCategory)
        matchesQuery && matchesCategory
    }

    // Saved Items
    val savedItems = allItems.filter { it.isFavorite }
    val filteredSavedItems = savedItems.filter { item ->
        val matchesQuery = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true) ||
                item.category.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
        matchesQuery && matchesCategory
    }

    val tabTitles = listOf(
        "All Items (${filteredAllItems.size})",
        "Active (${filteredActiveOrPending.size})",
        "History (${filteredReturnedHistory.size})",
        "Wishlist (${filteredSavedItems.size})"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
    ) {
        // 1. Search Bar at Top of Item Availability Dashboard
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter items by keyword or category...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Dashboard",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("dashboard_search_bar"),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        // 2. Category Filter Chips Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("dashboard_category_chips")
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                        selectedCategory = category
                    }
                ) {
                    Text(
                        text = category,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Active Filter indicator badge
        if (searchQuery.isNotBlank() || selectedCategory != "All") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtered by: " + listOfNotNull(
                        searchQuery.takeIf { it.isNotBlank() }?.let { "\"$it\"" },
                        selectedCategory.takeIf { it != "All" }?.let { "Category: $it" }
                    ).joinToString(" • "),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Reset",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        searchQuery = ""
                        selectedCategory = "All"
                    }
                )
            }
        }

        // 3. Top Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Availability & Exchange Hub",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .clickable { viewModel.openAddListing() }
                                .testTag("dashboard_add_item_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Add Item",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        if (isOffline) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFF3E0)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Sync, contentDescription = null, tint = EcoAmber, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Offline", fontSize = 10.sp, color = Color(0xFFE65100))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${filteredAllItems.count { it.availability == "AVAILABLE_NOW" }}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenPrimary
                        )
                        Text("Available Now", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${filteredActiveOrPending.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("Active Holds", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val totalDepositHeld = filteredActiveOrPending.sumOf { it.depositPaid }
                        Text(
                            text = "$${totalDepositHeld.toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoAmber
                        )
                        Text("Deposits Active", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${filteredReturnedHistory.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenDark
                        )
                        Text("Completed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 4. Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("dashboard_tabs")
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 5. Tab Content
        when (selectedTabIndex) {
            0 -> {
                // All Equipment Availability
                if (filteredAllItems.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        QuickAlertSearchBanner(
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            onSetAlertClick = {
                                viewModel.openCreateAlertDialog(
                                    initialKeyword = searchQuery,
                                    initialCategory = selectedCategory
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No equipment matches '$searchQuery'.\nTry searching for tools, garden, or camping gear.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredAllItems, key = { it.id }) { item ->
                            ItemCard(
                                item = item,
                                onClick = { onItemClick(item) },
                                onFavoriteToggle = { viewModel.toggleFavorite(item) },
                                onCapturePhoto = { viewModel.openCameraForPhotoCapture(item) }
                            )
                        }
                    }
                }
            }

            1 -> {
                // Active & Pending Requests
                if (filteredActiveOrPending.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "No active holds match your search."
                            else "No active borrowing sessions right now.\nBrowse community items to share!",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredActiveOrPending, key = { it.id }) { req ->
                            ActiveRequestCard(
                                request = req,
                                onReturnClick = { viewModel.markItemReturned(req) },
                                onChatClick = { viewModel.openChatConversation("conv_${req.itemId}") }
                            )
                        }
                    }
                }
            }

            2 -> {
                // History (Accessible Offline)
                if (filteredReturnedHistory.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotBlank()) "No completed history matches your search."
                            else "Your completed exchange history will show here.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredReturnedHistory, key = { it.id }) { req ->
                            ReturnedHistoryCard(
                                request = req,
                                onLeaveReview = { viewModel.openReviewDialog(req) }
                            )
                        }
                    }
                }
            }

            3 -> {
                // Wishlist / Saved Items (Room Persistence)
                if (filteredSavedItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("wishlist_empty_card"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (searchQuery.isNotBlank()) "No bookmarked items match '$searchQuery'"
                                    else "Your Wishlist is Empty",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Tap the heart ❤️ or bookmark icon on any community tool to save it here locally in Room for upcoming home repairs and gardening projects.",
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { selectedTabIndex = 0 },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Explore All Equipment", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("wishlist_items_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            // Wishlist Header Metric Card
                            Card(
                                modifier = Modifier.fillMaxWidth().testTag("wishlist_header_metric_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(EcoGreenPrimary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Bookmark,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "${filteredSavedItems.size} Bookmarked Tools",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EcoGreenDark
                                            )
                                            val totalPotentialWaste = filteredSavedItems.sumOf { it.estimatedWasteSavedKg }
                                            Text(
                                                text = "Could prevent ~${String.format("%.1f", totalPotentialWaste)}kg landfill waste",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                    ) {
                                        Text(
                                            text = "Stored in Room",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        items(filteredSavedItems, key = { it.id }) { item ->
                            val entry = wishlistEntries.find { it.itemId == item.id }
                            WishlistItemCard(
                                item = item,
                                entry = entry,
                                onClick = { onItemClick(item) },
                                onBorrowClick = { viewModel.startBorrowFlow(item) },
                                onEditNoteClick = { viewModel.openWishlistNoteDialog(item) },
                                onRemoveClick = { viewModel.removeFromWishlist(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WishlistItemCard(
    item: LendItem,
    entry: WishlistEntry?,
    onClick: () -> Unit,
    onBorrowClick: () -> Unit,
    onEditNoteClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wishlist_item_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(item.category),
                            contentDescription = item.category,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${item.distanceMiles} mi • ${item.neighborhood}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvailabilityChip(availability = item.availability)
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("wishlist_delete_btn_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove bookmark",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val photoBitmap = remember(item.photoUri) {
                if (!item.photoUri.isNullOrEmpty()) {
                    try {
                        android.graphics.BitmapFactory.decodeFile(item.photoUri)?.asImageBitmap()
                    } catch (_: Exception) {
                        null
                    }
                } else null
            }

            if (photoBitmap != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black.copy(alpha = 0.05f))
                ) {
                    Image(
                        bitmap = photoBitmap,
                        contentDescription = "Condition photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "📸 Local Condition Photo",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = item.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable { onClick() }
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.description,
                fontSize = 12.sp,
                maxLines = 2,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Saved Project Note & Target Date in Room
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Wishlist Notes & Target",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Edit Note",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable { onEditNoteClick() }
                                .testTag("edit_wishlist_note_btn_${item.id}")
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (!entry?.note.isNullOrBlank()) entry!!.note else "No personal note added yet. Tap 'Edit Note' to set reason or planned date.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (!entry?.targetDate.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🗓️ Planned: ${entry!!.targetDate}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("wishlist_view_details_${item.id}")
                ) {
                    Text("View Item", fontSize = 12.sp)
                }
                Button(
                    onClick = onBorrowClick,
                    enabled = item.availability == "AVAILABLE_NOW",
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("wishlist_borrow_btn_${item.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (item.availability == "AVAILABLE_NOW") "Borrow Now" else "Reserved",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveRequestCard(
    request: BorrowRequest,
    onReturnClick: () -> Unit,
    onChatClick: () -> Unit
) {
    val now = remember { System.currentTimeMillis() }
    val diffMillis = request.endDateMillis - now
    val hoursRemaining = ((diffMillis / (1000 * 3600))).toInt()
    val isDueWithin24h = diffMillis in 0..(24 * 3600 * 1000L)
    val isOverdue = diffMillis < 0

    val cardBorder = when {
        isOverdue -> androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.error)
        isDueWithin24h -> androidx.compose.foundation.BorderStroke(1.5.dp, EcoAmber)
        else -> null
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("active_request_card_${request.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isOverdue -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                isDueWithin24h -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        border = cardBorder,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDueWithin24h || isOverdue) 4.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Urgent 24-hour or Overdue Warning Banner
            if (isOverdue) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .testTag("request_overdue_banner_${request.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🚨 Past Due! Return promptly to release $${request.depositPaid.toInt()} deposit.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            } else if (isDueWithin24h) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF3E0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .testTag("request_urgent_24h_banner_${request.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "⏰ 24-Hour Reminder: Due soon! Return to earn +50 Eco Karma.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.itemTitle,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (request.status == "APPROVED" || request.status == "ACTIVE") Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                ) {
                    Text(
                        text = if (request.status == "APPROVED") "Ready for Pickup" else request.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (request.status == "APPROVED" || request.status == "ACTIVE") EcoGreenDark else Color(0xFFE65100),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Lender: ${request.ownerName} • Pickup: ${request.pickupOption}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = when {
                    isOverdue -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                    isDueWithin24h -> Color(0xFFFFF8E1)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HourglassBottom,
                            contentDescription = null,
                            tint = if (isOverdue) MaterialTheme.colorScheme.error else EcoAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                isOverdue -> "Past Due: Overdue by ${Math.abs(hoursRemaining)}h"
                                isDueWithin24h -> "Due: In ~$hoursRemaining hours ⏳"
                                else -> "Due: In ${hoursRemaining / 24} days"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isOverdue -> MaterialTheme.colorScheme.error
                                isDueWithin24h -> Color(0xFFE65100)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                    Text(
                        text = "Deposit: $${request.depositPaid.toInt()} Held",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onChatClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Coordinate Chat", fontSize = 12.sp)
                }
                Button(
                    onClick = onReturnClick,
                    modifier = Modifier.weight(1.3f).testTag("return_item_btn_${request.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Return & Release Deposit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReturnedHistoryCard(
    request: BorrowRequest,
    onLeaveReview: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = request.itemTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Lender: ${request.ownerName} • Returned",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Deposit $${request.depositPaid.toInt()} was 100% refunded",
                    fontSize = 12.sp,
                    color = EcoGreenDark,
                    fontWeight = FontWeight.Medium
                )
                Button(
                    onClick = onLeaveReview,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.testTag("history_leave_review_btn_${request.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Rate Lender",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
