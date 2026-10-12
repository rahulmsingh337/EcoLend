package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.EcoLendViewModel
import com.example.ui.components.AddItemFormComponent
import com.example.ui.components.CameraPhotoCaptureDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemFormScreen(
    viewModel: EcoLendViewModel,
    onBack: () -> Unit
) {
    var capturedPhotoPath by remember { mutableStateOf<String?>(null) }
    var showCameraDialog by remember { mutableStateOf(false) }
    var cameraItemTitle by remember { mutableStateOf("New Community Item") }
    var cameraItemCondition by remember { mutableStateOf("Like New") }

    // System back navigation
    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Add to Sharing Pool",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Offer a tool or item to your neighborhood",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("add_item_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Feed"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        AddItemFormComponent(
            onPublish = { title, description, category, deposit, condition, wasteKg, photoUri, maxBorrowDurationDays, availableFromMillis, availableToMillis, pickupLocationName, pickupAddress, pickupDistanceMiles, isFuzzyPrivacy, pickupNotes ->
                viewModel.addNewCommunityItem(
                    title = title,
                    description = description,
                    category = category,
                    deposit = deposit,
                    condition = condition,
                    wasteKg = wasteKg,
                    photoUri = photoUri,
                    maxBorrowDurationDays = maxBorrowDurationDays,
                    availableFromMillis = availableFromMillis,
                    availableUntilMillis = availableToMillis,
                    pickupLocationName = pickupLocationName,
                    pickupAddress = pickupAddress,
                    pickupDistanceMiles = pickupDistanceMiles,
                    isFuzzyPrivacy = isFuzzyPrivacy,
                    pickupNotes = pickupNotes
                )
            },
            onCancel = onBack,
            onLaunchCamera = { title, condition ->
                cameraItemTitle = title.ifBlank { "New Community Item" }
                cameraItemCondition = condition
                showCameraDialog = true
            },
            capturedPhotoPath = capturedPhotoPath,
            onRemovePhoto = { capturedPhotoPath = null },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }

    // Camera Capture Modal
    if (showCameraDialog) {
        CameraPhotoCaptureDialog(
            item = null,
            itemTitle = cameraItemTitle,
            itemCondition = cameraItemCondition,
            onDismiss = { showCameraDialog = false },
            onPhotoCaptured = { savedPath ->
                capturedPhotoPath = savedPath
                showCameraDialog = false
            }
        )
    }
}
