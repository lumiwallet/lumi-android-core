package com.lumiwallet.android.core.eos.ec

import com.lumiwallet.android.core.eos.utils.HexUtils
import org.bouncycastle.asn1.x9.X9ECParameters
import java.math.BigInteger

class EcSignature {

    companion object {
        private const val PREFIX = "SIG"
    }

    var recId = -1

    val r: BigInteger
    val s: BigInteger
    val curveParam: X9ECParameters

    /**
     * Returns true if the S component is "low", that means it is below HALF_CURVE_ORDER (n.shiftRight(1)).
     * See [BIP62](https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#Low_S_values_in_signatures).
     */
    private val isCanonical: Boolean
        get() = s <= curveParam.n.shiftRight(1)

    constructor(
            r: BigInteger,
            s: BigInteger,
            curveParam: X9ECParameters
    ) {
        this.r = r
        this.s = s
        this.curveParam = curveParam
    }

    constructor(
            r: BigInteger,
            s: BigInteger,
            curveParam: X9ECParameters,
            recId: Int
    ) : this(r, s, curveParam) {
        setRecid(recId)
    }

    constructor(base58Str: String) {
        val parts = EosEcUtil.safeSplitEosCryptoString(base58Str)
        require(parts.size >= 3) { "Invalid private key format: $base58Str" }
        require(PREFIX == parts[0]) { "Signature Key has invalid prefix: $base58Str" }
        require(parts[2].isNotEmpty()) { "Signature has no data: $base58Str" }

        this.curveParam = EosEcUtil.getCurveParamFrom(parts[1])
        val rawBytes = EosEcUtil.getBytesIfMatchedRipemd160(parts[2], parts[1], null)

        setRecid(rawBytes[0].toInt() - 27 - 4)

        this.r = BigInteger(rawBytes.copyOfRange(1, 33))
        this.s = BigInteger(rawBytes.copyOfRange(33, 65))
    }

    fun setRecid(recid: Int) {
        this.recId = recid
    }

    fun toCanonicalised(): EcSignature {
        return if (!isCanonical) {
            EcSignature(r, curveParam.n.subtract(s), curveParam)
        } else {
            this
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true

        if (null == other || javaClass != other.javaClass)
            return false

        val otherSig = other as EcSignature?
        return r == otherSig!!.r && s == otherSig.s
    }

    fun isRSEachLength(length: Int): Boolean {
        return r.toByteArray().size == length && s.toByteArray().size == length
    }

    private fun eosEncodingHex(compressed: Boolean): String {
        check(recId in 0..3) { "signature has invalid recid." }

        val headerByte = recId + 27 + if (compressed) 4 else 0
        val sigData = ByteArray(65) // 1 header + 32 bytes for R + 32 bytes for S
        sigData[0] = headerByte.toByte()
        System.arraycopy(EcTools.integerToBytes(this.r, 32), 0, sigData, 1, 32)
        System.arraycopy(EcTools.integerToBytes(this.s, 32), 0, sigData, 33, 32)

        return EosEcUtil.encodeEosCrypto(PREFIX, curveParam, sigData)
    }

    override fun toString(): String {
        return if (recId in 0..3) {
            eosEncodingHex(true)
        } else{
            "no recovery sig: " + HexUtils.toHex(this.r.toByteArray()) + HexUtils.toHex(this.s.toByteArray())
        }

    }

    override fun hashCode(): Int {
        var result = recId
        result = 31 * result + r.hashCode()
        result = 31 * result + s.hashCode()
        result = 31 * result + curveParam.hashCode()
        return result
    }
}
