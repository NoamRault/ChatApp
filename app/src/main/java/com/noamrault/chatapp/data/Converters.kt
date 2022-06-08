package com.noamrault.chatapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.noamrault.chatapp.data.friend.Friend
import java.util.*


object Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun listToJson(value: List<Friend>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Friend>::class.java).toList()
}