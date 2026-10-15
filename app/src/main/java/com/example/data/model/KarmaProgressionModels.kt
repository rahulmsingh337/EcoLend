package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class BadgeTier(
    val displayName: String,
    val levelNumber: Int,
    val primaryColor: Long,
    val containerColor: Long,
    val textColor: Long
) {
    NONE(
        displayName = "Locked",
        levelNumber = 0,
        primaryColor = 0xFF9E9E9E,
        containerColor = 0xFFF5F5F5,
        textColor = 0xFF616161
    ),
    BRONZE(
        displayName = "Bronze",
        levelNumber = 1,
        primaryColor = 0xFF8D6E63,
        containerColor = 0xFFEFEBE9,
        textColor = 0xFF4E342E
    ),
    SILVER(
        displayName = "Silver",
        levelNumber = 2,
        primaryColor = 0xFF78909C,
        containerColor = 0xFFECEFF1,
        textColor = 0xFF263238
    ),
    GOLD(
        displayName = "Gold",
        levelNumber = 3,
        primaryColor = 0xFFFFB300,
        containerColor = 0xFFFFF8E1,
        textColor = 0xFFE65100
    )
}

enum class BadgeCategory(val displayName: String) {
    COMMUNITY("Community Reputation"),
    LENDING("Equipment Lending"),
    BORROWING("Reliable Borrowing"),
    ECO_IMPACT("Eco & Waste Diversion")
}

data class BadgeMilestone(
    val tier: BadgeTier,
    val threshold: Int,
    val requirementText: String,
    val perkText: String
)

data class KarmaBadge(
    val id: String,
    val title: String,
    val category: BadgeCategory,
    val iconEmoji: String,
    val description: String,
    val currentTier: BadgeTier,
    val currentValue: Int,
    val unit: String,
    val nextTier: BadgeTier?,
    val currentTierThreshold: Int,
    val nextTierThreshold: Int,
    val progressFraction: Float, // 0.0f .. 1.0f
    val activePerk: String,
    val nextPerk: String?,
    val milestones: List<BadgeMilestone>,
    val motivationalTip: String
) {
    val isUnlocked: Boolean get() = currentTier != BadgeTier.NONE
}

data class KarmaBreakdown(
    val totalScore: Int,
    val returnPoints: Int,
    val feedbackPoints: Int,
    val trustBonusPoints: Int,
    val currentRankTitle: String,
    val currentTier: BadgeTier,
    val nextRankTitle: String,
    val nextRankThreshold: Int,
    val currentTierFloor: Int,
    val pointsToNextRank: Int,
    val progressToNextRank: Float, // 0.0f .. 1.0f
    val successfulReturnsCount: Int,
    val positiveReviewsCount: Int,
    val verifiedTrustFlair: String
)
