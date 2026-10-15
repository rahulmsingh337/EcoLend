package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.data.model.LendItem
import com.example.ui.EcoLendViewModel
import com.example.ui.components.GoogleNeighborhoodMap

@Composable
fun ExploreMapScreen(
    viewModel: EcoLendViewModel,
    onItemClick: (LendItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.filteredItems.collectAsState()
    val maxDistance by viewModel.maxDistanceMiles.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("explore_map_screen")
    ) {
        GoogleNeighborhoodMap(
            items = items,
            maxDistance = maxDistance,
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.selectedCategory.value = it },
            onDistanceChange = { viewModel.maxDistanceMiles.value = it },
            onItemSelected = onItemClick,
            onStartBorrow = { viewModel.startBorrowFlow(it) }
        )
    }
}
