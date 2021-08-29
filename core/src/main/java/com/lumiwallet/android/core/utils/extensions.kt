package com.lumiwallet.android.core.utils

import com.lumiwallet.android.core.eos.utils.HexUtils
import org.bouncycastle.crypto.digests.Blake2bDigest

val ByteArray.hex : String
    get() = HexUtils.toHex(this)

fun String.safeToByteArray(): ByteArray = try {
    HexUtils.toBytes(this)
} catch(e: Exception) {
    byteArrayOf()
}

fun storeInt32BE(value: Int, bytes: ByteArray, offSet: Int) {
    bytes[offSet + 3] = value.toByte()
    bytes[offSet + 2] = (value ushr 8).toByte()
    bytes[offSet + 1] = (value ushr 16).toByte()
    bytes[offSet] = (value ushr 24).toByte()
}

fun ByteArray.blake2b224(): ByteArray {
    val digest = Blake2bDigest(224)
    digest.update(this, 0, this.size)
    val hash = ByteArray(28) // 224/8
    digest.doFinal(hash, 0)
    return hash
}

fun ByteArray.blake2b256(): ByteArray {
    val digest = Blake2bDigest(256)
    digest.update(this, 0, this.size)
    val hash = ByteArray(32)
    digest.doFinal(hash, 0)
    return hash
}