package com.lumiwallet.android.core.bitcoin.util

object ValidationUtils {

    fun isTransactionId(stirng: String): Boolean = stirng.matches("\\p{XDigit}{64}".toRegex())

    fun isHexString(string: String): Boolean = string.matches("\\p{XDigit}+".toRegex())

    fun isBase58(string: String): Boolean =
        string.matches("[123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz]+".toRegex())

    fun isEmpty(string: String?): Boolean = string == null || string.trim { it <= ' ' }.isEmpty()
}