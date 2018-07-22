package com.frolov.nikita.market.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.frolov.nikita.PAGE_LIMIT
import com.frolov.nikita.market.database.MarketDBContract.GOODS_TABLE
import com.frolov.nikita.market.database.tables.GoodsDb
import com.frolov.nikita.market.models.goods.GoodsType
import io.reactivex.Flowable

@Dao
interface GoodsDao : BaseDao<GoodsDb> {

    @Query("SELECT * FROM $GOODS_TABLE WHERE id > :id ORDER BY id LIMIT $PAGE_LIMIT")
    fun getMoreGoods(id: Long): Flowable<List<GoodsDb>>

    @Query("SELECT * FROM $GOODS_TABLE LIMIT $PAGE_LIMIT")
    fun getGoods(): Flowable<List<GoodsDb>>

    @Query("UPDATE $GOODS_TABLE SET price = :price WHERE type = :type")
    fun updateGoods(price: String, type: GoodsType)

    @Query("SELECT * FROM $GOODS_TABLE WHERE id = :id")
    fun getById(id: Long): Flowable<GoodsDb>

    @Query("DELETE FROM $GOODS_TABLE")
    fun deleteAll()
}