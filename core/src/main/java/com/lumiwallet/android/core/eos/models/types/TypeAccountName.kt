package com.lumiwallet.android.core.eos.models.types


/**
 * Created by swapnibble on 2017-09-12.
 */

class TypeAccountName(name: String) : TypeName(name) {

    init {

        if (name.isNotEmpty()) {
            require(name.length <= MAX_ACCOUNT_NAME_LEN) {
                "account name can only be 12 chars long: $name" // changed from dawn3
            }

            require(!(name.indexOf(CHAR_NOT_ALLOWED) >= 0 && !name.startsWith("eosio."))) {
                "account name must not contain '.': $name"
            }
        }
    }

    companion object {

        private const val MAX_ACCOUNT_NAME_LEN = 12
        private const val CHAR_NOT_ALLOWED = '.'
    }
}
