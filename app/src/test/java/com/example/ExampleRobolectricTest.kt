package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.EcoLendDatabase
import com.example.data.model.LendItem
import com.example.data.model.WishlistEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: EcoLendDatabase

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, EcoLendDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("EcoLend", appName)
  }

  @Test
  fun `wishlist bookmark is stored and retrieved from Room`() = runBlocking {
    val dao = database.dao()
    val testItem = LendItem(
      id = 101,
      title = "Cordless Leaf Blower",
      description = "Variable speed turbo mode leaf blower",
      category = "Lawn & Garden",
      ownerId = "owner_1",
      ownerName = "Neighbor John",
      ownerRating = 5.0f,
      ownerReviewsCount = 10,
      ownerAvatarSeed = "NJ",
      distanceMiles = 0.2,
      neighborhood = "Greenway",
      condition = "Excellent",
      depositAmount = 30.0,
      dailyFee = 0.0,
      estimatedWasteSavedKg = 6.5,
      estimatedDollarSaved = 80.0,
      estimatedCo2SavedKg = 14.0,
      availability = "AVAILABLE_NOW",
      isFavorite = false
    )
    dao.insertItem(testItem)

    // Save item to Wishlist with a personal project note
    dao.updateFavorite(testItem.id, true)
    dao.insertWishlistEntry(
      WishlistEntry(
        itemId = testItem.id,
        itemTitle = testItem.title,
        category = testItem.category,
        note = "Plan to clear backyard autumn leaves",
        targetDate = "Next Sunday",
        notifyWhenAvailable = true
      )
    )

    // Verify favorite items query
    val favorites = dao.getFavoriteItems().first()
    assertEquals(1, favorites.size)
    assertEquals("Cordless Leaf Blower", favorites[0].title)

    // Verify wishlist entries query
    val entries = dao.getAllWishlistEntries().first()
    assertEquals(1, entries.size)
    assertEquals("Plan to clear backyard autumn leaves", entries[0].note)
    assertTrue(entries[0].notifyWhenAvailable)

    // Remove from wishlist
    dao.updateFavorite(testItem.id, false)
    dao.deleteWishlistEntry(testItem.id)

    val updatedFavorites = dao.getFavoriteItems().first()
    assertEquals(0, updatedFavorites.size)
    val updatedEntries = dao.getAllWishlistEntries().first()
    assertEquals(0, updatedEntries.size)
  }

  @Test
  fun `item photo metadata is stored and retrieved in Room`() = runBlocking {
    val dao = database.dao()
    val testItem = LendItem(
      id = 202,
      title = "Heavy Duty Pressure Washer",
      description = "Electric pressure washer 2000 PSI",
      category = "Power Tools",
      ownerId = "owner_2",
      ownerName = "Neighbor Sarah",
      ownerRating = 4.8f,
      ownerReviewsCount = 5,
      ownerAvatarSeed = "NS",
      distanceMiles = 0.5,
      neighborhood = "Oakridge",
      condition = "Like New",
      depositAmount = 50.0,
      dailyFee = 0.0,
      estimatedWasteSavedKg = 12.0,
      estimatedDollarSaved = 140.0,
      estimatedCo2SavedKg = 26.0,
      availability = "AVAILABLE_NOW",
      photoUri = null
    )
    dao.insertItem(testItem)

    // Capture photo metadata from camera
    val capturedPhotoPath = "/data/user/0/com.aistudio.ecolend/files/tool_photos/tool_202_test.jpg"
    dao.updateItemPhoto(testItem.id, capturedPhotoPath)

    val retrieved = dao.getItemByIdSync(testItem.id)
    assertEquals(capturedPhotoPath, retrieved?.photoUri)
  }

  @Test
  fun `searchable feed filtering by category and availability in Room`() = runBlocking {
    val dao = database.dao()
    val item1 = LendItem(
      id = 301,
      title = "Cordless Drill Set",
      description = "Comes with 20 drill bits",
      category = "Power Tools",
      ownerId = "user1",
      ownerName = "Dave",
      ownerRating = 4.9f,
      ownerReviewsCount = 8,
      ownerAvatarSeed = "DV",
      distanceMiles = 0.3,
      neighborhood = "Highland",
      condition = "Good",
      depositAmount = 25.0,
      dailyFee = 0.0,
      estimatedWasteSavedKg = 4.0,
      estimatedDollarSaved = 60.0,
      estimatedCo2SavedKg = 8.5,
      availability = "AVAILABLE_NOW"
    )
    val item2 = LendItem(
      id = 302,
      title = "Lawn Mower Electric",
      description = "Mulching lawn mower",
      category = "Lawn & Garden",
      ownerId = "user2",
      ownerName = "Elena",
      ownerRating = 5.0f,
      ownerReviewsCount = 12,
      ownerAvatarSeed = "EL",
      distanceMiles = 0.8,
      neighborhood = "Highland",
      condition = "Excellent",
      depositAmount = 40.0,
      dailyFee = 0.0,
      estimatedWasteSavedKg = 15.0,
      estimatedDollarSaved = 190.0,
      estimatedCo2SavedKg = 32.0,
      availability = "BORROWED"
    )
    dao.insertItems(listOf(item1, item2))

    val powerTools = dao.getItemsByCategory("Power Tools").first()
    assertEquals(1, powerTools.size)
    assertEquals("Cordless Drill Set", powerTools[0].title)

    val available = dao.getItemsByAvailability("AVAILABLE_NOW").first()
    assertEquals(1, available.size)
    assertEquals("Cordless Drill Set", available[0].title)
  }

  @Test
  fun `adding new item to neighborhood sharing pool with camera photo in Room`() = runBlocking {
    val dao = database.dao()
    val newItemWithPhoto = LendItem(
      id = 401,
      title = "Bosch Cordless Jigsaw",
      description = "Includes wood & metal cutting blades and charger.",
      category = "Power Tools",
      ownerId = "user_alex",
      ownerName = "Alex Rivera",
      ownerRating = 5.0f,
      ownerReviewsCount = 0,
      ownerAvatarSeed = "AG",
      distanceMiles = 0.1,
      neighborhood = "Greenwood District",
      condition = "Like New",
      depositAmount = 35.0,
      dailyFee = 0.0,
      estimatedWasteSavedKg = 7.5,
      estimatedDollarSaved = 75.0,
      estimatedCo2SavedKg = 16.5,
      availability = "AVAILABLE_NOW",
      photoUri = "/data/user/0/com.aistudio.ecolend/files/tool_photos/simulated_new_12345.jpg"
    )

    dao.insertItem(newItemWithPhoto)

    val allItems = dao.getAllItems().first()
    val addedItem = allItems.find { it.id == 401L }
    assertEquals("Bosch Cordless Jigsaw", addedItem?.title)
    assertEquals("Power Tools", addedItem?.category)
    assertEquals("Includes wood & metal cutting blades and charger.", addedItem?.description)
    assertEquals("/data/user/0/com.aistudio.ecolend/files/tool_photos/simulated_new_12345.jpg", addedItem?.photoUri)
    assertEquals("AVAILABLE_NOW", addedItem?.availability)
  }

  @Test
  fun `max borrow duration is saved on items and configurable`() = runBlocking {
    val dao = database.dao()
    val itemWithDuration = LendItem(
      id = 501,
      title = "Heavy Duty Carpet Cleaner",
      description = "Commercial grade deep shampoo extractor",
      category = "Cleaning Gear",
      ownerId = "user_clean",
      ownerName = "Clara",
      ownerRating = 4.9f,
      ownerReviewsCount = 6,
      ownerAvatarSeed = "CL",
      distanceMiles = 0.4,
      neighborhood = "Greenway",
      condition = "Excellent",
      depositAmount = 45.0,
      dailyFee = 0.0,
      estimatedWasteSavedKg = 18.0,
      estimatedDollarSaved = 160.0,
      estimatedCo2SavedKg = 38.0,
      availability = "AVAILABLE_NOW",
      maxBorrowDurationDays = 3
    )
    dao.insertItem(itemWithDuration)

    val retrieved = dao.getItemByIdSync(501L)
    assertEquals(3, retrieved?.maxBorrowDurationDays)
  }

  @Test
  fun `review and rating system updates lender reliability and tool reviews`() = runBlocking {
    val dao = database.dao()
    val item = LendItem(
      id = 601,
      title = "DeWalt Circular Saw",
      description = "Laser guide 7-1/4 inch circular saw",
      category = "Power Tools",
      ownerId = "user_lender",
      ownerName = "Marcus Vance",
      ownerRating = 4.0f,
      ownerReviewsCount = 1,
      ownerAvatarSeed = "MV",
      distanceMiles = 0.2,
      neighborhood = "Oakridge",
      condition = "Good",
      depositAmount = 40.0,
      dailyFee = 0.0,
      estimatedWasteSavedKg = 9.0,
      estimatedDollarSaved = 90.0,
      estimatedCo2SavedKg = 20.0,
      availability = "AVAILABLE_NOW"
    )
    dao.insertItem(item)

    // Submit review
    val review = com.example.data.model.Review(
      itemId = 601,
      itemTitle = "DeWalt Circular Saw",
      reviewerName = "Alex Green",
      targetUserName = "Marcus Vance",
      rating = 5,
      comment = "Great tool, sharp blade, super reliable neighbor!",
      dateStr = "Today",
      tags = "Mint Condition • Super Friendly"
    )
    dao.insertReview(review)

    // Verify reviews query
    val reviews = dao.getReviewsForItem(601L).first()
    assertEquals(1, reviews.size)
    assertEquals("Alex Green", reviews[0].reviewerName)
    assertEquals(5, reviews[0].rating)
    assertEquals("Mint Condition • Super Friendly", reviews[0].tags)

    // Update item rating
    val oldCount = item.ownerReviewsCount
    val newCount = oldCount + 1
    val newRating = ((item.ownerRating * oldCount) + 5) / newCount
    dao.updateItem(item.copy(ownerRating = newRating, ownerReviewsCount = newCount))

    val updatedItem = dao.getItemByIdSync(601L)
    assertEquals(2, updatedItem?.ownerReviewsCount)
    assertEquals(4.5f, updatedItem?.ownerRating)
  }

  @Test
  fun `status indicators reflect item availability correctly`() = runBlocking {
    val dao = database.dao()
    val item = LendItem(
      id = 701,
      title = "Camping Tent 4-Person",
      description = "Weatherproof family tent",
      category = "Camping & Outdoors",
      ownerId = "user_camp",
      ownerName = "Sam",
      ownerRating = 5.0f,
      ownerReviewsCount = 4,
      ownerAvatarSeed = "SM",
      distanceMiles = 0.6,
      neighborhood = "Highland",
      condition = "Like New",
      depositAmount = 30.0,
      dailyFee = 0.0,
      estimatedWasteSavedKg = 14.0,
      estimatedDollarSaved = 120.0,
      estimatedCo2SavedKg = 28.0,
      availability = "AVAILABLE_NOW"
    )
    dao.insertItem(item)

    // Check available status
    assertEquals("AVAILABLE_NOW", dao.getItemByIdSync(701L)?.availability)

    // Change to RESERVED
    dao.updateAvailability(701L, "RESERVED", null, "Alex Green")
    assertEquals("RESERVED", dao.getItemByIdSync(701L)?.availability)

    // Change to BORROWED
    dao.updateAvailability(701L, "BORROWED", System.currentTimeMillis() + 86400000L, "Alex Green")
    assertEquals("BORROWED", dao.getItemByIdSync(701L)?.availability)
  }
}

