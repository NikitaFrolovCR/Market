package com.frolov.nikita.market.ui.screen.basket

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.frolov.nikita.market.database.DatabaseModuleImpl
import com.frolov.nikita.market.models.goods.Goods
import com.frolov.nikita.market.models.goods.GoodsType
import com.frolov.nikita.market.ui.base.BaseViewModel
import com.frolov.nikita.market.utils.RxUtils
import io.reactivex.Flowable
import io.reactivex.functions.Consumer

class BasketViewModel(application: Application) : BaseViewModel(application) {
    val loadGoodsLiveData = MutableLiveData<List<Goods>>()

    val loadMoreGoodsLiveData = MutableLiveData<List<Goods>>()

    val refreshLiveData = MutableLiveData<Boolean>()

    val removeGoodsLiveData = MutableLiveData<Goods>()

    private val goodsRepository = DatabaseModuleImpl.getGoodsRepository()

    private val loadGoodsSuccessConsumer = Consumer<List<Goods>> {
        loadGoodsLiveData.value = it
        refreshLiveData.value = false
    }

    private val loadMoreVenuesSuccessConsumer = Consumer<List<Goods>> {
        loadMoreGoodsLiveData.value = it
        refreshLiveData.value = false
    }

    private val refreshErrorConsumer = Consumer<Throwable> {
        onErrorConsumer.accept(it)
        refreshLiveData.value = false
    }

    private val removeGoodsConsumer = Consumer<Goods> {
        removeGoodsLiveData.value = it
        refreshLiveData.value = false
    }

    fun loadGoods() {
        Flowable.fromCallable { Unit }
                .flatMap { goodsRepository.getGoods() }
                .compose(RxUtils.ioToMainTransformer())
                .subscribe(loadGoodsSuccessConsumer, refreshErrorConsumer)
    }

    fun loadMoreGoods(id: Long) {
        refreshLiveData.value = true
        Flowable.fromCallable { Unit }
                .flatMap { goodsRepository.getMoreGoods(id) }
                .compose(RxUtils.ioToMainTransformer())
                .subscribe(loadMoreVenuesSuccessConsumer, refreshErrorConsumer)
    }

    fun updatePrice(listPrice: List<Pair<GoodsType, String>>) {
        refreshLiveData.value = true
        Flowable.fromCallable { listPrice }
                .flatMapIterable { it }
                .map { goodsRepository.updateGoods(it.second, it.first) }
                .toList()
                .toFlowable()
                .compose(RxUtils.ioToMainTransformer())
                .subscribe(Consumer { loadGoods() }, refreshErrorConsumer)
    }

    fun removeGoods(goods: Goods) {
        refreshLiveData.value = true
        Flowable.fromCallable { Unit }
                .flatMap { goodsRepository.delete(goods) }
                .compose(RxUtils.ioToMainTransformer())
                .subscribe(removeGoodsConsumer, refreshErrorConsumer)
    }
}