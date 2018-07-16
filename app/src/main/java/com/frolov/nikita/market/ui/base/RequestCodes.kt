package com.frolov.nikita.market.ui.base

class RequestCodes {
    companion object {
        private var currentRequestCode = 0
    }

    enum class RequestCode {
        REQUEST_DIALOG_MESSAGE;

        private val value = ++currentRequestCode

        operator fun invoke() = value
    }
}