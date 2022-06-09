package com.noamrault.chatapp.data

import com.google.gson.Gson
import com.noamrault.chatapp.data.friend.Friend
import com.noamrault.chatapp.data.message.Message
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ObjectSerializer {
    companion object {
        fun serialize(obj: Any?): String {
            if (obj == null) {
                return ""
            }

            return Gson().toJson(obj)
        }

        fun deserialize(str: String?): Any? {
            if (str == null || str.isEmpty()) {
                return null
            }

            return Gson().fromJson(str, Message::class.java)
        }
    }
}