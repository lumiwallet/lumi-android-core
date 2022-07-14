package com.lumiwallet.android.core.litecoin.transaction

import com.lumiwallet.android.core.litecoin.script.ScriptType
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

        private val SEQUENCE = UInt.of(-0x0)
    }

    val transactionHashBytesLitEnd: ByteArray
    val isSegWit: Boolean
    var sequence: UInt

    init {
        validateInputData(transaction, index, lock, satoshi)
        transactionHashBytesLitEnd = ByteBuffer(*transaction.safeToByteArray()).bytesReversed()
        isSegWit = ScriptType.forLock(lock).isSegWit()
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

    fun getWitness(sigHash: ByteArray): ByteArray =
        if (isSegWit)
            WitnessProducer.produceWitness(sigHash, privateKey)
        else
            byteArrayOf(0x00.toByte())

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
            ScriptType.P2SH -> {
                val pubKeyHashSize = OpSize.ofByte(lockBytes[1])
                require(pubKeyHashSize.toInt() == lockBytes.size - 3) {
                    String.format(ErrorMessages.INPUT_WRONG_RS_SIZE, lock)
                }
            }
            ScriptType.P2WPKH -> {
                val pubKeyHash = privateKey.key.pubKeyHash
                require(lockBytes[0] == 0x00.toByte()) {
                    String.format(ErrorMessages.INPUT_LOCK_WRONG_FORMAT, lock)
                }
                require(OpSize.ofByte(lockBytes[1]).toInt() == pubKeyHash.size) {
                    String.format(ErrorMessages.INPUT_WRONG_WPKH_SIZE, lock)
                }
                require(lockBytes.drop(2).toByteArray().contentEquals(pubKeyHash)) {
                    String.format(ErrorMessages.INPUT_LOCK_WRONG_FORMAT, lock)
                }
            }
            else -> throw IllegalArgumentException("Provided locking script is not P2PKH, P2WPKH or P2SH [$lock]")
        }
    }

    private fun validateAmount(satoshi: Long) {
        require(satoshi > 0) { ErrorMessages.INPUT_AMOUNT_NOT_POSITIVE }
    }
}