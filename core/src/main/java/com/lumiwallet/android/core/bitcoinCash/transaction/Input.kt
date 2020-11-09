package com.lumiwallet.android.core.bitcoinCash.transaction

import com.lumiwallet.android.core.bitcoinCash.script.ScriptType
import com.lumiwallet.android.core.utils.btc_based.ErrorMessages
import com.lumiwallet.android.core.utils.btc_based.ValidationUtils.isHexString
import com.lumiwallet.android.core.utils.btc_based.ValidationUtils.isTransactionId
import com.lumiwallet.android.core.utils.btc_based.core.PrivateKey
import com.lumiwallet.android.core.utils.btc_based.types.OpSize
import com.lumiwallet.android.core.utils.btc_based.types.UInt
import com.lumiwallet.android.core.utils.btc_based.types.VarInt
import com.lumiwallet.android.core.utils.hex
import com.lumiwallet.android.core.utils.safeToByteArray

internal class Input(
    val transaction: String,
    val index: Int,
    val lock: String,
    val satoshi: Long,
    val privateKey: PrivateKey
) {

    companion object {

        private val SEQUENCE = UInt.of(-0x1)
    }

    val transactionHash: ByteArray
        get() = transaction.safeToByteArray()

    val sequence: UInt
        get() = SEQUENCE

    init {
        validateInputData(transaction, index, lock, satoshi)
    }

    fun fillTransaction(sigHash: ByteArray, transaction: Transaction) {

        transaction.addHeader("   Input")

        val unlocking = ScriptSigProducer.produceScriptSig(sigHash, privateKey)
        transaction.addData("      Transaction out", transactionHash.hex)
        transaction.addData("      Tout index", UInt.of(index).toString())
        transaction.addData(
            "      Unlock length",
            VarInt.of(unlocking.size.toLong()).asLitEndBytes().hex
        )
        transaction.addData("      Unlock", unlocking.hex)
        transaction.addData("      Sequence", SEQUENCE.toString())
    }

    override fun toString(): String = "$transaction $index $lock $satoshi"

    private fun validateInputData(
        transaction: String,
        index: Int,
        lock: String,
        satoshi: Long
    ) {
        validateTransactionId(transaction)
        validateOutputIndex(index)
        validateLockingScript(lock)
        validateAmount(satoshi)
    }

    private fun validateTransactionId(transaction: String) {
        require(transaction.isNotEmpty()) { ErrorMessages.INPUT_TRANSACTION_EMPTY }
        require(isTransactionId(transaction)) { ErrorMessages.INPUT_TRANSACTION_NOT_64_HEX }
    }

    private fun validateOutputIndex(index: Int) {
        require(index >= 0) { ErrorMessages.INPUT_INDEX_NEGATIVE }
    }

    private fun validateLockingScript(lock: String) {
        require(lock.isNotEmpty()) { ErrorMessages.INPUT_LOCK_EMPTY }
        require(isHexString(lock)) { ErrorMessages.INPUT_LOCK_NOT_HEX }
        when (ScriptType.forLock(lock)) {
            ScriptType.P2PKH -> {
                val lockBytes = lock.safeToByteArray()
                val pubKeyHashSize = OpSize.ofByte(lockBytes[2])
                require(pubKeyHashSize.toInt() == lockBytes.size - 5) {
                    String.format(
                        ErrorMessages.INPUT_WRONG_PKH_SIZE, lock
                    )
                }
            }
            else -> throw IllegalArgumentException("Provided locking script is not P2PKH[$lock]")
        }
    }

    private fun validateAmount(satoshi: Long) {
        require(satoshi > 0) { ErrorMessages.INPUT_AMOUNT_NOT_POSITIVE }
    }

}