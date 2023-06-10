package com.example.subscriptionmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "subscription")
data class Subscription(
    @PrimaryKey val id: UUID,
    val title: String,
    val price: Int,
)