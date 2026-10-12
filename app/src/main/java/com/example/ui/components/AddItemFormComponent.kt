package com.example.ui.components

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoEmerald
import com.example.ui.theme.EcoGold
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoOrange
import com.example.ui.theme.EcoSage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class AddItemStep(
    val stepIndex: Int,
    val title: String,
    val subtitle: String
) {
    ESSENTIALS(1, "Essentials", "Title & Category"),
    CONDITION(2, "Condition", "Wear & Working Status"),
    TERMS(3, "Availability & Location", "Dates, Map Pin & Impact"),
    PREVIEW(4, "Photos & Review", "Live Listing Card")
}

data class ToolCategoryOption(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val description: String
)

data class ConditionGuideItem(
    val name: String,
    val wearLevel: String,
    val badgeColor: Color,
    val description: String,
    val lenderExpectation: String
)

data class SampleToolPreset(
    val title: String,
    val category: String,
    val condition: String,
    val description: String,
    val deposit: Double,
    val wasteKg: Double,
    val durationDays: Int,
    val iconCategory: String,
    val location: String
)

@OptIn(ExperimentalLayoutApi::class)
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
    var currentStep by remember { mutableStateOf(AddItemStep.ESSENTIALS) }

    // Date Range State
    val todayUtcMillis = remember {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }
    var availableFromMillis by remember { mutableStateOf(todayUtcMillis) }
    var availableToMillis by remember { mutableStateOf(todayUtcMillis + initialMaxDurationDays * 86400000L) }
    var showDateRangeDialog by remember { mutableStateOf(false) }

    // Location & Pickup Point State
    var pickupLocationName by remember { mutableStateOf("Maplewood Community Garden") }
    var pickupAddress by remember { mutableStateOf("452 Maple Street, Locker Box #4") }
    var pickupDistanceMiles by remember { mutableDoubleStateOf(0.2) }
    var isFuzzyPrivacy by remember { mutableStateOf(true) }
    var pickupNotes by remember { mutableStateOf("") }
    var showLocationPickerDialog by remember { mutableStateOf(false) }

    // Form fields
    var title by remember { mutableStateOf(initialTitle) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var condition by remember { mutableStateOf(initialCondition) }
    var description by remember { mutableStateOf(initialDescription) }
    var depositStr by remember { mutableStateOf(initialDeposit.toInt().toString()) }
    var wasteKgStr by remember { mutableStateOf(initialWasteKg.toString()) }
    var maxDurationDays by remember { mutableIntStateOf(initialMaxDurationDays) }

    // Error states
    var titleError by remember { mutableStateOf(false) }
    var descError by remember { mutableStateOf(false) }

    val categories = remember {
        listOf(
            ToolCategoryOption("Power Tools", Icons.Default.Build, EcoGreenPrimary, "Drills, saws, sanders, rotary tools"),
            ToolCategoryOption("Lawn & Garden", Icons.Default.Park, EcoSage, "Mowers, shears, trimmers, blowers"),
            ToolCategoryOption("Home Repair", Icons.Default.HomeRepairService, EcoEmerald, "Ladders, plumbing, levels, painting"),
            ToolCategoryOption("Kitchen & Cooking", Icons.Default.Restaurant, EcoAmber, "Mixers, food processors, canning"),
            ToolCategoryOption("Camping & Outdoors", Icons.Default.Terrain, EcoGreenDark, "Tents, coolers, camp stoves, packs"),
            ToolCategoryOption("Cleaning Gear", Icons.Default.CleaningServices, EcoEmerald, "Pressure washers, carpet cleaners"),
            ToolCategoryOption("Party & Events", Icons.Default.Celebration, EcoGold, "Chairs, folding tables, canopies, lights")
        )
    }

    val conditions = remember {
        listOf(
            ConditionGuideItem("Brand New", "Mint / Unused", Color(0xFF00C853), "Unopened or never used, in original packaging.", "Expect borrower to return in spotless condition."),
            ConditionGuideItem("Like New", "Excellent / Flawless", EcoGreenPrimary, "Used very few times. Cleaned, fully functional.", "Tested, calibrated, and ready for immediate work."),
            ConditionGuideItem("Very Good", "Minor cosmetic marks", Color(0xFF00897B), "Light surface scuffs from normal careful handling.", "Complete with essential accessories and guards intact."),
            ConditionGuideItem("Good Condition", "Normal wear & tear", EcoAmber, "Shows visible signs of use. Solid workhorse.", "Functional tool with cosmetic character."),
            ConditionGuideItem("Well Loved", "Vintage / Heavy use", EcoOrange, "Older equipment, but fully operational and safe.", "Tested for electrical and mechanical safety.")
        )
    }

    val samplePresets = remember {
        listOf(
            SampleToolPreset("DeWalt 20V Cordless Drill & Bit Set", "Power Tools", "Like New", "High-torque 1/2\" brushless drill with 2 lithium batteries and 30-piece bit kit.", 35.0, 9.2, 7, "TOOLS", "Maplewood Community Garden"),
            SampleToolPreset("Honda Self-Propelled Gas Lawn Mower", "Lawn & Garden", "Good Condition", "21-inch steel deck with twin blades and grass bag. Starts on first pull.", 50.0, 28.0, 3, "GARDEN", "Westside Tool Lending Shed"),
            SampleToolPreset("Sun Joe Electric High-Pressure Washer", "Cleaning Gear", "Very Good", "2030 PSI pressure washer with 5 quick-connect spray tips and 20ft hose.", 40.0, 14.5, 3, "CLEANING", "Residential Porch Lockbox"),
            SampleToolPreset("Coleman 4-Person Waterproof Dome Tent", "Camping & Outdoors", "Like New", "WeatherTec system dome tent with rainfly, color-coded poles, and ground stakes.", 30.0, 12.0, 7, "CAMPING", "Central Park Recreation Pavilion"),
            SampleToolPreset("Cuisinart 14-Cup Food Processor", "Kitchen & Cooking", "Like New", "Heavy-duty 720-watt motor, stainless steel slicing/shredding discs.", 25.0, 6.5, 3, "KITCHEN", "Greenwood Eco Café Hub"),
            SampleToolPreset("Werner 16ft Aluminum Extension Ladder", "Home Repair", "Good Condition", "Type II 225-lb load capacity extension ladder with non-marring end caps.", 45.0, 18.0, 7, "TOOLS", "Private Garage / Driveway")
        )
    }

    val photoBitmap = remember(capturedPhotoPath) {
        if (!capturedPhotoPath.isNullOrEmpty()) {
            try {
                BitmapFactory.decodeFile(capturedPhotoPath)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    val selectedConditionGuide = conditions.find { it.name == condition } ?: conditions[1]

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("add_item_form_component")
    ) {
        // Step Progress Tracker Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("add_item_step_header"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AddItemStep.values().forEach { step ->
                        val isCurrent = step == currentStep
                        val isPassed = step.stepIndex < currentStep.stepIndex
                        val canTap = isPassed || (step == AddItemStep.CONDITION && title.isNotBlank()) ||
                                (step == AddItemStep.TERMS && title.isNotBlank()) ||
                                (step == AddItemStep.PREVIEW && title.isNotBlank())

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = canTap) { currentStep = step }
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                                .testTag("step_pill_${step.stepIndex}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> MaterialTheme.colorScheme.primary
                                            isPassed -> EcoGreenDark
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPassed) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        text = "${step.stepIndex}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = step.title,
                                fontSize = 11.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { currentStep.stepIndex / 4f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Step ${currentStep.stepIndex} of 4: ${currentStep.subtitle}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${(currentStep.stepIndex * 25)}% complete",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Stepped Content Body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState.stepIndex > initialState.stepIndex) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "step_content_transition"
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    when (step) {
                        AddItemStep.ESSENTIALS -> {
                            StepOneEssentials(
                                title = title,
                                onTitleChange = {
                                    title = it
                                    if (it.isNotBlank()) titleError = false
                                },
                                titleError = titleError,
                                selectedCategory = selectedCategory,
                                onCategorySelected = { selectedCategory = it },
                                categories = categories,
                                samplePresets = samplePresets,
                                onSelectPreset = { preset ->
                                    title = preset.title
                                    selectedCategory = preset.category
                                    condition = preset.condition
                                    description = preset.description
                                    depositStr = preset.deposit.toInt().toString()
                                    wasteKgStr = preset.wasteKg.toString()
                                    maxDurationDays = preset.durationDays
                                    availableToMillis = availableFromMillis + preset.durationDays * 86400000L
                                    pickupLocationName = preset.location
                                    titleError = false
                                    descError = false
                                }
                            )
                        }

                        AddItemStep.CONDITION -> {
                            StepTwoCondition(
                                condition = condition,
                                onConditionChange = { condition = it },
                                conditions = conditions,
                                selectedGuide = selectedConditionGuide
                            )
                        }

                        AddItemStep.TERMS -> {
                            StepThreeTermsAndLocation(
                                description = description,
                                onDescriptionChange = {
                                    description = it
                                    if (it.isNotBlank()) descError = false
                                },
                                descError = descError,
                                availableFromMillis = availableFromMillis,
                                availableToMillis = availableToMillis,
                                maxDurationDays = maxDurationDays,
                                onOpenDatePicker = { showDateRangeDialog = true },
                                onApplyDurationPreset = { days ->
                                    maxDurationDays = days
                                    availableToMillis = availableFromMillis + days * 86400000L
                                },
                                pickupLocationName = pickupLocationName,
                                pickupAddress = pickupAddress,
                                pickupDistanceMiles = pickupDistanceMiles,
                                isFuzzyPrivacy = isFuzzyPrivacy,
                                onOpenLocationPicker = { showLocationPickerDialog = true },
                                depositStr = depositStr,
                                onDepositChange = { depositStr = it },
                                wasteKgStr = wasteKgStr,
                                onWasteKgChange = { wasteKgStr = it }
                            )
                        }

                        AddItemStep.PREVIEW -> {
                            StepFourPhotosAndPreview(
                                title = title,
                                category = selectedCategory,
                                condition = condition,
                                description = description,
                                deposit = depositStr.toDoubleOrNull() ?: 25.0,
                                wasteKg = wasteKgStr.toDoubleOrNull() ?: 7.5,
                                maxBorrowDurationDays = maxDurationDays,
                                availableFromMillis = availableFromMillis,
                                availableToMillis = availableToMillis,
                                pickupLocationName = pickupLocationName,
                                pickupDistanceMiles = pickupDistanceMiles,
                                isFuzzyPrivacy = isFuzzyPrivacy,
                                photoBitmap = photoBitmap,
                                onLaunchCamera = { onLaunchCamera(title, condition) },
                                onRemovePhoto = onRemovePhoto
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Sticky Bottom Step Navigation Footer
        Surface(
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep == AddItemStep.ESSENTIALS) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("form_cancel_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            currentStep = when (currentStep) {
                                AddItemStep.CONDITION -> AddItemStep.ESSENTIALS
                                AddItemStep.TERMS -> AddItemStep.CONDITION
                                AddItemStep.PREVIEW -> AddItemStep.TERMS
                                else -> AddItemStep.ESSENTIALS
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("form_previous_step_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Back", fontWeight = FontWeight.SemiBold)
                    }
                }

                if (currentStep != AddItemStep.PREVIEW) {
                    Button(
                        onClick = {
                            when (currentStep) {
                                AddItemStep.ESSENTIALS -> {
                                    if (title.trim().isBlank()) {
                                        titleError = true
                                    } else {
                                        currentStep = AddItemStep.CONDITION
                                    }
                                }
                                AddItemStep.CONDITION -> {
                                    currentStep = AddItemStep.TERMS
                                }
                                AddItemStep.TERMS -> {
                                    if (description.trim().isBlank()) {
                                        descError = true
                                    } else {
                                        currentStep = AddItemStep.PREVIEW
                                    }
                                }
                                else -> {}
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(50.dp)
                            .testTag("form_next_step_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = when (currentStep) {
                                AddItemStep.ESSENTIALS -> "Next: Condition"
                                AddItemStep.CONDITION -> "Next: Logistics"
                                AddItemStep.TERMS -> "Next: Preview"
                                else -> "Continue"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            val trimmedTitle = title.trim()
                            val trimmedDesc = description.trim()
                            if (trimmedTitle.isBlank()) {
                                currentStep = AddItemStep.ESSENTIALS
                                titleError = true
                                return@Button
                            }
                            if (trimmedDesc.isBlank()) {
                                currentStep = AddItemStep.TERMS
                                descError = true
                                return@Button
                            }
                            val deposit = depositStr.toDoubleOrNull() ?: 20.0
                            val wasteKg = wasteKgStr.toDoubleOrNull() ?: 6.5
                            onPublish(
                                trimmedTitle,
                                trimmedDesc,
                                selectedCategory,
                                deposit,
                                condition,
                                wasteKg,
                                capturedPhotoPath,
                                maxDurationDays,
                                availableFromMillis,
                                availableToMillis,
                                pickupLocationName,
                                pickupAddress,
                                pickupDistanceMiles,
                                isFuzzyPrivacy,
                                pickupNotes
                            )
                        },
                        modifier = Modifier
                            .weight(1.6f)
                            .height(50.dp)
                            .testTag("publish_new_item_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Eco, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add to Sharing Pool", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }

    // Material 3 Date Range Picker Dialog
    if (showDateRangeDialog) {
        ToolAvailabilityDateRangeDialog(
            initialStartDateMillis = availableFromMillis,
            initialEndDateMillis = availableToMillis,
            onDismiss = { showDateRangeDialog = false },
            onConfirm = { startDate, endDate, durationDays ->
                availableFromMillis = startDate
                availableToMillis = endDate
                maxDurationDays = durationDays
                showDateRangeDialog = false
            }
        )
    }

    // Interactive Pickup Location & Radar Map Picker Dialog
    if (showLocationPickerDialog) {
        ToolPickupLocationPickerDialog(
            initialLocationName = pickupLocationName,
            initialAddress = pickupAddress,
            initialDistanceMiles = pickupDistanceMiles,
            initialIsFuzzy = isFuzzyPrivacy,
            initialPickupNotes = pickupNotes,
            onDismiss = { showLocationPickerDialog = false },
            onConfirm = { name, address, dist, fuzzy, notes ->
                pickupLocationName = name
                pickupAddress = address
                pickupDistanceMiles = dist
                isFuzzyPrivacy = fuzzy
                pickupNotes = notes
                showLocationPickerDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// STEP 1: Essentials
// -------------------------------------------------------------
@Composable
private fun StepOneEssentials(
    title: String,
    onTitleChange: (String) -> Unit,
    titleError: Boolean,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<ToolCategoryOption>,
    samplePresets: List<SampleToolPreset>,
    onSelectPreset: (SampleToolPreset) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.fillMaxWidth().testTag("add_item_eco_banner")
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Empower Your Neighbors 🌱", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Every shared item eliminates carbon emissions and saves neighbors money. Earn +30 Karma on listing!", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f), lineHeight = 15.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text("⚡ Quick Pick from Neighborhood Presets", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Tap a common tool to auto-populate title, specs, deposit, and eco-impact:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().testTag("quick_tool_presets_row")) {
            items(samplePresets) { preset ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = borderCard(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), 1.dp),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable { onSelectPreset(preset) }.testTag("preset_${preset.category.lowercase().replace(" ", "_")}")
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (preset.category) {
                                "Power Tools" -> Icons.Default.Build
                                "Lawn & Garden" -> Icons.Default.Park
                                "Home Repair" -> Icons.Default.HomeRepairService
                                "Kitchen & Cooking" -> Icons.Default.Restaurant
                                "Camping & Outdoors" -> Icons.Default.Terrain
                                else -> Icons.Default.CleaningServices
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(text = preset.title.take(24) + if (preset.title.length > 24) "..." else "", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "${preset.category} • $${preset.deposit.toInt()} dep", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Item Name *", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("e.g., DeWalt 20V Cordless Drill & Bit Set") },
            isError = titleError,
            supportingText = {
                if (titleError) Text("Title is required so neighbors can identify your item.")
                else Text("${title.length}/60 characters • Use brand & model if known")
            },
            singleLine = true,
            trailingIcon = {
                if (title.isNotEmpty()) {
                    IconButton(onClick = { onTitleChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("add_item_title_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text("Select Category *", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Categories organize tools across local searches and the neighborhood radar map.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(10.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().testTag("add_item_category_selector")) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat.name
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = if (isSelected) borderCard(MaterialTheme.colorScheme.primary, 1.5.dp) else borderCard(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), 1.dp),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onCategorySelected(cat.name) }.testTag("category_option_${cat.name.lowercase().replace(" ", "_").replace("&", "and")}")
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isSelected) MaterialTheme.colorScheme.primary else cat.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(cat.icon, contentDescription = null, tint = if (isSelected) Color.White else cat.color, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cat.name, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold)
                            Text(cat.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: Condition
// -------------------------------------------------------------
@Composable
private fun StepTwoCondition(
    condition: String,
    onConditionChange: (String) -> Unit,
    conditions: List<ConditionGuideItem>,
    selectedGuide: ConditionGuideItem
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Item Condition & Wear Assessment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Accurate condition assessments build community trust and prevent lending disputes.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().testTag("add_item_condition_selector")) {
            conditions.forEach { cond ->
                val isSelected = condition == cond.name
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) cond.badgeColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = if (isSelected) borderCard(cond.badgeColor, 2.dp) else borderCard(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), 1.dp),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onConditionChange(cond.name) }.testTag("condition_card_${cond.name.lowercase().replace(" ", "_")}")
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(14.dp).clip(CircleShape).background(cond.badgeColor))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(cond.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(shape = RoundedCornerShape(6.dp), color = cond.badgeColor.copy(alpha = 0.2f)) {
                                    Text(cond.wearLevel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = cond.badgeColor, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(cond.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = cond.badgeColor, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)), modifier = Modifier.fillMaxWidth().testTag("condition_guide_details_card")) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Borrower Expectation Note", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Neighbors borrowing a '${selectedGuide.name}' tool will see:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.height(4.dp))
                Text("\"${selectedGuide.lenderExpectation}\"", fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 3: Terms, Date Window, Map Pickup Location & Eco Impact
// -------------------------------------------------------------
@Composable
private fun StepThreeTermsAndLocation(
    description: String,
    onDescriptionChange: (String) -> Unit,
    descError: Boolean,
    availableFromMillis: Long,
    availableToMillis: Long,
    maxDurationDays: Int,
    onOpenDatePicker: () -> Unit,
    onApplyDurationPreset: (Int) -> Unit,
    pickupLocationName: String,
    pickupAddress: String,
    pickupDistanceMiles: Double,
    isFuzzyPrivacy: Boolean,
    onOpenLocationPicker: () -> Unit,
    depositStr: String,
    onDepositChange: (String) -> Unit,
    wasteKgStr: String,
    onWasteKgChange: (String) -> Unit
) {
    val wasteKg = wasteKgStr.toDoubleOrNull() ?: 7.5
    val co2Saved = (wasteKg * 2.2).toInt()
    val dollarsSaved = (wasteKg * 10.0).toInt()

    val dateFormatter = remember {
        SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    val quickDurationPresets = remember {
        listOf(
            3 to "Weekend (3d)",
            7 to "1 Week (7d)",
            14 to "2 Weeks (14d)",
            30 to "1 Month (30d)",
            60 to "Seasonal (60d)"
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Lending Window, Pickup & Terms", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Set tool availability dates, choose exchange point on radar map, and view eco-impact.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Availability Schedule Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            border = borderCard(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), 1.5.dp),
            modifier = Modifier.fillMaxWidth().testTag("availability_date_picker_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Tool Availability Dates", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("When neighbors can borrow", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Surface(shape = RoundedCornerShape(8.dp), color = EcoGreenDark) {
                        Text("$maxDurationDays days total", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onOpenDatePicker() }.testTag("active_date_range_display_btn")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ACTIVE AVAILABILITY DATES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(2.dp))
                            val startStr = dateFormatter.format(Date(availableFromMillis))
                            val endStr = dateFormatter.format(Date(availableToMillis))
                            Text("$startStr  ➔  $endStr", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        IconButton(onClick = onOpenDatePicker, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.EditCalendar, contentDescription = "Edit dates", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().testTag("date_range_quick_presets")) {
                    items(quickDurationPresets) { (days, label) ->
                        val isSelected = maxDurationDays == days
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            border = borderCard(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), 1.dp),
                            modifier = Modifier.clip(RoundedCornerShape(10.dp)).clickable { onApplyDurationPreset(days) }.testTag("duration_chip_${days}_days")
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onOpenDatePicker,
                    modifier = Modifier.fillMaxWidth().testTag("open_calendar_date_picker_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Customize Range in Calendar", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Pickup & Drop-Off Point Location Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            border = borderCard(EcoGreenPrimary.copy(alpha = 0.4f), 1.5.dp),
            modifier = Modifier.fillMaxWidth().testTag("pickup_location_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(EcoGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Pickup & Drop-Off Location", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Map pin or neighborhood hub", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    if (isFuzzyPrivacy) {
                        Surface(shape = RoundedCornerShape(8.dp), color = EcoGreenDark.copy(alpha = 0.85f)) {
                            Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Fuzzy Masked", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onOpenLocationPicker() }.testTag("open_location_picker_display_btn")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SELECTED EXCHANGE POINT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EcoGreenDark)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(pickupLocationName, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("$pickupAddress • ${pickupDistanceMiles} mi away", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = onOpenLocationPicker, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Map, contentDescription = "Edit on map", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onOpenLocationPicker,
                    modifier = Modifier.fillMaxWidth().testTag("open_map_location_picker_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Select Point on Radar Map", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Description Field
        Text("Description & Included Accessories *", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = { Text("List included accessories (cords, cases, drill bits), safety notes, and preferred porch pickup times.") },
            isError = descError,
            supportingText = {
                if (descError) Text("Description is required so neighbors know what accessories are included.")
                else Text("Tip: Mention if eye protection or specific battery packs are included.")
            },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth().testTag("add_item_description_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Deposit & Waste Inputs Row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Refundable Deposit ($)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = depositStr,
                    onValueChange = onDepositChange,
                    singleLine = true,
                    leadingIcon = { Text("$", fontWeight = FontWeight.Bold, color = EcoGreenDark) },
                    modifier = Modifier.fillMaxWidth().testTag("add_item_deposit_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("Est. Waste Saved (kg)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = wasteKgStr,
                    onValueChange = onWasteKgChange,
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Eco, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth().testTag("add_item_waste_input"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Eco-Impact Calculator Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth().testTag("eco_impact_calculation_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🌱 Estimated Neighborhood Environmental Impact", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    ImpactMetricItem(icon = Icons.Default.Co2, value = "${co2Saved} kg", label = "CO2 Prevented", color = EcoGreenDark)
                    ImpactMetricItem(icon = Icons.Default.Paid, value = "$${dollarsSaved}", label = "Community Saved", color = EcoAmber)
                    ImpactMetricItem(icon = Icons.Default.Star, value = "+30", label = "Eco Karma", color = EcoGold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 4: Photos, Sample Presets & Live Listing Preview
// -------------------------------------------------------------
@Composable
private fun StepFourPhotosAndPreview(
    title: String,
    category: String,
    condition: String,
    description: String,
    deposit: Double,
    wasteKg: Double,
    maxBorrowDurationDays: Int,
    availableFromMillis: Long,
    availableToMillis: Long,
    pickupLocationName: String,
    pickupDistanceMiles: Double,
    isFuzzyPrivacy: Boolean,
    photoBitmap: android.graphics.Bitmap?,
    onLaunchCamera: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    val shortDateFormatter = remember {
        SimpleDateFormat("MMM d", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Tool Visuals & Live Listing Preview", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Attach a live camera photo of your tool and verify how it will appear in the neighborhood feed.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().testTag("add_item_photo_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                if (photoBitmap != null) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(Color.Black)) {
                        Image(bitmap = photoBitmap.asImageBitmap(), contentDescription = "Captured Item Photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)

                        Surface(shape = RoundedCornerShape(8.dp), color = EcoGreenDark.copy(alpha = 0.85f), modifier = Modifier.padding(8.dp).align(Alignment.TopStart)) {
                            Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Live Snapshot Attached", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        IconButton(
                            onClick = onLaunchCamera,
                            modifier = Modifier.padding(8.dp).align(Alignment.TopEnd).size(36.dp).background(Color.Black.copy(alpha = 0.6f), CircleShape).testTag("retake_item_photo_btn")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retake", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("✓ Real photo attached to tool listing", fontSize = 11.sp, color = EcoGreenDark, fontWeight = FontWeight.SemiBold)
                        TextButton(onClick = onRemovePhoto, modifier = Modifier.testTag("remove_photo_btn")) {
                            Text("Remove", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(12.dp)).border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Box(modifier = Modifier.size(46.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = "Take Photo", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Capture tool photo with device camera", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onLaunchCamera,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("open_camera_for_item_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp)) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Open Camera", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("👀 Live Community Feed Preview", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("This is exactly how neighbors will discover your tool on the home feed and map:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(10.dp))

        // Community Feed Mock Card
        Card(
            modifier = Modifier.fillMaxWidth().testTag("live_feed_preview_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            border = borderCard(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), 1.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().height(140.dp).background(
                        when (category) {
                            "Power Tools" -> EcoGreenPrimary.copy(alpha = 0.15f)
                            "Lawn & Garden" -> EcoSage.copy(alpha = 0.2f)
                            "Camping & Outdoors" -> EcoGreenDark.copy(alpha = 0.2f)
                            "Cleaning Gear" -> EcoEmerald.copy(alpha = 0.18f)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoBitmap != null) {
                        Image(bitmap = photoBitmap.asImageBitmap(), contentDescription = "Item Photo", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = when (category) {
                                    "Power Tools" -> Icons.Default.Build
                                    "Lawn & Garden" -> Icons.Default.Park
                                    "Home Repair" -> Icons.Default.HomeRepairService
                                    "Kitchen & Cooking" -> Icons.Default.Restaurant
                                    "Camping & Outdoors" -> Icons.Default.Terrain
                                    else -> Icons.Default.CleaningServices
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(category.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Surface(shape = RoundedCornerShape(8.dp), color = EcoGreenDark, modifier = Modifier.padding(8.dp).align(Alignment.TopEnd)) {
                        Text("AVAILABLE NOW", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title.ifBlank { "Untitled Tool" }, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Offered by You • $pickupDistanceMiles mi away", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)) {
                            Text(condition, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Location & Privacy Badge
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = EcoGreenDark, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Pickup: $pickupLocationName ${if (isFuzzyPrivacy) "(Fuzzy Masked ~0.25 mi)" else ""}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val startStr = shortDateFormatter.format(Date(availableFromMillis))
                    val endStr = shortDateFormatter.format(Date(availableToMillis))
                    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Available: $startStr – $endStr ($maxBorrowDurationDays days max loan)", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(description.ifBlank { "No description entered yet." }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = EcoAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$${deposit.toInt()} Refundable Deposit", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Eco, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Saves ${wasteKg}kg waste", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoGreenDark)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ImpactMetricItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
    }
}

private fun borderCard(color: Color, width: androidx.compose.ui.unit.Dp): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(width, color)
}
