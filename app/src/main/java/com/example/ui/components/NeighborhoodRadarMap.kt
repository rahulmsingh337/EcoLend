package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LendItem
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun NeighborhoodRadarMap(
    items: List<LendItem>,
    maxDistance: Double,
    onDistanceChange: (Double) -> Unit,
    onItemSelected: (LendItem) -> Unit,
    onStartBorrow: (LendItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItem by remember { mutableStateOf<LendItem?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Radar Radius & Filter Control Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationSearching,
                        contentDescription = "Search radius",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Neighborhood Radius",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = String.format("%.1f miles", maxDistance),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = maxDistance.toFloat(),
                            onValueChange = { onDistanceChange(it.toDouble()) },
                            valueRange = 0.5f..5.0f,
                            steps = 9,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }
            }

            // Radar Canvas Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Interactive Radar Canvas
                val primaryColor = MaterialTheme.colorScheme.primary
                val onSurfaceColor = MaterialTheme.colorScheme.onSurface

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .pointerInput(items, maxDistance) {
                            detectTapGestures { tapOffset ->
                                val centerX = size.width / 2f
                                val centerY = size.height / 2f
                                val maxRadiusPx = minOf(centerX, centerY) * 0.85f

                                // Check which item was tapped
                                var nearestItem: LendItem? = null
                                var nearestDist = 50f // touch target radius in px

                                items.forEachIndexed { index, item ->
                                    val angleRad = Math.toRadians((index * 47.0 + 25.0) % 360.0)
                                    val normalizedDist = (item.distanceMiles / maxDistance).coerceIn(0.1, 1.0)
                                    val itemRadius = (normalizedDist * maxRadiusPx).toFloat()
                                    val itemX = centerX + itemRadius * cos(angleRad).toFloat()
                                    val itemY = centerY + itemRadius * sin(angleRad).toFloat()

                                    val dist = kotlin.math.hypot(tapOffset.x - itemX, tapOffset.y - itemY)
                                    if (dist < nearestDist) {
                                        nearestDist = dist
                                        nearestItem = item
                                    }
                                }
                                selectedItem = nearestItem
                            }
                        }
                ) {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val maxRadiusPx = minOf(centerX, centerY) * 0.85f

                    // Draw neighborhood grid crosshairs
                    drawLine(
                        color = primaryColor.copy(alpha = 0.15f),
                        start = Offset(centerX, centerY - maxRadiusPx),
                        end = Offset(centerX, centerY + maxRadiusPx),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = primaryColor.copy(alpha = 0.15f),
                        start = Offset(centerX - maxRadiusPx, centerY),
                        end = Offset(centerX + maxRadiusPx, centerY),
                        strokeWidth = 1.5f
                    )

                    // Draw concentric distance rings
                    val rings = listOf(0.33f, 0.66f, 1.0f)
                    rings.forEach { ratio ->
                        drawCircle(
                            color = primaryColor.copy(alpha = 0.2f),
                            radius = maxRadiusPx * ratio,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 1.5f)
                        )
                    }

                    // Center marker (User Home)
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.2f),
                        radius = 22f,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = primaryColor,
                        radius = 9f,
                        center = Offset(centerX, centerY)
                    )

                    // Draw item blips
                    items.forEachIndexed { index, item ->
                        val angleRad = Math.toRadians((index * 47.0 + 25.0) % 360.0)
                        val normalizedDist = (item.distanceMiles / maxDistance).coerceIn(0.1, 1.0)
                        val itemRadius = (normalizedDist * maxRadiusPx).toFloat()
                        val itemX = centerX + itemRadius * cos(angleRad).toFloat()
                        val itemY = centerY + itemRadius * sin(angleRad).toFloat()

                        val isAvailable = item.availability == "AVAILABLE_NOW"
                        val blipColor = if (isAvailable) EcoGreenPrimary else EcoAmber
                        val isCurrentSelected = selectedItem?.id == item.id

                        if (isCurrentSelected) {
                            // Pulsing halo for selected item
                            drawCircle(
                                color = blipColor.copy(alpha = 0.35f),
                                radius = 26f,
                                center = Offset(itemX, itemY)
                            )
                        }

                        // Outer ring
                        drawCircle(
                            color = Color.White,
                            radius = if (isCurrentSelected) 14f else 11f,
                            center = Offset(itemX, itemY)
                        )
                        // Inner dot
                        drawCircle(
                            color = blipColor,
                            radius = if (isCurrentSelected) 11f else 8f,
                            center = Offset(itemX, itemY)
                        )
                    }
                }

                // Legend at top-right
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EcoGreenPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Available", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(EcoAmber)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("In Use", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Bottom Selected Item Preview Card
            AnimatedVisibility(visible = selectedItem != null) {
                selectedItem?.let { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .testTag("map_item_preview_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${item.neighborhood} • ${item.distanceMiles} mi",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                AvailabilityChip(availability = item.availability)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            EcoImpactBadge(
                                wasteKg = item.estimatedWasteSavedKg,
                                dollarsSaved = item.estimatedDollarSaved
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onItemSelected(item) },
                                    modifier = Modifier.weight(1f).testTag("map_view_details_btn")
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Details", fontSize = 13.sp)
                                }
                                Button(
                                    onClick = { onStartBorrow(item) },
                                    enabled = item.availability == "AVAILABLE_NOW",
                                    modifier = Modifier.weight(1f).testTag("map_quick_borrow_btn"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("Borrow Now", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
