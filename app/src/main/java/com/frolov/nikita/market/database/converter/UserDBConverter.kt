package com.frolov.nikita.market.database.converter

import com.frolov.nikita.market.database.tables.GoodsDb
import com.frolov.nikita.market.models.converters.BaseConverter
import com.frolov.nikita.market.models.goods.Goods
import com.frolov.nikita.market.models.goods.GoodsModel

interface GoodsDBConverter

class GoodsDBConverterImpl : BaseConverter<Goods, GoodsDb>(), GoodsDBConverter {

    override fun processConvertInToOut(inObject: Goods) = inObject.run {
        GoodsDb(id, name, description, type, image, price)
    }

    override fun processConvertOutToIn(outObject: GoodsDb) = outObject.run {
        GoodsModel(id, name, description, type, image, price)
    }

}