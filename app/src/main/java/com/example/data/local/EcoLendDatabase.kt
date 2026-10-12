package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AppNotification
import com.example.data.model.BorrowRequest
import com.example.data.model.ChatMessage
import com.example.data.model.LendItem
import com.example.data.model.Review
import com.example.data.model.UserProfile
import com.example.data.model.WishlistEntry

@Database(
    entities = [
        LendItem::class,
        BorrowRequest::class,
        ChatMessage::class,
        Review::class,
        AppNotification::class,
        UserProfile::class,
        WishlistEntry::class
    ],
    version = 6,
    exportSchema = false
)
abstract class EcoLendDatabase : RoomDatabase() {
    abstract fun dao(): EcoLendDao

    companion object {
        @Volatile
        private var INSTANCE: EcoLendDatabase? = null

        fun getDatabase(context: Context): EcoLendDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EcoLendDatabase::class.java,
                    "ecolend_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
