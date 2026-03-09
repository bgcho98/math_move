package com.pinkmandarin.mathmove.presentation.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinkmandarin.mathmove.domain.repository.RankingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RankingUiEntry(
    val rank: Int = 0,
    val userId: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val score: String = "",
    val detail: String = ""
)

data class RankingUiState(
    val selectedTab: RankingTab = RankingTab.STAGE,
    val selectedStage: Int = 1,
    val stageRankings: List<RankingUiEntry> = emptyList(),
    val globalRankings: List<RankingUiEntry> = emptyList(),
    val availableStages: List<Int> = (1..50).toList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

enum class RankingTab {
    STAGE,
    GLOBAL
}

@HiltViewModel
class RankingViewModel @Inject constructor(
    private val rankingRepository: RankingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    init {
        loadGlobalRanking()
    }

    fun selectTab(tab: RankingTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        when (tab) {
            RankingTab.STAGE -> loadStageRanking(_uiState.value.selectedStage)
            RankingTab.GLOBAL -> loadGlobalRanking()
        }
    }

    fun selectStage(stage: Int) {
        _uiState.update { it.copy(selectedStage = stage) }
        loadStageRanking(stage)
    }

    private fun loadStageRanking(stage: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                rankingRepository.getStageRanking(stage).collect { entries ->
                    val rankingEntries = entries.mapIndexed { index, entry ->
                        RankingUiEntry(
                            rank = index + 1,
                            userId = entry.uid,
                            displayName = entry.displayName,
                            photoUrl = entry.photoUrl,
                            score = "${entry.stars} stars",
                            detail = formatTime(entry.bestTime)
                        )
                    }
                    _uiState.update {
                        it.copy(
                            stageRankings = rankingEntries,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load rankings"
                    )
                }
            }
        }
    }

    private fun loadGlobalRanking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                rankingRepository.getGlobalRanking().collect { entries ->
                    val rankingEntries = entries.mapIndexed { index, entry ->
                        RankingUiEntry(
                            rank = index + 1,
                            userId = entry.uid,
                            displayName = entry.displayName,
                            photoUrl = entry.photoUrl,
                            score = "Stage ${entry.maxClearedStage}",
                            detail = "⭐ ${entry.totalStars} · avg ${formatTime(entry.avgBestTime)}"
                        )
                    }
                    _uiState.update {
                        it.copy(
                            globalRankings = rankingEntries,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("RankingViewModel", "Failed to load global ranking", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load rankings"
                    )
                }
            }
        }
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = (millis / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
