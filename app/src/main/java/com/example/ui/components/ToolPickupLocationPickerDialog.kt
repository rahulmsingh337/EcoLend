package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoSage
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class PickupLocationSuggestion(
    val name: String,
    val address: String,
    val distanceMiles: Double,
    val icon: ImageVector,
    val isCommunityHub: Boolean,
    val defaultNormalizedRadius: Float,
    val defaultAngleDegrees: Float
)

@Composable
fun ToolPickupLocationPickerDialog(
    initialLocationName: String,
    initialAddress: String,
    initialDistanceMiles: Double,
    initialIsFuzzy: Boolean,
    initialPickupNotes: String,
    onDismiss: () -> Unit,
    onConfirm: (
        locationName: String,
        address: String,
        distanceMiles: Double,
        isFuzzy: Boolean,
        pickupNotes: String
    ) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocationName by remember { mutableStateOf(initialLocationName.ifBlank { "Maplewood Community Garden" }) }
    var selectedAddress by remember { mutableStateOf(initialAddress.ifBlank { "452 Maple Street, Maplewood Heights" }) }
    var selectedDistanceMiles by remember { mutableDoubleStateOf(if (initialDistanceMiles <= 0.0) 0.2 else initialDistanceMiles) }
    var isFuzzyPrivacy by remember { mutableStateOf(initialIsFuzzy) }
    var pickupNotes by remember { mutableStateOf(initialPickupNotes) }

    // Pin position relative to radar center (normalized radius 0.0 to 1.0 and angle in radians)
    var pinNormRadius by remember { mutableFloatStateOf((selectedDistanceMiles / 1.5).coerceIn(0.1, 0.85).toFloat()) }
    var pinAngleRad by remember { mutableFloatStateOf(Math.toRadians(45.0).toFloat()) }

    val presetSuggestions = remember {
        listOf(
            PickupLocationSuggestion(
                name = "Maplewood Community Garden",
                address = "452 Maple Street, Locker Box #4",
                distanceMiles = 0.2,
                icon = Icons.Default.Park,
                isCommunityHub = true,
                defaultNormalizedRadius = 0.25f,
                defaultAngleDegrees = 40f
            ),
            PickupLocationSuggestion(
                name = "Westside Tool Lending Shed",
                address = "128 Oak Avenue, Workshop Depot",
                distanceMiles = 0.4,
                icon = Icons.Default.Storefront,
                isCommunityHub = true,
                defaultNormalizedRadius = 0.45f,
                defaultAngleDegrees = 135f
            ),
            PickupLocationSuggestion(
                name = "Residential Porch Lockbox",
                address = "789 Pine Ridge Rd, Contactless Bench",
                distanceMiles = 0.1,
                icon = Icons.Default.Lock,
                isCommunityHub = false,
                defaultNormalizedRadius = 0.18f,
                defaultAngleDegrees = 220f
            ),
            PickupLocationSuggestion(
                name = "Greenwood Eco Café Hub",
                address = "234 Cedar Boulevard, Exchange Counter",
                distanceMiles = 0.6,
                icon = Icons.Default.MeetingRoom,
                isCommunityHub = true,
                defaultNormalizedRadius = 0.60f,
                defaultAngleDegrees = 300f
            ),
            PickupLocationSuggestion(
                name = "Central Park Recreation Pavilion",
                address = "512 Birchwood Way, Front Kiosk",
                distanceMiles = 0.8,
                icon = Icons.Default.Park,
                isCommunityHub = true,
                defaultNormalizedRadius = 0.75f,
                defaultAngleDegrees = 90f
            ),
            PickupLocationSuggestion(
                name = "Private Garage / Driveway",
                address = "890 Elm Terrace, Maplewood",
                distanceMiles = 0.3,
                icon = Icons.Default.LocationOn,
                isCommunityHub = false,
                defaultNormalizedRadius = 0.35f,
                defaultAngleDegrees = 180f
            )
        )
    }

    val filteredSuggestions = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            presetSuggestions
        } else {
            presetSuggestions.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.address.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("tool_pickup_location_picker_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog Header
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
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Select Pickup & Drop-Off Point",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Move map pin or choose a neighborhood hub",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_location_picker_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Address Search Autocomplete Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search address, garden, or tool shed...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("location_search_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick Autocomplete Dropdown suggestions if typing
                AnimatedVisibility(visible = searchQuery.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            filteredSuggestions.take(3).forEach { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedLocationName = suggestion.name
                                            selectedAddress = suggestion.address
                                            selectedDistanceMiles = suggestion.distanceMiles
                                            pinNormRadius = suggestion.defaultNormalizedRadius
                                            pinAngleRad = Math.toRadians(suggestion.defaultAngleDegrees.toDouble()).toFloat()
                                            searchQuery = ""
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = suggestion.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = suggestion.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = suggestion.address,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "${suggestion.distanceMiles} mi",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EcoGreenDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Interactive Radar Map Canvas with Pin
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                        .testTag("interactive_location_radar_canvas"),
                    contentAlignment = Alignment.Center
                ) {
                    val primaryColor = MaterialTheme.colorScheme.primary
                    val fuzzyRingColor = EcoGreenPrimary.copy(alpha = 0.22f)
                    val fuzzyBorderColor = EcoGreenDark.copy(alpha = 0.5f)
                    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures { tapOffset ->
                                    val centerX = size.width / 2f
                                    val centerY = size.height / 2f
                                    val maxRadiusPx = minOf(centerX, centerY) * 0.85f

                                    val dx = tapOffset.x - centerX
                                    val dy = tapOffset.y - centerY
                                    val distPx = hypot(dx, dy)

                                    val normR = (distPx / maxRadiusPx).coerceIn(0.08f, 0.95f)
                                    val angle = atan2(dy, dx)

                                    pinNormRadius = normR
                                    pinAngleRad = angle

                                    // Calculate miles (max radius corresponds to 1.5 miles)
                                    val calculatedMiles = Math.round((normR * 1.5) * 10.0) / 10.0
                                    selectedDistanceMiles = calculatedMiles.coerceAtLeast(0.1)

                                    // Auto-update location label if not preset
                                    if (!presetSuggestions.any { it.name == selectedLocationName }) {
                                        selectedLocationName = "Custom Pin (${selectedDistanceMiles} mi away)"
                                    }
                                }
                            }
                    ) {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val maxRadiusPx = minOf(centerX, centerY) * 0.85f

                        // Draw Grid lines
                        drawLine(
                            color = gridColor,
                            start = Offset(centerX, centerY - maxRadiusPx),
                            end = Offset(centerX, centerY + maxRadiusPx),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = gridColor,
                            start = Offset(centerX - maxRadiusPx, centerY),
                            end = Offset(centerX + maxRadiusPx, centerY),
                            strokeWidth = 1f
                        )

                        // Draw Concentric Radar Rings
                        val ringSteps = listOf(0.33f, 0.66f, 1.0f)
                        val ringDistances = listOf("0.2 mi", "0.5 mi", "1.0 mi")
                        ringSteps.forEachIndexed { i, step ->
                            val r = maxRadiusPx * step
                            drawCircle(
                                color = gridColor,
                                radius = r,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 1.2f)
                            )
                        }

                        // Home Base (User's anchor)
                        drawCircle(
                            color = Color(0xFF1976D2),
                            radius = 6.dp.toPx(),
                            center = Offset(centerX, centerY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.5.dp.toPx(),
                            center = Offset(centerX, centerY)
                        )

                        // Calculate current Pin coordinates
                        val pinRadiusPx = pinNormRadius * maxRadiusPx
                        val pinX = centerX + pinRadiusPx * cos(pinAngleRad)
                        val pinY = centerY + pinRadiusPx * sin(pinAngleRad)
                        val pinCenter = Offset(pinX, pinY)

                        // Draw Fuzzy Privacy Zone circle if enabled (~0.25 mile radius)
                        if (isFuzzyPrivacy) {
                            val fuzzyRadiusPx = 36.dp.toPx()
                            drawCircle(
                                color = fuzzyRingColor,
                                radius = fuzzyRadiusPx,
                                center = pinCenter
                            )
                            drawCircle(
                                color = fuzzyBorderColor,
                                radius = fuzzyRadiusPx,
                                center = pinCenter,
                                style = Stroke(width = 1.5f)
                            )
                        }

                        // Draw Teardrop Location Pin
                        val pinHeadRadius = 9.dp.toPx()
                        drawCircle(
                            color = EcoGreenDark,
                            radius = pinHeadRadius,
                            center = Offset(pinX, pinY - 10.dp.toPx())
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.5.dp.toPx(),
                            center = Offset(pinX, pinY - 10.dp.toPx())
                        )

                        // Pin stem
                        val path = Path().apply {
                            moveTo(pinX - pinHeadRadius * 0.7f, pinY - 8.dp.toPx())
                            lineTo(pinX, pinY)
                            lineTo(pinX + pinHeadRadius * 0.7f, pinY - 8.dp.toPx())
                            close()
                        }
                        drawPath(path = path, color = EcoGreenDark)
                    }

                    // Radar Helper Overlay Badges
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = null,
                                    tint = Color(0xFF1976D2),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Home Base Center",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EcoGreenDark
                        ) {
                            Text(
                                text = "Tap map to reposition pin",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Quick Community Hub Presets Row
                Text(
                    text = "Recommended Neighborhood Exchange Hubs:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("community_hub_chips_row")
                ) {
                    items(presetSuggestions) { hub ->
                        val isSelected = selectedLocationName == hub.name
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = borderCard(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                1.dp
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedLocationName = hub.name
                                    selectedAddress = hub.address
                                    selectedDistanceMiles = hub.distanceMiles
                                    pinNormRadius = hub.defaultNormalizedRadius
                                    pinAngleRad = Math.toRadians(hub.defaultAngleDegrees.toDouble()).toFloat()
                                }
                                .testTag("hub_chip_${hub.name.lowercase().replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = hub.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = hub.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Location Summary Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = EcoGreenDark,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedLocationName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$selectedAddress • ${selectedDistanceMiles} mi away",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Lender Address Privacy (Fuzzy Radius) Switch
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (isFuzzyPrivacy) EcoGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Approximate Fuzzy Privacy Zone",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isFuzzyPrivacy) {
                                        "Masks exact address (~0.25 mi) until request approved"
                                    } else {
                                        "Exact address shown publicly on listing"
                                    },
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = isFuzzyPrivacy,
                            onCheckedChange = { isFuzzyPrivacy = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("fuzzy_privacy_toggle")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 5. Pickup / Lockbox Access Instructions
                OutlinedTextField(
                    value = pickupNotes,
                    onValueChange = { pickupNotes = it },
                    label = { Text("Special Pickup / Porch Notes (Optional)") },
                    placeholder = { Text("e.g., Leave on porch bench; lockbox code sent on approval") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pickup_notes_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons (Cancel / Confirm)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("cancel_location_picker_btn")
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            onConfirm(
                                selectedLocationName,
                                selectedAddress,
                                selectedDistanceMiles,
                                isFuzzyPrivacy,
                                pickupNotes
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(48.dp)
                            .testTag("confirm_location_picker_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Confirm Location", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun borderCard(color: Color, width: androidx.compose.ui.unit.Dp): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(width, color)
}
