package com.frolov.nikita.market.ui.screen.goods

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.frolov.nikita.PAGE_LIMIT
import com.frolov.nikita.market.models.goods.Goods
import com.frolov.nikita.market.models.goods.GoodsType
import com.frolov.nikita.market.network.MockGoods
import com.frolov.nikita.market.ui.base.BaseViewModel
import com.frolov.nikita.market.utils.RxUtils
import io.reactivex.Flowable
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

class GoodsViewModel(application: Application) : BaseViewModel(application) {
    companion object {
        private const val DELAY_IN_MILLISECONDS = 1500L
    }

    val loadGoodsLiveData = MutableLiveData<List<Goods>>()

    val loadMoreGoodsLiveData = MutableLiveData<List<Goods>>()

    val refreshLiveData = MutableLiveData<Boolean>()

    private val loadGoodsSuccessConsumer = Consumer<List<Goods>> {
        loadGoodsLiveData.value = it
    }

    private val loadMoreVenuesSuccessConsumer = Consumer<List<Goods>> {
        loadMoreGoodsLiveData.value = it
        refreshLiveData.value = false
    }

    private val refreshErrorConsumer = Consumer<Throwable> {
        onErrorConsumer.accept(it)
        refreshLiveData.value = false
    }

    fun loadGoods(type: GoodsType) {
        Flowable.fromCallable { type }
                .map { typeGoods -> MockGoods.goods.filter { it.type == typeGoods }.take(PAGE_LIMIT) }
                .debounce(DELAY_IN_MILLISECONDS, TimeUnit.MILLISECONDS)
                .compose(RxUtils.ioToMainTransformer())
                .subscribe(loadGoodsSuccessConsumer, refreshErrorConsumer)
    }

    fun loadMoreGoods(offset: Int, type: GoodsType) {
        refreshLiveData.value = true
        Flowable.fromCallable { type }
                .map { typeGoods -> MockGoods.goods.filter { it.type == typeGoods }.subList(offset, offset + PAGE_LIMIT) }
                .debounce(DELAY_IN_MILLISECONDS, TimeUnit.MILLISECONDS)
                .compose(RxUtils.ioToMainTransformer())
                .subscribe(loadMoreVenuesSuccessConsumer, refreshErrorConsumer)
    }

}