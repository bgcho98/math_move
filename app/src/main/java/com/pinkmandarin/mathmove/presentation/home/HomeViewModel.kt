package com.pinkmandarin.mathmove.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinkmandarin.mathmove.domain.model.Stage
import com.pinkmandarin.mathmove.domain.repository.AuthRepository
import com.pinkmandarin.mathmove.domain.usecase.GetStagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val stages: List<Stage> = emptyList(),
    val userName: String = "",
    val userPhotoUrl: String? = null,
    val maxClearedStage: Int = 0,
    val totalStars: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStagesUseCase: GetStagesUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val user = authRepository.getCurrentUser().first()
                if (user == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Not logged in") }
                    return@launch
                }

                currentUserId = user.uid
                _uiState.update {
                    it.copy(
                        userName = user.displayName.ifEmpty { "Player" },
                        userPhotoUrl = user.photoUrl
                    )
                }

                getStagesUseCase(user.uid).collect { stages ->
                    val maxCleared = stages.filter { it.stars > 0 }.maxOfOrNull { it.number } ?: 0
                    val totalStars = stages.sumOf { it.stars }
                    _uiState.update {
                        it.copy(
                            stages = stages,
                            maxClearedStage = maxCleared,
                            totalStars = totalStars,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load data"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadData()
    }
}
