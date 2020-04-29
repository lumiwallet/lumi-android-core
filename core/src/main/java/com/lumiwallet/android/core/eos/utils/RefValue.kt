package com.lumiwallet.android.core.eos.utils

class RefValue<T> {
    var data: T? = null

    constructor() {
        this.data = null
    }

    constructor(paramT: T) {
        this.data = paramT
    }
}
