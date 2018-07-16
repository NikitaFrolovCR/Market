package com.frolov.nikita.market.network.exceptions

import com.frolov.nikita.market.MarketApp
import com.frolov.nikita.market.R

class NoNetworkException : Exception() {

    companion object {
        private val ERROR_MESSAGE = MarketApp.instance.getString(R.string.no_internet_connection)
    }

    override val message = ERROR_MESSAGE
}
