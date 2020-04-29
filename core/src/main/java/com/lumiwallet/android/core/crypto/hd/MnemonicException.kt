package com.lumiwallet.android.core.crypto.hd

/**
 * Exceptions thrown by the MnemonicCode
 */
open class MnemonicException : Exception {
    constructor() : super()

    constructor(msg: String) : super(msg)

    class MnemonicLengthException(msg: String) : MnemonicException(msg)

    class MnemonicChecksumException : MnemonicException()

    class MnemonicWordException(val badWord: String) : MnemonicException()
}
