package com.frolov.nikita.market.ui.screen.goods

import android.arch.lifecycle.Observer
import com.frolov.nikita.market.R
import com.frolov.nikita.market.models.goods.Goods
import com.frolov.nikita.market.models.goods.GoodsType
import com.frolov.nikita.market.ui.base.FragmentArgumentDelegate
import com.frolov.nikita.market.ui.base.pagination.BaseListFragment
import kotlinx.android.synthetic.main.fragment_goods.*
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.toast

class GoodsFragment : BaseListFragment<GoodsViewModel, Goods, GoodsAdapter>(), GoodsAdapterCallback {

    override val viewModelClass = GoodsViewModel::class.java
    override val layoutId = R.layout.fragment_goods
    override val recyclerViewId = R.id.rvGoods
    override val noResultViewId = R.id.tvNoResults
    override val refreshLayoutId = R.id.swRefreshGoods

    companion object {
        fun newInstance(type: GoodsType) = GoodsFragment().apply { this.type = type }
    }

    override fun getScreenTitle() = TITLE_STRING
    override fun getStringScreenTitle() = type.getNameCategory()
    override fun hasToolbar() = true
    override fun getToolbarId() = R.id.toolbar

    override fun getAdapter() = goodsAdapter
            ?: GoodsAdapter(ctx, this).apply { goodsAdapter = this }

    override fun loadInitial() = viewModel.loadGoods(type)
    override fun loadMoreData() = viewModel.loadMoreGoods(getAdapter().itemCount, type)

    override fun observeLiveData() {
        with(viewModel) {
            loadGoodsLiveData.observe(this@GoodsFragment, loadGoodsLiveDataObservable)
            loadMoreGoodsLiveData.observe(this@GoodsFragment, loadMoreGoodsLiveDataObservable)
            refreshLiveData.observe(this@GoodsFragment, refreshObserver)
            addGoodsLiveData.observe(this@GoodsFragment, addGoodsObserver)
            setLoadingLiveData(loadGoodsLiveData, loadMoreGoodsLiveData, refreshLiveData)
        }
        loadInitial()
    }

    private val loadGoodsLiveDataObservable = Observer<List<Goods>> {
        it?.let { onInitialDataLoaded(it) }
    }
    private val loadMoreGoodsLiveDataObservable = Observer<List<Goods>> {
        it?.let { onDataRangeLoaded(it) }
    }
    private val refreshObserver = Observer<Boolean> { it?.let { swRefreshGoods.isRefreshing = it } }
    private val addGoodsObserver = Observer<Goods> {
        it?.let {
            toast("Goods ${it.id}: ${it.type?.getNameCategory()} added to basket!")
        }
    }
    private var goodsAdapter: GoodsAdapter? = null
    private var type by FragmentArgumentDelegate<GoodsType>()

    override fun onClickItem(goods: Goods) {
        viewModel.addGoodsToBasket(goods)
    }
}