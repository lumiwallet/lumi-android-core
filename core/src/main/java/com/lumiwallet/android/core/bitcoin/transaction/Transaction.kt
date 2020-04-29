package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.utils.Sha256Hash
import org.bouncycastle.util.Arrays
import org.bouncycastle.util.encoders.Hex
import kotlin.math.ceil

class Transaction {

    companion object {
        private const val ALIGN_FORMAT = "%-25s"
    }

    private val raw = StringBuilder()
    private val split = StringBuilder()
    private var fee: Long = 0
    private var realSize: Int = 0
    private var newSize: Int = 0

    val rawTransaction: String
        get() = raw.toString()

    val rawTransactionAsBytes: ByteArray
        get() = Hex.decode(raw.toString())

    val splitTransaction: String
        get() = split.toString()

    val hash: String
        get() {
            val rawTx = rawTransactionAsBytes
            val hash = Arrays.reverse(Sha256Hash.hashTwice(rawTx))
            return Hex.toHexString(hash)
        }

    val transactionInfo: String
        get() {
            val size = ceil((newSize * 3 + realSize) / 4.0).toInt()
            return StringBuilder()
                .appendln((if (realSize == newSize) "" else "Virtual ") + "Size (bytes):")
                .appendln(size)
                .appendln("Fee (satoshi/byte):")
                .appendln(String.format("%.3f", fee / size.toDouble()))
                .toString()
        }

    val txSize: Int
        get() = raw.length / 2

    @JvmOverloads
    internal fun addData(
        name: String,
        value: String,
        countSize: Boolean = true
    ) {
        val dataSize = value.length / 2
        realSize += dataSize
        newSize += if (countSize) dataSize else 0

        raw.append(value)

        split.append(aligned(name))
        split.appendln(value)
    }

    internal fun addHeader(name: String) {
        split.appendln(name)
    }

    internal fun setFee(fee: Long) {
        this.fee = fee
    }

    private fun aligned(string: String): String {
        return String.format(ALIGN_FORMAT, string)
    }

}

