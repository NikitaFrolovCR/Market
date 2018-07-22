package com.frolov.nikita.market.database.converter

import android.arch.persistence.room.TypeConverter
import com.frolov.nikita.market.models.goods.GoodsType

class EnumTypeConverters {

    @TypeConverter
    fun toRole(type: String) = GoodsType.fromValue(type)

    @TypeConverter
    fun fromRole(goodsType: GoodsType) = goodsType()

}