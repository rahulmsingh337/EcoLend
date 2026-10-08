package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppNotification
import com.example.data.model.BorrowRequest
import com.example.data.model.ChatMessage
import com.example.data.model.LendItem
import com.example.data.model.Review
import com.example.data.model.UserProfile
import com.example.data.model.WishlistEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface EcoLendDao {

    // --- Lend Items ---
    @Query("SELECT * FROM lend_items ORDER BY id ASC")
    fun getAllItems(): Flow<List<LendItem>>

    @Query("SELECT * FROM lend_items WHERE id = :id")
    fun getItemById(id: Long): Flow<LendItem?>

    @Query("SELECT * FROM lend_items WHERE id = :id")
    suspend fun getItemByIdSync(id: Long): LendItem?

    @Query("SELECT * FROM lend_items WHERE category = :category ORDER BY id ASC")
    fun getItemsByCategory(category: String): Flow<List<LendItem>>

    @Query("SELECT * FROM lend_items WHERE availability = :status ORDER BY id ASC")
    fun getItemsByAvailability(status: String): Flow<List<LendItem>>

    @Query("SELECT * FROM lend_items WHERE isFavorite = 1")
    fun getFavoriteItems(): Flow<List<LendItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: LendItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<LendItem>)

    @Update
    suspend fun updateItem(item: LendItem)

    @Query("UPDATE lend_items SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFav: Boolean)

    @Query("UPDATE lend_items SET availability = :status, returnDateMillis = :returnDate, borrowerName = :borrower WHERE id = :id")
    suspend fun updateAvailability(id: Long, status: String, returnDate: Long?, borrower: String?)

    @Query("UPDATE lend_items SET photoUri = :uri WHERE id = :id")
    suspend fun updateItemPhoto(id: Long, uri: String)

    // --- Borrow Requests ---
    @Query("SELECT * FROM borrow_requests ORDER BY createdAtMillis DESC")
    fun getAllRequests(): Flow<List<BorrowRequest>>

    @Query("SELECT * FROM borrow_requests WHERE status IN ('PENDING', 'APPROVED', 'ACTIVE')")
    suspend fun getActiveRequestsSync(): List<BorrowRequest>

    @Query("SELECT * FROM borrow_requests WHERE itemId = :itemId ORDER BY createdAtMillis DESC")
    fun getRequestsByItem(itemId: Long): Flow<List<BorrowRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: BorrowRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<BorrowRequest>)

    @Update
    suspend fun updateRequest(request: BorrowRequest)

    @Query("UPDATE borrow_requests SET status = :status WHERE id = :id")
    suspend fun updateRequestStatus(id: Long, status: String)

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages WHERE conversationId = :convId ORDER BY timestampMillis ASC")
    fun getMessagesForConversation(convId: String): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestampMillis DESC")
    fun getAllMessages(): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<ChatMessage>)

    // --- Reviews ---
    @Query("SELECT * FROM reviews WHERE itemId = :itemId ORDER BY id DESC")
    fun getReviewsForItem(itemId: Long): Flow<List<Review>>

    @Query("SELECT * FROM reviews ORDER BY id DESC")
    fun getAllReviews(): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<Review>)

    // --- Notifications ---
    @Query("SELECT * FROM app_notifications ORDER BY timestampMillis DESC")
    fun getAllNotifications(): Flow<List<AppNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: AppNotification): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<AppNotification>)

    @Query("UPDATE app_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    @Query("UPDATE app_notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()

    // --- User Profile ---
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Update
    suspend fun updateUserProfile(profile: UserProfile)

    // --- Wishlist / Saved Items ---
    @Query("SELECT * FROM wishlist_entries ORDER BY addedAtMillis DESC")
    fun getAllWishlistEntries(): Flow<List<WishlistEntry>>

    @Query("SELECT * FROM wishlist_entries WHERE itemId = :itemId")
    suspend fun getWishlistEntry(itemId: Long): WishlistEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistEntry(entry: WishlistEntry)

    @Query("DELETE FROM wishlist_entries WHERE itemId = :itemId")
    suspend fun deleteWishlistEntry(itemId: Long)

    @Query("UPDATE wishlist_entries SET note = :note, targetDate = :targetDate, notifyWhenAvailable = :notify WHERE itemId = :itemId")
    suspend fun updateWishlistDetails(itemId: Long, note: String, targetDate: String, notify: Boolean)
}
