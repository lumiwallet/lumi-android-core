package com.lumiwallet.android.core.eos.ec

import com.lumiwallet.android.core.crypto.ECKey
import com.lumiwallet.android.core.utils.Base58
import com.lumiwallet.android.core.utils.Sha256Hash
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.math.ec.FixedPointCombMultiplier
import java.math.BigInteger
import java.security.SecureRandom


/**
 * https://github.com/EOSIO/eosjs-ecc
 * https://github.com/EOSIO/eosjs-ecc/blob/master/src/ecdsa.js
 */

class EosPrivateKey {

    companion object {

        private const val PREFIX = "PVT"

        val secuRandom: SecureRandom = SecureRandom()

    }

    val asBigInteger: BigInteger
    val publicKey: EosPublicKey
    val curveParam: X9ECParameters
    private lateinit var ecKey: ECKey

    val bytes: ByteArray
        get() {
            val result = ByteArray(32)
            val bytes = asBigInteger.toByteArray()
            if (bytes.size <= result.size) {
                System.arraycopy(bytes, 0, result, result.size - bytes.size, bytes.size)
            } else {
                assert(bytes.size == 33 && bytes[0].toInt() == 0)
                System.arraycopy(bytes, 1, result, 0, bytes.size - 1)
            }
            return result
        }

    constructor(ecKey: ECKey){
        this.ecKey = ecKey
        this.asBigInteger = ecKey.privKey
        this.curveParam = ECKey.SECP256_K1_CURVE_PARAMS
        this.publicKey = EosPublicKey(findPubKey(asBigInteger), curveParam)
    }

    constructor(base58Str: String) {
        val split = EosEcUtil.safeSplitEosCryptoString(base58Str)
        val keyBytes: ByteArray?

        if (split.size == 1) {
            curveParam = ECKey.SECP256_K1_CURVE_PARAMS
            keyBytes = EosEcUtil.getBytesIfMatchedSha256(base58Str, null)
        } else {
            require(split.size >= 3) { "Invalid private key format: $base58Str" }

            curveParam = EosEcUtil.getCurveParamFrom(split[1])
            keyBytes = EosEcUtil.getBytesIfMatchedRipemd160(split[2], split[1], null)
        }


        if (keyBytes.size < 5) {
            throw IllegalArgumentException("Invalid private key length")
        }

        asBigInteger = getOrCreatePrivKeyBigInteger(keyBytes)
        publicKey = EosPublicKey(findPubKey(asBigInteger), curveParam)
    }


    fun clear() {
        asBigInteger.multiply(BigInteger.ZERO)
    }

    private fun findPubKey(bnum: BigInteger): ByteArray {
        return FixedPointCombMultiplier().multiply(curveParam.g, bnum).getEncoded(true)
    }

    fun toWif(): String {
        val rawPrivKey = bytes
        val resultWIFBytes = ByteArray(1 + 32 + 4)

        resultWIFBytes[0] = 0x80.toByte()
        System.arraycopy(rawPrivKey, if (rawPrivKey.size > 32) 1 else 0, resultWIFBytes, 1, 32)

        val hash = Sha256Hash.hashTwice(resultWIFBytes, 0, 33)

        System.arraycopy(hash, 0, resultWIFBytes, 33, 4)

        return Base58.encode(resultWIFBytes)
    }

    fun sign(digest: Sha256Hash): EcSignature {
        return EcDsa.sign(digest.bytes, this)
    }

    override fun toString(): String {
        return if (curveParam == ECKey.SECP256_K1_CURVE_PARAMS) {
            toWif()
        } else EosEcUtil.encodeEosCrypto(PREFIX, curveParam, bytes)

    }

    fun getBytes(value: BigInteger): ByteArray {
        val result = ByteArray(32)
        val bytes = value.toByteArray()
        if (bytes.size <= result.size) {
            System.arraycopy(bytes, 0, result, result.size - bytes.size, bytes.size)
        } else {
            // This happens if the most significant bit is set and we have an
            // extra leading zero to avoid a negative BigInteger
            assert(bytes.size == 33 && bytes[0].toInt() == 0)
            System.arraycopy(bytes, 1, result, 0, bytes.size - 1)
        }
        return result
    }

    private fun toUnsignedBigInteger(value: BigInteger): BigInteger {
        return if (value.signum() < 0) {
            BigInteger(1, value.toByteArray())
        } else value

    }

    private fun toUnsignedBigInteger(value: ByteArray): BigInteger {
        return if (value[0].toInt() and 0x80 != 0) {
            BigInteger(1, value)
        } else BigInteger(value)

    }

    private fun getOrCreatePrivKeyBigInteger(value: ByteArray?): BigInteger {
        if (null != value) {
            return if (value[0].toInt() and 0x80 != 0) {
                BigInteger(1, value)
            } else BigInteger(value)

        }

        val nBitLength = curveParam.n.bitLength()
        var d: BigInteger
        do {
            // Make a BigInteger from bytes to ensure that Android and 'classic'
            // java make the same BigIntegers from the same random source with the
            // same seed. Using BigInteger(nBitLength, random)
            // produces different results on Android compared to 'classic' java.
            val bytes = ByteArray(nBitLength / 8)
            secuRandom.nextBytes(bytes)
            bytes[0] = (bytes[0].toInt() and 0x7F).toByte()
            d = BigInteger(bytes)
        } while (d == BigInteger.ZERO || d >= curveParam.n)

        return d
    }

}
