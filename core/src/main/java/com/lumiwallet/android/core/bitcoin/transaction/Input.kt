package com.lumiwallet.android.core.bitcoin.transaction

import com.lumiwallet.android.core.bitcoin.constant.ErrorMessages
import com.lumiwallet.android.core.bitcoin.core.PrivateKey
import com.lumiwallet.android.core.bitcoin.script.ScriptType
import com.lumiwallet.android.core.bitcoin.types.OpSize
import com.lumiwallet.android.core.bitcoin.types.UInt
import com.lumiwallet.android.core.bitcoin.types.VarInt
import com.lumiwallet.android.core.bitcoin.util.ByteBuffer
import com.lumiwallet.android.core.bitcoin.util.HexUtils
import com.lumiwallet.android.core.bitcoin.util.ValidationUtils.isEmpty
import com.lumiwallet.android.core.bitcoin.util.ValidationUtils.isHexString
import com.lumiwallet.android.core.bitcoin.util.ValidationUtils.isTransactionId
import org.bouncycastle.util.encoders.Hex

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
        get() = ByteBuffer(*HexUtils.asBytes(transaction)).bytesReversed()

    val isSegWit: Boolean
        get() = ScriptType.forLock(lock).isSegWit()

    val sequence: UInt
        get() = SEQUENCE

    init {
        validateInputData(transaction, index, lock, satoshi)
    }

    fun fillTransaction(sigHash: ByteArray, transaction: Transaction) {
        val segWit = isSegWit

        transaction.addHeader(if (segWit) "   Input (Segwit)" else "   Input")

        val unlocking = ScriptSigProducer.produceScriptSig(segWit, sigHash, privateKey)
        transaction.addData("      Transaction out", Hex.toHexString(transactionHashBytesLitEnd))
        transaction.addData("      Tout index", UInt.of(index).toString())
        transaction.addData("      Unlock length", Hex.toHexString(VarInt.of(unlocking.size.toLong()).asLitEndBytes()))
        transaction.addData("      Unlock", Hex.toHexString(unlocking))
        transaction.addData("      Sequence", SEQUENCE.toString())
    }

    fun getWitness(sigHash: ByteArray): ByteArray =
        WitnessProducer.produceWitness(isSegWit, sigHash, privateKey)

    override fun toString(): String = "$transaction $index $lock $satoshi"

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
        when (ScriptType.forLock(lock)) {
            ScriptType.P2PKH -> {
                val lockBytes = HexUtils.asBytes(lock)
                val pubKeyHashSize = OpSize.ofByte(lockBytes[2])
                require(pubKeyHashSize.size.toInt() == lockBytes.size - 5) { String.format(ErrorMessages.INPUT_WRONG_PKH_SIZE, lock) }
            }
            ScriptType.P2SH -> {
                val lockBytes = HexUtils.asBytes(lock)
                val pubKeyHashSize = OpSize.ofByte(lockBytes[1])
                require(pubKeyHashSize.size.toInt() == lockBytes.size - 3) { String.format(ErrorMessages.INPUT_WRONG_RS_SIZE, lock) }
            }
            else -> throw IllegalArgumentException("Provided locking script is not P2PKH or P2SH [$lock]")
        }
    }

    private fun validateAmount(satoshi: Long) {
        require(satoshi > 0) { ErrorMessages.INPUT_AMOUNT_NOT_POSITIVE }
    }

}