package com.lumiwallet.android.core.doge.transaction

import com.lumiwallet.android.core.doge.script.ScriptType
import com.lumiwallet.android.core.utils.btc_based.ByteBuffer
import com.lumiwallet.android.core.utils.btc_based.ErrorMessages
import com.lumiwallet.android.core.utils.btc_based.ValidationUtils.isEmpty
import com.lumiwallet.android.core.utils.btc_based.ValidationUtils.isHexString
import com.lumiwallet.android.core.utils.btc_based.ValidationUtils.isTransactionId
import com.lumiwallet.android.core.utils.btc_based.core.PrivateKey
import com.lumiwallet.android.core.utils.btc_based.types.OpSize
import com.lumiwallet.android.core.utils.btc_based.types.UInt
import com.lumiwallet.android.core.utils.btc_based.types.VarInt
import com.lumiwallet.android.core.utils.hex
import com.lumiwallet.android.core.utils.safeToByteArray

internal class Input(
    private val transaction: String,
    val index: Int,
    val lock: String,
    val satoshi: Long,
    val privateKey: PrivateKey
) {

    companion object {

        private val SEQUENCE = UInt.of(-0x1)
    }

    val transactionHashBytesLitEnd: ByteArray
    var sequence: UInt

    init {
        validateInputData(transaction, index, lock, satoshi)
        transactionHashBytesLitEnd = ByteBuffer(*transaction.safeToByteArray()).bytesReversed()
        sequence = SEQUENCE
    }

    fun fillTransaction(sigHash: ByteArray, transaction: Transaction, scriptType: ScriptType) {
        transaction.addHeader(Transaction.TxPart.INPUT)

        val unlocking = ScriptSigProducer.produceScriptSig(sigHash, privateKey, scriptType)
        transaction.addData(Transaction.TxPart.TRANSACTION_OUT, transactionHashBytesLitEnd.hex)
        transaction.addData(Transaction.TxPart.TOUT_INDEX, UInt.of(index).toString())
        transaction.addData(Transaction.TxPart.UNLOCK_LENGTH, VarInt.of(unlocking.size.toLong()).asLitEndBytes().hex)
        transaction.addData(Transaction.TxPart.UNLOCK, unlocking.hex)
        transaction.addData(Transaction.TxPart.SEQUENCE, SEQUENCE.toString())
    }

    override fun toString(): String = "$transaction\n$index\n$lock\n$satoshi"

    private fun validateInputData(transaction: String, index: Int, lock: String, satoshi: Long) {
        validateTransactionId(transaction)
        validateOutputIndex(index)
        validateLockingScript(lock)
        validateAmount(satoshi)
    }

    private fun validateTransactionId(transaction: String) {
        require(!isEmpty(transaction)) { ErrorMessages.INPUT_TRANSACTION_EMPTY }
        require(isTransactionId(transaction)) { ErrorMessages.INPUT_TRANSACTION_NOT_64_HEX }
    }

    private fun validateOutputIndex(index: Int) {
        require(index >= 0) { ErrorMessages.INPUT_INDEX_NEGATIVE }
    }

    private fun validateLockingScript(lock: String) {
        require(!isEmpty(lock)) { ErrorMessages.INPUT_LOCK_EMPTY }
        require(isHexString(lock)) { ErrorMessages.INPUT_LOCK_NOT_HEX }
        val lockBytes = lock.safeToByteArray()
        when (ScriptType.forLock(lock)) {
            ScriptType.P2PKH -> {
                val pubKeyHashSize = OpSize.ofByte(lockBytes[2])
                require(pubKeyHashSize.toInt() == lockBytes.size - 5) {
                    String.format(ErrorMessages.INPUT_WRONG_PKH_SIZE, lock)
                }
            }
            else -> throw IllegalArgumentException("Provided locking script is not P2PKH, P2WPKH or P2SH [$lock]")
        }
    }

    private fun validateAmount(satoshi: Long) {
        require(satoshi > 0) { ErrorMessages.INPUT_AMOUNT_NOT_POSITIVE }
    }
}