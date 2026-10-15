package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BadgeTier
import com.example.data.model.KarmaBadge
import com.example.data.model.KarmaBreakdown
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoGold
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.EcoMint

/**
 * Reusable badge showing the user's community trust status (Bronze, Silver, Gold).
 * Displayed on listings, profiles, and exchange requests.
 */
@Composable
fun VerifiedTrustBadge(
    tier: BadgeTier,
    modifier: Modifier = Modifier,
    label: String = "Verified Community Lender",
    showIcon: Boolean = true
) {
    val tierColor = Color(tier.primaryColor)
    val containerColor = Color(tier.containerColor)
    val textColor = Color(tier.textColor)

    Surface(
        modifier = modifier.testTag("verified_trust_badge_${tier.name.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, tierColor.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (showIcon) {
                Icon(
                    imageVector = when (tier) {
                        BadgeTier.GOLD -> Icons.Default.MilitaryTech
                        BadgeTier.SILVER -> Icons.Default.Verified
                        BadgeTier.BRONZE -> Icons.Default.Shield
                        BadgeTier.NONE -> Icons.Default.Shield
                    },
                    contentDescription = null,
                    tint = tierColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = "$label • ${tier.displayName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

/**
 * Prominent Community Karma Scorecard showing total points, dynamic breakdown
 * from returns and reviews, and tier progression bar.
 */
@Composable
fun KarmaScorecardView(
    breakdown: KarmaBreakdown,
    onInspectBadges: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = breakdown.progressToNextRank,
        animationSpec = tween(durationMillis = 800),
        label = "karma_progress_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("karma_scorecard_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header Row: Trophy + Rank Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(EcoGreenPrimary, EcoGreenDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Karma Trophy",
                            tint = EcoGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Community Karma",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Earned via punctual returns & trust",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Rank Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(breakdown.currentTier.containerColor),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(breakdown.currentTier.primaryColor).copy(alpha = 0.7f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MilitaryTech,
                            contentDescription = null,
                            tint = Color(breakdown.currentTier.primaryColor),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = breakdown.currentRankTitle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(breakdown.currentTier.textColor)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score Counter Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "${breakdown.totalScore}",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EcoGreenDark
                    )
                    Text(
                        text = "Total Reputation Points",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${breakdown.pointsToNextRank} pts to ${breakdown.nextRankTitle}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${(breakdown.progressToNextRank * 100).toInt()}% of next milestone",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar to next tier
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("karma_progress_indicator"),
                color = EcoGreenPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contribution Source Breakdown Row
            Text(
                text = "Points Breakdown",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Returns chip
                ContributionStatChip(
                    icon = "🔄",
                    label = "Returns",
                    count = "${breakdown.successfulReturnsCount} returns",
                    points = "+${breakdown.returnPoints} pts",
                    color = EcoGreenPrimary,
                    modifier = Modifier.weight(1f)
                )
                // Reviews chip
                ContributionStatChip(
                    icon = "⭐",
                    label = "Feedback",
                    count = "${breakdown.positiveReviewsCount} reviews",
                    points = "+${breakdown.feedbackPoints} pts",
                    color = EcoAmber,
                    modifier = Modifier.weight(1f)
                )
                // Trust bonus chip
                ContributionStatChip(
                    icon = "🛡️",
                    label = "Trust Bonus",
                    count = "Clean Record",
                    points = "+${breakdown.trustBonusPoints} pts",
                    color = Color(0xFF1976D2),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Active Trust Perks Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = EcoGreenDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = breakdown.verifiedTrustFlair,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Verified trust shield actively displayed on your listings & borrow requests",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action: View Badges
            OutlinedButton(
                onClick = onInspectBadges,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("explore_badge_tiers_btn"),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = EcoGreenDark
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Explore Badge Tiers & Perks Showcase",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EcoGreenDark
                )
            }
        }
    }
}

@Composable
private fun ContributionStatChip(
    icon: String,
    label: String,
    count: String,
    points: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = count,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = points,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

/**
 * Interactive Badge Showcase displaying multi-tiered badges (Bronze, Silver, Gold)
 * with circular progress rings and tap-to-inspect detail dialogs.
 */
@Composable
fun KarmaBadgeShowcase(
    badges: List<KarmaBadge>,
    onBadgeClick: (KarmaBadge) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Community Badges & Tiers",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Unlock Bronze, Silver & Gold by contributing to the neighborhood",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Grid or vertical list of rich badge cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            badges.chunked(2).forEach { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowBadges.forEach { badge ->
                        KarmaBadgeCard(
                            badge = badge,
                            onClick = { onBadgeClick(badge) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowBadges.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Single Badge Card featuring a custom animated circular progress ring,
 * metallic rank medal pill, and active community perk preview.
 */
@Composable
fun KarmaBadgeCard(
    badge: KarmaBadge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tier = badge.currentTier
    val tierColor = Color(tier.primaryColor)
    val nextTierColor = badge.nextTier?.let { Color(it.primaryColor) } ?: EcoGold
    val ringColor = if (badge.nextTier != null) nextTierColor else tierColor

    val animatedProgress by animateFloatAsState(
        targetValue = badge.progressFraction,
        animationSpec = tween(durationMillis = 1000),
        label = "badge_ring_${badge.id}"
    )

    Card(
        modifier = modifier
            .testTag("badge_card_${badge.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Progress Ring around Icon
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .testTag("progress_ring_${badge.id}"),
                contentAlignment = Alignment.Center
            ) {
                // Background Track and Progress Arc
                Canvas(modifier = Modifier.size(70.dp)) {
                    val strokeWidth = 5.dp.toPx()
                    // Background track
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        style = Stroke(width = strokeWidth)
                    )
                    // Progress arc
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Inner circle with medal background
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(tier.containerColor),
                                    Color(tier.containerColor).copy(alpha = 0.8f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge.iconEmoji,
                        fontSize = 24.sp
                    )
                }

                // Mini Tier Tag Pill at bottom of ring
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(6.dp),
                    color = tierColor
                ) {
                    Text(
                        text = tier.displayName.uppercase(),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = badge.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Current progress vs target
            Spacer(modifier = Modifier.height(2.dp))
            val progressText = if (badge.nextTier != null) {
                "${badge.currentValue} / ${badge.nextTierThreshold} ${badge.unit}"
            } else {
                "Max Rank (${badge.currentValue} ${badge.unit})"
            }
            Text(
                text = progressText,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            // Next tier target or active perk hint
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ) {
                Text(
                    text = if (badge.nextTier != null) {
                        "Next: ${badge.nextTier.displayName} (${(badge.progressFraction * 100).toInt()}%)"
                    } else {
                        "✓ Gold Complete"
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.nextTier != null) EcoGreenPrimary else EcoGold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Interactive Dialog displaying full badge progression details,
 * Bronze / Silver / Gold milestone requirements, active and upcoming perks.
 */
@Composable
fun BadgeDetailDialog(
    badge: KarmaBadge,
    onDismiss: () -> Unit,
    onSimulateBonus: (() -> Unit)? = null
) {
    val tier = badge.currentTier
    val tierColor = Color(tier.primaryColor)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("badge_detail_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Top close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = badge.category.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_badge_detail_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hero Medal Graphic
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(tier.containerColor),
                                        Color(tier.primaryColor).copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .border(2.dp, tierColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = badge.iconEmoji, fontSize = 38.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = badge.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    VerifiedTrustBadge(
                        tier = tier,
                        label = "Current Rank",
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = badge.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                // Milestone Ladder (Bronze -> Silver -> Gold)
                Text(
                    text = "Progression Ladder & Perks",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    badge.milestones.forEach { milestone ->
                        val isAchieved = badge.currentValue >= milestone.threshold
                        val isCurrentTier = badge.currentTier == milestone.tier

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isAchieved) {
                                Color(milestone.tier.containerColor).copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            },
                            border = if (isCurrentTier) {
                                androidx.compose.foundation.BorderStroke(1.5.dp, Color(milestone.tier.primaryColor))
                            } else null,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(milestone.tier.primaryColor)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isAchieved) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${milestone.tier.displayName} Milestone (${milestone.threshold} ${badge.unit})",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(milestone.tier.textColor)
                                        )
                                        if (isAchieved) {
                                            Text(
                                                text = "UNLOCKED",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = EcoGreenDark
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Perk: ${milestone.perkText}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Motivational Tip Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EcoMint
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = EcoGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = badge.motivationalTip,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = EcoGreenDark,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onSimulateBonus != null) {
                        OutlinedButton(
                            onClick = onSimulateBonus,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("simulate_karma_increment_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("+50 Karma Test", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dismiss_badge_detail_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Got it!", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
