package com.lumiwallet.android.core.crypto

class SignatureDecodeException : Exception {
    constructor() : super() {}

    constructor(message: String) : super(message) {}

    constructor(cause: Throwable) : super(cause) {}

    constructor(message: String, cause: Throwable) : super(message, cause) {}
}
