package com.lumiwallet.android.core.doge.script

import com.lumiwallet.android.core.doge.constant.OpCodes.CHECKSIG
import com.lumiwallet.android.core.doge.constant.OpCodes.DUP
import com.lumiwallet.android.core.doge.constant.OpCodes.EQUALVERIFY
import com.lumiwallet.android.core.doge.constant.OpCodes.HASH160
import com.lumiwallet.android.core.doge.params.DogeNetParams
import com.lumiwallet.android.core.utils.Base58
import com.lumiwallet.android.core.utils.btc_based.ErrorMessages
import com.lumiwallet.android.core.utils.safeToByteArray

enum class ScriptType(
    val id: Int
) {

    P2PKH(1);

    companion object {

        fun fromAddressPrefix(address: String) = when {
            Base58.decodeChecked(address).first() == DogeNetParams.addressHeader.toByte() -> P2PKH
            else ->
                throw IllegalArgumentException(ErrorMessages.INPUT_LOCK_WRONG_FORMAT)
        }

        fun forLock(lock: String): ScriptType {
            val lockBytes = lock.safeToByteArray()
            return when {
                isP2PKH(lockBytes) -> P2PKH
                else -> throw IllegalArgumentException(
                    String.format(ErrorMessages.INPUT_LOCK_WRONG_FORMAT, lock)
                )
            }
        }

        private fun isP2PKH(lockBytes: ByteArray): Boolean {
            return (lockBytes[0] == DUP
                    && lockBytes[1] == HASH160
                    && lockBytes[lockBytes.size - 2] == EQUALVERIFY
                    && lockBytes[lockBytes.size - 1] == CHECKSIG)
        }

    }

}


