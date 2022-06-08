package com.noamrault.chatapp.data

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

            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(obj)
            oos.close()

            return encodeBytes(baos.toByteArray())
        }

        fun deserialize(str: String?): Any? {
            if (str == null || str.isEmpty()) {
                return null
            }

            val bais = ByteArrayInputStream(decodeBytes(str))
            val ois = ObjectInputStream(bais)

            return ois.readObject()
        }

        private fun encodeBytes(bytes: ByteArray): String {
            val buffer = StringBuffer()

            for (byte in bytes) {
                buffer.append((((byte.toInt() shr 4) and 0xF).plus('a'.code)).toChar())
                buffer.append((((byte.toInt()) and 0xF).plus('a'.code)).toChar())
            }

            return buffer.toString()
        }

        private fun decodeBytes(str: String): ByteArray {
            val bytes = ByteArray(str.length / 2)

            for (i in str.indices) {
                var c = str[i]
                bytes[i / 2] = ((c.minus('a')) shl 4).toByte()

                c = str[i + 1]
                bytes[i / 2] = (bytes[i / 2] + (c.minus('a'))).toByte()
            }

            return bytes
        }
    }
}