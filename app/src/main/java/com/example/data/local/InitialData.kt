package com.example.data.local

import com.example.data.model.AppNotification
import com.example.data.model.BorrowRequest
import com.example.data.model.ChatMessage
import com.example.data.model.LendItem
import com.example.data.model.Review
import com.example.data.model.UserProfile

object InitialData {

    val sampleItems = listOf(
        LendItem(
            id = 1,
            title = "2000 PSI Electric Pressure Washer",
            description = "High-power surface cleaner, 25ft hose, turbo nozzle. Ideal for driveways, patios, and outdoor siding without buying a gas machine.",
            category = "Power Tools",
            ownerId = "user_sarah",
            ownerName = "Sarah Miller",
            ownerRating = 4.95f,
            ownerReviewsCount = 28,
            ownerAvatarSeed = "SM",
            distanceMiles = 0.3,
            neighborhood = "Maple Grove (3 doors down)",
            condition = "Like New",
            depositAmount = 40.0,
            dailyFee = 0.0,
            estimatedWasteSavedKg = 18.5,
            estimatedDollarSaved = 195.0,
            estimatedCo2SavedKg = 44.0,
            availability = "AVAILABLE_NOW",
            iconCategory = "TOOLS"
        ),
        LendItem(
            id = 2,
            title = "17-ft Telescoping Multi-Position Ladder",
            description = "Heavy-duty aluminum ANSI Type 1A rated (300 lbs). Configurable as A-frame, extension, staircase, or 90-degree ladder. Safe & sturdy.",
            category = "Home Repair",
            ownerId = "user_david",
            ownerName = "David Chen",
            ownerRating = 4.88f,
            ownerReviewsCount = 42,
            ownerAvatarSeed = "DC",
            distanceMiles = 0.5,
            neighborhood = "Oakridge Crescent",
            condition = "Excellent",
            depositAmount = 30.0,
            dailyFee = 0.0,
            estimatedWasteSavedKg = 16.0,
            estimatedDollarSaved = 170.0,
            estimatedCo2SavedKg = 38.0,
            availability = "BORROWED",
            returnDateMillis = System.currentTimeMillis() + (86400000L * 2), // 2 days
            borrowerName = "Maya Johnson",
            iconCategory = "TOOLS"
        ),
        LendItem(
            id = 3,
            title = "Cordless Brushless Lawn Mower + 2 Batteries",
            description = "Quiet 21-inch steel deck mower with mulching plug and bagger. No fumes, no gas storage. Perfect for zero-emission weekend yard care.",
            category = "Lawn & Garden",
            ownerId = "user_marcus",
            ownerName = "Marcus Taylor",
            ownerRating = 5.0f,
            ownerReviewsCount = 19,
            ownerAvatarSeed = "MT",
            distanceMiles = 0.8,
            neighborhood = "Cedar Crest Way",
            condition = "Like New",
            depositAmount = 50.0,
            dailyFee = 5.0, // small maintenance share
            estimatedWasteSavedKg = 28.0,
            estimatedDollarSaved = 340.0,
            estimatedCo2SavedKg = 72.0,
            availability = "AVAILABLE_NOW",
            iconCategory = "GARDEN"
        ),
        LendItem(
            id = 4,
            title = "Heavy-Duty Carpet & Upholstery Deep Cleaner",
            description = "Commercial grade dual-suction spot & stair cleaner. Includes pet stain formula tank and rotating scrubbing brushes. Saves buying plastic jugs.",
            category = "Cleaning Gear",
            ownerId = "user_elena",
            ownerName = "Elena Rostova",
            ownerRating = 4.92f,
            ownerReviewsCount = 31,
            ownerAvatarSeed = "ER",
            distanceMiles = 1.1,
            neighborhood = "Pinecrest Lane",
            condition = "Excellent",
            depositAmount = 35.0,
            dailyFee = 0.0,
            estimatedWasteSavedKg = 12.4,
            estimatedDollarSaved = 180.0,
            estimatedCo2SavedKg = 29.0,
            availability = "AVAILABLE_NOW",
            iconCategory = "CLEANING"
        ),
        LendItem(
            id = 5,
            title = "6-Person Weatherproof Camping Tent & Stove Kit",
            description = "Spacious cabin tent with rainfly, gear loft, and dual-burner camp propane stove. Great for state park getaways without fast-camping waste.",
            category = "Camping & Outdoors",
            ownerId = "user_brian",
            ownerName = "Brian O'Connor",
            ownerRating = 4.85f,
            ownerReviewsCount = 15,
            ownerAvatarSeed = "BO",
            distanceMiles = 1.4,
            neighborhood = "Willow Creek Loop",
            condition = "Good",
            depositAmount = 45.0,
            dailyFee = 0.0,
            estimatedWasteSavedKg = 14.2,
            estimatedDollarSaved = 210.0,
            estimatedCo2SavedKg = 34.0,
            availability = "RESERVED",
            returnDateMillis = System.currentTimeMillis() + (86400000L * 4),
            borrowerName = "Lucas Vance",
            iconCategory = "CAMPING"
        ),
        LendItem(
            id = 6,
            title = "1080p Outdoor Movie Projector + 120-Inch Screen",
            description = "High-lumens outdoor cinema projector with HDMI/Bluetooth and fold-out tripod frame screen. Perfect for backyard community movie nights!",
            category = "Party & Events",
            ownerId = "user_hannah",
            ownerName = "Hannah Kim",
            ownerRating = 5.0f,
            ownerReviewsCount = 22,
            ownerAvatarSeed = "HK",
            distanceMiles = 0.6,
            neighborhood = "Sunset Terrace",
            condition = "Like New",
            depositAmount = 50.0,
            dailyFee = 0.0,
            estimatedWasteSavedKg = 8.5,
            estimatedDollarSaved = 260.0,
            estimatedCo2SavedKg = 21.0,
            availability = "AVAILABLE_NOW",
            iconCategory = "PARTY"
        ),
        LendItem(
            id = 7,
            title = "Digital Food Dehydrator with 6 Stainless Trays",
            description = "Preserve orchard apples, garden herbs, and make dried camping fruit snacks. Temperature range 95°F–165°F with timer.",
            category = "Kitchen & Cooking",
            ownerId = "user_amara",
            ownerName = "Amara Diallo",
            ownerRating = 4.9f,
            ownerReviewsCount = 17,
            ownerAvatarSeed = "AD",
            distanceMiles = 0.4,
            neighborhood = "Oakridge Park Ave",
            condition = "Like New",
            depositAmount = 25.0,
            dailyFee = 0.0,
            estimatedWasteSavedKg = 5.2,
            estimatedDollarSaved = 85.0,
            estimatedCo2SavedKg = 12.0,
            availability = "AVAILABLE_NOW",
            iconCategory = "KITCHEN"
        ),
        LendItem(
            id = 8,
            title = "Rotary Laser Level with Tripod & Target",
            description = "Self-leveling 360-degree horizontal and vertical line projector. Essential for deck framing, tile alignment, and fence posts.",
            category = "Power Tools",
            ownerId = "user_greg",
            ownerName = "Greg Wallace",
            ownerRating = 4.82f,
            ownerReviewsCount = 11,
            ownerAvatarSeed = "GW",
            distanceMiles = 1.7,
            neighborhood = "Highland Hills",
            condition = "Excellent",
            depositAmount = 35.0,
            dailyFee = 0.0,
            estimatedWasteSavedKg = 4.8,
            estimatedDollarSaved = 130.0,
            estimatedCo2SavedKg = 15.0,
            availability = "AVAILABLE_NOW",
            iconCategory = "TOOLS"
        )
    )

    val sampleReviews = listOf(
        Review(
            id = 1,
            itemId = 1,
            itemTitle = "2000 PSI Electric Pressure Washer",
            reviewerName = "Liam Brooks",
            targetUserName = "Sarah Miller",
            rating = 5,
            comment = "Sarah made the porch pickup effortless! The washer cleaned our entire rear cedar deck in an afternoon. Cleaned it up and returned on time. Saved buying a new unit that would just sit in my garage.",
            dateStr = "2 days ago",
            tags = "Mint Condition • Easy Pickup • Zero Waste"
        ),
        Review(
            id = 2,
            itemId = 1,
            itemTitle = "2000 PSI Electric Pressure Washer",
            reviewerName = "Chloe Sanders",
            targetUserName = "Sarah Miller",
            rating = 5,
            comment = "Super helpful neighbor! She even provided safety goggles and the turbo wand attachment. Highly recommend.",
            dateStr = "1 week ago",
            tags = "Friendly Neighbor • High Quality"
        ),
        Review(
            id = 3,
            itemId = 3,
            itemTitle = "Cordless Brushless Lawn Mower",
            reviewerName = "Alex Green",
            targetUserName = "Marcus Taylor",
            rating = 5,
            comment = "Marcus had both batteries topped off. Quiet operation didn't disturb anyone on a Sunday morning. Wonderful community spirit!",
            dateStr = "3 days ago",
            tags = "Batteries Charged • Punctual"
        ),
        Review(
            id = 4,
            itemId = 6,
            itemTitle = "Outdoor Movie Projector + Screen",
            reviewerName = "Maya Johnson",
            targetUserName = "Hannah Kim",
            rating = 5,
            comment = "The kids on our cul-de-sac had the best outdoor Pixar movie night! Setup took 10 minutes. Thanks Hannah!",
            dateStr = "5 days ago",
            tags = "Community Champion • Flawless Setup"
        )
    )

    val sampleRequests = listOf(
        BorrowRequest(
            id = 1,
            itemId = 1,
            itemTitle = "2000 PSI Electric Pressure Washer",
            requesterName = "Alex Green",
            ownerName = "Sarah Miller",
            startDateMillis = System.currentTimeMillis() + 86400000L,
            endDateMillis = System.currentTimeMillis() + (86400000L * 2),
            status = "APPROVED",
            depositPaid = 40.0,
            feePaid = 0.0,
            transactionId = "TXN-88492-HOLD",
            note = "Need for cleaning our front porch pavers on Saturday morning.",
            pickupOption = "Porch Pickup Box"
        ),
        BorrowRequest(
            id = 2,
            itemId = 3,
            itemTitle = "Cordless Brushless Lawn Mower + 2 Batteries",
            requesterName = "Alex Green",
            ownerName = "Marcus Taylor",
            startDateMillis = System.currentTimeMillis() - (86400000L * 5),
            endDateMillis = System.currentTimeMillis() - (86400000L * 4),
            status = "RETURNED",
            depositPaid = 50.0,
            feePaid = 5.0,
            transactionId = "TXN-77312-REFUNDED",
            note = "Mowed front and back lawn. Cleaned under-deck before return.",
            pickupOption = "Porch Pickup Box"
        )
    )

    val sampleChatMessages = listOf(
        ChatMessage(
            id = 1,
            conversationId = "conv_1",
            itemId = 1,
            itemTitle = "2000 PSI Electric Pressure Washer",
            senderName = "Alex Green",
            text = "Hi Sarah! I saw your pressure washer is available. Can I borrow it this Saturday for 24 hours to do our driveway?",
            timestampMillis = System.currentTimeMillis() - 7200000L,
            isFromCurrentUser = true,
            isRead = true
        ),
        ChatMessage(
            id = 2,
            conversationId = "conv_1",
            itemId = 1,
            itemTitle = "2000 PSI Electric Pressure Washer",
            senderName = "Sarah Miller",
            text = "Hi Alex! Absolutely! It's all clean and ready in the blue porch storage bin next to the garage. No need to meet face to face if you prefer contactless.",
            timestampMillis = System.currentTimeMillis() - 5400000L,
            isFromCurrentUser = false,
            isRead = true
        ),
        ChatMessage(
            id = 3,
            conversationId = "conv_1",
            itemId = 1,
            itemTitle = "2000 PSI Electric Pressure Washer",
            senderName = "Alex Green",
            text = "That's wonderful! I placed the refundable deposit via the app. I will pick it up at 9:30 AM and return it by 5 PM.",
            timestampMillis = System.currentTimeMillis() - 3600000L,
            isFromCurrentUser = true,
            isRead = true
        ),
        ChatMessage(
            id = 4,
            conversationId = "conv_1",
            itemId = 1,
            itemTitle = "2000 PSI Electric Pressure Washer",
            senderName = "Sarah Miller",
            text = "Approved! Let me know if you need the extra nozzle tips. Happy washing!",
            timestampMillis = System.currentTimeMillis() - 1800000L,
            isFromCurrentUser = false,
            isRead = true
        )
    )

    val sampleNotifications = listOf(
        AppNotification(
            id = 1,
            title = "Borrow Request Approved! 🎉",
            message = "Sarah Miller approved your borrow request for '2000 PSI Electric Pressure Washer'. Check chat for pickup details.",
            type = "REQUEST",
            timestampMillis = System.currentTimeMillis() - 1800000L,
            isRead = false,
            isOfflineQueued = false
        ),
        AppNotification(
            id = 2,
            title = "Eco Karma Reward Earned! 🌿",
            message = "You earned +50 Karma points for completing on-time return with Marcus Taylor. 28 kg waste diverted!",
            type = "REWARD",
            timestampMillis = System.currentTimeMillis() - (86400000L * 4),
            isRead = true,
            isOfflineQueued = false
        ),
        AppNotification(
            id = 3,
            title = "Pending Offline Request Synced",
            message = "Your request was confirmed in offline mode and registered to the local community ledger.",
            type = "STATUS_UPDATE",
            timestampMillis = System.currentTimeMillis() - (86400000L * 1),
            isRead = true,
            isOfflineQueued = false
        )
    )
}
