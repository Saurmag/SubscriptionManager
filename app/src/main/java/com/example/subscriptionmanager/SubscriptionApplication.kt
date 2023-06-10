package com.example.subscriptionmanager

import android.app.Application
import com.example.subscriptionmanager.model.SubscriptionRepository

class SubscriptionApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        SubscriptionRepository.initialize(this)
    }
}