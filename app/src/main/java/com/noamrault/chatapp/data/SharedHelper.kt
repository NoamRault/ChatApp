package com.noamrault.chatapp.data

import java.util.*

class SharedHelper {

    companion object {
        private const val allowedCharacters =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWYXZ"

        fun getRandomString(): String {
            val random = Random()
            val sb = StringBuilder(20)
            for (i in 0 until 20)
                sb.append(allowedCharacters[random.nextInt(allowedCharacters.length)])
            return sb.toString()
        }
    }
}