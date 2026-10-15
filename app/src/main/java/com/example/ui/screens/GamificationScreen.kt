package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.EcoLendViewModel
import com.example.ui.components.BadgeDetailDialog
import com.example.ui.components.KarmaBadgeShowcase
import com.example.ui.theme.EcoAmber
import com.example.ui.theme.EcoGold
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenPrimary

data class CommunityBadge(
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean,
    val progress: String
)

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val neighborhood: String,
    val wasteKg: Double,
    val points: Int,
    val isUser: Boolean
)

@Composable
fun GamificationScreen(
    viewModel: EcoLendViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val karmaBadges by viewModel.karmaBadges.collectAsState()
    val selectedBadgeForDetail by viewModel.selectedBadgeForDetail.collectAsState()

    val badges = listOf(
        CommunityBadge(
            title = "Zero Waste Pioneer",
            description = "Diverted over 25 kg of manufacturing waste from landfills.",
            iconName = "🌱",
            isUnlocked = true,
            progress = "Completed (42.6 kg)"
        ),
        CommunityBadge(
            title = "Five-Star Neighbor",
            description = "Maintained a 4.9+ rating over 10+ completed exchanges.",
            iconName = "⭐",
            isUnlocked = true,
            progress = "4.95 Rating"
        ),
        CommunityBadge(
            title = "Porch Master",
            description = "Successfully executed 10 contactless porch pickups on time.",
            iconName = "📦",
            isUnlocked = true,
            progress = "Completed (14 pickups)"
        ),
        CommunityBadge(
            title = "Tool Maestro",
            description = "Shared tools across 4 different categories with neighbors.",
            iconName = "🛠️",
            isUnlocked = true,
            progress = "4 / 4 Categories"
        ),
        CommunityBadge(
            title = "Century Conserver",
            description = "Divert 100 kg of equipment waste through active sharing.",
            iconName = "🏆",
            isUnlocked = false,
            progress = "42.6 / 100 kg"
        )
    )

    val leaderboard = listOf(
        LeaderboardEntry(1, "Elena Rostova", "Pinecrest Lane", 58.2, 540, false),
        LeaderboardEntry(2, "${userProfile.name} (You)", userProfile.neighborhood.take(18), userProfile.wasteSavedKg, userProfile.karmaScore, true),
        LeaderboardEntry(3, "David Chen", "Oakridge Crescent", 39.5, 390, false),
        LeaderboardEntry(4, "Sarah Miller", "Maple Grove", 36.0, 360, false),
        LeaderboardEntry(5, "Marcus Taylor", "Cedar Crest Way", 31.2, 310, false)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("gamification_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Eco Karma Level Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = EcoGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = userProfile.levelTitle,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenDark
                                )
                                Text(
                                    text = "${userProfile.karmaScore} Eco Karma Points",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress to next level
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Progress to Tier 4", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${userProfile.karmaScore} / 600 XP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (userProfile.karmaScore / 600f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EcoGreenPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.toggleShareImpactDialog(true) },
                        modifier = Modifier.fillMaxWidth().testTag("gamification_share_card_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Impact to Social Media", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Personal Impact Metrics Grid
        item {
            Text(
                text = "Your Lifetime Contribution",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Waste Diverted", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${userProfile.wasteSavedKg} kg",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoGreenDark
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Money Saved", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${userProfile.moneySavedDollars.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EcoAmber
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 1.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("CO2 Prevented", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${userProfile.co2PreventedKg.toInt()} kg",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // 3. Multi-tiered Community Badges Showcase
        item {
            KarmaBadgeShowcase(
                badges = karmaBadges,
                onBadgeClick = { badge -> viewModel.selectBadge(badge) }
            )
        }

        // 4. District Leaderboard
        item {
            Text(
                text = "District Sharing Leaderboard 🏅",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    leaderboard.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "#${entry.rank}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (entry.rank == 1) EcoGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(32.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (entry.isUser) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (entry.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${entry.neighborhood} • ${entry.wasteKg}kg diverted",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${entry.points} pts",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (entry.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }

    selectedBadgeForDetail?.let { badge ->
        BadgeDetailDialog(
            badge = badge,
            onDismiss = { viewModel.closeBadgeDetail() },
            onSimulateBonus = { viewModel.simulateKarmaBonus(50) }
        )
    }
}
