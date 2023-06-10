package com.example.subscriptionmanager.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.subscriptionmanager.model.Subscription

@Database(entities = [Subscription::class], version = 2)
abstract class SubscriptionDatabase : RoomDatabase() {
    abstract fun subscriptionDao() : SubscriptionDao
}

val migration_1_2 = object : Migration(1, 2){

    override fun migrate(database: SupportSQLiteDatabase) {

        with(database){
            execSQL("CREATE TABLE subscription_backup(id BLOB NOT NULL, title TEXT NOT NULL, price INTEGER NOT NULL, PRIMARY KEY(id))")
            execSQL("INSERT INTO subscription_backup(id, title, price) SELECT id, title, price FROM subscription")
            execSQL("DROP TABLE subscription")
            execSQL("ALTER TABLE subscription_backup RENAME TO subscription")
        }
    }
}