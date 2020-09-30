package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.utils.Sha256Hash
import com.lumiwallet.android.core.utils.safeToByteArray
import org.bouncycastle.util.Arrays
import org.bouncycastle.util.encoders.Hex
import kotlin.math.ceil

class Transaction {

    class HashBuilder {

        private val payload: StringBuilder = StringBuilder()

        fun append(data: String) {
            payload.append(data)
        }

        override fun toString(): String {
            val payloadByteArray = payload.toString().safeToByteArray()
            val hash = Arrays.reverse(Sha256Hash.hashTwice(payloadByteArray))
            return Hex.toHexString(hash)
        }
    }

    companion object {
        private const val ALIGN_FORMAT = "%-25s"
    }

    enum class TxPart(
        val alias: String,
        val isUsedInTxId: Boolean = false,
        val hasSize: Boolean = true
    ) {
        VERSION("Version", true),
        MARKER("Marker", false, false),
        FLAG("Flag", false, false),
        INPUT_COUNT("Input count", true),
        INPUT("  Input", false),
        TRANSACTION_OUT("    Transaction out", true),
        TOUT_INDEX("    Transaction out index", true),
        UNLOCK_LENGTH("    Unlock length", true),
        UNLOCK("    Length", true),
        SEQUENCE("    Sequence", true),
        OUTPUT_COUNT("Output count", true),
        OUTPUT("  Output", false),
        AMOUNT("    Amount (satoshi)", true),
        LOCK_LENGTH("    Lock length", true),
        LOCK("    Lock", true),
        WITNESSES("Witnesses", false),
         WITNESS("  Witness", false, false),
        LOCKTIME("Locktime", true);
    }

    private val raw = StringBuilder()
    private val split = StringBuilder()
    private val hashBuilder: HashBuilder = HashBuilder()
    private var fee: Long = 0
    private var realSize: Int = 0
    private var newSize: Int = 0

    val rawTransaction: String
        get() = raw.toString()

    val rawTransactionAsBytes: ByteArray
        get() = raw.toString().safeToByteArray()

    val splitTransaction: String
        get() = split.toString()

    val hash: String
        get() = hashBuilder.toString()

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
        part: TxPart,
        value: String
    ) {
        val dataSize = value.length / 2
        realSize += dataSize
        newSize += if (part.hasSize) dataSize else 0

        raw.append(value)

        /**
         * https://github.com/bitcoin/bips/blob/master/bip-0144.mediawiki#hashes
         */
        if (part.isUsedInTxId) {
            hashBuilder.append(value)
        }

        split.append(aligned(part.alias))
        split.appendln(value)
    }

    internal fun addHeader(part: TxPart) {
        split.appendln(part.alias)
    }

    internal fun setFee(fee: Long) {
        this.fee = fee
    }

    private fun aligned(string: String): String {
        return String.format(ALIGN_FORMAT, string)
    }

}

