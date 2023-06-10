package com.example.subscriptionmanager.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.subscriptionmanager.model.Subscription
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM subscription")
    fun getSubscriptions() : Flow<List<Subscription>>

    @Query("SELECT * FROM subscription WHERE id=(:id)")
    suspend fun getSubscription(id: UUID) : Subscription

    @Update
    suspend fun updateSubscription(subscription: Subscription)

    @Insert
    suspend fun addSubscription(subscription: Subscription)

    @Query("DELETE FROM subscription WHERE id=(:id)")
    suspend fun deleteSubscription(id: UUID)

    @Query("SELECT sum(price) FROM subscription")
    fun amountPrice() : Flow<Int>
}