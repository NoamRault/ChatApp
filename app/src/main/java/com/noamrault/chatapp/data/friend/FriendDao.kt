package com.noamrault.chatapp.data.friend

import androidx.room.*
import com.noamrault.chatapp.data.group.Group

@Dao
interface FriendDao {
    @Query("SELECT * FROM friend")
    fun getAll(): List<Friend>

    @Query("SELECT * FROM friend WHERE id LIKE :friendId")
    fun findById(friendId: String): Friend

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(friendList: List<Friend>)

    @Delete
    fun delete(friend: Friend)

    @Query("DELETE FROM friend")
    fun deleteAll()
}