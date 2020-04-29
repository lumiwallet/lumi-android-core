package com.lumiwallet.lumi_core.domain.entity

class HeaderViewModel (
    val title: String,
    val button: String,
    val headerType: Int
) {

    companion object {
        const val HEADER_TYPE_OUTPUT = 0
        const val HEADER_TYPE_INPUT = 1
    }
}