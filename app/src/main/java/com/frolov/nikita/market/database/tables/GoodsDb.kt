package com.frolov.nikita.market.database.tables

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.frolov.nikita.market.database.MarketDBContract
import com.frolov.nikita.market.models.goods.GoodsType

@Entity(tableName = MarketDBContract.GOODS_TABLE)
data class GoodsDb(@PrimaryKey var id: Long?,
                   var name: String?,
                   var description: String?,
                   var type: GoodsType?,
                   var image: String?,
                   var price: String?)