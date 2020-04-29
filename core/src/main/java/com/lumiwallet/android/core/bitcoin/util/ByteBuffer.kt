package com.lumiwallet.android.core.bitcoin.util

import java.io.ByteArrayOutputStream
import java.io.IOException

class ByteBuffer(vararg bytes: Byte) {

    private var buffer = ByteArrayOutputStream()

    init {
        buffer = ByteArrayOutputStream()
        append(*bytes)
    }

    fun append(vararg bytes: Byte) {
        try {
            buffer.write(bytes)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun putFirst(vararg bytes: Byte) {
        val current = buffer.toByteArray()
        buffer.reset()
        try {
            buffer.write(bytes)
            buffer.write(current)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun bytes(): ByteArray = buffer.toByteArray()

    fun bytesReversed(): ByteArray {
        val bytes = bytes()
        val result = ByteArray(bytes.size)
        for (i in bytes.indices) {
            result[i] = bytes[bytes.size - 1 - i]
        }
        return result
    }

    fun size(): Int = buffer.size()

}
