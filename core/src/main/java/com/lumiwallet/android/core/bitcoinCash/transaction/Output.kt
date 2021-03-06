package com.lumiwallet.android.core.bitcoinCash.transaction

import com.lumiwallet.android.core.utils.Base58
import com.lumiwallet.android.core.utils.btc_based.ByteBuffer
import com.lumiwallet.android.core.utils.btc_based.ErrorMessages
import com.lumiwallet.android.core.utils.btc_based.ValidationUtils.isBase58
import com.lumiwallet.android.core.utils.btc_based.ValidationUtils.isEmpty
import com.lumiwallet.android.core.utils.btc_based.types.ULong
import com.lumiwallet.android.core.utils.btc_based.types.VarInt
import org.bouncycastle.util.encoders.Hex

enum class OutputType(val desc: String) {
    CUSTOM("Custom"),
    CHANGE("Change")
}


internal class Output(
    val satoshi: Long,
    private val destination: String,
    private val type: OutputType
) {

    private val decodedAddress: ByteArray

    private val lockingScript: ByteArray
        get() = ScriptPubKeyProducer.produceScript(
            decodedAddress[0],
            decodedAddress.copyOfRange(1, decodedAddress.size)
        )

    init {
        validateOutputData(satoshi, destination)
        this.decodedAddress = decodeAndValidateAddress(destination)
    }

    fun serializeForSigHash(): ByteArray {
        val lockingScript = lockingScript
        return ByteBuffer(
            *ULong.of(satoshi).asLitEndBytes(),
            *VarInt.of(lockingScript.size.toLong()).asLitEndBytes(),
            *lockingScript
        ).bytes
    }

    fun fillTransaction(transaction: Transaction) {
        transaction.addHeader("   Output (${type.desc})")
        transaction.addData("      Satoshi", ULong.of(satoshi).toString())
        transaction.addData("      Lock length", VarInt.of(lockingScript.size.toLong()).toString())
        transaction.addData("      Lock", Hex.toHexString(lockingScript))
    }

    override fun toString(): String {
        return "$destination $satoshi"
    }

    fun getDestination(): String = destination

    private fun validateOutputData(satoshi: Long, destination: String) {
        validateDestinationAddress(destination)
        validateAmount(satoshi);
    }

    private fun validateAmount(satoshi: Long) {
        require(satoshi > 0) { ErrorMessages.OUTPUT_AMOUNT_NOT_POSITIVE }
    }

    private fun validateDestinationAddress(destination: String) {
        require(!isEmpty(destination)) { ErrorMessages.OUTPUT_ADDRESS_EMPTY }
        require(isBase58(destination)) { ErrorMessages.OUTPUT_ADDRESS_NOT_BASE_58 }

        val prefixP2PKH = listOf('1')
        //val prefixP2SH = listOf('3')
        val prefix = destination[0]

        require(prefixP2PKH.contains(prefix)) {
            String.format(ErrorMessages.OUTPUT_ADDRESS_WRONG_PREFIX, prefixP2PKH/*, prefixP2SH*/)
        }
    }

    private fun decodeAndValidateAddress(base58: String): ByteArray = Base58.decodeChecked(base58)
}
