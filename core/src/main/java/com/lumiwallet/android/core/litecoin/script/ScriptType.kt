package com.lumiwallet.android.core.litecoin.script

import com.lumiwallet.android.core.litecoin.constant.OpCodes
import com.lumiwallet.android.core.litecoin.constant.OpCodes.CHECKSIG
import com.lumiwallet.android.core.litecoin.constant.OpCodes.DUP
import com.lumiwallet.android.core.litecoin.constant.OpCodes.EQUAL
import com.lumiwallet.android.core.litecoin.constant.OpCodes.EQUALVERIFY
import com.lumiwallet.android.core.litecoin.constant.OpCodes.HASH160
import com.lumiwallet.android.core.litecoin.core.LtcSegwitAddress
import com.lumiwallet.android.core.litecoin.params.LitecoinMainNetParams
import com.lumiwallet.android.core.utils.Base58
import com.lumiwallet.android.core.utils.btc_based.ErrorMessages
import com.lumiwallet.android.core.utils.safeToByteArray

enum class ScriptType(
    val id: Int,
    private val segwit: Boolean
) {

    P2PKH(1, false), // pay to pubkey hash (aka pay to address)
    P2PK(2, false),  // pay to pubkey
    P2SH(3, true),   // pay to script hash
    P2WPKH(4, true), // pay to witness pubkey hash
    P2WSH(5, true);  // pay to witness script hash

    companion object {

        fun fromAddressPrefix(address: String) = when {
            address.startsWith(LitecoinMainNetParams.segwitAddressHrp) -> {
                val segwitAddress = LtcSegwitAddress.fromBech32(LitecoinMainNetParams, address)
                if (segwitAddress.witnessProgram.size == LtcSegwitAddress.WITNESS_PROGRAM_LENGTH_PKH)
                    P2WPKH
                else
                    throw IllegalArgumentException(ErrorMessages.INPUT_LOCK_WRONG_FORMAT)
            }
            Base58.decodeChecked(address).first() == LitecoinMainNetParams.addressHeader.toByte() -> P2PKH
            Base58.decodeChecked(address).first() == LitecoinMainNetParams.p2SHHeader.toByte() -> P2SH
            else ->
                throw IllegalArgumentException(ErrorMessages.INPUT_LOCK_WRONG_FORMAT)
        }

        fun forLock(lock: String): ScriptType {
            val lockBytes = lock.safeToByteArray()
            return when {
                isP2PKH(lockBytes) -> P2PKH
                isP2SH(lockBytes) -> P2SH
                isP2WPKH(lockBytes) -> P2WPKH
                else -> throw IllegalArgumentException(
                    String.format(ErrorMessages.INPUT_LOCK_WRONG_FORMAT, lock)
                )
            }
        }

        private fun isP2SH(lockBytes: ByteArray): Boolean =
            lockBytes[0] == HASH160 && lockBytes[lockBytes.size - 1] == EQUAL

        private fun isP2PKH(lockBytes: ByteArray): Boolean {
            return (lockBytes[0] == DUP
                    && lockBytes[1] == HASH160
                    && lockBytes[lockBytes.size - 2] == EQUALVERIFY
                    && lockBytes[lockBytes.size - 1] == CHECKSIG)
        }

        private fun isP2WPKH(lockBytes: ByteArray): Boolean =
            (lockBytes.size == 22
                    && lockBytes[0] == OpCodes.FALSE
                    && lockBytes[1] == 0x14.toByte())
    }

    fun isSegWit(): Boolean = segwit
}