package com.lumiwallet.android.core.eos.ec

import com.lumiwallet.android.core.crypto.ECKey
import com.lumiwallet.android.core.crypto.Ripemd160
import com.lumiwallet.android.core.eos.utils.RefValue
import com.lumiwallet.android.core.utils.Base58
import com.lumiwallet.android.core.utils.BitUtils
import com.lumiwallet.android.core.utils.Sha256Hash
import org.bouncycastle.asn1.x9.X9ECParameters
import java.util.*
import java.util.regex.PatternSyntaxException

object EosEcUtil {

    private const val EOS_CRYPTO_STR_SPLITTER = "_"
    private const val PREFIX_K1 = "K1"
    private const val PREFIX_R1 = "R1"

    fun concatEosCryptoStr(vararg strData: String): String {

        var result = ""

        for (i in strData.indices) {
            result += strData[i] + if (i < strData.size - 1) EOS_CRYPTO_STR_SPLITTER else ""
        }
        return result
    }

    fun encodeEosCrypto(
            prefix: String,
            curveParam: X9ECParameters?,
            data: ByteArray
    ): String {
        var typePart = ""
        if (curveParam != null) {
            if (curveParam == ECKey.SECP256_K1_CURVE_PARAMS) {
                typePart = PREFIX_K1
            } else if (curveParam == ECKey.SECP256_R1_CURVE_PARAMS) {
                typePart = PREFIX_R1
            }
        }

        val toHashData = ByteArray(data.size + typePart.length)
        System.arraycopy(data, 0, toHashData, 0, data.size)
        if (typePart.isNotEmpty()) {
            System.arraycopy(typePart.toByteArray(), 0, toHashData, data.size, typePart.length)
        }

        val dataToEncodeBase58 = ByteArray(data.size + 4)

        val ripemd160 = Ripemd160.from(toHashData)
        val checksumBytes = ripemd160

        System.arraycopy(data, 0, dataToEncodeBase58, 0, data.size) // copy source data
        System.arraycopy(checksumBytes, 0, dataToEncodeBase58, data.size, 4) // copy checksum data


        val result: String = if (typePart.isEmpty()) {
            prefix
        } else {
            prefix + EOS_CRYPTO_STR_SPLITTER + typePart + EOS_CRYPTO_STR_SPLITTER
        }

        return result + Base58.encode(dataToEncodeBase58)
                //Base58.encode(dataToEncodeBase58)
    }


    fun extractFromRipemd160(base58Data: String): ByteArray? {
        val data = Base58.decode(base58Data)
        return if (data[0].toInt() == data.size) {
            Arrays.copyOfRange(data, 2, data.size)
        } else null

    }

    fun getBytesIfMatchedRipemd160(base58Data: String, prefix: String?, checksumRef: RefValue<Long>?): ByteArray {
        val prefixBytes = if (prefix.isNullOrEmpty()) ByteArray(0) else prefix.toByteArray()

        val data = Base58.decode(base58Data)

        val toHashData = ByteArray(data.size - 4 + prefixBytes.size)
        System.arraycopy(data, 0, toHashData, 0, data.size - 4) // key data

        System.arraycopy(prefixBytes, 0, toHashData, data.size - 4, prefixBytes.size)

        val ripemd160 = Ripemd160.from(toHashData) //byte[] data, int startOffset, int length
        val checksumByCal = BitUtils.uint32ToLong(ripemd160, 0)
        val checksumFromData = BitUtils.uint32ToLong(data, data.size - 4)
        require(checksumByCal == checksumFromData) { "Invalid format, checksum mismatch" }

        if (checksumRef != null) {
            checksumRef.data = checksumFromData
        }

        return Arrays.copyOfRange(data, 0, data.size - 4)
    }


    fun getBytesIfMatchedSha256(base58Data: String, checksumRef: RefValue<Long>?): ByteArray {
        val data = Base58.decode(base58Data)

        val checkOne = Sha256Hash.of(data, 0, data.size - 4)//Sha256.from(data, 0, data.size - 4)
        val checkTwo = Sha256Hash.of(checkOne.bytes)//Sha256.from(checkOne.bytes)
        if (checkTwo.equalsFromOffset(data, data.size - 4, 4) || checkOne.equalsFromOffset(data, data.size - 4, 4)) {

            if (checksumRef != null) {
                checksumRef.data = BitUtils.uint32ToLong(data, data.size - 4)
            }

            return Arrays.copyOfRange(data, 1, data.size - 4)
        } else {
            throw IllegalArgumentException("Invalid format, checksum mismatch")
        }
    }

    fun getCurveParamFrom(curveType: String): X9ECParameters {
        return if (PREFIX_R1 == curveType)
            ECKey.SECP256_R1_CURVE_PARAMS
        else
            ECKey.SECP256_K1_CURVE_PARAMS
    }

    fun safeSplitEosCryptoString(cryptoStr: String): Array<String> {
        if (cryptoStr.isEmpty()) {
            return arrayOf(cryptoStr)
        }

        return try {
            cryptoStr.split(EOS_CRYPTO_STR_SPLITTER.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        } catch (e: PatternSyntaxException) {
            e.printStackTrace()
            arrayOf(cryptoStr)
        }

    }
}
