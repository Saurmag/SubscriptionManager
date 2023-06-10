package com.example.subscriptionmanager.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.subscriptionmanager.model.database.SubscriptionDatabase
import com.example.subscriptionmanager.model.database.migration_1_2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

private const val SUBSCRIPTION_DATABASE = "SUBSCRIPTION_DATABASE"

data class ProfilePreferences(
    val fullName: String,
    val profit: Int
)

class SubscriptionRepository @OptIn(DelicateCoroutinesApi::class)
private constructor(
    context: Context,
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope = GlobalScope
){

    private val database = Room.databaseBuilder(
        context,
        SubscriptionDatabase::class.java,
        SUBSCRIPTION_DATABASE
    )
        .addMigrations(migration_1_2)
        .build()

    fun getSubscriptions() : Flow<List<Subscription>> = database.subscriptionDao().getSubscriptions()

    suspend fun getSubscription(subscriptionID: UUID) = database.subscriptionDao().getSubscription(subscriptionID)

    fun updateSubscription(subscription: Subscription) {

        coroutineScope.launch {

            database.subscriptionDao().updateSubscription(subscription)
        }
    }

    suspend fun addSubscription(subscription: Subscription) = database.subscriptionDao().addSubscription(subscription)

    suspend fun deleteSubscription(id: UUID) = database.subscriptionDao().deleteSubscription(id)

    fun amountPrice() = database.subscriptionDao().amountPrice()

    val profilePreferencesFlow: Flow<ProfilePreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val fullName = preferences[FULL_NAME] ?: "User"
            val profit = preferences[PROFIT] ?: 50
            ProfilePreferences(fullName, profit)
        }.distinctUntilChanged()

    suspend fun updateFullName(fullName: String) {

        dataStore.edit { preferences ->

            preferences[FULL_NAME] = fullName
        }
    }

    suspend fun updateProfit(profit: Int) {

        dataStore.edit { preferences ->

            preferences[PROFIT] = profit
        }
    }

    companion object{
        private val FULL_NAME = stringPreferencesKey("fullName")
        private val PROFIT = intPreferencesKey("profit")
        private var INSTANCE: SubscriptionRepository? = null

        fun initialize(context: Context){
            if (INSTANCE == null){
                val dataStore = PreferenceDataStoreFactory.create {
                    context.preferencesDataStoreFile("profile")
                }
                INSTANCE = SubscriptionRepository(context, dataStore)
            }
        }

        fun get() : SubscriptionRepository {
            return INSTANCE ?:
            throw IllegalStateException("Repository must be initialized")
        }
    }
}