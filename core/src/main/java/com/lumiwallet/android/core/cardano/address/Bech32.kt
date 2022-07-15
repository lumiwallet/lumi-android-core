package com.lumiwallet.android.core.cardano.address

import com.lumiwallet.android.core.utils.BitsConverter
import com.lumiwallet.android.core.utils.btc_based.core.AddressFormatException
import java.util.*

object Bech32 {
    /** The Bech32 character set for encoding.  */
    private const val CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"

    /** The Bech32 character set for decoding.  */
    private val CHARSET_REV = byteArrayOf(
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        15, -1, 10, 17, 21, 20, 26, 30, 7, 5, -1, -1, -1, -1, -1, -1,
        -1, 29, -1, 24, 13, 25, 9, 8, 23, -1, 18, 22, 31, 27, 19, -1,
        1, 0, 3, 16, 11, 28, 12, 14, 6, 4, 2, -1, -1, -1, -1, -1,
        -1, 29, -1, 24, 13, 25, 9, 8, 23, -1, 18, 22, 31, 27, 19, -1,
        1, 0, 3, 16, 11, 28, 12, 14, 6, 4, 2, -1, -1, -1, -1, -1
    )

    /** Find the polynomial with value coefficients mod the generator as 30-bit.  */
    private fun polymod(data: ByteArray): Long {
        val generator: ArrayList<Long> = arrayListOf(
            0x3b6a57b2,
            0x26508e6d,
            0x1ea119fa,
            0x3d4233dd,
            0x2a1462b3
        )
        var checksum: Long = 1L

        data.forEach {

            val value = it.toLong()
            val topBits = checksum shr 25
            checksum = ((checksum and 0x1ffffff) shl 5) xor value

            generator.forEachIndexed { j, it ->
                if (((topBits shr j) and 1) == 1L) {
                    checksum = checksum xor generator[j]
                }
            }
        }
        return (checksum xor 1)
    }

    /** Expand a HRP for use in checksum computation.  */
    private fun expandHrp(hrpString: String): ByteArray {
        val hrp = hrpString.toByteArray()
        val buf1 = ByteArray(hrp.size)
        val buf2 = ByteArray(hrp.size)
        val mid = ByteArray(1)
        for (i in hrp.indices) {
            buf1[i] = (hrp[i].toInt() shr 5).toByte()
        }
        mid[0] = 0x00
        for (i in hrp.indices) {
            buf2[i] = (hrp[i].toInt() and 0x1f).toByte()
        }
        val ret = ByteArray(hrp.size * 2 + 1)
        System.arraycopy(buf1, 0, ret, 0, buf1.size)
        System.arraycopy(mid, 0, ret, buf1.size, mid.size)
        System.arraycopy(buf2, 0, ret, buf1.size + mid.size, buf2.size)
        return ret
    }

    /** Verify a checksum.  */
    private fun verifyChecksum(hrp: String, values: ByteArray): Boolean {
        val expandedHrp = expandHrp(hrp)
        val checksum = createChecksum(expandedHrp, values.dropLast(8).toByteArray())
        val exceptedChecksum = values.takeLast(8).toByteArray()
        return checksum.contentEquals(exceptedChecksum)
    }

    private fun createChecksum(hrpExpanded: ByteArray, values: ByteArray): ByteArray {
        val enc = ByteArray(hrpExpanded.size + values.size + 6)
        System.arraycopy(hrpExpanded, 0, enc, 0, hrpExpanded.size)
        System.arraycopy(values, 0, enc, hrpExpanded.size, values.size)
        val mod = polymod(enc)
        val ret = ByteArray(6)
        for (i in 0..5) {
            ret[i] = (mod ushr 5 * (5 - i) and 0x1f).toByte()
        }
        return ret
    }

    fun encode(pubKeyHash: ByteArray, hrpString: String): String {
        val payload = BitsConverter.convertBits(pubKeyHash, 0, pubKeyHash.size, 8, 5, true)

        val hrp = expandHrp(hrpString)
        val checksum = createChecksum(hrp, payload)

        val result = payload + checksum

        val sb = StringBuilder()
        for (b in result) {
            sb.append(CHARSET[b.toInt()])
        }
        return sb.toString()
    }

    /** Decode a Bech32 string.  */
    @Throws(AddressFormatException::class)
    fun decode(data: String, hrp: String): ByteArray {
        var lower = false
        var upper = false
        if (data.length < 8) throw AddressFormatException.InvalidDataLength("Input too short: " + data.length)
        if (data.length > 90) throw AddressFormatException.InvalidDataLength("Input too long: " + data.length)
        for (i in data.indices) {
            val c = data[i]
            if (c.toInt() < 33 || c.toInt() > 126) throw AddressFormatException.InvalidCharacter(c, i)
            if (c in 'a'..'z') {
                if (upper) throw AddressFormatException.InvalidCharacter(c, i)
                lower = true
            }
            if (c in 'A'..'Z') {
                if (lower) throw AddressFormatException.InvalidCharacter(c, i)
                upper = true
            }
        }
        val values = ByteArray(data.length)
        for (i in data.indices) {
            val c = data[i]
            if (CHARSET_REV[c.toInt()] == (-1).toByte()) throw AddressFormatException.InvalidCharacter(c, i)
            values[i] = CHARSET_REV[c.toInt()]
        }
        if (!verifyChecksum(hrp, values)) { throw AddressFormatException.InvalidChecksum() }
        return BitsConverter
            .convertBits(values, 0, values.size, 5, 8, true)
            .drop(1)
            .dropLast(6)
            .toByteArray()
    }
}