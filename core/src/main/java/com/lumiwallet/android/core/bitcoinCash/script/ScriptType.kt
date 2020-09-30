package com.lumiwallet.android.core.bitcoinCash.script

import com.lumiwallet.android.core.bitcoinCash.constant.ErrorMessages
import com.lumiwallet.android.core.bitcoinCash.constant.OpCodes.CHECKSIG
import com.lumiwallet.android.core.bitcoinCash.constant.OpCodes.DUP
import com.lumiwallet.android.core.bitcoinCash.constant.OpCodes.EQUAL
import com.lumiwallet.android.core.bitcoinCash.constant.OpCodes.EQUALVERIFY
import com.lumiwallet.android.core.bitcoinCash.constant.OpCodes.HASH160
import com.lumiwallet.android.core.utils.safeToByteArray

enum class ScriptType(
    val id: Int
) {

    P2PKH(1), // pay to pubkey hash (aka pay to address)
    P2PK(2),  // pay to pubkey
    P2SH(3);   // pay to script hash

    companion object{

        fun forLock(lock: String): ScriptType {
            val lockBytes = lock.safeToByteArray()
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

}


