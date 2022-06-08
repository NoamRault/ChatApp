package com.noamrault.chatapp.data.group

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noamrault.chatapp.data.friend.Friend
import java.io.Serializable

@Entity
data class Group(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "members") val members: List<Friend>
) : Serializable