package com.noamrault.chatapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noamrault.chatapp.data.friend.Friend
import com.noamrault.chatapp.data.friend.FriendDao
import com.noamrault.chatapp.data.group.Group
import com.noamrault.chatapp.data.group.GroupDao
import com.noamrault.chatapp.data.message.Message
import com.noamrault.chatapp.data.message.MessageDao

@Database(entities = [Message::class, Group::class, Friend::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun groupDao(): GroupDao
    abstract fun friendDao(): FriendDao
}