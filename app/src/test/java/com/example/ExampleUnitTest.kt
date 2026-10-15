package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testGetItemCoordinates_returnsDeterministicAndBoundedCoordinates() {
    val center = com.example.ui.components.DEFAULT_NEIGHBORHOOD_CENTER
    val item1 = com.example.data.model.LendItem(
      id = 1L,
      title = "DeWalt Drill",
      description = "Cordless drill",
      category = "Power Tools",
      ownerId = "owner_1",
      ownerName = "Alex",
      ownerRating = 4.9f,
      ownerReviewsCount = 12,
      ownerAvatarSeed = "alex",
      distanceMiles = 0.8,
      neighborhood = "Oakridge",
      condition = "Excellent",
      depositAmount = 40.0,
      dailyFee = 0.0,
      estimatedWasteSavedKg = 2.0,
      estimatedDollarSaved = 45.0,
      estimatedCo2SavedKg = 8.0,
      availability = "AVAILABLE_NOW"
    )
    val item2 = item1.copy(id = 2L, distanceMiles = 1.5)

    val coord1 = com.example.ui.components.getItemCoordinates(item1, center)
    val coord2 = com.example.ui.components.getItemCoordinates(item2, center)

    // Verify coordinates are deterministic
    val coord1Repeat = com.example.ui.components.getItemCoordinates(item1, center)
    assertEquals(coord1.latitude, coord1Repeat.latitude, 0.000001)
    assertEquals(coord1.longitude, coord1Repeat.longitude, 0.000001)

    // Verify distinct items get distinct coordinates
    assertNotEquals(coord1.latitude, coord2.latitude, 0.00001)
    assertNotEquals(coord1.longitude, coord2.longitude, 0.00001)

    // Verify proximity to neighborhood center
    val latDiff = Math.abs(coord1.latitude - center.latitude)
    val lngDiff = Math.abs(coord1.longitude - center.longitude)
    assertTrue("Latitude diff should be within reasonable bounds", latDiff < 0.2)
    assertTrue("Longitude diff should be within reasonable bounds", lngDiff < 0.2)
  }
}
