package com.noamrault.chatapp.data.group

import androidx.room.*
import com.noamrault.chatapp.data.friend.Friend

@Dao
interface GroupDao {
    @Query("SELECT * FROM [group]")
    fun getAll(): List<Group>

    @Query("SELECT * FROM [group] WHERE id LIKE :groupId")
    fun findById(groupId: String): Group

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(groupList: List<Group>)

    @Delete
    fun delete(group: Group)

    @Query("DELETE FROM [group]")
    fun deleteAll()
}