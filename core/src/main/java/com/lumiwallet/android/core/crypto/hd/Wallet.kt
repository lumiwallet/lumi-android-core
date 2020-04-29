package com.lumiwallet.android.core.crypto.hd

import com.lumiwallet.android.core.bitcoin.core.NetworkParameters
import java.util.*

class Wallet {
    private var accounts: MutableList<Account> = mutableListOf()


    constructor(params: NetworkParameters, seed: ByteArray, nbAccounts: Int) {
        val dkKey = HDKeyDerivation.createMasterPrivateKey(seed)
        val dKey = HDKeyDerivation.deriveChildKey(dkKey, 44 or ChildNumber.HARDENED_BIT)
        val dkRoot = HDKeyDerivation.deriveChildKey(dKey, ChildNumber.HARDENED_BIT)

        accounts = ArrayList()
        for (i in 0 until nbAccounts) {
            accounts.add(Account(params, dkRoot, i))
        }

    }

    constructor(coinType: Int, params: NetworkParameters, seed: ByteArray, nbAccounts: Int) {
        val dkKey = HDKeyDerivation.createMasterPrivateKey(seed)
        val dKey = HDKeyDerivation.deriveChildKey(dkKey, 44 or ChildNumber.HARDENED_BIT)

        val dkRoot = HDKeyDerivation.deriveChildKey(dKey, coinType)

        accounts = ArrayList()
        for (i in 0 until nbAccounts) {
            accounts.add(Account(params, dkRoot, i))
        }

    }

    fun getAccount(accountId: Int): Account = accounts[accountId]
}
