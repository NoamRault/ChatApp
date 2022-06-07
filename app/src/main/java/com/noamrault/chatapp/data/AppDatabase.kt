package com.noamrault.chatapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noamrault.chatapp.data.message.Message
import com.noamrault.chatapp.data.message.MessageDao

@Database(entities = [Message::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}