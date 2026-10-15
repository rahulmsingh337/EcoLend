package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LendItem
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

val DEFAULT_NEIGHBORHOOD_CENTER = LatLng(37.7749, -122.4194) // Oakridge Woods Center

/**
 * Calculates a stable, deterministic geographic location for a lend item
 * within the neighborhood radius to protect precise physical home addresses.
 */
fun getItemCoordinates(item: LendItem, center: LatLng = DEFAULT_NEIGHBORHOOD_CENTER): LatLng {
    val distMiles = item.distanceMiles.coerceIn(0.1, 15.0)
    // Golden angle dispersion for natural, non-overlapping spatial distribution
    val angleRad = Math.toRadians((item.id * 137.508 + 23.0) % 360.0)
    val dLat = (distMiles * cos(angleRad)) / 69.0
    val dLng = (distMiles * sin(angleRad)) / (69.0 * cos(Math.toRadians(center.latitude)))
    return LatLng(center.latitude + dLat, center.longitude + dLng)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleNeighborhoodMap(
    items: List<LendItem>,
    maxDistance: Double,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onDistanceChange: (Double) -> Unit,
    onItemSelected: (LendItem) -> Unit,
    onStartBorrow: (LendItem) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<String> = listOf("All", "Power Tools", "Lawn & Garden", "Home Repair", "Kitchen & Cooking", "Camping & Outdoors", "Cleaning Gear", "Party & Events")
) {
    var selectedItem by remember { mutableStateOf<LendItem?>(null) }
    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var isRadarFallbackMode by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_NEIGHBORHOOD_CENTER, 13.2f)
    }

    // Auto-center camera if a new item is selected
    LaunchedEffect(selectedItem) {
        selectedItem?.let { item ->
            val pos = getItemCoordinates(item)
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(pos, 14.5f),
                durationMs = 600
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("google_neighborhood_map_container")
    ) {
        if (!isRadarFallbackMode) {
            // Google Map View
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("google_map_view"),
                cameraPositionState = cameraPositionState,
                properties = remember(mapType) {
                    MapProperties(
                        mapType = mapType,
                        isMyLocationEnabled = false
                    )
                },
                uiSettings = remember {
                    MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false,
                        compassEnabled = true,
                        rotationGesturesEnabled = true,
                        tiltGesturesEnabled = false
                    )
                },
                onMapClick = {
                    selectedItem = null
                }
            ) {
                // Outer neighborhood search horizon circle
                Circle(
                    center = DEFAULT_NEIGHBORHOOD_CENTER,
                    radius = maxDistance * 1609.34, // miles to meters
                    fillColor = Color(0x0C2E7D32),
                    strokeColor = Color(0x402E7D32),
                    strokeWidth = 2f
                )

                // User neighborhood center pin
                MarkerComposable(
                    state = rememberMarkerState(position = DEFAULT_NEIGHBORHOOD_CENTER),
                    title = "My Neighborhood",
                    snippet = "Oakridge Woods Central Hub"
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(EcoGreenPrimary)
                            .border(3.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "My Neighborhood Hub",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Neighborhood Items with Fuzzy Privacy Circles and Custom Pins
                items.forEach { item ->
                    val itemPos = remember(item.id, item.distanceMiles) { getItemCoordinates(item) }
                    val isSelected = selectedItem?.id == item.id
                    val isAvailable = item.availability == "AVAILABLE_NOW"

                    // Privacy-protecting fuzzy pickup zone circle (~0.32 mile radius = ~515 meters)
                    Circle(
                        center = itemPos,
                        radius = 520.0,
                        fillColor = if (isSelected) Color(0x382E7D32) else Color(0x182E7D32),
                        strokeColor = if (isSelected) EcoGreenPrimary else Color(0x662E7D32),
                        strokeWidth = if (isSelected) 4f else 2.5f,
                        clickable = true,
                        onClick = {
                            selectedItem = item
                        }
                    )

                    // Interactive Custom Marker
                    MarkerComposable(
                        state = rememberMarkerState(position = itemPos),
                        title = item.title,
                        snippet = "${item.distanceMiles} mi • ${item.neighborhood}",
                        onClick = {
                            selectedItem = item
                            true
                        }
                    ) {
                        ItemMapPinBadge(
                            item = item,
                            isSelected = isSelected,
                            isAvailable = isAvailable
                        )
                    }
                }
            }
        } else {
            // Radar Map Mode Fallback
            NeighborhoodRadarMap(
                items = items,
                maxDistance = maxDistance,
                onDistanceChange = onDistanceChange,
                onItemSelected = onItemSelected,
                onStartBorrow = onStartBorrow
            )
        }

        // Top Map Controls & Category Filter Chips
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            // Top Bar with Neighborhood title, View Mode Toggle, and Map Type Toggle
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                shadowElevation = 5.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(EcoGreenPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = EcoGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Oakridge Woods",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${items.size} tools & gear nearby",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Toggle Map Style (Normal / Satellite)
                        if (!isRadarFallbackMode) {
                            IconButton(
                                onClick = {
                                    mapType = if (mapType == MapType.NORMAL) MapType.HYBRID else MapType.NORMAL
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("toggle_map_type_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = "Toggle Satellite",
                                    tint = if (mapType == MapType.HYBRID) EcoGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Toggle Radar / Map View
                        IconButton(
                            onClick = { isRadarFallbackMode = !isRadarFallbackMode },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("toggle_map_view_mode_btn")
                        ) {
                            Icon(
                                imageVector = if (isRadarFallbackMode) Icons.Default.Map else Icons.Default.Radar,
                                contentDescription = "Switch Map View",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal Category Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = category.equals(selectedCategory, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelected(category) },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EcoGreenPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) EcoGreenPrimary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.testTag("map_category_chip_${category.lowercase().replace(" ", "_")}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Distance Radius Control Slider Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationSearching,
                        contentDescription = "Search radius",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Radius: ${String.format("%.1f", maxDistance)} mi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Max 10 mi",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Slider(
                            value = maxDistance.toFloat(),
                            onValueChange = { onDistanceChange(it.toDouble()) },
                            valueRange = 0.5f..10.0f,
                            steps = 18,
                            colors = SliderDefaults.colors(
                                thumbColor = EcoGreenPrimary,
                                activeTrackColor = EcoGreenPrimary,
                                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                                .testTag("map_distance_slider")
                        )
                    }
                }
            }
        }

        // Recenter FAB (Floating over map)
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(DEFAULT_NEIGHBORHOOD_CENTER, 13.5f),
                        durationMs = 500
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EcoGreenPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = if (selectedItem != null) 260.dp else 24.dp, end = 16.dp)
                .shadow(6.dp, CircleShape)
                .testTag("map_recenter_fab")
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Recenter to My Neighborhood",
                modifier = Modifier.size(24.dp)
            )
        }

        // Floating Item Preview Card (Slides up when marker/circle is tapped)
        AnimatedVisibility(
            visible = selectedItem != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            selectedItem?.let { item ->
                FloatingMapItemPreviewCard(
                    item = item,
                    onDismiss = { selectedItem = null },
                    onViewDetails = { onItemSelected(item) },
                    onStartBorrow = { onStartBorrow(item) }
                )
            }
        }
    }
}

/**
 * Custom Pin Marker rendered directly onto the Google Map.
 */
@Composable
fun ItemMapPinBadge(
    item: LendItem,
    isSelected: Boolean,
    isAvailable: Boolean
) {
    val badgeBg = if (isSelected) EcoGreenDark else if (isAvailable) EcoGreenPrimary else EcoAmber

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(2.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = badgeBg,
            shadowElevation = if (isSelected) 8.dp else 4.dp,
            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color.White) else null
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (item.iconCategory.uppercase()) {
                        "GARDEN" -> "🌱"
                        "KITCHEN" -> "🍳"
                        "CAMPING" -> "⛺"
                        "CLEANING" -> "✨"
                        "PARTY" -> "🎉"
                        "GAMES" -> "🎲"
                        else -> "🛠️"
                    },
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.title.take(16),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Pointer triangle
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(badgeBg)
        )
    }
}

/**
 * Floating preview card with photo, owner trust badge, condition, deposit,
 * and quick borrow action as requested in the design interview.
 */
@Composable
fun FloatingMapItemPreviewCard(
    item: LendItem,
    onDismiss: () -> Unit,
    onViewDetails: () -> Unit,
    onStartBorrow: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAvailable = item.availability == "AVAILABLE_NOW"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("map_item_preview_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category emoji, Title, Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(EcoGreenPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (item.iconCategory.uppercase()) {
                                "GARDEN" -> "🌱"
                                "KITCHEN" -> "🍳"
                                "CAMPING" -> "⛺"
                                "CLEANING" -> "✨"
                                "PARTY" -> "🎉"
                                "GAMES" -> "🎲"
                                else -> "🛠️"
                            },
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "${item.neighborhood} • ${item.distanceMiles} mi",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isAvailable) EcoGreenLight else EcoAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isAvailable) "Available" else "In Use",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isAvailable) EcoGreenDark else EcoAmber,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("dismiss_preview_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close preview",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Owner trust & verification badge, Deposit amount, Condition
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Verified Owner",
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.ownerName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = EcoAmber,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = String.format("%.1f", item.ownerRating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = "$${item.depositAmount.toInt()} deposit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Eco Impact indicator: CO2 & $ saved
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EcoGreenPrimary.copy(alpha = 0.08f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌱", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.estimatedCo2SavedKg.toInt()}kg CO₂ saved",
                            fontSize = 11.sp,
                            color = EcoGreenDark,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💰", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Save $${item.estimatedDollarSaved.toInt()}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actions: Details & Request Borrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("map_view_details_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Details", fontSize = 13.sp)
                }

                Button(
                    onClick = onStartBorrow,
                    enabled = isAvailable,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EcoGreenPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.3f)
                        .testTag("map_quick_borrow_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Handshake,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAvailable) "Request Borrow" else "Unavailable",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
