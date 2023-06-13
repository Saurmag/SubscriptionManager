package com.example.subscriptionmanager.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptionmanager.model.SubscriptionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "PROFILE_FRAGMENT_VIEWMODEL"

class ProfileFragmentViewModel : ViewModel() {

    private val repository = SubscriptionRepository.get()

    private val _uiState: MutableStateFlow<ProfileFragmentUiState> = MutableStateFlow(
        ProfileFragmentUiState()
    )

    val uiState: StateFlow<ProfileFragmentUiState> = _uiState.asStateFlow()

    init {

        viewModelScope.launch {

            repository.amountPrice().combine(repository.profilePreferencesFlow) { amountPrice, profilePreferences ->

                ProfileFragmentUiState(
                    amountPrice = amountPrice,
                    profit = profilePreferences.profit,
                    fullName = profilePreferences.fullName)

            }.collect { oldProfileFragmentUiState ->

                _uiState.update { newProfileFragmentUiState ->

                    newProfileFragmentUiState.copy(
                        amountPrice = oldProfileFragmentUiState.amountPrice,
                        profit = oldProfileFragmentUiState.profit,
                        fullName = oldProfileFragmentUiState.fullName
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ON CLEARED")
    }

    fun updateFullName(fullName: String) {

        viewModelScope.launch {

            repository.updateFullName(fullName)
        }
    }

    fun updateProfit(profit: Int) {

        viewModelScope.launch {

            repository.updateProfit(profit)
        }
    }

    fun spendOnSubscriptions(state: ProfileFragmentUiState): Int {

        val profit = state.profit

        val amount = state.amountPrice

        return if (profit != 0) {
            ((amount / profit.toDouble()) * 100).toInt()
        } else {
            0
        }
    }

}

data class ProfileFragmentUiState(
    val amountPrice: Int = 0,
    val profit: Int = 0,
    val fullName: String = "",
)