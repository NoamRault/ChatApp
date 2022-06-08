package com.noamrault.chatapp.data.newGroup

import com.noamrault.chatapp.data.friend.Friend

class NewGroupModel(private val friend: Friend) {
    private var isSelected = false

    fun getId(): String {
        return friend.id
    }

    fun getUsername(): String {
        return friend.username
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
    }

    fun isSelected(): Boolean {
        return isSelected
    }
}