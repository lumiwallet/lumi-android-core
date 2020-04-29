package com.lumiwallet.android.core.eos.ec

import com.lumiwallet.android.core.crypto.ECKey
import com.lumiwallet.android.core.crypto.Ripemd160
import com.lumiwallet.android.core.eos.utils.RefValue
import com.lumiwallet.android.core.utils.BitUtils
import org.bouncycastle.asn1.x9.X9ECParameters
import java.util.*

class EosPublicKey {

    private val mCheck: Long
    private val mCurveParam: X9ECParameters?
    val bytes: ByteArray

    class IllegalEosPubkeyFormatException(pubkeyStr: String) : IllegalArgumentException("invalid eos public key : $pubkeyStr")

    @JvmOverloads
    constructor(data: ByteArray, curveParam: X9ECParameters = ECKey.SECP256_K1_CURVE_PARAMS) {
        bytes = Arrays.copyOf(data, 33)
        mCurveParam = curveParam

        mCheck = BitUtils.uint32ToLong(Ripemd160.from(bytes), 0)
    }

    constructor(base58Str: String) {
        val checksumRef = RefValue<Long>()

        val parts = EosEcUtil.safeSplitEosCryptoString(base58Str)
        if (base58Str.startsWith(LEGACY_PREFIX)) {
            if (parts.size == 1) {
                mCurveParam = ECKey.SECP256_K1_CURVE_PARAMS
                bytes = EosEcUtil.getBytesIfMatchedRipemd160(base58Str.substring(LEGACY_PREFIX.length), null, checksumRef)
            } else {
                throw IllegalEosPubkeyFormatException(base58Str)
            }
        } else {
            if (parts.size < 3) {
                throw IllegalEosPubkeyFormatException(base58Str)
            }

            // [0]: prefix, [1]: curve type, [2]: data
            if (PREFIX != parts[0]) throw IllegalEosPubkeyFormatException(base58Str)

            mCurveParam = EosEcUtil.getCurveParamFrom(parts[1])
            bytes = EosEcUtil.getBytesIfMatchedRipemd160(parts[2], parts[1], checksumRef)
        }

        mCheck = checksumRef.data!!
    }


    override fun toString(): String {

        val isR1 = mCurveParam == ECKey.SECP256_R1_CURVE_PARAMS

        return EosEcUtil.encodeEosCrypto(
                prefix = if (isR1) PREFIX else LEGACY_PREFIX,
                curveParam = if (isR1) mCurveParam else null,
                data = bytes
        )

    }

    override fun hashCode(): Int {
        return (mCheck and 0xFFFFFFFFL).toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        return if (null == other || javaClass != other.javaClass) false else BitUtils.areEqual(this.bytes, (other as EosPublicKey).bytes)

    }

    companion object {
        private val LEGACY_PREFIX = "EOS"
        private val PREFIX = "PUB"

        private val CHECK_BYTE_LEN = 4
    }
}