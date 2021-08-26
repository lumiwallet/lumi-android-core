package com.lumiwallet.android.core.cardano.crypto

object Native {

    external fun stringFromJNI(): String
    external fun cardanoCryptoEd25519publickey(
        privateKey: ByteArray
    ): ByteArray

    external fun cardanoCryptoEd25519sign(
        message: ByteArray,
        messageLen: Int,
        privateKey: ByteArray
    ): ByteArray


    external fun sha512HMAC(
        key: ByteArray,
        keySize: Int,
        message: ByteArray,
        messageLen: Int,
    ): ByteArray

    external fun deriveCardanoKey(
        key: ByteArray,
        chainCode: ByteArray,
        sequence: Int,
        hardened: Boolean
    ): ByteArray
}