package com.frolov.nikita.market.models.goods

import com.frolov.nikita.market.R
import com.frolov.nikita.market.getStringApp

enum class GoodsType(private val value: String, private val nameCategory: String) {
    TECHNIQUE("technique", getStringApp(R.string.technique)),
    APPLIANCE("appliances", getStringApp(R.string.appliances)),
    SPORT("sport", getStringApp(R.string.sport)),
    CLOTHES("clothes", getStringApp(R.string.clothes));

    operator fun invoke() = value

    companion object {
        private val map = values().associateBy(GoodsType::value)
        fun fromValue(value: String?) = map[value] ?: TECHNIQUE
    }

    fun getNameCategory() = nameCategory
}