package com.example.subscriptionmanager.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.subscriptionmanager.model.Subscription
import com.example.subscriptionmanager.model.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class CreateSubscriptionViewModel(subscriptionID: UUID) : ViewModel() {

    private val repository = SubscriptionRepository.get()

    private var _subscription: MutableStateFlow<Subscription?> = MutableStateFlow(null)

    val subscription: StateFlow<Subscription?> = _subscription.asStateFlow()

    init {
        viewModelScope.launch {

            _subscription.value = repository.getSubscription(subscriptionID)
        }
    }

    override fun onCleared() {
        super.onCleared()

        subscription.value?.let { subscription ->

            repository.updateSubscription(subscription)
        }

        Log.d("ON_CLEARED","CURRENT SUBSCRIPTION - ${subscription.value?.id}")
    }

    fun updateSubscription(onUpdate: (Subscription) -> Subscription) {

        _subscription.update { oldSub ->

            oldSub?.let { onUpdate(it) }
        }
    }

    suspend fun deleteSubscription(id: UUID) = repository.deleteSubscription(id)
}


class CreateSubscriptionViewModelFactory(
    private val subscriptionID: UUID
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateSubscriptionViewModel(subscriptionID) as T
    }
}