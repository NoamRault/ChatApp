package com.noamrault.chatapp.data.friend

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Friend(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "username") val username: String
) : Serializable