package com.lumiwallet.android.core.cardano.address

import com.lumiwallet.android.core.utils.BitsConverter
import com.lumiwallet.android.core.utils.btc_based.core.AddressFormatException
import java.util.*

object Bech32 {
    /** The Bech32 character set for encoding.  */
    const val CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"

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
    fun polymod(data: ByteArray): Long {
        val generator: ArrayList<Long> = arrayListOf(
            0x98f2bc8e61L,
            0x79b76d99e2L,
            0xf33e5fb3c4L,
            0xae2eabe2a8L,
            0x1e4f43e470L
        )
        var checksum: Long = 1L

        data.forEach {

            val value = it.toLong()
            val topBits = checksum shr 35
            checksum = ((checksum and 0x07ffffffffL) shl 5) xor value

            generator.forEachIndexed { j, it ->
                if (((topBits shr j) and 1) == 1L) {
                    checksum = checksum xor generator[j]
                }
            }
        }
        return (checksum xor 1)
    }

    /** Expand a HRP for use in checksum computation.  */
    fun expandHrp(hrp: String): ByteArray {
        val hrpLength = hrp.length
        val ret = ByteArray(hrpLength + 1)
        for (i in 0 until hrpLength) {
            val c = hrp[i].toInt() and 0x7f // Limit to standard 7-bit ASCI
            ret[i] = (c and 0x1f).toByte()
        }
        ret[hrpLength] = 0
        return ret
    }

    /** Verify a checksum.  */
    private fun verifyChecksum(hrp: String, values: ByteArray): Boolean {
        val expandedHrp = expandHrp(hrp)
        val checksum = createChecksum(expandedHrp, values.dropLast(8).toByteArray())
        val exceptedChecksum = values.takeLast(8).toByteArray()
        return checksum.contentEquals(exceptedChecksum)
    }

    /** Create a checksum.  */
    private fun createChecksum(hrp: ByteArray, payload: ByteArray): ByteArray {
        val checksum = hrp + payload + byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0)

        val mod = polymod(checksum)
        val ret = ByteArray(8)
        for (i in 0..7) {
            ret[i] = (mod ushr 5 * (7 - i) and 31).toByte()
        }
        return ret
    }

    fun encode(pubKeyHash: ByteArray, hrpString: String): String {
        val hrp = expandHrp(hrpString)
        val payload = BitsConverter.convertBits(pubKeyHash, 0, pubKeyHash.size, 8, 5, true)
        val checksum = createChecksum(hrp, payload)

        val result  = payload + checksum

        val sb = StringBuilder()
        for (b in result) {
            sb.append(CHARSET[b.toInt()])
        }
        return sb.toString()
    }

    fun extractPubKeyHash(cashAddress: String): ByteArray {
        val address = cashAddress.split(":")
        val pubKeyHash = if (address.size == 1) {
            decode(cashAddress, "bitcoincash")
        } else {
            decode(address.last(), address.first())
        }
        return pubKeyHash
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