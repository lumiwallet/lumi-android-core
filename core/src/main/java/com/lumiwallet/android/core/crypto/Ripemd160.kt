package com.lumiwallet.android.core.crypto

import org.bouncycastle.crypto.digests.RIPEMD160Digest

object Ripemd160 {

    fun from(bytes: ByteArray): ByteArray {
        val digest = RIPEMD160Digest()
        digest.update(bytes, 0, bytes.size)
        val result = ByteArray(digest.digestSize)
        digest.doFinal(result, 0)
        return result
    }
}
