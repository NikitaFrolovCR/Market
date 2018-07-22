package com.frolov.nikita.market.database.repositories

import com.frolov.nikita.market.database.DatabaseCreator
import com.frolov.nikita.market.database.converter.GoodsDBConverterImpl
import com.frolov.nikita.market.database.tables.GoodsDb
import com.frolov.nikita.market.models.goods.Goods
import com.frolov.nikita.market.models.goods.GoodsType
import io.reactivex.Flowable

interface GoodsRepository {
    fun saveGoods(goods: Goods): Flowable<Goods>

    fun getById(id: Long): Flowable<Goods?>

    fun getMoreGoods(id: Long): Flowable<List<Goods>>

    fun getGoods(): Flowable<List<Goods>>

    fun deleteAll(): Flowable<Unit>

    fun updateGoods(goods: Goods): Flowable<Goods>

    fun updateGoods(price: String, type: GoodsType)

    fun delete(goods: Goods): Flowable<Goods>
}

class GoodsRepositoryImpl : BaseRepository<Goods, GoodsDb, Long>(), GoodsRepository {
    override val converter = GoodsDBConverterImpl()
    override val dao = DatabaseCreator.database.goodsDao()

    override fun saveGoods(goods: Goods): Flowable<Goods> =
            Flowable.just(goods)
                    .map { dao.insert(converter.convertInToOut(it)) }
                    .map { goods }

    override fun getById(id: Long): Flowable<Goods?> =
            dao.getById(id)
                    .map { converter.convertOutToIn(it) }

    override fun deleteAll(): Flowable<Unit> =
            Flowable.just(Unit)
                    .map { dao.deleteAll() }

    override fun updateGoods(goods: Goods): Flowable<Goods> =
            Flowable.just(goods)
                    .map { dao.update(converter.convertInToOut(it)) }
                    .map { goods }

    override fun getMoreGoods(id: Long): Flowable<List<Goods>> =
            dao.getMoreGoods(id)
                    .map { converter.convertListOutToIn(it) }

    override fun getGoods(): Flowable<List<Goods>> =
            dao.getGoods()
                    .map { converter.convertListOutToIn(it) }

    override fun updateGoods(price: String, type: GoodsType) = dao.updateGoods(price, type)

    override fun delete(goods: Goods): Flowable<Goods> =
            Flowable.just(goods)
                    .map { dao.delete(converter.convertInToOut(goods)) }
                    .map { goods }

}