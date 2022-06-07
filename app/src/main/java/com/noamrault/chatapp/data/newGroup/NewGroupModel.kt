package com.noamrault.chatapp.data.newGroup

class NewGroupModel(private val text: String) {
    private var isSelected = false

    fun getText(): String {
        return text
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
    }

    fun isSelected(): Boolean {
        return isSelected
    }
}