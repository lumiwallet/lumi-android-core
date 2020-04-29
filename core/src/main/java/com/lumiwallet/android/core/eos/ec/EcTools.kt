package com.lumiwallet.android.core.eos.ec

import com.lumiwallet.android.core.crypto.ECKey
import org.bouncycastle.asn1.x9.X9ECParameters
import org.bouncycastle.math.ec.ECPoint
import java.math.BigInteger

fun X9ECParameters.getQ(): BigInteger{ // like a p, but q
    return when (this) {
        ECKey.SECP256_K1_CURVE_PARAMS ->
            "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F".toBigInteger(16)
        ECKey.SECP256_R1_CURVE_PARAMS ->
            "ffffffff00000001000000000000000000000000ffffffffffffffffffffffff".toBigInteger(16)
        else ->
            throw IllegalArgumentException("unknown curve type")
    }
}

object EcTools {

    /**
     * Get the length of the byte encoding of a field element
     */
    fun getByteLength(fieldSize: Int): Int {
        return (fieldSize + 7) / 8
    }


    /**
     * Get a big integer as an array of bytes of a specified length
     */
    fun integerToBytes(s: BigInteger, length: Int): ByteArray {
        val bytes = s.toByteArray()

        if (length < bytes.size) {
            // The length is smaller than the byte representation. Truncate by
            // copying over the least significant bytes
            val tmp = ByteArray(length)
            System.arraycopy(bytes, bytes.size - tmp.size, tmp, 0, tmp.size)
            return tmp
        } else if (length > bytes.size) {
            // The length is larger than the byte representation. Copy over all
            // bytes and leave it prefixed by zeros.
            val tmp = ByteArray(length)
            System.arraycopy(bytes, 0, tmp, tmp.size - bytes.size, bytes.size)
            return tmp
        }
        return bytes
    }

    //ported from BitcoinJ
    fun decompressKey(
            param: X9ECParameters,
            x: BigInteger,
            firstBit: Boolean
    ): ECPoint {
        val size = 1 + getByteLength(param.curve.fieldSize)
        val dest = integerToBytes(x, size)
        dest[0] = (if (firstBit) 0x03 else 0x02).toByte()
        return param.curve.decodePoint(dest)
    }

    /*private fun decompressKey(
          param: X9ECParameters,
          xBN: BigInteger,
          yBit: Boolean
  ): ECPoint {
      val x9 = X9IntegerConverter()
      val compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.getCurve()))
      compEnc[0] = (if (yBit) 0x03 else 0x02).toByte()
      return param.curve.decodePoint(compEnc)
  }*/
}