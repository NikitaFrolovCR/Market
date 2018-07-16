package com.frolov.nikita.market.models.goods

import com.frolov.nikita.market.R
import com.frolov.nikita.market.getStringApp

enum class GoodsType(private val value: Int, private val nameCategory: String) {
    TECHNIQUE(0, getStringApp(R.string.technique)),
    APPLIANCE(1, getStringApp(R.string.appliances)),
    SPORT(2, getStringApp(R.string.sport)),
    CLOTHES(3, getStringApp(R.string.clothes)),
    BUSINESS(4, getStringApp(R.string.business));

    operator fun invoke() = value

    companion object {
        private val map = values().associateBy(GoodsType::value)
        fun fromValue(value: Int?) = map[value] ?: TECHNIQUE
    }

    fun getNameCategory() = nameCategory
}