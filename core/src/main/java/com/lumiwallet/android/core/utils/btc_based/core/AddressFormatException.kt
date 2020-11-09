package com.lumiwallet.android.core.utils.btc_based.core

open class AddressFormatException(message: String) : IllegalArgumentException(message) {

    class InvalidCharacter(
            character: Char,
            val position: Int
    ) : AddressFormatException("Invalid character '$character' at position $position")

    class InvalidDataLength(message: String) : AddressFormatException(message)

    class InvalidChecksum : AddressFormatException("Checksum does not validate")

    open class InvalidPrefix(message: String) : AddressFormatException(message)

    class WrongNetwork : InvalidPrefix {
        constructor(versionHeader: Int) : super("Version code of address did not match acceptable versions for network: $versionHeader")
        constructor(hrp: String) : super("Human readable part of address did not match acceptable HRPs for network: $hrp")
    }
}
