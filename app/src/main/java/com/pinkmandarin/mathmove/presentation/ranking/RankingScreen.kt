package com.pinkmandarin.mathmove.presentation.ranking

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pinkmandarin.mathmove.R
import com.pinkmandarin.mathmove.presentation.theme.BubbleLilac
import com.pinkmandarin.mathmove.presentation.theme.BubblePeach
import com.pinkmandarin.mathmove.presentation.theme.BubbleSky
import com.pinkmandarin.mathmove.presentation.theme.ElectricPurpleEnd
import com.pinkmandarin.mathmove.presentation.theme.ElectricPurpleStart
import com.pinkmandarin.mathmove.presentation.theme.GradientPurpleEnd
import com.pinkmandarin.mathmove.presentation.theme.GradientPurpleStart
import com.pinkmandarin.mathmove.presentation.theme.PrimaryOrange
import com.pinkmandarin.mathmove.presentation.theme.SecondaryBlue
import com.pinkmandarin.mathmove.presentation.theme.StarGold
import com.pinkmandarin.mathmove.presentation.theme.TextOnPrimary

private val GoldMedal = Color(0xFFFFD700)
private val SilverMedal = Color(0xFFC0C0C0)
private val BronzeMedal = Color(0xFFCD7F32)

private val DarkSurface = Color(0xFF1A1A2E)
private val DarkCard = Color(0xFF16213E)
private val DarkCardLight = Color(0xFF1E2A4A)
private val AccentPurple = Color(0xFF7C3AED)
private val AccentCyan = Color(0xFF06B6D4)
private val SubtleText = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onBackClick: () -> Unit,
    viewModel: RankingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.ranking_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextOnPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextOnPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        listOf(ElectricPurpleStart, ElectricPurpleEnd)
                    )
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkSurface)
        ) {
            // Tab selector - pill style
            TabSelector(
                selectedTab = uiState.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )

            // Stage selector (only for stage tab)
            if (uiState.selectedTab == RankingTab.STAGE) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.availableStages) { stage ->
                        val isSelected = stage == uiState.selectedStage
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectStage(stage) },
                            label = {
                                Text(
                                    text = "$stage",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentPurple,
                                selectedLabelColor = Color.White,
                                containerColor = DarkCard,
                                labelColor = SubtleText
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = DarkCardLight,
                                selectedBorderColor = AccentPurple,
                                enabled = true,
                                selected = isSelected
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Content
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentCyan)
                }
            } else {
                val rankings = when (uiState.selectedTab) {
                    RankingTab.STAGE -> uiState.stageRankings
                    RankingTab.GLOBAL -> uiState.globalRankings
                }

                if (rankings.isEmpty()) {
                    EmptyRankingView()
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Top 3 podium
                        if (rankings.size >= 3) {
                            item {
                                PodiumSection(
                                    first = rankings[0],
                                    second = rankings[1],
                                    third = rankings[2]
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                            // Rest of rankings
                            items(rankings.drop(3)) { entry ->
                                RankingListItem(entry = entry)
                            }
                        } else {
                            items(rankings) { entry ->
                                RankingListItem(entry = entry)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabSelector(
    selectedTab: RankingTab,
    onTabSelected: (RankingTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkCard)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabPill(
                text = stringResource(R.string.ranking_stage),
                isSelected = selectedTab == RankingTab.STAGE,
                onClick = { onTabSelected(RankingTab.STAGE) },
                modifier = Modifier.weight(1f)
            )
            TabPill(
                text = stringResource(R.string.ranking_global),
                isSelected = selectedTab == RankingTab.GLOBAL,
                onClick = { onTabSelected(RankingTab.GLOBAL) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) AccentPurple else Color.Transparent,
        animationSpec = tween(200),
        label = "tabBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else SubtleText,
        animationSpec = tween(200),
        label = "tabText"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun PodiumSection(
    first: RankingUiEntry,
    second: RankingUiEntry,
    third: RankingUiEntry
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd place
        PodiumItem(
            entry = second,
            medalColor = SilverMedal,
            podiumHeight = 80.dp.value,
            medal = "\uD83E\uDD48" // silver medal
        )

        // 1st place
        PodiumItem(
            entry = first,
            medalColor = GoldMedal,
            podiumHeight = 100.dp.value,
            medal = "\uD83E\uDD47", // gold medal
            isFirst = true
        )

        // 3rd place
        PodiumItem(
            entry = third,
            medalColor = BronzeMedal,
            podiumHeight = 64.dp.value,
            medal = "\uD83E\uDD49" // bronze medal
        )
    }
}

@Composable
private fun PodiumItem(
    entry: RankingUiEntry,
    medalColor: Color,
    podiumHeight: Float,
    medal: String,
    isFirst: Boolean = false
) {
    val avatarSize = if (isFirst) 64.dp else 52.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(if (isFirst) 120.dp else 100.dp)
    ) {
        // Medal emoji
        Text(
            text = medal,
            fontSize = if (isFirst) 28.sp else 22.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(avatarSize)
                .border(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        listOf(medalColor, medalColor.copy(alpha = 0.5f))
                    ),
                    shape = CircleShape
                )
                .padding(3.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            GradientPurpleStart.copy(alpha = 0.7f),
                            GradientPurpleEnd.copy(alpha = 0.7f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.displayName.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = if (isFirst) 26.sp else 20.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Name
        Text(
            text = entry.displayName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        // Score
        Text(
            text = entry.score,
            style = MaterialTheme.typography.bodySmall,
            color = medalColor,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Podium bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            medalColor.copy(alpha = 0.4f),
                            medalColor.copy(alpha = 0.15f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.detail,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun RankingListItem(
    entry: RankingUiEntry,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank number
            Text(
                text = "#${entry.rank}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = SubtleText,
                modifier = Modifier.width(40.dp)
            )

            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(BubbleSky.copy(alpha = 0.5f), BubbleLilac.copy(alpha = 0.5f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.displayName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name and detail
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = SubtleText
                )
            }

            // Score
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentPurple.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = entry.score,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun EmptyRankingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "\uD83C\uDFC6",
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.ranking_empty),
                style = MaterialTheme.typography.headlineSmall,
                color = SubtleText
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.ranking_be_first),
                style = MaterialTheme.typography.bodyLarge,
                color = AccentCyan
            )
        }
    }
}
