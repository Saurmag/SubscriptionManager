package com.example.subscriptionmanager.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptionmanager.model.Subscription
import com.example.subscriptionmanager.model.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val SUBSCRIPTION_LIST_VIEWMODEL = "SUBSCRIPTION_LIST_VIEWMODEL"

class SubscriptionListViewModel : ViewModel() {

    private val repository = SubscriptionRepository.get()

    private val _subscriptions: MutableStateFlow<List<Subscription>> = MutableStateFlow(emptyList())

    val subscriptions: StateFlow<List<Subscription>>
        get() = _subscriptions.asStateFlow()

    init {

        viewModelScope.launch {

            repository.getSubscriptions().collect {

                _subscriptions.value = it
            }
        }
    }

    suspend fun addSubscription(subscription: Subscription) {

        repository.addSubscription(subscription)
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(SUBSCRIPTION_LIST_VIEWMODEL, "ON CLEARED")
    }
}