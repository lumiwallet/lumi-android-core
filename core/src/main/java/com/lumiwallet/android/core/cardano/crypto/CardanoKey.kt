package com.lumiwallet.android.core.cardano.crypto

import com.lumiwallet.android.core.utils.storeInt32BE
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.digests.SHA512Digest
import org.bouncycastle.crypto.macs.HMac
import org.bouncycastle.crypto.params.KeyParameter
import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor

class CardanoKey(
    val privateKey: ByteArray,
    val chainCode: ByteArray,
) {

    fun deriveChild(index: Int, hardened: Boolean): CardanoKey {
        val exKey = Native.deriveCardanoKey(privateKey, chainCode, index, hardened)
        return CardanoKey(
            exKey.take(KEY_LENGTH).toByteArray(),
            exKey.takeLast(CHAINCODE_LENGTH).toByteArray()
        )
    }

    fun deriveByPath(path: String): CardanoKey {
        var key = this
        for (chunk in path.split( '/')) {
            var hardened = false
            var indexText = chunk

            if (chunk.contains("'") || chunk.contains("H")) {
                hardened = true
                indexText = indexText.dropLast(1)
            }

            val index = indexText.toInt()
            key = key.deriveChild(index, hardened)
        }
        return key
    }

    private var _publicKey: ByteArray? = null
        get() = if (field == null) {
            Native.cardanoCryptoEd25519publickey(privateKey)
        } else {
            field
        }

    val publicKey: ByteArray
        get() = _publicKey!!

    companion object {
        const val KEY_LENGTH = 64
        const val CHAINCODE_LENGTH = 32
        private const val PBKDF2_ROUNDS = 4096

        fun fromEntropy(
            entropy: ByteArray
        ): CardanoKey {
            val password = ""
            val salt = entropy

            val hmac = HMac(SHA512Digest())
            val hLen = hmac.macSize;
            if (KEY_LENGTH + CHAINCODE_LENGTH > (Math.pow(
                    2.0,
                    32.0
                ) - 1) * hLen
            ) throw IllegalStateException("Derived key to long")
            val derivedKey = ByteArray(KEY_LENGTH + CHAINCODE_LENGTH)
            val J = 0
            val K: Int = hmac.macSize
            val U: Int = hmac.macSize shl 1
            val B = K + U
            val workingArray = ByteArray(K + U + 4)

            val macParams: CipherParameters = KeyParameter(password.toByteArray())

            hmac.init(macParams)

            var kpos = 0
            var blk = 1
            while (kpos < KEY_LENGTH + CHAINCODE_LENGTH) {
                storeInt32BE(blk, workingArray, B)
                hmac.update(salt, 0, salt.size)
                hmac.reset()
                hmac.update(salt, 0, salt.size)
                hmac.update(workingArray, B, 4)
                hmac.doFinal(workingArray, U)
                System.arraycopy(workingArray, U, workingArray, J, K)
                var i = 1
                var j = J
                var k = K
                while (i < PBKDF2_ROUNDS) {
                    hmac.init(macParams)
                    hmac.update(workingArray, j, K)
                    hmac.doFinal(workingArray, k)
                    var u = U
                    var v = k
                    while (u < B) {
                        workingArray[u] = workingArray[u] xor workingArray[v]
                        u++
                        v++
                    }
                    val swp = k
                    k = j
                    j = swp
                    i++
                }
                val tocpy = Math.min(KEY_LENGTH + CHAINCODE_LENGTH - kpos, K)
                System.arraycopy(workingArray, U, derivedKey, kpos, tocpy)
                kpos += K
                blk++
            }
            workingArray.fill(0x00.toByte())

            val prvKey = derivedKey.take(KEY_LENGTH).toByteArray()

            prvKey[0] = prvKey[0] and 248.toByte()
            prvKey[31] = prvKey[31] and 0x1F
            prvKey[31] = prvKey[31] or 64

            val cc = derivedKey.takeLast(CHAINCODE_LENGTH).toByteArray()

            return CardanoKey(prvKey, cc)
        }
    }
}