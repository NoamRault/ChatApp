package com.noamrault.chatapp.data.message

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(primaryKeys = ["id", "group_id"])
data class Message(
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "group_id") val groupId: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "sent_date") val sentDate: Date,
    @ColumnInfo(name = "author") val author: String
)