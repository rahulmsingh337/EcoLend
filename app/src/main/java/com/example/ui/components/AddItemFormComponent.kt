package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoEmerald
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoOrange
import java.io.File
import java.util.Calendar
import java.util.TimeZone

data class ToolPreset(
    val title: String,
    val category: String,
    val description: String,
    val deposit: Double,
    val condition: String,
    val wasteKg: Double,
    val durationDays: Int,
    val iconEmoji: String
)

val sampleToolPresets = listOf(
    ToolPreset(
        title = "DeWalt 20V Cordless Brushless Drill",
        category = "Power Tools",
        description = "High-torque 20V compact brushless drill kit. Includes 2x 2.0Ah lithium batteries, quick charger, and 14-piece drill & driver bit set in hard case.",
        deposit = 35.0,
        condition = "Like New",
        wasteKg = 6.8,
        durationDays = 7,
        iconEmoji = "🛠️"
    ),
    ToolPreset(
        title = "Sun Joe 2030 PSI Electric Pressure Washer",
        category = "Power Tools",
        description = "Powerful electric pressure washer with 5 quick-connect spray tips and dual detergent tanks. Ideal for deep cleaning decks, siding, and driveways.",
        deposit = 45.0,
        condition = "Gently Used",
        wasteKg = 14.5,
        durationDays = 3,
        iconEmoji = "💦"
    ),
    ToolPreset(
        title = "Greenworks 40V Cordless Lawn Mower",
        category = "Lawn & Garden",
        description = "20-inch brushless push lawn mower with mulching plug and rear collection bag. Runs quiet with zero gas fumes or oil maintenance.",
        deposit = 60.0,
        condition = "Like New",
        wasteKg = 22.0,
        durationDays = 3,
        iconEmoji = "🌿"
    ),
    ToolPreset(
        title = "Little Giant 17ft Multi-Position Ladder",
        category = "Home & DIY",
        description = "Aircraft-grade aluminum ladder converts to A-frame, extension, staircase, and 90-degree positions. Rated 300 lbs with wide flare legs.",
        deposit = 40.0,
        condition = "Gently Used",
        wasteKg = 12.0,
        durationDays = 7,
        iconEmoji = "🪜"
    ),
    ToolPreset(
        title = "Singer Heavy Duty 4432 Sewing Machine",
        category = "Home & DIY",
        description = "Heavy duty mechanical sewing machine with 32 built-in stitches and stainless steel bedplate. Great for canvas, denim, and clothing repairs.",
        deposit = 30.0,
        condition = "Like New",
        wasteKg = 7.5,
        durationDays = 14,
        iconEmoji = "🧵"
    ),
    ToolPreset(
        title = "Bissell Little Green Portable Carpet Cleaner",
        category = "Cleaning",
        description = "Compact spot cleaner with powerful suction and 3-inch tough stain brush. Erases pet messes, car interior grime, and couch spills.",
        deposit = 35.0,
        condition = "Gently Used",
        wasteKg = 8.2,
        durationDays = 3,
        iconEmoji = "✨"
    ),
    ToolPreset(
        title = "Coleman Montana 6-Person Camping Tent",
        category = "Camping & Outdoor",
        description = "Spacious family cabin dome tent with weathertec inverted seams, hinged door, and vestibule rainfly awning. Fast 10-minute setup.",
        deposit = 50.0,
        condition = "Gently Used",
        wasteKg = 9.8,
        durationDays = 7,
        iconEmoji = "⛺"
    )
)

data class FormCategoryItem(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

val formCategories = listOf(
    FormCategoryItem("Power Tools", Icons.Default.Build, Color(0xFF1E88E5)),
    FormCategoryItem("Lawn & Garden", Icons.Default.Park, Color(0xFF43A047)),
    FormCategoryItem("Home & DIY", Icons.Default.Handyman, Color(0xFFFB8C00)),
    FormCategoryItem("Camping & Outdoor", Icons.Default.Terrain, Color(0xFF00897B)),
    FormCategoryItem("Cleaning", Icons.Default.CleaningServices, Color(0xFF00ACC1)),
    FormCategoryItem("Kitchen", Icons.Default.Restaurant, Color(0xFF8E24AA)),
    FormCategoryItem("Moving & Heavy", Icons.Default.HomeRepairService, Color(0xFF5D4037)),
    FormCategoryItem("Other", Icons.Default.Eco, Color(0xFF2E7D32))
)

@Composable
fun AddItemFormComponent(
    onPublish: (
        title: String,
        description: String,
        category: String,
        deposit: Double,
        condition: String,
        wasteKg: Double,
        photoUri: String?,
        maxBorrowDurationDays: Int,
        availableFromMillis: Long,
        availableToMillis: Long,
        pickupLocationName: String,
        pickupAddress: String,
        pickupDistanceMiles: Double,
        isFuzzyPrivacy: Boolean,
        pickupNotes: String
    ) -> Unit,
    onCancel: () -> Unit,
    onLaunchCamera: (title: String, condition: String) -> Unit,
    capturedPhotoPath: String?,
    onRemovePhoto: () -> Unit,
    modifier: Modifier = Modifier,
    initialTitle: String = "",
    initialCategory: String = "Power Tools",
    initialCondition: String = "Like New",
    initialDescription: String = "",
    initialDeposit: Double = 30.0,
    initialWasteKg: Double = 8.5,
    initialMaxDurationDays: Int = 7
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form fields state
    var title by remember { mutableStateOf(initialTitle) }
    var category by remember { mutableStateOf(initialCategory) }
    var condition by remember { mutableStateOf(initialCondition) }
    var description by remember { mutableStateOf(initialDescription) }
    var depositStr by remember { mutableStateOf(if (initialDeposit > 0) initialDeposit.toInt().toString() else "30") }
    var wasteKgStr by remember { mutableStateOf(initialWasteKg.toString()) }
    var maxDurationDays by remember { mutableIntStateOf(initialMaxDurationDays) }

    // Active photo path (either captured from Camera or chosen via Gallery)
    var currentPhotoPath by remember { mutableStateOf<String?>(capturedPhotoPath) }

    // Sync external captured photo updates from Camera dialog
    LaunchedEffect(capturedPhotoPath) {
        if (capturedPhotoPath != null) {
            currentPhotoPath = capturedPhotoPath
        }
    }

    // Zero-permission Android Photo Picker for gallery image selection
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentPhotoPath = uri.toString()
        }
    }

    // Validation state
    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var depositError by remember { mutableStateOf<String?>(null) }

    // Preset selected feedback banner
    var appliedPresetFeedback by remember { mutableStateOf<String?>(null) }

    // Availability dates
    val todayUtcMillis = remember {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }
    val availableFromMillis = todayUtcMillis
    val availableToMillis = todayUtcMillis + (maxDurationDays * 86400000L)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. Header Card: Community Sharing Incentive ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_item_header_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Offer Equipment to Neighbors",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Lend idle tools to keep gear in circular use. Earn +30 Eco Karma upon publishing!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EcoGreenPrimary,
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Text(
                        text = "+30 Karma",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // --- 2. Quick-Fill Tool Presets ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Handyman,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Smart Quick-Fill Presets",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "1-Tap Fill",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "Tap a common household item to pre-fill specs, recommended deposits, and eco stats:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    sampleToolPresets.forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (title == preset.title) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            },
                            modifier = Modifier
                                .clickable {
                                    title = preset.title
                                    category = preset.category
                                    description = preset.description
                                    condition = preset.condition
                                    depositStr = preset.deposit.toInt().toString()
                                    wasteKgStr = preset.wasteKg.toString()
                                    maxDurationDays = preset.durationDays
                                    appliedPresetFeedback = "Loaded preset: ${preset.title}"
                                    titleError = null
                                    descriptionError = null
                                }
                                .testTag("preset_${preset.title.take(10).replace(" ", "_")}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = preset.iconEmoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = preset.title.take(24) + if (preset.title.length > 24) "..." else "",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (title == preset.title) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$${preset.deposit.toInt()} deposit · ${preset.wasteKg} kg saved",
                                        fontSize = 10.sp,
                                        color = if (title == preset.title) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                // Preset Feedback
                if (appliedPresetFeedback != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EcoGreenDark, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = appliedPresetFeedback ?: "",
                                fontSize = 11.sp,
                                color = EcoGreenDark,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // --- 3. Image Upload via Camera & Gallery ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_item_photo_section"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Item Photograph",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (currentPhotoPath != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EcoGreenPrimary
                        ) {
                            Text(
                                text = "Photo Attached",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "Clear photos build borrower trust and speed up loan acceptance.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                )

                // Photo Display or Placeholder Card
                val bitmap = remember(currentPhotoPath) {
                    loadBitmapFromPathOrUri(context, currentPhotoPath)
                }

                if (bitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                    ) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Captured Tool Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Condition flair overlay
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = condition,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Remove / Retake Actions Row
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onLaunchCamera(title, condition) },
                                    modifier = Modifier.size(36.dp).testTag("retake_photo_btn")
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Retake Photo", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                IconButton(
                                    onClick = {
                                        currentPhotoPath = null
                                        onRemovePhoto()
                                    },
                                    modifier = Modifier.size(36.dp).testTag("delete_photo_btn")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Photo", tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                } else {
                    // Empty state: Dotted upload container
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Snap a live photo of your equipment",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Camera reticle aligns tool against neutral background",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dual buttons: In-App Camera + Gallery Fallback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onLaunchCamera(title, condition) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("launch_camera_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentPhotoPath != null) "Retake (Camera)" else "Take Photo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            galleryPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("choose_gallery_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pick from Gallery", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // --- 4. Title, Category, Description & Condition Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_item_details_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tool Specifications",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        if (it.length <= 60) {
                            title = it
                            if (it.isNotBlank()) titleError = null
                        }
                    },
                    label = { Text("Item Title *") },
                    placeholder = { Text("e.g. DeWalt 20V Cordless Brushless Drill") },
                    isError = titleError != null,
                    supportingText = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = titleError ?: "Clear brand & model name", color = if (titleError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "${title.length}/60", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_title_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Chips Selector
                Text(
                    text = "Category *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    formCategories.forEach { catItem ->
                        FilterChip(
                            selected = category == catItem.name,
                            onClick = { category = catItem.name },
                            label = { Text(catItem.name, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = catItem.icon,
                                    contentDescription = null,
                                    tint = if (category == catItem.name) Color.White else catItem.color,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("cat_chip_${catItem.name.replace(" ", "_")}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Condition Selector
                Text(
                    text = "Working Condition *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                val conditions = listOf(
                    Triple("Like New", EcoGreenDark, "Pristine, full power"),
                    Triple("Gently Used", Color(0xFF1976D2), "Clean, cosmetic wear"),
                    Triple("Functional", Color(0xFFE65100), "Older unit, fully working")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    conditions.forEach { (condName, condColor, condDesc) ->
                        val isSelected = condition == condName
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) condColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) condColor else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { condition = condName }
                                .testTag("cond_${condName.replace(" ", "_")}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = condColor, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = condName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) condColor else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = condDesc,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        if (it.isNotBlank()) descriptionError = null
                    },
                    label = { Text("Description & Included Accessories *") },
                    placeholder = { Text("List what's included (batteries, charger, bits, safety goggles), usage tips, or special handling notes.") },
                    minLines = 3,
                    maxLines = 6,
                    isError = descriptionError != null,
                    supportingText = {
                        Text(
                            text = descriptionError ?: "Neighbors appreciate knowing about attachments & battery status",
                            color = if (descriptionError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("item_description_input")
                )
            }
        }

        // --- 5. Lending Terms, Deposit & Eco Impact Card ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("add_item_terms_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Deposit Hold & Sustainability Impact",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Refundable Deposit Field
                    OutlinedTextField(
                        value = depositStr,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() } && input.length <= 4) {
                                depositStr = input
                                depositError = null
                            }
                        },
                        label = { Text("Deposit ($) *") },
                        prefix = { Text("$", fontWeight = FontWeight.Bold) },
                        placeholder = { Text("30") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = depositError != null,
                        supportingText = { Text("100% refunded upon return", fontSize = 10.sp) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("item_deposit_input")
                    )

                    // Estimated Waste Diverted
                    OutlinedTextField(
                        value = wasteKgStr,
                        onValueChange = { input ->
                            if (input.matches(Regex("^\\d*\\.?\\d*$")) && input.length <= 5) {
                                wasteKgStr = input
                            }
                        },
                        label = { Text("Waste Saved (kg)") },
                        suffix = { Text("kg", fontWeight = FontWeight.Medium) },
                        placeholder = { Text("8.5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        supportingText = { Text("Mfg. waste prevented", fontSize = 10.sp) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("item_waste_input")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Max Loan Duration Window
                Text(
                    text = "Recommended Max Loan Duration",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                val durationOptions = listOf(3, 7, 14, 30)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durationOptions.forEach { days ->
                        FilterChip(
                            selected = maxDurationDays == days,
                            onClick = { maxDurationDays = days },
                            label = { Text("$days Days", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("duration_${days}d")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Community Safety Reassurance Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Porch pickup coordinates remain fuzzy until loan is approved. Exact home addresses are never published publicly.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // --- 6. Publishing Action Buttons ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    var hasError = false
                    if (title.isBlank()) {
                        titleError = "Title is required"
                        hasError = true
                    }
                    if (description.isBlank()) {
                        descriptionError = "Please add a brief description of the item"
                        hasError = true
                    }
                    val dep = depositStr.toDoubleOrNull()
                    if (dep == null || dep < 0) {
                        depositError = "Enter a valid deposit amount"
                        hasError = true
                    }

                    if (!hasError) {
                        val parsedWaste = wasteKgStr.toDoubleOrNull() ?: 8.5
                        onPublish(
                            title.trim(),
                            description.trim(),
                            category,
                            dep ?: 30.0,
                            condition,
                            parsedWaste,
                            currentPhotoPath,
                            maxDurationDays,
                            availableFromMillis,
                            availableToMillis,
                            "Oakridge Porch Box #4",
                            "Oakridge Woods, Sector 4",
                            0.3,
                            true,
                            "Porch pickup locker with code provided upon loan approval."
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("publish_new_item_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Publish Tool to Community (+30 Karma)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("cancel_add_item_btn"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Cancel & Return to Feed",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun loadBitmapFromPathOrUri(context: Context, pathOrUri: String?): Bitmap? {
    if (pathOrUri.isNullOrBlank()) return null
    return try {
        if (pathOrUri.startsWith("content://")) {
            val uri = Uri.parse(pathOrUri)
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
        } else {
            val file = File(pathOrUri)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                BitmapFactory.decodeFile(pathOrUri)
            }
        }
    } catch (_: Exception) {
        null
    }
}
