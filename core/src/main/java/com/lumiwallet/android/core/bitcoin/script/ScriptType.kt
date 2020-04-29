package com.lumiwallet.android.core.bitcoin.script

import com.lumiwallet.android.core.bitcoin.constant.ErrorMessages
import com.lumiwallet.android.core.bitcoin.constant.OpCodes.CHECKSIG
import com.lumiwallet.android.core.bitcoin.constant.OpCodes.DUP
import com.lumiwallet.android.core.bitcoin.constant.OpCodes.EQUAL
import com.lumiwallet.android.core.bitcoin.constant.OpCodes.EQUALVERIFY
import com.lumiwallet.android.core.bitcoin.constant.OpCodes.HASH160
import com.lumiwallet.android.core.bitcoin.util.HexUtils

enum class ScriptType(
        val id: Int,
        private val segwit: Boolean
) {

    P2PKH(1, false), // pay to pubkey hash (aka pay to address)
    P2PK(2, false),  // pay to pubkey
    P2SH(3, true),   // pay to script hash
    P2WPKH(4, true), // pay to witness pubkey hash
    P2WSH(5, true);  // pay to witness script hash

    companion object{

        fun forLock(lock: String): ScriptType {
            val lockBytes = HexUtils.asBytes(lock)
            return when {
                isP2PKH(lockBytes) -> P2PKH
                isP2SH(lockBytes) -> P2SH
                else -> throw IllegalArgumentException(String.format(ErrorMessages.INPUT_LOCK_WRONG_FORMAT, lock))
            }
        }

        private fun isP2SH(lockBytes: ByteArray): Boolean {
            return lockBytes[0] == HASH160 && lockBytes[lockBytes.size - 1] == EQUAL
        }

        private fun isP2PKH(lockBytes: ByteArray): Boolean {
            return (lockBytes[0] == DUP
                    && lockBytes[1] == HASH160
                    && lockBytes[lockBytes.size - 2] == EQUALVERIFY
                    && lockBytes[lockBytes.size - 1] == CHECKSIG)
        }

    }

    fun isSegWit(): Boolean = segwit
}


