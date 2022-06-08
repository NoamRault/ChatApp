package com.noamrault.chatapp.data.message

import androidx.fragment.app.Fragment
import com.noamrault.chatapp.MainActivity

/**
 * Class that retrieves messages from a group.
 */
class MessageDataSource {

    companion object {
        fun getMessages(
            groupId: String,
            fragment: Fragment
        ): ArrayList<Message> {
            return ArrayList(
                (fragment.requireActivity() as MainActivity)
                    .database
                    .messageDao()
                    .findByGroup(groupId)
            )
        }
    }
}