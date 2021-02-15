package com.lumiwallet.android.core.bitcoinVault.script

import com.lumiwallet.android.core.bitcoinVault.constant.OpCodes
import com.lumiwallet.android.core.bitcoinVault.core.SegwitAddress
import com.lumiwallet.android.core.bitcoinVault.params.BitcoinVaultParams
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
            address.startsWith(BitcoinVaultParams.segwitAddressHrp) -> {
                val segwitAddress = SegwitAddress.fromBech32(BitcoinVaultParams, address)
                if (segwitAddress.witnessProgram.size == SegwitAddress.WITNESS_PROGRAM_LENGTH_PKH)
                    P2WPKH
                else
                    throw IllegalArgumentException(ErrorMessages.INPUT_LOCK_WRONG_FORMAT)
            }
            Base58.decodeChecked(address).first() == BitcoinVaultParams.addressHeader.toByte() -> P2PKH
            Base58.decodeChecked(address).first() == BitcoinVaultParams.p2SHHeader.toByte() -> P2SH
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
            lockBytes[0] == OpCodes.HASH160 && lockBytes[lockBytes.size - 1] == OpCodes.EQUAL

        private fun isP2PKH(lockBytes: ByteArray): Boolean {
            return (lockBytes[0] == OpCodes.DUP
                    && lockBytes[1] == OpCodes.HASH160
                    && lockBytes[lockBytes.size - 2] == OpCodes.EQUALVERIFY
                    && lockBytes[lockBytes.size - 1] == OpCodes.CHECKSIG)
        }

        private fun isP2WPKH(lockBytes: ByteArray): Boolean =
            (lockBytes.size == 22
                    && lockBytes[0] == OpCodes.FALSE
                    && lockBytes[1] == 0x14.toByte())
    }

    fun isSegWit(): Boolean = segwit
}