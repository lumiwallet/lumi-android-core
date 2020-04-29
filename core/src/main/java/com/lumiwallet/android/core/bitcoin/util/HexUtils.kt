package com.lumiwallet.android.core.bitcoin.util

import org.bouncycastle.util.encoders.Hex

object HexUtils {

    fun asBytes(string: String): ByteArray {
        var string = string
        if (string.length % 2 == 1) {
            string = "0$string"
        }
        return Hex.decode(string)
    }

}
