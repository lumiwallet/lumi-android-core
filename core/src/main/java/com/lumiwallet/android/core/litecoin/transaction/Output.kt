package com.lumiwallet.android.core.litecoin.transaction

import com.lumiwallet.android.core.litecoin.core.LtcSegwitAddress
import com.lumiwallet.android.core.litecoin.params.LitecoinMainNetParams
import com.lumiwallet.android.core.litecoin.script.ScriptType
import com.lumiwallet.android.core.utils.Base58
import com.lumiwallet.android.core.utils.btc_based.ByteBuffer
import com.lumiwallet.android.core.utils.btc_based.ErrorMessages
import com.lumiwallet.android.core.utils.btc_based.ValidationUtils.isBase58
import com.lumiwallet.android.core.utils.btc_based.ValidationUtils.isEmpty
import com.lumiwallet.android.core.utils.btc_based.types.ULong
import com.lumiwallet.android.core.utils.btc_based.types.VarInt
import com.lumiwallet.android.core.utils.hex


enum class OutputType(val desc: String) {
    CUSTOM("Custom"),
    CHANGE("Change")
}

internal class Output(
    val satoshi: Long, // amount
    private val destination: String,
    private val type: OutputType
) {

    private val decodedAddress: ByteArray
    private val addressType: ScriptType

    private val lockingScript: ByteArray
        get() = ScriptPubKeyProducer.produceScript(decodedAddress, addressType)

    init {
        validateOutputData(satoshi, destination)
        this.addressType = decodeAddressType(destination)
        this.decodedAddress = decodeAndValidateAddress(destination, addressType)
    }

    fun serializeForSigHash(): ByteArray {
        val lockingScript = lockingScript
        return ByteBuffer().apply {
            append(*ULong.of(satoshi).asLitEndBytes())
            append(*VarInt.of(lockingScript.size.toLong()).asLitEndBytes())
            append(*lockingScript)
        }
            .bytes
    }

    fun fillTransaction(transaction: Transaction) {
        transaction.addHeader(Transaction.TxPart.OUTPUT)
        transaction.addData(Transaction.TxPart.AMOUNT, ULong.of(satoshi).toString())
        transaction.addData(Transaction.TxPart.LOCK_LENGTH, VarInt.of(lockingScript.size.toLong()).toString())
        transaction.addData(Transaction.TxPart.LOCK, lockingScript.hex)
    }

    override fun toString(): String {
        return "$destination $satoshi"
    }

    fun getDestination(): String = destination

    private fun validateOutputData(satoshi: Long, destination: String) {
        validateDestinationAddress(destination)
        validateAmount(satoshi)
    }

    private fun validateAmount(satoshi: Long) {
        require(satoshi > 0) { ErrorMessages.OUTPUT_AMOUNT_NOT_POSITIVE }
    }

    private fun validateDestinationAddress(destination: String) {
        require(!isEmpty(destination)) { ErrorMessages.OUTPUT_ADDRESS_EMPTY }

        require(
            destination.startsWith(LitecoinMainNetParams.segwitAddressHrp) ||
                    Base58.decodeChecked(destination)[0] == LitecoinMainNetParams.addressHeader.toByte() ||
                    Base58.decodeChecked(destination)[0] == LitecoinMainNetParams.p2SHHeader.toByte()
        ) { ErrorMessages.OUTPUT_ADDRESS_WRONG_PREFIX }
    }

    private fun decodeAddressType(address: String): ScriptType =
        ScriptType.fromAddressPrefix(address)

    private fun decodeAndValidateAddress(address: String, addressType: ScriptType): ByteArray =
        when (addressType) {
            ScriptType.P2WPKH -> {
                LtcSegwitAddress.fromBech32(LitecoinMainNetParams, address).witnessProgram
            }
            ScriptType.P2PKH, ScriptType.P2SH -> {
                require(isBase58(destination)) { ErrorMessages.OUTPUT_ADDRESS_NOT_BASE_58 }
                Base58.decodeChecked(address).drop(1).toByteArray()
            }
            else ->
                throw IllegalArgumentException(ErrorMessages.OUTPUT_ADDRESS_WRONG_PREFIX)
        }
}