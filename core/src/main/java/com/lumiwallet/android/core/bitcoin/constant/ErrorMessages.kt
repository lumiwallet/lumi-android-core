package com.lumiwallet.android.core.bitcoin.constant

object ErrorMessages {
    const val INPUT_TRANSACTION_EMPTY = "Transaction hash must not be null or empty"
    const val INPUT_TRANSACTION_NOT_64_HEX = "Transaction hash must be 64 digit hex"
    const val INPUT_INDEX_NEGATIVE = "Previous transaction output index must not be negative"
    const val INPUT_LOCK_EMPTY = "Locking script must not be null or empty"
    const val INPUT_LOCK_NOT_HEX = "Locking script must be in hex"
    const val INPUT_AMOUNT_NOT_POSITIVE = "Amount of satoshi must be a positive coinValue"
    const val INPUT_LOCK_WRONG_FORMAT = "Provided locking script is not P2PKH, P2WPKH or P2SH [%s]"
    const val INPUT_WRONG_PKH_SIZE = "Incorrect PKH size [%s]"
    const val INPUT_WRONG_WPKH_SIZE = "Incorrect WPKH size [%s]"
    const val INPUT_WRONG_RS_SIZE = "Incorrect redeemScript size [%s]"

    const val OUTPUT_ADDRESS_EMPTY = "Address must not be null or empty"
    const val OUTPUT_ADDRESS_NOT_BASE_58 = "Address must contain only base58 characters"
    const val OUTPUT_ADDRESS_WRONG_PREFIX =
        "Only addresses starting with 1 (P2PKH), bc1 (P2WPKH) or 3 (P2SH) supported."
    const val OUTPUT_AMOUNT_NOT_POSITIVE = "Amount of satoshi must be a positive coinValue"

    const val SPK_UNSUPPORTED_PRODUCER = "Unsupported producer for mainnet: true, type: %d]"
    const val FEE_NEGATIVE = "Fee must not be less than zero"
}