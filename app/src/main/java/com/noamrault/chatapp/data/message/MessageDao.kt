package com.noamrault.chatapp.data.message

import androidx.room.*

@Dao
interface MessageDao {
    @Query("SELECT * FROM message")
    fun getAll(): List<Message>

    @Query("SELECT * FROM message WHERE id IN (:messageIds)")
    fun findByIds(messageIds: Array<String>): List<Message>

    @Query("SELECT * FROM message WHERE group_id LIKE :groupId")
    fun findByGroup(groupId: String): List<Message>

    @Query("SELECT id FROM message WHERE group_id LIKE :groupId")
    fun findIdByGroup(groupId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg messages: Message)

    @Delete
    fun delete(message: Message)
}